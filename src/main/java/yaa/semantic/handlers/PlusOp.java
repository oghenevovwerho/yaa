package yaa.semantic.handlers;

import yaa.ast.Plus;
import yaa.pojos.*;
import yaa.semantic.passes.fs6.results.CallResult;

import static yaa.pojos.GlobalData.*;
import static yaa.pojos.GlobalData.plus_op_name;

public class PlusOp {
  public static YaaInfo plus(Plus ctx) {
    var l_value = ctx.e1.visit(GlobalData.fs);
    var r_value = ctx.e2.visit(GlobalData.fs);
    if (l_value.name.equals("java.lang.String")) {
      var string$mtd = new YaaFun(plus_op_name, "java/lang/String");
      string$mtd.parameters.add(r_value);
      string$mtd.type = l_value;
      var result = new CallResult(string$mtd);
      results.get(fs.path).put(ctx, result);
      return l_value;
    }
    if (l_value instanceof YaaClz clz) {
      if (l_value.isPrimitive() && r_value.isBoxed()){
        throw new YaaError(
          ctx.e2.placeOfUse(),
          "No method in " + l_value + " matched the given arguments",
          clz.getMethod(plus_op_name).candidates()
        );
      }
      if (r_value.isBoxed() && l_value.isPrimitive()){
        throw new YaaError(
          ctx.op.placeOfUse(), r_value.toString(),
          "The type above does not define the " + plus_op_name + " method"
        );
      }
      var pack = clz.getMethod(plus_op_name);
      if (pack != null) {
        var mtd = pack.choseOpMtd(r_value);
        if (mtd != null) {
          var result = new CallResult(mtd);
          results.get(fs.path).put(ctx, result);
          return mtd.type;
        }
        throw new YaaError(
          ctx.e2.placeOfUse(),
          "No method in " + l_value + " matched the given arguments",
          pack.candidates()
        );
      }
      throw new YaaError(
        ctx.op.placeOfUse(), l_value.toString(),
        "The type above does not define the " + plus_op_name + " method"
      );
    }
    throw new YaaError(
      ctx.op.placeOfUse(), "The attempted operation is illegal"
    );
  }
}