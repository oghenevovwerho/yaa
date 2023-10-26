package yaa.semantic.handlers;

import yaa.ast.VCall;
import yaa.pojos.*;
import yaa.pojos.primitives.ArrayMold;
import yaa.semantic.passes.fs6.results.CallResult;
import yaa.semantic.passes.fs6.results.InitResult;

import java.lang.annotation.ElementType;
import java.util.ArrayList;
import java.util.List;

import static yaa.pojos.GlobalData.*;
import static yaa.pojos.TypeCategory.enum_c;
import static yaa.pojos.TypeCategory.trait_c;
import static yaa.semantic.handlers.OpUtils.t$arguments;
import static yaa.semantic.handlers.OpUtils.v$arguments;

public class VCallOp {
  public static YaaInfo vCall(VCall ctx) {
    var name = ctx.name.content;

    var values = v$arguments(ctx.arguments);
    var types = t$arguments(ctx.types);
    var info = fs.getSymbolInSameScope(name);
    //this is to check if a function in same scope is forward referenced
    if (info != null) {
      if (info.startLine > ctx.start.line) {
        throw new YaaError(
            ctx.placeOfUse(),
            "Forward referencing of " +
                "symbols in the same scope is not allowed"
        );
      }
    } else {
      info = fs.getSymbol(name);
      if (info == null) {
        throw new YaaError(
            ctx.placeOfUse(),
            "The called name \"" + name
                + "\" is not defined in scope"
        );
      }
    }

    if (info instanceof MtdPack pack) {
      if (ctx.metaCalls.size() > 0) {
        throw new YaaError(
            ctx.metaCalls.get(0).placeOfUse(),
            "Only types can carry annotations"
        );
      }
      var mtd = pack.choseMtd(types, values);
      if (mtd != null) {
        results.get(fs.path).put(ctx, new CallResult(mtd));
        return mtd.type;
      }
    }

    if (ctx.metaCalls.size() > 0) {
      if (!(info instanceof YaaClz)) {
        throw new YaaError(
            ctx.metaCalls.get(0).placeOfUse(),
            "Only types can carry annotations"
        );
      } else {
        MetaCallOp.metaCalls(ctx.metaCalls, ElementType.TYPE_USE);
      }
    }

    if (ctx.types.size() > 0) {
      for (var type_arg : ctx.types) {
        MetaCallOp.metaCalls(type_arg, ElementType.TYPE_USE);
      }
    }

    if (info instanceof YaaField field) {
      return handleData(ctx, field.data, types, values);
    }
    return handleData(ctx, info, types, values);
  }

  private static YaaInfo handleData(
      VCall ctx, YaaInfo info, List<YaaClz> types, List<YaaInfo> values) {
    if (info instanceof YaaClz clz) {
      return handleClzCase(ctx, clz, types, values);
    } else if (info instanceof MtdPack pack) {
      if (types.size() > 0) {
        return paramPackMtd(ctx, pack, types, values);
      } else {
        return plainPackMtd(ctx, pack, values);
      }
    } else if (info instanceof YaaFun fun) {
      var mtd = fun.acceptsMtd(values);
      if (mtd != null) {
        var call_result = new CallResult(fun);
        results.get(fs.path).put(ctx, call_result);
        return mtd.type;
      }
      throw new YaaError(
          ctx.placeOfUse(), values.toString(),
          "There is no function in scope with the parameters above",
          "The function below is the only candidate", fun.toString()
      );
    }
    var name = ctx.name.content;
    throw new YaaError(ctx.placeOfUse(), "\"" + name + "\" is not a callable");
  }

  private static YaaInfo handleClzCase(
      VCall ctx, YaaClz clz, List<YaaClz> types, List<YaaInfo> values) {
    if (clz.name.equals(array$name)) {
      return handleArray(ctx, types, values);
    }
    if (clz.outerClz != null) {
      if (clz.outerClz.category == enum_c) {
        throw new YaaError(
            ctx.placeOfUse(), clz.toString(),
            "An instance of the enclosing enum must come first",
            clz.outerClz.toString()
        );
      }
      throw new YaaError(
          ctx.placeOfUse(), clz.toString(),
          "The enclosing type below must be called first",
          clz.outerClz.toString()
      );
    }
    if (clz.category == enum_c) {
      throw new YaaError(
          ctx.placeOfUse(), clz.toString(),
          "An enum cannot be called"
      );
    }
    if (clz.cbIndex > -1 || clz.mbIndex > -1) {
      throw new YaaError(
          ctx.placeOfUse(), clz.toString(),
          "A bound cannot be called"
      );
    }
    if (clz.category == trait_c) {
      throw new YaaError(
          ctx.placeOfUse(), clz.toString(),
          "A trait type cannot be initialized"
      );
    }
    if (clz.isAbstract) {
      throw new YaaError(
          ctx.placeOfUse(), clz.toString(),
          "An abstract class cannot be called"
      );
    }
    clz.checkTypeArgumentSize(types.size(), ctx.placeOfUse());
    var addresses = new ArrayList<String>();
    for (var type : ctx.types) {
      addresses.add(type.placeOfUse());
    }
    clz.checkTypeArguments(types, addresses);
    if (clz.inputted.size() > 0) {
      clz = clz.changeCBounds(types);
    }

    var changedClzInits = clz.inputted.size() > 0 ? getChangedInits(clz, types) : clz.inits;

    for (var init : changedClzInits) {
      var acceptedInit = init.acceptsInit(values);
      if (acceptedInit != null) {
        results.get(fs.path).put(ctx, new InitResult(acceptedInit, clz));
        return clz;
      }
    }

    throw new YaaError(
        ctx.placeOfUse(), clz + " does not define " +
        "a initializer with the given arguments",
        values.toString(), clz.initCandidates(types)
    );
  }

