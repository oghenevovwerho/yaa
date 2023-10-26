package yaa.semantic.handlers;

import yaa.ast.EMtd;
import yaa.pojos.*;
import yaa.semantic.passes.fs6.results.CallResult;
import yaa.semantic.passes.fs6.results.InitResult;

import java.util.ArrayList;

import static yaa.pojos.GlobalData.*;

public class EMtdOp {
  public static YaaInfo eMtd(EMtd ctx) {
    var info = ctx.e.visit(fs);
    var mtdName = ctx.mName.content;
    var types = OpUtils.t$arguments(ctx.types);
    var values = OpUtils.v$arguments(ctx.arguments);
    if (info instanceof YaaClz clz) {
      if (no_call_object_mtds.contains(mtdName)) {
        throw new YaaError(
            ctx.mName.placeOfUse(),
            "The java.lang.Object method \""
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
      var pack = clz.getMethod(mtdName);
      if (pack == null) {
        var inner$clz = clz.internal$classes.get(mtdName);
        if (inner$clz != null) {
          clz.checkTypeArgumentSize(types.size(), ctx.placeOfUse());
          var addresses = new ArrayList<String>();
          for (var type : ctx.types) {
            addresses.add(type.placeOfUse());
          }
          clz.checkTypeArguments(types, addresses);
          if (clz.inputted.size() > 0) {
            inner$clz = inner$clz.changeCBounds(types);
          }
          for (var init : inner$clz.inits) {
            var descriptor = init.acceptsInit(values);
            if (descriptor != null) {
              var call$result = new InitResult(init, inner$clz);
              results.get(fs.path).put(ctx, call$result);
              return inner$clz;
            }
          }
          throw new YaaError(
              ctx.mName.placeOfUse(), inner$clz.toString(),
              "The class above has initializer with the given arguments",
              inner$clz.initCandidates(new ArrayList<>(0))
          );
        }
        throw new YaaError(
            ctx.mName.placeOfUse(), clz.toString(),
            "The class above does not define the method \"" + mtdName + "\""
        );
      }


      if (clz.inputted.size() > 0) {
        var new_pack = new MtdPack(mtdName);
        for (var mtd : pack.methods) {
          if (!mtd.hasClzTypeParam || mtd.parameters.size() != values.size()) {
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
            } else if (param.mbIndex > -1) {
              new$parameters.add(clz.inputted.get(param.mbIndex));
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
          "The class above has no method with the given arguments",
          pack.candidates()
      );
    }
    throw new YaaError(
        ctx.mName.placeOfUse(), info.toString(),
        "The field above does not reference a callable object",
        mtdName
    );
  }
}
