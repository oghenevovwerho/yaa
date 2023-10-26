package yaa.ast;

import yaa.parser.YaaToken;
import yaa.pojos.FileState;
import yaa.pojos.YaaInfo;

public class Byted extends Stmt {
  public YaaToken token;

  public Byted(YaaToken token) {
    this.token = token;
  }

  @Override
  public YaaInfo visit(FileState fs) {
    return fs.$byte(this);
  }

  @Override
  public String toString() {
    return token.content;
  }
}
