package yaa.semantic.passes.fs1;

import yaa.ast.IfStmt;
import yaa.parser.YaaToken;
import yaa.pojos.YaaError;
import yaa.pojos.GlobalData;

import java.util.HashMap;

public class F1IfStmt {
  public static void ifStmt(IfStmt ifStmt) {
    GlobalData.fs1.newTable();
    var cases = ifStmt.cases;
    var case$size = cases.size();
    var defined$holders = new HashMap<String, YaaToken>(case$size);
    for (var ifCase : ifStmt.cases) {
      GlobalData.fs1.newTable();
      var holder = ifCase.caseLabel;
      if (holder != null) {
        var v$token = defined$holders.get(holder.content);
        if (v$token != null) {
          throw new YaaError(
              v$token.placeOfUse(), v$token.content + " at " + v$token.placeOfUse(),
              "The case variable above and the one below are the same",
              "All the case variables of a conditional must be distinct",
              v$token.content + " at " + holder.placeOfUse()
          );
        }
        defined$holders.put(holder.content, holder);
      }
      ifCase.stmt.visit(GlobalData.fs1);
      GlobalData.fs1.storeTable(ifCase);
      GlobalData.fs1.popTable();
    }
    for (var elseStmt : ifStmt.elseStmts) {
      elseStmt.visit(GlobalData.fs1);
    }
    ifStmt.catchHolders = defined$holders;
    GlobalData.fs1.storeTable(ifStmt);
    GlobalData.fs1.popTable();
  }
}
