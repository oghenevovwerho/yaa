package yaa.ast;

import yaa.parser.BasexToken;
import yaa.pojos.FileState;
import yaa.pojos.YaaInfo;

public class Basex extends Stmt {
  public BasexToken xToken;

  public Basex(BasexToken xToken) {
    this.xToken = xToken;
  }

  @Override
  public YaaInfo visit(FileState fs) {
    return fs.$basex(this);
  }

  @Override
  public String toString() {
    return xToken.content;
  }
}
