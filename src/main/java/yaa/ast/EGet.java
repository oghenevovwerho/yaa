package yaa.ast;

import yaa.parser.YaaToken;
import yaa.pojos.FileState;
import yaa.pojos.YaaInfo;

public class EGet extends Stmt {
  public Stmt e;
  public YaaToken name;

  public EGet(Stmt e, YaaToken name) {
    this.e = e;
    this.name = name;
  }

  @Override
  public String toString() {
    return "(" + e + "." + name.content + ")";
  }

  @Override
  public YaaInfo visit(FileState fs) {
    return fs.$eGet(this);
  }
}
