package yaa.semantic.handlers;

import yaa.ast.LThan;
import yaa.pojos.GlobalData;
import yaa.pojos.YaaInfo;

import static yaa.semantic.handlers.OpUtils.binaryOp;

public class LThanOp {
  public static YaaInfo lThan(LThan ctx) {
    return binaryOp(GlobalData.lesser_op_name, ctx);
  }
}
