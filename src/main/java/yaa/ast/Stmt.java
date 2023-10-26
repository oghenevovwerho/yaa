package yaa.ast;

import yaa.parser.YaaToken;
import yaa.pojos.FileState;
import yaa.pojos.YaaInfo;

import static java.util.Objects.hash;

public class Stmt {
  public YaaToken start;
  public YaaToken close;

  public YaaInfo visit(FileState fs) {
    return null;
  }

  public Stmt setClose(YaaToken close) {
    this.close = close;
    return this;
  }

  public Stmt setStart(YaaToken start) {
    this.start = start;
    return this;
  }

  public String placeOfUse() {
    return start.line + ": " + start.column;
  }

  @Override
  public boolean equals(Object otherObject) {
    if (otherObject instanceof Stmt otherStmt) {
      if (otherStmt.start.line != start.line) {
        return false;
      }
      if (otherStmt.close.line != close.line) {
        return false;
      }
      if (otherStmt.start.column != start.column) {
        return false;
      }
      return otherStmt.close.column == close.column;
    }
    return false;
  }

  @Override
  public int hashCode() {
    return hash(start.line, start.column, close.line, close.column);
  }
}
