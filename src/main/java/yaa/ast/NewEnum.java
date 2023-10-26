package yaa.ast;

import yaa.parser.YaaToken;
import yaa.pojos.FileState;
import yaa.pojos.YaaInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NewEnum extends Stmt {
  public Map<String, YaaToken> options = new HashMap<>(1);
  public List<NewClass> classes;
  public List<NewEnum> enums;
  public List<RunBlock> runBlocks = new ArrayList<>(1);
  public List<NewFun> methods = new ArrayList<>(1);
  public NewFun toStringParentMtd;
  public YaaToken name;
  public List<Init> inits;
  public List<VDeclaration> vDeclarations;
  public List<VDefinition> vDefinitions;
  public boolean itIsTopLevelClz;
  public List<EnumOption> enumOptions;
  public List<OverBlock> implementations = new ArrayList<>(1);

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
    return fs.$newEnum(this);
  }

  public NewEnum(YaaToken name) {
    this.name = name;
  }

  @Override
  public String toString() {
    return name.content + "{...}";
  }
}

