package yaa.ast;

import yaa.pojos.FileState;
import yaa.pojos.YaaInfo;

public class Break extends JumpAst{
  @Override
  public YaaInfo visit(FileState fs) {
    return fs.$break(this);
  }
}
