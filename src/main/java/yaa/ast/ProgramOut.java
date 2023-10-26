package yaa.ast;

import yaa.pojos.FileState;
import yaa.pojos.YaaInfo;

public class ProgramOut extends Stmt {
  @Override
  public YaaInfo visit(FileState fs) {
    return fs.$programOut(this);
  }
}
