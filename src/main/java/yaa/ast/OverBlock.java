package yaa.ast;

import yaa.pojos.FileState;
import yaa.pojos.YaaInfo;

import java.util.List;
import java.util.Map;

public class OverBlock extends Stmt {
  public ObjectType type;
  public Map<String, List<NewFun>> methods;
  public boolean isClass;

  public OverBlock(ObjectType type, Map<String, List<NewFun>> methods) {
    this.methods = methods;
    this.type = type;
  }

  @Override
  public YaaInfo visit(FileState fs) {
    return fs.$blockInClz();
  }

  @Override
  public String toString() {
    return type + "{...}";
  }
}
