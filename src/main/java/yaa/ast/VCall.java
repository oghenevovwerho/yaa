package yaa.ast;

import yaa.parser.YaaToken;
import yaa.pojos.FileState;
import yaa.pojos.YaaInfo;

import java.util.ArrayList;
import java.util.List;

public class VCall extends Stmt {
  public YaaToken name;
  public List<Stmt> arguments;
  public List<ObjectType> types;

  @Override
  public YaaInfo visit(FileState fs) {
    return fs.$vCall(this);
  }

  public VCall(YaaToken name, List<Stmt> arguments) {
    this.name = name;
    this.arguments = arguments;
    this.types = new ArrayList<>(1);
  }

  public List<YaaMetaCall> metaCalls = new ArrayList<>(1);

  public VCall(YaaToken name) {
    this.name = name;
    this.arguments = new ArrayList<>(1);
    this.types = new ArrayList<>(1);
  }

  public VCall(YaaToken name, List<ObjectType> types, List<Stmt> arguments) {
    this.name = name;
    this.types = types;
    this.arguments = arguments;
  }

  @Override
  public String toString() {
    var sb = new StringBuilder();
    sb.append(name.content);
    if (types.size() == 1) {
      sb.append("<").append(types.get(0)).append(">");
    } else if (types.size() > 1) {
      sb.append("<").append(types.get(0));
      for (int i = 1; i < types.size(); i++) {
        sb.append(", ").append(types.get(i));
      }
      sb.append(">");
    }
    if (arguments.size() == 0) {
      sb.append("()");
      return sb.toString();
    } else if (arguments.size() == 1) {
      sb.append("(").append(arguments.get(0)).append(")");
      return sb.toString();
    } else {
      sb.append("(").append(arguments.get(0));
      for (int i = 1; i < arguments.size(); i++) {
        sb.append(", ").append(arguments.get(i));
      }
      return sb.append(")").toString();
    }
  }
}
