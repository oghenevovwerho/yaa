package yaa.ast;

import yaa.pojos.YaaInfo;
import yaa.pojos.FileState;

public class Leave extends Stmt {
  @Override
  public YaaInfo visit(FileState fs) {
    return fs.$leave();
  }

  @Override
  public String toString() {
    return "comot;";
  }
}
