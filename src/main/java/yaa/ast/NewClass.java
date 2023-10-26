package yaa.ast;

import yaa.parser.YaaToken;
import yaa.pojos.FileState;
import yaa.pojos.YaaInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class NewClass extends Stmt {
  public List<TypeParam> typeParams = new ArrayList<>(1);
  public List<RunBlock> runBlocks = new ArrayList<>(1);
  public Map<String, List<NewFun>> parentMtds = Map.of();
  public YaaToken name;
  public ObjectType parent;
  public List<NewClass> classes = new ArrayList<>(1);
  public boolean isTrait;
  public boolean itIsTopLevelClz = false;
  public List<NewEnum> enums = new ArrayList<>(1);
  public List<OverBlock> implementations = new ArrayList<>(1);
  public List<VDeclaration> vDeclarations = new ArrayList<>(2);
  public List<VDefinition> vDefinitions = new ArrayList<>(2);
  public List<NewFun> methods = new ArrayList<>(3);
  public List<YaaMetaCall> metaCalls = List.of();
  public List<NewFunctionalInterface> fInterfaces = new ArrayList<>(1);
  public Init init;
  public String parentCallDescriptor;

  @Override
  public YaaInfo visit(FileState fs) {
    return fs.$newClass(this);
  }

  public NewClass(YaaToken name) {
    this.name = name;
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
    ob.append("{...}");
    return ob.toString();
  }
}
