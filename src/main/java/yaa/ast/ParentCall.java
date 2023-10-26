package yaa.ast;

import java.util.List;

public class ParentCall extends Stmt {
  public List<Stmt> arguments;

  public ParentCall(List<Stmt> parentArgs) {
    this.arguments = parentArgs;
  }
}