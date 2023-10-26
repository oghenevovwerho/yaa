package yaa.semantic.passes.fs6.results;

import yaa.pojos.YaaClz;
import yaa.pojos.YaaInit;

public class InitResult extends YaaResult {
  public final YaaInit init;
  public final YaaClz clz;

  public InitResult(YaaInit init, YaaClz clz) {
    this.init = init;
    this.clz = clz;
  }
}
