package yaa.semantic.handlers;

import yaa.ast.Floated;
import yaa.pojos.GlobalData;
import yaa.pojos.YaaClz;

public class FloatOp {
  public static YaaClz floated(Floated ft) {
    return GlobalData.float$clz;
  }
}
