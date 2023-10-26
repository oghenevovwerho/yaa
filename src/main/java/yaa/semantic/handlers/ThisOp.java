package yaa.semantic.handlers;

import yaa.ast.This;
import yaa.pojos.GlobalData;
import yaa.pojos.YaaError;
import yaa.pojos.YaaInfo;

public class ThisOp {
  public static YaaInfo thisOp(This ctx) {
    var topClz = GlobalData.topClz;
    if (topClz.isEmpty()) {
      throw new YaaError(
        ctx.placeOfUse(),
        "\"this\" must only be used in a class declaration"
      );
    }
    return topClz.peek();
  }
}
