package yaa.semantic.passes.fs2;

import yaa.ast.TryCatch;

import static yaa.pojos.GlobalData.*;

public class F2Try {
  public static void f2Try(TryCatch tCatch) {
    fs2.pushTable(tCatch);
    fs2.pushTable(tCatch.tried);
    for (var stmt : tCatch.tried.stmts) {
      stmt.visit(fs2);
    }
    fs2.popTable();

    for (var caught : tCatch.caught) {
      fs2.pushTable(caught);
      for (var stmt : caught.stmts) {
        stmt.visit(fs2);
      }
      fs2.popTable();
    }

    fs2.pushTable(tCatch.finals);
    for (var stmt : tCatch.finals.stmts) {
      stmt.visit(fs2);
    }
    fs2.popTable();

    fs2.popTable();
  }
}
