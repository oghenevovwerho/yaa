package yaa.semantic.handlers;

import yaa.ast.Group;
import yaa.pojos.GlobalData;
import yaa.pojos.YaaInfo;

public class GroupOp {
  public static YaaInfo groupOp(Group group){
    return group.e.visit(GlobalData.fs);
  }
}
