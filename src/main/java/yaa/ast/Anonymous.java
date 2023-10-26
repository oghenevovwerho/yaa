package yaa.ast;

import yaa.parser.YaaToken;
import yaa.pojos.FileState;
import yaa.pojos.MtdIsWhat;
import yaa.pojos.YaaInfo;

import java.util.List;

public class Anonymous extends Stmt {
  public MtdIsWhat itIsWhat = MtdIsWhat.stmtMtd;
  public Stmt stmt;
  public List<YaaToken> parameters;

  @Override
  public YaaInfo visit(FileState fs) {
    return fs.$anonymous(this);
  }

  public Anonymous(List<YaaToken> parameters) {
    this.parameters = parameters;
  }

  @Override
  public String toString() {
    return "fx (){...}";
  }
}
