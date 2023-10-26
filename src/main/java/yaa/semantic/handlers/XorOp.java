package yaa.semantic.handlers;

import yaa.ast.Xor;
import yaa.pojos.GlobalData;
import yaa.pojos.YaaInfo;

import static yaa.semantic.handlers.OpUtils.binaryOp;

public class XorOp {
  public static YaaInfo xor(Xor ctx) {
    return binaryOp(GlobalData.xor_op_name, ctx);
  }
}
