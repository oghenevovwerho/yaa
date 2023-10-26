package yaa.semantic.passes.fs2;

import yaa.ast.IfCase;
import yaa.pojos.GlobalData;

public class F2IfCase {
  public static void f2IfCase(IfCase ifCase) {
    GlobalData.fs2.pushTable(ifCase);
    ifCase.stmt.visit(GlobalData.fs2);
    GlobalData.fs2.popTable();
  }
}
