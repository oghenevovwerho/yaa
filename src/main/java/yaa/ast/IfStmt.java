package yaa.ast;

import yaa.parser.YaaToken;
import yaa.pojos.FileState;
import yaa.pojos.YaaInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class IfStmt extends Stmt {
  public Stmt e;
  public List<IfCase> cases;
  public List<Stmt> elseStmts;
  public boolean allCasesAreIntegral;
  public boolean hasPrimitiveTarget;
  public boolean canBeTableSwitched;
  public Map<String, YaaToken> catchHolders;
  public List<String> enumOptions;
  public boolean isEnumSwitch;

  @Override
  public YaaInfo visit(FileState fs) {
    return fs.$ifStmt(this);
  }

  public IfStmt(Stmt e, List<IfCase> stmts) {
    this.e = e;
    this.cases = stmts;
    this.elseStmts = new ArrayList<>(1);
  }

  public IfStmt(Stmt e, List<IfCase> stmts, List<Stmt> elseStmts) {
    this.e = e;
    this.elseStmts = elseStmts;
    this.cases = stmts;
  }

  @Override
  public String toString() {
    var ob = new StringBuilder();
    ob.append(e);
    ob.append("{\n");
    for (var stmt : cases) {
      ob.append(stmt).append("\n");
    }
    if (elseStmts.size() > 0) {
      ob.append("----\n");
      for (var stmt : elseStmts) {
        ob.append(stmt).append("\n");
      }
    }
    ob.append("}");
    return ob.toString();
  }
}
