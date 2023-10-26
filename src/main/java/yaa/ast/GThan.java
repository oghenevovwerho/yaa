package yaa.ast;

import yaa.parser.BinaryStmt;
import yaa.parser.YaaToken;
import yaa.pojos.FileState;
import yaa.pojos.YaaInfo;

public class GThan extends BinaryStmt {
  public GThan(Stmt e1, YaaToken op, Stmt e2) {
    super(e1, op, e2);
  }

  @Override
  public YaaInfo visit(FileState fs) {
    return fs.$gThan(this);
  }
}
