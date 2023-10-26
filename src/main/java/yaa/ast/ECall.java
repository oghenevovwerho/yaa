package yaa.ast;

import yaa.pojos.FileState;
import yaa.pojos.YaaInfo;

import java.util.ArrayList;
import java.util.List;

public class ECall extends Stmt {
  public Stmt e;
  public List<Stmt> values;
  public List<ObjectType> types;

  public ECall(Stmt e, List<Stmt> values) {
    this.e = e;
    this.values = values;
    this.types = new ArrayList<>(1);
  }

  @Override
  public YaaInfo visit(FileState fs) {
    return fs.$eCall(this);
  }

  public ECall(Stmt e, List<ObjectType> types, List<Stmt> values) {
    this.e = e;
    this.types = types;
    this.values = values;
  }

  @Override
  public String toString() {
    var sb = new StringBuilder();
    sb.append(e);
    if (types.size() == 1) {
      sb.append("<").append(types.get(0)).append(">");
    } else if (types.size() > 1) {
      sb.append("<").append(types.get(0));
      for (int i = 1; i < types.size(); i++) {
        sb.append(", ").append(types.get(i));
      }
      sb.append(">");
    }
    if (values.size() == 0) {
      sb.append("()");
      return sb.toString();
    } else if (values.size() == 1) {
      sb.append("(").append(values.get(0)).append(")");
      return sb.toString();
    } else {
      sb.append("(").append(values.get(0));
      for (int i = 1; i < values.size(); i++) {
        sb.append(", ").append(values.get(i));
      }
      return sb.append(")").toString();
    }
  }
}
