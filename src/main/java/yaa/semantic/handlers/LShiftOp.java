package yaa.semantic.handlers;

import yaa.ast.LShift;
import yaa.pojos.GlobalData;
import yaa.pojos.YaaInfo;

import static yaa.semantic.handlers.OpUtils.binaryOp;

public class LShiftOp {
  public static YaaInfo lShift(LShift ctx) {
    return binaryOp(GlobalData.left_shift_op_name, ctx);
  }
}
