package yaa.semantic.handlers;

import yaa.ast.Decimal;
import yaa.pojos.GlobalData;
import yaa.pojos.YaaClz;

public class DecimalOp {
  public static YaaClz decimal(Decimal dec) {
    return GlobalData.int$clz;
  }
}
