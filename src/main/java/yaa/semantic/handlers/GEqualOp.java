package yaa.semantic.handlers;

import yaa.ast.GEqual;
import yaa.pojos.GlobalData;
import yaa.pojos.YaaInfo;

import static yaa.semantic.handlers.OpUtils.binaryOp;

public class GEqualOp {
  public static YaaInfo gEqual(GEqual ctx) {
    return binaryOp(GlobalData.greater_equal_op_name, ctx);
  }
}
