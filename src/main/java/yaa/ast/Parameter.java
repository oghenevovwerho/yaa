package yaa.ast;

import yaa.parser.YaaToken;

import java.util.ArrayList;
import java.util.List;

public class Parameter extends Stmt {
  public YaaToken name;
  public ObjectType type;
  public List<YaaToken> options;

  public Parameter(){}

  @Override
  public String toString() {
    var ob = new StringBuilder();
    for (var option : options) {
      ob.append(" ").append(option.content);
    }
    return name.content + " " + type + ob;
  }
}
