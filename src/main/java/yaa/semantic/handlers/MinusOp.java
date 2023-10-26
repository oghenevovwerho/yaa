package yaa.semantic.handlers;

import yaa.ast.Minus;
import yaa.pojos.GlobalData;
import yaa.pojos.YaaInfo;

import static yaa.semantic.handlers.OpUtils.binaryOp;

public class MinusOp {
  public static YaaInfo minus(Minus ctx) {
    return binaryOp(GlobalData.minus_op_name, ctx);
  }
}
