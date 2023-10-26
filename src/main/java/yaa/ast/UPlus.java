package yaa.ast;

import yaa.parser.YaaToken;
import yaa.pojos.FileState;
import yaa.pojos.YaaInfo;

public class UPlus extends Stmt {
  public YaaToken plus;
  public Stmt e;

  public UPlus(YaaToken plus, Stmt e) {
    this.plus = plus;
    this.e = e;
  }

  @Override
  public YaaInfo visit(FileState fs) {
    return fs.$uPlus(this);
  }

  @Override
  public String toString() {
    return "+" + "(" + e + ")";
  }
}
