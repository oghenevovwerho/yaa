package yaa.semantic.passes.fs3;

import yaa.ast.Init;
import yaa.pojos.GlobalData;
import yaa.pojos.YaaField;
import yaa.pojos.YaaFun;
import yaa.pojos.YaaInit;

import static java.lang.System.out;
import static yaa.pojos.GlobalData.fs1;
import static yaa.pojos.GlobalData.fs3;

public class F3Init {
  public static void f3Init(Init init) {
    fs3.pushTable(init);
    var yaaInit = (YaaInit) fs3.getSymbol(init.placeOfUse());
    for (int i = 0; i < init.parameters.size(); i++) {
      var param = init.parameters.get(i);
      var paramName = param.name.content;
      var paramInfo = param.type.visit(fs3);
      yaaInit.parameters.set(i, paramInfo);
      var pField = (YaaField) fs3.getSymbol(paramName);
      pField.data = paramInfo;
    }
    init.stmt.visit(fs3);
    fs3.popTable();
  }
}