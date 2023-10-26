package yaa.semantic.handlers;

import yaa.ast.GThan;
import yaa.pojos.GlobalData;
import yaa.pojos.YaaInfo;

import static yaa.semantic.handlers.OpUtils.binaryOp;

public class GThanOp {
  public static YaaInfo gThan(GThan ctx) {
    return binaryOp(GlobalData.greater_op_name, ctx);
  }
}
