package yaa.ast;

import yaa.parser.YaaToken;
import yaa.pojos.FileState;
import yaa.pojos.YaaInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NewRecord extends Stmt {
  public Map<String, YaaToken> options = new HashMap<>(1);
  public List<TypeParam> typeParams = new ArrayList<>(1);
  public List<RunBlock> runBlocks = new ArrayList<>(1);
  public List<Init> inits;
  public YaaToken name;
  public List<NewClass> classes;
  public boolean itIsTopLevelClz = false;
  public List<NewEnum> enums;
  public List<OverBlock> parents = new ArrayList<>(1);
  public List<VDeclaration> vDeclarations;
  public List<VDefinition> vDefinitions;
  public List<NewFun> methods = new ArrayList<>(1);

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
    return fs.$newRecord(this);
  }

  public NewRecord(YaaToken name, List<Init> inits) {
    vDefinitions = new ArrayList<>(1);
    vDeclarations = new ArrayList<>(1);
    classes = new ArrayList<>(1);
    enums = new ArrayList<>(1);
    this.inits = inits;
    this.name = name;
  }

  @Override
  public String toString() {
    var ob = new StringBuilder();
    ob.append(name.content);
    for (var init : inits) {
      ob.append(init).append("\n");
    }
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
    ob.append("{...}");
    return ob.toString();
  }
}
