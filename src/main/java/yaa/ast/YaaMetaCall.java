package yaa.ast;

import yaa.parser.YaaToken;
import yaa.pojos.YaaInfo;
import yaa.pojos.FileState;

import java.util.Map;

public class YaaMetaCall extends Stmt {
  public YaaToken name;
  public Map<YaaToken, Stmt> arguments;

  public YaaMetaCall(){}

  @Override
  public YaaInfo visit(FileState fs) {
    return fs.$metaCall(this);
  }

  @Override
  public String toString() {
    return "@" + name.content + arguments;
  }
}
