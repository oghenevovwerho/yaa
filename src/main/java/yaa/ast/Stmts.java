package yaa.ast;

import java.util.ArrayList;
import java.util.List;

public class Stmts {
  public List<Stmt> stmts;
  public List<YaaMetaCall> metaCalls;

  public Stmts(){}

  public Stmts(List<Stmt> stmts, List<YaaMetaCall> metaCalls) {
    this.stmts = stmts;
    this.metaCalls = metaCalls;
  }
}
