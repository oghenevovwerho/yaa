package yaa.ast;

import yaa.parser.YaaToken;
import yaa.pojos.FileState;
import yaa.pojos.YaaInfo;

public class UNot extends Stmt {
  public YaaToken not;
  public Stmt e;

  public UNot(YaaToken not, Stmt e) {
    this.not = not;
    this.e = e;
  }

  @Override
  public YaaInfo visit(FileState fs) {
    return fs.$uNot(this);
  }

  @Override
  public String toString() {
    return "!" + "(" + e + ")";
  }
}
