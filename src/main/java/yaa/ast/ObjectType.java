package yaa.ast;

import yaa.parser.YaaToken;
import yaa.pojos.FileState;
import yaa.pojos.YaaInfo;

import java.util.ArrayList;
import java.util.List;

public class ObjectType extends Stmt {
  public YaaToken typeName;
  public List<ObjectType> arguments;
  public List<YaaMetaCall> metaCalls = new ArrayList<>(1);
  public boolean hasInternalMeta;

  public ObjectType(YaaToken typeName) {
    this.typeName = typeName;
    this.arguments = new ArrayList<>(1);
  }

  public ObjectType(){}

  @Override
  public YaaInfo visit(FileState fs) {
    return fs.$objectType(this);
  }

  public ObjectType(YaaToken typeName, List<ObjectType> arguments) {
    this.typeName = typeName;
    this.arguments = arguments;
  }

  @Override
  public String toString() {
    var bd = new StringBuilder();
    bd.append(typeName.content);
    if (arguments.size() == 1) {
      bd.append("<").append(arguments.get(0)).append(">");
    } else if (arguments.size() > 1) {
      bd.append("<").append(arguments.get(0));
      for (int i = 1; i < arguments.size(); i++) {
        bd.append(", ").append(arguments.get(i));
      }
      bd.append(">");
    }
    for (YaaMetaCall metaCall : metaCalls) {
      bd.append(metaCall);
    }
    return bd.toString();
  }
}
