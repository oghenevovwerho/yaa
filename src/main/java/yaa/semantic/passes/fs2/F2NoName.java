package yaa.semantic.passes.fs2;

import yaa.ast.Anonymous;
import yaa.pojos.GlobalData;

import static yaa.pojos.GlobalData.fs2;
import static yaa.semantic.passes.fs6.F6Utils.generateRandomName;

public class F2NoName {
  public static void f2NoName(Anonymous anonymous) {
    fs2.pushTable(anonymous);
    anonymous.stmt.visit(fs2);
    fs2.popTable();
  }
}
