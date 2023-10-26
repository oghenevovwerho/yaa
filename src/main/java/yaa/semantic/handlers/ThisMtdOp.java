package yaa.semantic.handlers;

import yaa.ast.ThisMtd;
import yaa.semantic.passes.fs6.results.CallResult;
import yaa.pojos.YaaError;
import yaa.pojos.YaaInfo;

import java.util.ArrayList;

import static yaa.semantic.handlers.OpUtils.v$arguments;
import static yaa.pojos.GlobalData.*;

public class ThisMtdOp {
  public static YaaInfo thisMtd(ThisMtd ctx) {
    var clz = topClz.peek();
    if (clz == null) {
      throw new YaaError(
        ctx.name.placeOfUse(), "\"this\" must be used from within a type"
      );
    }
    var mtdName = ctx.name.content;
    var pack = clz.getMethod(mtdName);
    if (pack == null) {
      throw new YaaError(
        ctx.name.placeOfUse(),
        clz.toString(), "The type above does not define " +
        "any methods with the name \"" + mtdName + "\""
      );
    }
    var mtd = pack.choseMtd(new ArrayList<>(0), v$arguments(ctx.arguments));
    if (mtd != null) {
      results.get(fs.path).put(ctx, new CallResult(mtd, clz));
      return mtd.type;
    }
    throw new YaaError(
      ctx.name.placeOfUse(),
      clz.toString(),
      "The class above does not define any " +
        "method with the given arguments",
      pack.candidates()
    );
  }
}
