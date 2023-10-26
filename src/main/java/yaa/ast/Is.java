package yaa.ast;

import yaa.parser.BinaryStmt;
import yaa.parser.YaaToken;
import yaa.pojos.FileState;
import yaa.pojos.YaaInfo;

public class Is extends BinaryStmt {
  public Stmt e;
  public ObjectType type;

  public Is(Stmt e, YaaToken op, ObjectType type) {
    super(e, op, type);
    this.e = e;
    this.type = type;
  }

  @Override
  public YaaInfo visit(FileState fs) {
    return fs.$is(this);
  }
}
