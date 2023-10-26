package yaa.semantic.handlers;

import yaa.ast.SuperMtd;
import yaa.semantic.passes.fs6.results.CallResult;
import yaa.pojos.YaaError;
import yaa.pojos.YaaInfo;
import yaa.pojos.GlobalData;

import java.util.ArrayList;

public class SuperMtdOp {
  public static YaaInfo superMtd(SuperMtd ctx) {
    var top$clz = GlobalData.topClz.peek();
    var mtdName = ctx.name.content;
    if (top$clz == null) {
      throw new YaaError(
        ctx.placeOfUse(), "A parent can only be called in a type"
      );
    }
    var clz = top$clz.parent;
    if (clz == null) {
      throw new YaaError(
        ctx.placeOfUse(), GlobalData.topClz.peek() + " has no parent"
      );
    }
    var pack = clz.getMethod(mtdName);
    if (pack == null) {
      throw new YaaError(
        ctx.name.placeOfUse(),
        clz.toString(), "The type above does not define " +
        "any methods with the name \"" + mtdName + "\""
      );
    }
    var values = OpUtils.v$arguments(ctx.arguments);
    var mtd = pack.choseMtd(new ArrayList<>(0), values);
    if (mtd != null) {
      var result = new CallResult(mtd, clz);
      GlobalData.results.get(GlobalData.fs.path).put(ctx, result);
      return mtd.type;
    }
    throw new YaaError(
      ctx.name.placeOfUse(), clz.toString(),
      "The class above does not define any " +
        "method with the given arguments",
      pack.candidates()
    );
  }
}
