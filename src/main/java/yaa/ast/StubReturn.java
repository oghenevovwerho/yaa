package yaa.ast;

import yaa.pojos.YaaInfo;
import yaa.pojos.FileState;

public class StubReturn extends Stmt {
  @Override
  public YaaInfo visit(FileState fs) {
    return fs.$stubReturn(this);
  }
}
