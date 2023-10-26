package yaa.ast;

import yaa.pojos.FileState;
import yaa.pojos.YaaInfo;

public class Ternary extends Stmt {
  public Stmt cond;
  public Stmt l;
  public Stmt r;

  public Ternary(Stmt cond, Stmt l, Stmt r) {
    this.cond = cond;
    this.l = l;
    this.r = r;
  }

  @Override
  public YaaInfo visit(FileState fs) {
    return fs.$ternary(this);
  }
}
