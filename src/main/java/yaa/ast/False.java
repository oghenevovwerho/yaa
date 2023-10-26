package yaa.ast;

import yaa.pojos.FileState;
import yaa.pojos.YaaInfo;

public class False extends Stmt{
  @Override
  public YaaInfo visit(FileState fs) {
    return fs.$false(this);
  }
}
