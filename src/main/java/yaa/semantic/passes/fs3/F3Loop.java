package yaa.semantic.passes.fs3;

import yaa.ast.Loop;
import yaa.pojos.GlobalData;

import static yaa.pojos.GlobalData.fs3;

public class F3Loop {
  public static void f3LoopStmt(Loop loop) {
    fs3.pushTable(loop);
    loop.stmt.visit(fs3);
    fs3.popTable();
  }
}
