package yaa.ast;

import yaa.parser.BinaryStmt;
import yaa.parser.YaaToken;
import yaa.pojos.FileState;
import yaa.pojos.YaaInfo;

public class Plus extends BinaryStmt {
  public Plus(Stmt e1, YaaToken op, Stmt e2) {
    super(e1, op, e2);
  }

  @Override
  public YaaInfo visit(FileState fs) {
    return fs.$plus(this);
  }

  @Override
  public String toString() {
    return "(" + e1 + " " + op.content + " " + e2 + ")";
  }
}
