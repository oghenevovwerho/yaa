package yaa.ast;

import java.util.List;

public class Tried extends Stmt {
  public List<Stmt> stmts;
  public List<VDefinition> resources;

  public Tried(List<Stmt> stmts) {
    this.stmts = stmts;
  }
}
