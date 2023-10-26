package yaa.ast;

import yaa.parser.YaaToken;

import java.util.List;

public class Catch extends Stmt {
  public List<ObjectType> types;
  public YaaToken holder;
  public List<Stmt> stmts;

  public Catch(List<ObjectType> types, YaaToken holder, List<Stmt> stmts) {
    this.types = types;
    this.holder = holder;
    this.stmts = stmts;
  }

  @Override
  public String toString() {
    var bd = new StringBuilder();
    for (var type: types) {
      bd.append(type).append("\n");
    }
    bd.append(holder.content).append("{\n");
    for (var stmt : stmts) {
      bd.append(stmt).append("\n");
    }
    return bd.append("}").toString();
  }
}
