package yaa.ast;

import yaa.parser.YaaToken;
import yaa.pojos.FileState;
import yaa.pojos.YaaInfo;

import java.util.List;

public class ThisMtd extends Stmt {
  public List<Stmt> arguments;
  public YaaToken name;

  @Override
  public YaaInfo visit(FileState fs) {
    return fs.$thisMtd(this);
  }
}
