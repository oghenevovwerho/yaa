package yaa.semantic.handlers;

import yaa.ast.URShift;
import yaa.pojos.GlobalData;
import yaa.pojos.YaaInfo;

public class URShiftOp {
  public static YaaInfo uRShift(URShift ctx) {
    return OpUtils.binaryOp(GlobalData.ur_shift_op_name, ctx);
  }
}
