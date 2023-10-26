package yaa.ast;

import yaa.pojos.YaaInfo;
import yaa.pojos.FileState;

import java.util.List;

public class InnerBlock extends Stmt {
  public List<Stmt> stmts;

  @Override
  public YaaInfo visit(FileState fs) {
    return fs.$innerBlock(this);
  }
}