package yaa.semantic.handlers;

import yaa.ast.ECall;
import yaa.pojos.*;
import yaa.semantic.passes.fs6.results.CallResult;

import static yaa.semantic.handlers.OpUtils.t$arguments;
import static yaa.semantic.handlers.OpUtils.v$arguments;
import static yaa.pojos.GlobalData.*;

public class ECallOp {
  public static YaaInfo eCall(ECall ctx) {
    var arguments = v$arguments(ctx.values);
    var types = t$arguments(ctx.types);
    var info = ctx.e.visit(fs);
    if (info instanceof YaaClz clz) {
      //for things like 9(), or profile.name()() that tries
      //to call the returned possibly String class.
      throw new YaaError(
        ctx.e.placeOfUse(), clz + " can only " +
        "be initialized in its name form"
      );
    }
    if (info instanceof MtdPack pack) {
      var mtd = pack.choseMtd(types, arguments);
      if (mtd != null) {
        var call$result = new CallResult(mtd);
        results.get(fs.path).put(ctx, call$result);
        return mtd.type;
      }
      throw new YaaError(
        ctx.placeOfUse(), ctx.toString(),
        "There is no function in scope with the matching parameters",
        pack.candidates()
      );
    } else if (info instanceof YaaFun fun) {
      var mtd = fun.acceptsMtd(arguments);
      if (mtd != null) {
        var result = new CallResult(mtd);
        results.get(fs.path).put(ctx, result);
        return mtd.type;
      }
      throw new YaaError(
        ctx.placeOfUse(), ctx.toString(),
        "There is no function in scope with the matching parameters",
        "The function below is the only candidate", fun.toString()
      );
    } else {
      throw new YaaError(
        ctx.placeOfUse(),
        "The symbol above is not a callable"
      );
    }
  }
}
