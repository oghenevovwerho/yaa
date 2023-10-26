package yaa.semantic.handlers;

import yaa.ast.Shorted;
import yaa.pojos.GlobalData;
import yaa.pojos.YaaClz;

public class ShortOp {
  public static YaaClz shorted(Shorted st) {
    return GlobalData.short$clz;
  }
}
