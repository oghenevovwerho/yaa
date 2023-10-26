package yaa.semantic.handlers;

import yaa.ast.Power;
import yaa.pojos.GlobalData;
import yaa.pojos.YaaInfo;

import static yaa.semantic.handlers.OpUtils.binaryOp;

public class PowerOp {
  public static YaaInfo power(Power ctx) {
    return binaryOp(GlobalData.power_op_name, ctx);
  }
}
