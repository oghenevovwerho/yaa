package yaa.semantic.handlers;

import yaa.ast.Modulo;
import yaa.pojos.GlobalData;
import yaa.pojos.YaaInfo;

import static yaa.semantic.handlers.OpUtils.binaryOp;

public class ModOp {
  public static YaaInfo mod(Modulo ctx) {
    return binaryOp(GlobalData.modulo_op_name, ctx);
  }
}
