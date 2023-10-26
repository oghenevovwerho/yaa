package yaa.ast;

import yaa.parser.YaaToken;
import yaa.pojos.YaaInfo;
import yaa.pojos.FileState;

public class MainFunction extends Stmt {
  public Stmt stmt;
  public String file$name;

  @Override
  public YaaInfo visit(FileState fs) {
    return fs.$main(this);
  }

  public MainFunction(String file$name, Stmt stmt) {
    this.stmt = stmt;
    this.file$name = file$name;
  }

  public YaaToken name;

  @Override
  public String toString() {
    return "(){...}";
  }
}