  public static List<YaaInit> getChangedInits(YaaClz clz, List<YaaClz> types) {
    var yaaInits = new ArrayList<YaaInit>(clz.inits.size());
    for (var init : clz.inits) {
      var cloned_init = (YaaInit) init.cloneInfo();
      var raw_parameters = cloned_init.parameters;
      cloned_init.raw_parameters = raw_parameters;
      var new$parameters = new ArrayList<YaaInfo>(raw_parameters.size());
      for (var param : raw_parameters) {
        if (param.cbIndex > -1) {
          new$parameters.add(types.get(param.cbIndex));
        } else if (param instanceof YaaClz pClz) {
          new$parameters.add(pClz.changeCBounds(types));
        } else {
          new$parameters.add(param);
        }
      }
      cloned_init.parameters = new$parameters;
      yaaInits.add(cloned_init);
    }

    return yaaInits;
  }

  private static YaaInfo handleArray(
      VCall ctx, List<YaaClz> types, List<YaaInfo> values) {
    if (ctx.types.size() == 0) {
      return ArrayOp.arrayOp(ctx, values);
    }
    if (values.size() > 1) {
      throw new YaaError(
          ctx.arguments.get(1).placeOfUse(),
          "An array can not have more than one argument for size"
      );
    }
    if (types.size() == 0) {
      var type$arguments = new ArrayList<YaaClz>(1);
      type$arguments.add(object$clz);
      var array$type = ArrayMold.newArray().changeCBounds(type$arguments);
      var result = new InitResult(new YaaInit(), array$type);
      results.get(fs.path).put(ctx, result);
      return array$type;
    } else if (types.size() == 1) {
      var type$arguments = new ArrayList<YaaClz>(1);
      type$arguments.add(types.get(0));
      var array$type = ArrayMold.newArray().changeCBounds(type$arguments);
      var result = new InitResult(new YaaInit(), array$type);
      results.get(fs.path).put(ctx, result);
      return array$type;
    } else {
      throw new YaaError(
          ctx.types.get(1).placeOfUse(),
          "An array can not have more than one type argument"
      );
    }
  }

  private static YaaInfo plainPackMtd(
      VCall ctx, MtdPack pack, List<YaaInfo> values) {
    var mtd = pack.choseMtd(new ArrayList<>(0), values);
    if (mtd != null) {
      results.get(fs.path).put(ctx, new CallResult(mtd));
      return mtd.type;
    }

    throw new YaaError(
        ctx.placeOfUse(), values.toString(),
        "There is no function in scope with the parameters above",
        pack.candidates()
    );
  }

  private static YaaInfo paramPackMtd(
      VCall ctx, MtdPack pack,
      List<YaaClz> types, List<YaaInfo> values) {
    //a static java method cannot have a class type parameter
    //as parameter, return type, as part of a complex return
    //type or complex parameter.
    var mtdName = ctx.name.content;
    var new_pack = new MtdPack(new ArrayList<>(pack.methods.size()), mtdName);
    for (var mtd : pack.methods) {
      if (mtd.inputted.size() != types.size()) {
        //new_pack.methods.add(mtd);
        continue;
      }

      if (!typeArgsAcceptable(mtd, types)) {
        //new_pack.methods.add(mtd);
        continue;
      }

      var cloned_mtd = (YaaFun) mtd.cloneInfo();
      var raw_parameters = cloned_mtd.parameters;
      cloned_mtd.raw_parameters = raw_parameters;
      var new$parameters = new ArrayList<YaaInfo>(raw_parameters.size());
      for (var param : raw_parameters) {
        if (param.mbIndex > -1) {
          new$parameters.add(types.get(param.mbIndex));
        } else if (param instanceof YaaClz pClz) {
          new$parameters.add(pClz.changeMBounds(types));
        } else {
          new$parameters.add(param);
        }
      }
      cloned_mtd.parameters = new$parameters;
      new_pack.methods.add(cloned_mtd);
    }

    var mtd = new_pack.choseMtd(types, values);
    if (mtd != null) {
      mtd.rawType = mtd.type;
      if (mtd.type instanceof YaaClz mtd$type) {
        if (mtd$type.inputted.size() > 0) {
          mtd.type = mtd$type.changeMBounds(types);
        } else if (mtd$type.mbIndex > -1) {
          mtd.type = types.get(mtd$type.mbIndex);
        }
      }
      results.get(fs.path).put(ctx, new CallResult(mtd));
      return mtd.type;
    }

    throw new YaaError(
        ctx.placeOfUse(), values.toString(),
        "There is no function in scope with the parameters above",
        new_pack.candidates()
    );
  }

  public static boolean typeArgsAcceptable(YaaFun fun, List<YaaClz> typeArguments) {
    for (int i = 0; i < fun.inputted.size(); i++) {
      var inputtedClz = fun.inputted.get(i);
      var argumentClz = typeArguments.get(i);
      if (!inputtedClz.parent.isParentOf(argumentClz)) {
        return false;
      }
      for (var trait : inputtedClz.parent.traits.values()) {
        if (argumentClz.hasTrait(trait) == null) {
          return false;
        }
      }
    }
    return true;
  }
}