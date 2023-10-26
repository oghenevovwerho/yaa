package yaa.semantic.passes.fs3;

import yaa.ast.TryCatch;

import static yaa.pojos.GlobalData.*;

public class F3Try {
  public static void f3Try(TryCatch tCatch) {
    fs3.pushTable(tCatch);
    fs3.pushTable(tCatch.tried);
    for (var stmt : tCatch.tried.stmts) {
      stmt.visit(fs3);
    }
    fs3.popTable();

    for (var caught : tCatch.caught) {
      fs3.pushTable(caught);
      for (var stmt : caught.stmts) {
        stmt.visit(fs3);
      }
      fs3.popTable();
    }

    fs3.pushTable(tCatch.finals);
    for (var stmt : tCatch.finals.stmts) {
      stmt.visit(fs3);
    }
    fs3.popTable();

    fs3.popTable();
  }
}
