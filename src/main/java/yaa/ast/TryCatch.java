package yaa.ast;

import yaa.pojos.FileState;
import yaa.pojos.YaaInfo;

import java.util.ArrayList;
import java.util.List;

public class TryCatch extends Stmt {
  public Tried tried;
  public Final finals = new Final(new ArrayList<>(0));
  public List<Catch> caught;

  @Override
  public YaaInfo visit(FileState fs) {
    return fs.$tryCatch(this);
  }

  public void setCaught(List<Catch> catches) {
    this.caught = catches;
  }

  public void setTried(Tried tried) {
    this.tried = tried;
  }

  public void setFinals(Final finals) {
    this.finals = finals;
  }

  @Override
  public String toString() {
    var bd = new StringBuilder();
    bd.append("{\n");
    for (var stmt : tried.stmts) {
      bd.append(stmt).append("\n");
    }
    if (finals.stmts.size() > 0) {
      bd.append("----\n");
      for (var stmt : finals.stmts) {
        bd.append(stmt).append("\n");
      }
      bd.append("}\n");
    }
    for (var stmt : caught) {
      bd.append(stmt).append("\n");
    }
    return bd.toString();
  }
}
