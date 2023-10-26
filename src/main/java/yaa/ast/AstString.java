package yaa.ast;

import yaa.pojos.FileState;
import yaa.pojos.YaaInfo;

import java.util.List;

public class AstString extends Stmt {
  public List<Object> content;
  public boolean itIsInterpolated;

  @Override
  public YaaInfo visit(FileState fs) {
    return fs.$string(this);
  }

  @Override
  public java.lang.String toString() {
    var sb = new StringBuilder();
    sb.append("\"");
    for (var item : content) {
      if (item instanceof Stmt) {
        sb.append("(").append(item).append(")");
      } else {
        sb.append(item);
      }
    }
    return sb.append("\"").toString();
  }
}
