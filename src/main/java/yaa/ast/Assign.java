package yaa.ast;

import yaa.pojos.FileState;
import yaa.pojos.YaaInfo;

public class Assign extends Stmt {
  public Stmt e1;
  public Stmt e2;

  @Override
  public YaaInfo visit(FileState fs) {
    return fs.$assign(this);
  }

  public Assign(Stmt e1, Stmt e2) {
    this.e1 = e1;
    this.e2 = e2;
  }

  @Override
  public String toString() {
    return e1 + " = " + e2;
  }
}
