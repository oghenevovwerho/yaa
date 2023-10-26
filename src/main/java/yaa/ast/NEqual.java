package yaa.ast;

import yaa.parser.YaaToken;
import yaa.parser.BinaryStmt;
import yaa.pojos.FileState;
import yaa.pojos.YaaInfo;

public class NEqual extends BinaryStmt {
  public NEqual(Stmt e1, YaaToken op, Stmt e2) {
    super(e1, op, e2);
  }

  @Override
  public YaaInfo visit(FileState fs) {
    return fs.$nEqual(this);
  }
}
