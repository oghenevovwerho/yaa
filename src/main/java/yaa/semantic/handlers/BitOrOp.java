package yaa.semantic.handlers;

import yaa.ast.BitOr;
import yaa.pojos.YaaInfo;

import static yaa.pojos.GlobalData.bit_or_op_name;
import static yaa.semantic.handlers.OpUtils.binaryOp;

public class BitOrOp {
  public static YaaInfo bitOr(BitOr ctx) {
    return binaryOp(bit_or_op_name, ctx);
  }
}
