package yaa.ast;

import yaa.pojos.FileState;
import yaa.pojos.YaaInfo;

public class ThisCall extends Stmt {
  @Override
  public YaaInfo visit(FileState fs) {
    return fs.$thisCall(this);
  }
}
