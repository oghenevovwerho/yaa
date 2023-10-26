package yaa.semantic.passes.fs6.results;

import yaa.pojos.YaaInfo;

public class CastResult extends YaaResult{
  public YaaInfo from;
  public YaaInfo to;

  public CastResult(YaaInfo from, YaaInfo to) {
    this.from = from;
    this.to = to;
  }
}
