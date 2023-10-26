package yaa.ast;

import yaa.parser.YaaToken;
import yaa.pojos.FileState;
import yaa.pojos.YaaInfo;

public class Tab extends Stmt {
  public YaaToken token;

  public Tab(YaaToken token) {
    this.token = token;
  }

  @Override
  public YaaInfo visit(FileState fs) {
    return fs.$tab();
  }

  @Override
  public String toString() {
    return "\\t";
  }
}
