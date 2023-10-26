package yaa.semantic.handlers;

import yaa.ast.BitAnd;
import yaa.pojos.YaaInfo;

import static yaa.pojos.GlobalData.bit_and_op_name;
import static yaa.semantic.handlers.OpUtils.binaryOp;

public class BitAndOp {
  public static YaaInfo bitAnd(BitAnd ctx) {
    return binaryOp(bit_and_op_name, ctx);
  }
}
