package yaa.ast;

import yaa.parser.YaaToken;
import yaa.pojos.FileState;
import yaa.pojos.YaaInfo;

import java.util.List;

public class SuperMtd extends Stmt {
  public YaaToken name;
  public List<Stmt> arguments;

  @Override
  public YaaInfo visit(FileState fs) {
    return fs.$superMtd(this);
  }
}
