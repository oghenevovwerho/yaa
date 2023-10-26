package yaa.ast;

import yaa.parser.YaaToken;
import yaa.pojos.FileState;
import yaa.pojos.YaaInfo;

public class Floated extends Stmt {
  public YaaToken token;

  public Floated(YaaToken token) {
    this.token = token;
  }

  @Override
  public String toString() {
    return token.content;
  }

  @Override
  public YaaInfo visit(FileState fs) {
    return fs.$float(this);
  }
}
