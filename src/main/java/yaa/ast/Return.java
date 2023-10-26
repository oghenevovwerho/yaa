package yaa.ast;

import yaa.pojos.FileState;
import yaa.pojos.YaaInfo;

public class Return extends Stmt {
  public Stmt e;

  public Return(Stmt e) {
    this.e = e;
  }

  @Override
  public YaaInfo visit(FileState fs) {
    return fs.$return(this);
  }

  @Override
  public String toString() {
    return "-> " + e;
  }
}
