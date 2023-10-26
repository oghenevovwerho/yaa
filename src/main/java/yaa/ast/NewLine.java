package yaa.ast;

import yaa.parser.YaaToken;

public class NewLine {
  public YaaToken token;

  public NewLine(YaaToken token) {
    this.token = token;
  }

  @Override
  public String toString() {
    return "\\n";
  }
}
