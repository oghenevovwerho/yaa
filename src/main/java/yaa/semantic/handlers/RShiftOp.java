package yaa.semantic.handlers;

import yaa.ast.RShift;
import yaa.pojos.GlobalData;
import yaa.pojos.YaaInfo;

import static yaa.semantic.handlers.OpUtils.binaryOp;

public class RShiftOp {
  public static YaaInfo rShift(RShift ctx) {
    return binaryOp(GlobalData.right_shift_op_name, ctx);
  }
}
