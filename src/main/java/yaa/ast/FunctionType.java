package yaa.ast;

import java.util.List;

public class FunctionType extends Stmt {
  public List<ObjectType> parameters;
  public ObjectType type;
}
