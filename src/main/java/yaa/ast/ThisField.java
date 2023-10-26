package yaa.ast;

import yaa.parser.YaaToken;
import yaa.pojos.FileState;
import yaa.pojos.YaaInfo;

public class ThisField extends Stmt {
  public YaaToken name;

  @Override
  public YaaInfo visit(FileState fs) {
    return fs.$thisField(this);
  }
}
