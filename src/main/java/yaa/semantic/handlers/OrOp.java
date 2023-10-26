package yaa.semantic.handlers;

import yaa.ast.Or;
import yaa.semantic.passes.fs6.results.CallResult;
import yaa.pojos.GlobalData;
import yaa.pojos.YaaClz;
import yaa.pojos.YaaError;
import yaa.pojos.YaaInfo;

import static yaa.pojos.GlobalData.or_op_name;

public class OrOp {
  public static YaaInfo or(Or ctx) {
    var l_value = ctx.e1.visit(GlobalData.fs);
    var r_value = ctx.e2.visit(GlobalData.fs);
    if (l_value instanceof YaaClz clz) {
      if (l_value.isPrimitive() && r_value.isBoxed()){
        throw new YaaError(
          ctx.e2.placeOfUse(),
          "No method in " + l_value + " matched the given arguments",
          clz.getMethod(or_op_name).candidates()
        );
      }
      if (r_value.isBoxed() && l_value.isPrimitive()){
        throw new YaaError(
          ctx.op.placeOfUse(), r_value.toString(),
          "The type above does not define the " + or_op_name + " method"
        );
      }
      var pack = clz.getMethod(or_op_name);
      if (pack != null) {
        var mtd = pack.choseOpMtd(r_value);
        if (mtd != null) {
          var result = new CallResult(mtd);
          GlobalData.results.get(GlobalData.fs.path).put(ctx, result);
          return mtd.type;
        }
        throw new YaaError(
            ctx.op.placeOfUse(),
            "No method in " + l_value + " matched the given arguments",
            pack.candidates()
        );
      }
      throw new YaaError(
          ctx.op.placeOfUse(),
          l_value.toString(), r_value.toString(),
          l_value + " does not define the "+or_op_name+" method"
      );
    }
    throw new YaaError(
        ctx.placeOfUse(),
        l_value.toString(), r_value.toString(),
        "The operation above is undefined"
    );
  }
}
