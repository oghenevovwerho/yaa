package yaa.ast;

import yaa.parser.YaaToken;

public class BTick {
  public YaaToken token;

  public BTick(YaaToken token) {
    this.token = token;
  }

  @Override
  public String toString() {
    return "\\'";
  }
}
