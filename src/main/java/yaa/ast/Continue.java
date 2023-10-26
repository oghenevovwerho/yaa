package yaa.ast;

import yaa.pojos.FileState;
import yaa.pojos.YaaInfo;

public class Continue extends JumpAst {
  @Override
  public YaaInfo visit(FileState fs) {
    return fs.$continue(this);
  }
}
