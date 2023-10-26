package yaa.parser;

import yaa.ast.Stmt;

import java.util.List;

public class ParseResult {
  public final List<Stmt> stmts;
  public final String path;

  public ParseResult(String path, List<Stmt> stmts) {
    this.stmts = stmts;
    this.path = path;
  }
}
