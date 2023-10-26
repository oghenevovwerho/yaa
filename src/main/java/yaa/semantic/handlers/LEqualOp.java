package yaa.semantic.handlers;

import yaa.ast.LEqual;
import yaa.pojos.GlobalData;
import yaa.pojos.YaaInfo;

import static yaa.semantic.handlers.OpUtils.binaryOp;

public class LEqualOp {
  public static YaaInfo lEqual(LEqual ctx) {
    return binaryOp(GlobalData.lesser_equal_op_name, ctx);
  }
}
