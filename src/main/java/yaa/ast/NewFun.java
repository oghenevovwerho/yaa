package yaa.ast;

import yaa.parser.YaaToken;
import yaa.pojos.MtdIsWhat;
import yaa.pojos.YaaInfo;
import yaa.pojos.FileState;

import java.util.ArrayList;
import java.util.List;

public class NewFun extends Stmt {
  public MtdIsWhat itIsWhat = MtdIsWhat.stmtMtd;
  public YaaToken name;
  public List<Parameter> parameters;
  public Stmt stmt;
  public ObjectType type;
  public List<TypeParam> typeParams = new ArrayList<>(1);
  public List<YaaMetaCall> metaCalls;
  public int privacy;

  public NewFun() {
  }

  @Override
  public YaaInfo visit(FileState fs) {
    return fs.$function(this);
  }

  public NewFun(List<Parameter> ps, ObjectType type, Stmt ss) {
    this.parameters = ps;
    this.stmt = ss;
    this.type = type;
  }

  @Override
  public String toString() {
    var ob = new StringBuilder();
    ob.append(name.content);
    if (typeParams.size() == 1) {
      ob.append('<').append(typeParams.get(0)).append(">");
    } else if (typeParams.size() > 1) {
      ob.append("<");
      ob.append(typeParams.get(0));
      for (int i = 1; i < typeParams.size(); i++) {
        ob.append(", ").append(typeParams.get(i));
      }
      ob.append(">");
    }
    ob.append("(");
    if (parameters.size() == 1) {
      ob.append(parameters.get(0));
    } else if (parameters.size() > 1) {
      ob.append(parameters.get(0));
      for (int i = 1; i < parameters.size(); i++) {
        ob.append(", ").append(parameters.get(i));
      }
    }
    ob.append(")");
    if (type != null) {
      ob.append(type);
    }
    ob.append("{...}");
    return ob.toString();
  }
}
