package yaa.ast;

import yaa.parser.YaaToken;
import yaa.pojos.FileState;
import yaa.pojos.YaaInfo;

public class Decimal extends Stmt {
  public YaaToken token;

  public Decimal(YaaToken token) {
    this.token = token;
  }

  @Override
  public YaaInfo visit(FileState fs) {
    return fs.$decimal(this);
  }

  @Override
  public String toString() {
    return token.content;
  }
}
