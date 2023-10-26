package yaa.semantic.handlers;

import yaa.ast.VMtd;
import yaa.pojos.*;
import yaa.pojos.jMold.JMold;
import yaa.semantic.passes.fs5.F5Callable;
import yaa.semantic.passes.fs6.results.CallResult;
import yaa.semantic.passes.fs6.results.InitResult;

import java.util.ArrayList;
import java.util.List;

import static yaa.pojos.GlobalData.no_call_object_mtds;
import static yaa.semantic.handlers.OpUtils.t$arguments;
import static yaa.semantic.handlers.OpUtils.v$arguments;
import static yaa.pojos.GlobalData.fs;
import static yaa.pojos.GlobalData.results;

public class VMtdOp {
  public static YaaInfo vMtd(VMtd ctx) {
    var fieldName = ctx.eName.content;
    var info = fs.getSymbol(fieldName);
    var mtdName = ctx.mName.content;
    if (info == null) {
      throw new YaaError(
          ctx.eName.placeOfUse(),
          "The referenced name \"" + fieldName + "\" is not defined in scope"
      );
    }
    var types = t$arguments(ctx.types);
    var values = v$arguments(ctx.arguments);
    if (info.cbIndex > -1) {
      throw new YaaError(
          ctx.eName.placeOfUse(),
          "\"" + fieldName + "\" is a bound" +
              ", type bounds cannot act as expressions"
      );
    }
    if (info instanceof YaaClz clz) {
      if (clz.category == TypeCategory.enum_c) {
        throw new YaaError(
            ctx.eName.placeOfUse(),
            "\"" + fieldName + "\" is an enum" +
                ", enums cannot act as expressions"
        );
      }
      var pack = clz.getStaticMethod(mtdName);
      if (pack == null) {
        throw new YaaError(
            ctx.mName.placeOfUse(), clz.toString(), "The static method \""
            + mtdName + "\" is not defined in the type above"
        );
      }

      if (clz.name.equals("java.lang.System") && mtdName.equals("exit")) {
        F5Callable.wasExit = true;
      }

      if (types.size() > 0) {
        return paramStaticMtd(ctx, clz, pack, types, values);
      } else {
        return plainStaticMtd(ctx, clz, pack, values);
      }
    } else if (info instanceof YaaField field) {
      if (field.data instanceof YaaClz clz) {
        var pack = clz.getMethod(mtdName);
        if (pack == null) {
          return handleInternalClz(ctx, clz, types, values);
        }
        if (no_call_object_mtds.contains(mtdName)) {
          //this should only happen for instance methods
          throw new YaaError(
              ctx.mName.placeOfUse(), "The java.lang.Object method \""
              + mtdName + "\" must not be called"
          );
        }
        if (mtdName.equals("equals")) {
          //this should only happen for instance methods
          throw new YaaError(
              ctx.mName.placeOfUse(), "The java.lang.Object method \""
              + mtdName + "\" must not be called",
              "All object value based comparisons must be done with =="
          );
        }
        if (types.size() > 0) {
          return paramInstanceMtd(ctx, clz, pack, types, values);
        } else {
          return plainInstanceMtd(ctx, clz, pack, values);
        }
      }
      throw new YaaError(
          ctx.eName.placeOfUse(), info.toString(),
          "The referenced field above does not reference a callable object"
      );
    } else {
      throw new YaaError(
          ctx.eName.placeOfUse(), info.toString(),
          "The referenced field above does not reference a callable object"
      );
    }
  }

  private static YaaInfo plainInstanceMtd(
      VMtd ctx, YaaClz clz, MtdPack pack, List<YaaInfo> values) {
    var mtdName = ctx.mName.content;
    if (clz.inputted.size() > 0) {
      var new_pack = new MtdPack(mtdName);
      for (var mtd : pack.methods) {
        if (mtd.parameters.size() != values.size()) {
          continue;
        }
        if (!mtd.hasClzTypeParam) {
          new_pack.methods.add(mtd);
          continue;
        }
        var cloned_mtd = (YaaFun) mtd.cloneInfo();
        var raw_parameters = cloned_mtd.parameters;
        cloned_mtd.raw_parameters = raw_parameters;
        var new$parameters = new ArrayList<YaaInfo>(raw_parameters.size());
        for (var param : raw_parameters) {
          if (param.cbIndex > -1) {
            new$parameters.add(clz.inputted.get(param.cbIndex));
          } else if (param instanceof YaaClz pClz && pClz.inputted.size() != 0) {
            new$parameters.add(pClz.changeCBounds(clz.inputted));
          } else {
            new$parameters.add(param);
          }
        }
        cloned_mtd.parameters = new$parameters;
        new_pack.methods.add(cloned_mtd);
      }
      pack = new_pack;
    }
    var mtd = pack.choseMtd(new ArrayList<>(0), values);
    if (mtd != null) {
      OpUtils.checkForVisibility(ctx.mName.placeOfUse(), mtd);
      mtd.rawType = mtd.type;
      if (clz.inputted.size() > 0) {
        if (mtd.type instanceof YaaClz mtd$type) {
          if (mtd$type.inputted.size() > 0) {
            mtd.type = mtd$type.changeCBounds(clz.inputted);
          } else if (mtd$type.cbIndex > -1) {
            mtd.type = clz.inputted.get(mtd$type.cbIndex);
          }
        }
      }
      results.get(fs.path).put(ctx, new CallResult(mtd, clz));
      return mtd.type;
    }
    throw new YaaError(
        ctx.mName.placeOfUse(), clz.toString(),
        "The class above does not define any " +
            "method with the arguments below",
        values.toString(), pack.candidates()
    );
  }

