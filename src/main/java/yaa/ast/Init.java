package yaa.ast;

import yaa.pojos.FileState;
import yaa.pojos.YaaInfo;

import java.util.List;

public class Init extends Stmt {
  public List<Parameter> parameters;
  public Stmt stmt;
  public List<YaaMetaCall> metaCalls;
  public ParentCall parentCall;

  @Override
  public YaaInfo visit(FileState fs) {
    return fs.$init(this);
  }

  @Override
  public String toString() {
    var ob = new StringBuilder();
    ob.append("(");
    if (parameters.size() == 1) {
      ob.append(parameters.get(0));
    } else if (parameters.size() > 1) {
      ob.append(parameters.get(0));
      for (int i = 1; i < parameters.size(); i++) {
        ob.append(" ").append(parameters.get(i));
      }
    }
    ob.append(")");
    ob.append(stmt).append("\n");
    return ob.toString();
  }
}
