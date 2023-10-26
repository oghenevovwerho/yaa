package yaa.ast;

import yaa.parser.YaaToken;
import yaa.pojos.FileState;
import yaa.pojos.YaaInfo;

import java.util.List;

public class VMtd extends Stmt {
  public YaaToken eName;
  public YaaToken mName;
  public List<Stmt> arguments;
  public List<ObjectType> types;

  public VMtd(){}

  @Override
  public YaaInfo visit(FileState fs) {
    return fs.$vMtd(this);
  }

  public VMtd(YaaToken eName, YaaToken mName,
              List<ObjectType> types, List<Stmt> arguments) {
    this.eName = eName;
    this.types = types;
    this.mName = mName;
    this.arguments = arguments;
  }

  @Override
  public String toString() {
    var sb = new StringBuilder();
    sb.append(eName.content).append(".").append(mName.content);
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
