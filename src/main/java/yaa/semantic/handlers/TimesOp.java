package yaa.semantic.handlers;

import yaa.ast.Times;
import yaa.pojos.GlobalData;
import yaa.pojos.YaaInfo;

import static yaa.semantic.handlers.OpUtils.binaryOp;

public class TimesOp {
  public static YaaInfo times(Times ctx) {
    return binaryOp(GlobalData.times_op_name, ctx);
  }
}