package yaa.semantic.passes.fs3;

import yaa.ast.NewFun;
import yaa.pojos.YaaField;
import yaa.pojos.YaaFun;

import static yaa.pojos.GlobalData.fs3;

public class F3ParentMtd {
  public static void f3ParentMtd(NewFun newFun) {
    fs3.pushTable(newFun);
    var newMtd = (YaaFun) fs3.getSymbol(newFun.placeOfUse());
    for (int i = 0; i < newMtd.parameters.size(); i++) {
      var param = newFun.parameters.get(i);
      var paramType = param.type.visit(fs3);
      newMtd.parameters.set(i, paramType);
      var paramField = (YaaField) fs3.getSymbol(param.name.content);
      paramField.data = paramType;
    }

    if (newFun.type != null) {
      newMtd.type = newFun.type.visit(fs3);
    }

    newFun.stmt.visit(fs3);

    fs3.popTable();
  }
}
