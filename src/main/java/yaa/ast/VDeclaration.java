package yaa.ast;

import yaa.parser.YaaToken;
import yaa.pojos.FileState;
import yaa.pojos.FieldIsWhat;
import yaa.pojos.YaaInfo;

import java.util.HashMap;

public class VDeclaration extends Stmt {
  public HashMap<String, YaaToken> options = new HashMap<>(1);
  public ObjectType type;
  public FieldIsWhat itIsWhat;
  public YaaToken name;

  public VDeclaration(YaaToken name, ObjectType type) {
    this.type = type;
    this.name = name;
  }

  @Override
  public YaaInfo visit(FileState fs) {
    return fs.$vDec(this);
  }

  @Override
  public String toString() {
    var ob = new StringBuilder();
    ob.append(name.content);
    for (var option : options.values()) {
      ob.append(" ").append(option.content);
    }
    return ob + ": " + type;
  }

  public int privacy() {
    if (options.containsKey("public")) {
      return 0;
    }
    if (options.containsKey("protected")) {
      return 1;
    }
    if (options.containsKey("private")) {
      return 2;
    }
    return 0;
  }
}
