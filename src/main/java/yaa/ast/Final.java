package yaa.ast;

import java.util.List;

public class Final extends Stmt {
  public List<Stmt> stmts;

  public Final(List<Stmt> stmts) {
    this.stmts = stmts;
  }
}
