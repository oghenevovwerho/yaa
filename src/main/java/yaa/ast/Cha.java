package yaa.ast;

import yaa.parser.YaaToken;
import yaa.pojos.FileState;
import yaa.pojos.YaaInfo;

public class Cha extends Stmt {
  public Object content;
  public boolean itIsInterpolated;
  public char char$content;

  public Cha(Object content) {
    this.content = content;
  }

  public Cha() {
  }

  @Override
  public YaaInfo visit(FileState fs) {
    return fs.$cha(this);
  }

  @Override
  public String toString() {
    if (content instanceof YaaToken token) {
      return token.content;
    }
    return "'" + content + "'";
  }
}
