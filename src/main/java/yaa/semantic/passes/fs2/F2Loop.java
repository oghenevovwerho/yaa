package yaa.semantic.passes.fs2;

import yaa.ast.Loop;
import yaa.pojos.GlobalData;

import static yaa.pojos.GlobalData.fs2;

public class F2Loop {
  public static void f2LoopStmt(Loop loop) {
    fs2.pushTable(loop);
    loop.stmt.visit(fs2);
    fs2.popTable();
  }
}
