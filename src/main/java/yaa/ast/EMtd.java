package yaa.ast;

import yaa.parser.YaaToken;
import yaa.pojos.FileState;
import yaa.pojos.YaaInfo;

import java.util.List;

public class EMtd extends Stmt {
  public Stmt e;
  public YaaToken mName;
  public List<Stmt> arguments;
  public List<ObjectType> types;

  public EMtd(){}

  @Override
  public YaaInfo visit(FileState fs) {
    return fs.$eMtd(this);
  }

  public EMtd(Stmt e, YaaToken mName, List<ObjectType> types, List<Stmt> arguments) {
    this.e = e;
    this.mName = mName;
    this.types = types;
    this.arguments = arguments;
  }

  @Override
  public String toString() {
    var sb = new StringBuilder();
    sb.append(e).append(".").append(mName.content);
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
