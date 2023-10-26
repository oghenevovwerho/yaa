package yaa.ast;

import yaa.parser.YaaToken;
import yaa.pojos.FileState;
import yaa.pojos.YaaInfo;

public class SuperField extends Stmt{
  public YaaToken name;

  @Override
  public YaaInfo visit(FileState fs) {
    return fs.$superField(this);
  }
}
