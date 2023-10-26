package yaa.semantic.passes.fs4;

import yaa.ast.IfStmt;
import yaa.pojos.GlobalData;

public class F4IfStmt {
  public static void ifStmt(IfStmt ifStmt) {
    GlobalData.fs4.pushTable(ifStmt);
    for (var ifCase : ifStmt.cases) {
      GlobalData.fs4.pushTable(ifCase);
      F4.runF4Stmt(ifCase.stmt);
      GlobalData.fs4.popTable();
    }
    F4.runF4Stmts(ifStmt.elseStmts);
    GlobalData.fs4.popTable();
  }
}
