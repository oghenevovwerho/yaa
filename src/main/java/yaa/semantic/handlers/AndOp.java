package yaa.semantic.handlers;

import yaa.ast.And;
import yaa.pojos.GlobalData;
import yaa.pojos.YaaClz;
import yaa.pojos.YaaError;
import yaa.pojos.YaaInfo;
import yaa.semantic.passes.fs6.results.CallResult;

import static yaa.pojos.GlobalData.*;

public class AndOp {
  public static YaaInfo and(And ctx) {
    var l_value = ctx.e1.visit(GlobalData.fs);
    var r_value = ctx.e2.visit(GlobalData.fs);
    if (l_value instanceof YaaClz clz) {
      if (l_value.isPrimitive() && r_value.isBoxed()) {
        throw new YaaError(
          ctx.op.placeOfUse(),
          "No method in " + l_value + " matched the given arguments",
          clz.getMethod(and_op_name).candidates()
        );
      }
      if (r_value.isBoxed() && l_value.isPrimitive()) {
        throw new YaaError(
          ctx.op.placeOfUse(), r_value.toString(),
          "The type above does not define the " + and_op_name + " method"
        );
      }
      var pack = clz.getMethod(and_op_name);
      if (pack != null) {
        var mtd = pack.choseOpMtd(r_value);
        if (mtd != null) {
          var result = new CallResult(mtd);
          results.get(fs.path).put(ctx, result);
          return mtd.type;
        }
        throw new YaaError(
          ctx.placeOfUse(),
          l_value + " && " + r_value,
          "No method in " + l_value + " matched the given arguments",
          pack.candidates()
        );
      }
      throw new YaaError(
        ctx.placeOfUse(),
        l_value.toString(),
        "The type above does not define the $and method"
      );
    }
    throw new YaaError(
      ctx.placeOfUse(),
      l_value.toString(), r_value.toString(),
      "The operation above is undefined"
    );
  }
}
