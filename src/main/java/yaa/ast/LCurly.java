package yaa.ast;

import yaa.parser.YaaToken;

public class LCurly {
  public YaaToken token;

  public LCurly(YaaToken token) {
    this.token = token;
  }

  @Override
  public String toString() {
    return "{";
  }
}
