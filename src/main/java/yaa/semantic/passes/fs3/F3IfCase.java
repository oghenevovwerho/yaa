package yaa.semantic.passes.fs3;

import yaa.ast.IfCase;
import yaa.pojos.GlobalData;

public class F3IfCase {
  public static void f3IfCase(IfCase ifCase) {
    GlobalData.fs3.pushTable(ifCase);
    ifCase.stmt.visit(GlobalData.fs3);
    GlobalData.fs3.popTable();
  }
}
