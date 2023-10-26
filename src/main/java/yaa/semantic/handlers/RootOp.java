package yaa.semantic.handlers;

import yaa.ast.RootTo;
import yaa.pojos.GlobalData;
import yaa.pojos.YaaInfo;

import static yaa.semantic.handlers.OpUtils.binaryOp;

public class RootOp {
  public static YaaInfo root(RootTo ctx) {
    return binaryOp(GlobalData.root_op_name, ctx);
  }
}