package yaa.semantic.passes.fs4;

import yaa.ast.TryCatch;
import yaa.pojos.GlobalData;

import static yaa.semantic.passes.fs4.F4.runF4Stmts;

public class F4Try {
  public static void f4Try(TryCatch ctx) {
    GlobalData.fs4.pushTable(ctx);

    GlobalData.fs4.pushTable(ctx.tried);
    runF4Stmts(ctx.tried.stmts);
    GlobalData.fs4.popTable();

    for (var caught : ctx.caught) {
      GlobalData.fs4.pushTable(caught);
      runF4Stmts(caught.stmts);
      GlobalData.fs4.popTable();
    }

    GlobalData.fs4.pushTable(ctx.finals);
    runF4Stmts(ctx.finals.stmts);
    GlobalData.fs4.popTable();

    GlobalData.fs4.popTable();
  }
}
