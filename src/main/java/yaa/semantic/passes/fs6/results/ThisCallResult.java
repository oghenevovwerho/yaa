package yaa.semantic.passes.fs6.results;

import yaa.pojos.YaaClz;
import yaa.pojos.YaaInit;

public class ThisCallResult extends YaaResult {
  public YaaClz clz;
  public YaaInit init;

  public ThisCallResult(YaaInit init, YaaClz clz) {
    this.clz = clz;
    this.init = init;
  }
}
