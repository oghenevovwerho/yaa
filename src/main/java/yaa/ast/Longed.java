package yaa.ast;

import yaa.parser.YaaToken;
import yaa.pojos.FileState;
import yaa.pojos.YaaInfo;

public class Longed extends Stmt {
  public YaaToken token;

  public Longed(YaaToken token) {
    this.token = token;
  }

  @Override
  public String toString() {
    return token.content;
  }

  @Override
  public YaaInfo visit(FileState fs) {
    return fs.$long(this);
  }
}
