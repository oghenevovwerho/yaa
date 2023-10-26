package yaa.ast;

import yaa.pojos.FileState;
import yaa.pojos.YaaInfo;

public class Throw extends Stmt {
  public Stmt e;

  public Throw(Stmt e) {
    this.e = e;
  }

  @Override
  public YaaInfo visit(FileState fs) {
    return fs.$throwStmt(this);
  }

  @Override
  public String toString() {
    return "-> " + e;
  }
}
