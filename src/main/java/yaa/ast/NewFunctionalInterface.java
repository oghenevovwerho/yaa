package yaa.ast;

import yaa.parser.YaaToken;
import yaa.pojos.ScopeKind;
import yaa.pojos.YaaInfo;
import yaa.pojos.FileState;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class NewFunctionalInterface extends Stmt {
  public YaaToken name;
  public List<TypeParam> typeParams = new ArrayList<>(0);
  public List<Parameter> parameters = new ArrayList<>(0);
  public ObjectType type;
  public ScopeKind scope;
  public HashMap<String, YaaToken> options = new HashMap<>(1);
  public StubReturn retStmt;

  public NewFunctionalInterface(YaaToken name) {
    this.name = name;
    retStmt = new StubReturn();
  }

  @Override
  public YaaInfo visit(FileState fs) {
    return fs.$fInterface(this);
  }
}
