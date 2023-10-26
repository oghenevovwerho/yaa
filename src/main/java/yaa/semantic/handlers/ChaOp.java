package yaa.semantic.handlers;

import yaa.ast.Stmt;
import yaa.pojos.YaaError;
import yaa.pojos.GlobalData;
import yaa.pojos.YaaClz;
import yaa.pojos.YaaInfo;
import yaa.ast.Cha;

public class ChaOp {
  public static YaaInfo cha(Cha cha) {
    var char$result = GlobalData.char$clz;
    if (cha.content instanceof Stmt item) {
      if (item.visit(GlobalData.fs) instanceof YaaClz itemClz) {
        if (char$result.isSame$Obj(itemClz)) {
          return char$result;
        }
      }
      throw new YaaError(
           cha.placeOfUse(), cha.toString(),
          "The result of the expression in a char must evaluate to yaa.char.Char"
      );
    }
    return char$result;
  }
}
