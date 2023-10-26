package yaa.ast;

import yaa.parser.YaaToken;
import yaa.pojos.FileState;
import yaa.pojos.YaaInfo;

public class Pointed extends Stmt {
  public YaaToken token;

  public Pointed(YaaToken token) {
    this.token = token;
  }

  @Override
  public YaaInfo visit(FileState fs) {
    return fs.$pointed(this);
  }

  @Override
  public String toString() {
    return token.content;
  }
}
