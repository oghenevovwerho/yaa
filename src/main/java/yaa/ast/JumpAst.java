package yaa.ast;

import yaa.parser.YaaToken;

public class JumpAst extends Stmt {
  //The parent of break and continue;
  public YaaToken name;
  public boolean itIsLastInCase;
}
