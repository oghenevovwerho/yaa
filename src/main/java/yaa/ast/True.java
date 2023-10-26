package yaa.ast;

import yaa.pojos.FileState;
import yaa.pojos.YaaInfo;

public class True extends Stmt {
  @Override
  public YaaInfo visit(FileState fs) {
    return fs.$true(this);
  }
}
