package yaa.ast;

import yaa.parser.YaaToken;

public class BSlash{
  public YaaToken token;

  public BSlash(YaaToken token) {
    this.token = token;
  }

  @Override
  public String toString() {
    return "\\";
  }
}
