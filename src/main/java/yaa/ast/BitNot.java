package yaa.ast;

import yaa.parser.YaaToken;
import yaa.pojos.FileState;
import yaa.pojos.YaaInfo;

public class BitNot extends Stmt {
  public YaaToken not;
  public Stmt e;

  public BitNot(YaaToken not, Stmt e) {
    this.not = not;
    this.e = e;
  }

  @Override
  public YaaInfo visit(FileState fs) {
    return fs.$bitNot(this);
  }

  @Override
  public String toString() {
    return "~" + "(" + e + ")";
  }
}
