package yaa.ast;

import yaa.parser.YaaToken;
import yaa.pojos.FileState;
import yaa.pojos.YaaInfo;

public class Shorted extends Stmt {
  public YaaToken token;

  public Shorted(YaaToken token) {
    this.token = token;
  }

  @Override
  public YaaInfo visit(FileState fs) {
    return fs.$short(this);
  }

  @Override
  public String toString() {
    return token.content;
  }
}
