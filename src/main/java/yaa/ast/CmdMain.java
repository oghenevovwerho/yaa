package yaa.ast;

import yaa.pojos.YaaInfo;
import yaa.pojos.FileState;

import java.util.List;

public class CmdMain extends Stmt {
  public List<Stmt> stmts;

  public CmdMain(List<Stmt> stmts) {
    this.stmts = stmts;
  }

  @Override
  public YaaInfo visit(FileState fs) {
    return fs.cmdMain(this);
  }
}
