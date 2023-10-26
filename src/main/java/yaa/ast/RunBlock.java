package yaa.ast;

import java.util.List;

public class RunBlock extends Stmt {
  public List<Stmt> stmts;

  public RunBlock(List<Stmt> stmts) {
    this.stmts = stmts;
  }
}
