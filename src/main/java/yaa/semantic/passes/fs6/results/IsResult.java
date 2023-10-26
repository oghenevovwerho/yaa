package yaa.semantic.passes.fs6.results;

import yaa.pojos.YaaInfo;

public class IsResult extends YaaResult {
  public YaaInfo type;
  public YaaInfo left;

  public IsResult(YaaInfo left, YaaInfo type) {
    this.type = type;
    this.left = left;
  }
}
