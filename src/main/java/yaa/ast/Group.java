package yaa.ast;

import yaa.pojos.FileState;
import yaa.pojos.YaaInfo;

public class Group extends Stmt {
  public Stmt e;

  public Group(Stmt e) {
    this.e = e;
  }

  @Override
  public String toString() {
    return e.toString();
  }

  @Override
  public YaaInfo visit(FileState fs) {
    return fs.$group(this);
  }
}
