package yaa.semantic.passes.fs6.results;

import yaa.pojos.YaaClz;
import yaa.pojos.YaaFun;

public class CallResult extends YaaResult {
  public final YaaFun mtd;
  public YaaClz clz;
  public boolean isPredefined;
  public boolean arrayTypesAreDivergent;

  public CallResult(YaaFun mtd) {
    this.mtd = mtd;
  }

  public CallResult(YaaFun mtd, boolean isPredefined) {
    this.mtd = mtd;
    this.isPredefined = isPredefined;
  }

  public CallResult(YaaFun mtd, YaaClz clz) {
    this.mtd = mtd;
    this.clz = clz;
  }
}
