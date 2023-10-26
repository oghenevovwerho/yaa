package yaa.ast;

import yaa.parser.YaaToken;

public class SQuote {
  public YaaToken token;

  public SQuote(YaaToken token) {
    this.token = token;
  }

  @Override
  public String toString() {
    return "\\'";
  }
}
