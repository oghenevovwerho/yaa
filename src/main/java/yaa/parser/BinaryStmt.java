package yaa.parser;

import yaa.ast.Stmt;

public class BinaryStmt extends Stmt {
  public Stmt e1;
  public yaa.parser.YaaToken op;
  public Stmt e2;

  public BinaryStmt(Stmt e1, yaa.parser.YaaToken op, Stmt e2) {
    this.e1 = e1;
    this.op = op;
    this.e2 = e2;
    start = e1.start;
  }

  @Override
  public String toString() {
    return e1 + " " + op.content + " " + e2;
  }
}
