package yaa.ast;

import yaa.parser.YaaToken;
import yaa.pojos.FileState;
import yaa.pojos.YaaInfo;

public class Name extends Stmt {
  public YaaToken token;

  public Name(YaaToken token) {
    this.token = token;
  }

  @Override
  public YaaInfo visit(FileState fs) {
    return fs.$name(this);
  }

  @Override
  public String toString() {
    return token.content;
  }
}
