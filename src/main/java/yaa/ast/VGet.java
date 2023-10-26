package yaa.ast;

import yaa.parser.YaaToken;
import yaa.pojos.FileState;
import yaa.pojos.YaaInfo;

public class VGet extends Stmt {
  public YaaToken n1;
  public YaaToken n2;

  public VGet(YaaToken n1, YaaToken n2) {
    this.n1 = n1;
    this.n2 = n2;
  }

  @Override
  public YaaInfo visit(FileState fs) {
    return fs.$vGet(this);
  }

  @Override
  public String toString() {
    return n1.content + "." + n2.content;
  }
}
