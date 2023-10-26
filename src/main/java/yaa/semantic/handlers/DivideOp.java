package yaa.semantic.handlers;

import yaa.ast.Divide;
import yaa.pojos.GlobalData;
import yaa.pojos.YaaInfo;

import static yaa.semantic.handlers.OpUtils.binaryOp;

public class DivideOp {
  public static YaaInfo divide(Divide ctx) {
    return binaryOp(GlobalData.divide_op_name, ctx);
  }
}
