package yaa.ast;

import yaa.parser.YaaToken;
import yaa.pojos.FileState;
import yaa.pojos.YaaInfo;

public class UMinus extends Stmt {
  public YaaToken minus;
  public Stmt e;

  public UMinus(YaaToken minus, Stmt e) {
    this.minus = minus;
    this.e = e;
  }

  @Override
  public YaaInfo visit(FileState fs) {
    return fs.$uMinus(this);
  }

  @Override
  public String toString() {
    return "-" + "(" + e + ")";
  }
}
