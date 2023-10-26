package yaa.semantic.handlers;

import yaa.ast.Longed;
import yaa.pojos.GlobalData;
import yaa.pojos.YaaClz;

public class LongOp {
  public static YaaClz longed(Longed ld) {
    return GlobalData.long$clz;
  }
}
