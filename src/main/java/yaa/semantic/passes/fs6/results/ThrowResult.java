package yaa.semantic.passes.fs6.results;

import yaa.pojos.YaaClz;

public class ThrowResult extends YaaResult {
  public YaaClz clz;
  public boolean extendsThrowable;

  public ThrowResult(YaaClz clz, boolean extendsThrowable) {
    this.clz = clz;
    this.extendsThrowable = extendsThrowable;
  }
}
