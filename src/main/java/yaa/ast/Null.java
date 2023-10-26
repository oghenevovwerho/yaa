package yaa.ast;

import yaa.pojos.FileState;
import yaa.pojos.YaaInfo;

public class Null extends Stmt {
  @Override
  public YaaInfo visit(FileState fs) {
    return fs.$nullStmt(this);
  }
}