  private static YaaInfo paramInstanceMtd(
      VMtd ctx, YaaClz clz, MtdPack pack, List<YaaClz> types, List<YaaInfo> values
  ) {
    var mtdName = ctx.mName.content;
    if (clz.inputted.size() > 0) {
      var new_pack = new MtdPack(mtdName);
      for (var mtd : pack.methods) {
        if (mtd.parameters.size() != values.size()) {
          continue;
        }
        if (!mtd.hasClzTypeParam) {
          new_pack.methods.add(mtd);
          continue;
        }
        var cloned_mtd = (YaaFun) mtd.cloneInfo();
        var raw_parameters = cloned_mtd.parameters;
        cloned_mtd.raw_parameters = raw_parameters;
        var new$parameters = new ArrayList<YaaInfo>(raw_parameters.size());
        for (var param : raw_parameters) {
          if (param.cbIndex > -1) {
            new$parameters.add(clz.inputted.get(param.cbIndex));
          } else if (param instanceof YaaClz pClz && pClz.inputted.size() != 0) {
            new$parameters.add(pClz.changeCBounds(clz.inputted));
          } else {
            new$parameters.add(param);
          }
        }
        cloned_mtd.parameters = new$parameters;
        new_pack.methods.add(cloned_mtd);
      }
      pack = new_pack;
    }
    var mtd = pack.choseMtd(types, values);
    if (mtd != null) {
      OpUtils.checkForVisibility(ctx.mName.placeOfUse(), mtd);
      mtd.rawType = mtd.type;
      if (clz.inputted.size() > 0) {
        if (mtd.type instanceof YaaClz mtd$type) {
          if (mtd$type.inputted.size() > 0) {
            mtd.type = mtd$type.changeCBounds(types);
          } else if (mtd$type.cbIndex > -1) {
            mtd.type = types.get(mtd$type.cbIndex);
          }
        }
      }
      if (mtd.inputted.size() > 0) {
        if (mtd.type instanceof YaaClz mtd$type) {
          if (mtd$type.inputted.size() > 0) {
            mtd.type = mtd$type.changeMBounds(types);
          } else if (mtd$type.mbIndex > -1) {
            mtd.type = types.get(mtd$type.mbIndex);
          }
        }
      }
      results.get(fs.path).put(ctx, new CallResult(mtd, clz));
      return mtd.type;
    }
    throw new YaaError(
        ctx.mName.placeOfUse(), clz.toString(),
        "The class above does not define any " +
            "method with the arguments below",
        values.toString(), pack.candidates()
    );
  }

  private static YaaInfo plainStaticMtd(
      VMtd ctx, YaaClz clz, MtdPack pack, List<YaaInfo> values) {
    var mtd = pack.choseMtd(new ArrayList<>(0), values);
    if (mtd != null) {
      OpUtils.checkForVisibility(ctx.mName.placeOfUse(), mtd);
      results.get(fs.path).put(ctx, new CallResult(mtd, clz));
      return mtd.type;
    }

    throw new YaaError(
        ctx.mName.placeOfUse(), clz + " does not define any " +
        "static method with the given arguments",
        values.toString(), pack.candidates()
    );
  }

  private static YaaInfo paramStaticMtd(
      VMtd ctx, YaaClz clz, MtdPack pack,
      List<YaaClz> types, List<YaaInfo> values) {
    //a static java method cannot have a class type parameter
    //as parameter, return type, as part of a complex return
    //type or complex parameter.
    var mtdName = ctx.mName.content;
    var new_pack = new MtdPack(mtdName);
    for (var mtd : pack.methods) {
      if (mtd.inputted.size() != types.size()) {
        new_pack.methods.add(mtd);
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
      OpUtils.checkForVisibility(ctx.mName.placeOfUse(), mtd);
      mtd.rawType = mtd.type;
      if (mtd.type instanceof YaaClz mtd$type) {
        if (mtd$type.inputted.size() > 0) {
          mtd.type = mtd$type.changeMBounds(types);
        } else if (mtd$type.mbIndex > -1) {
          mtd.type = types.get(mtd$type.mbIndex);
        }
      }
      results.get(fs.path).put(ctx, new CallResult(mtd, clz));
      return mtd.type;
    }

    throw new YaaError(
        ctx.mName.placeOfUse(), clz + " does not define any " +
        "static method with the given arguments",
        new_pack.candidates()
    );
  }

  private static YaaInfo handleInternalClz(
      VMtd ctx, YaaClz clz, List<YaaClz> types, List<YaaInfo> values) {
    var mtdName = ctx.mName.content;
    var inner = clz.internal$classes.get(mtdName);
    if (inner == null) {
      var clz_name = clz.toString();
      if (clz.cbIndex > -1 || clz.mbIndex > -1) {
        clz_name = clz.parent.toString();
      }
      throw new YaaError(
          ctx.mName.placeOfUse(), clz_name,
          "The type above does not define " +
              "any methods with the name \"" + mtdName + "\"",
          "The following are the valid functions",
          clz.instanceMethods.toString()
      );
    }
    clz.checkTypeArgumentSize(ctx.types.size(), ctx.placeOfUse());
    var addresses = new ArrayList<String>();
    for (var type : ctx.types) {
      addresses.add(type.placeOfUse());
    }
    clz.checkTypeArguments(types, addresses);
    if (clz.inputted.size() > 0) {
      clz = clz.changeCBounds(types);
    }
    for (var init : clz.inits) {
      var descriptor = init.acceptsInit(values);
      if (descriptor != null) {
        var call$result = new InitResult(init, inner);
        results.get(fs.path).put(ctx, call$result);
        return inner;
      }
    }
    throw new YaaError(
        ctx.mName.placeOfUse(), inner.toString(),
        "The class above does not define any " +
            "initializer with the given arguments",
        values.toString()
    );
  }
}
