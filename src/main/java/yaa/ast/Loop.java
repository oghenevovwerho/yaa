package yaa.ast;

import yaa.parser.YaaToken;
import yaa.pojos.FileState;
import yaa.pojos.YaaInfo;

public class Loop extends Stmt {
  public Stmt condition;
  public Stmt init$value;
  public YaaToken value$name;
  public Stmt stmt;
  public Assign assign;

  @Override
  public YaaInfo visit(FileState fs) {
    return fs.$loop(this);
  }
}
