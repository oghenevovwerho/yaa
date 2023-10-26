package yaa.ast;

import yaa.parser.YaaToken;

import java.util.List;

public class EnumOption {
  public YaaToken name;
  public List<Stmt> arguments;
}
