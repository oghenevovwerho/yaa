package yaa.semantic.handlers;

import yaa.ast.BitNot;
import yaa.semantic.passes.fs6.results.CallResult;
import yaa.pojos.GlobalData;
import yaa.pojos.YaaClz;
import yaa.pojos.YaaError;
import yaa.pojos.YaaInfo;

import static yaa.pojos.GlobalData.*;

public class BitNegateOp {
  public static YaaInfo bitNot(BitNot ctx) {
    var value = ctx.e.visit(fs);
    if (value instanceof YaaClz clz) {
      var pack = clz.getMethod(bit_negate_op_name);
      if (pack != null) {
        for (var mtd : pack.methods) {
          if (mtd.parameters.size() == 0) {
            var result = new CallResult(mtd);
            GlobalData.results.get(fs.path).put(ctx, result);
            return mtd.type;
          }
        }
        throw new YaaError(
          ctx.placeOfUse(),
          "No method in " + value + " matched the given arguments",
          pack.candidates()
        );
      }
      throw new YaaError(
        ctx.placeOfUse(),
        value.toString(), value + " does not define the " +
        "" + minus_op_name + " method"
      );
    }
    throw new YaaError(
      ctx.placeOfUse(),
      value.toString(), "The operation above is undefined"
    );
  }
}
