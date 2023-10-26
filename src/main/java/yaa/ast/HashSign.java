package yaa.ast;

import yaa.parser.YaaToken;

public class HashSign {
  public YaaToken token;

  public HashSign(YaaToken token) {
    this.token = token;
  }

  @Override
  public String toString() {
    return "#";
  }
}
