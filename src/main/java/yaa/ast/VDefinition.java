package yaa.ast;

import yaa.parser.YaaToken;
import yaa.pojos.FieldIsWhat;
import yaa.pojos.YaaInfo;
import yaa.pojos.FileState;

import java.util.HashMap;

public class VDefinition extends Stmt {
  public YaaToken name;
  public HashMap<String, YaaToken> options = new HashMap<>(1);
  public ObjectType type;
  public Stmt value;
  public FieldIsWhat itIsWhat;

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

  @Override
  public YaaInfo visit(FileState fs) {
    return fs.$vDef(this);
  }

  public VDefinition(YaaToken name, Stmt value) {
    this.value = value;
    this.name = name;
  }

  public VDefinition(YaaToken name) {
    this.name = name;
  }

  @Override
  public String toString() {
    var ob = new StringBuilder();
    ob.append(name.content);
    for (var option : options.values()) {
      ob.append(" ").append(option.content);
    }
    if (type != null) {
      return ob + ": " + type + " = " + value;
    } else {
      return ob + " := " + value;
    }
  }
}
