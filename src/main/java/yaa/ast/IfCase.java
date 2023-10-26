package yaa.ast;

import yaa.parser.YaaToken;

public class IfCase extends Stmt {
  public Stmt case_condition;
  public Stmt stmt;
  public YaaToken caseLabel;
  public JumpAst lastJump;
  public String targetName;
  public boolean isNotPrimitive;
  public String resultName;

  public boolean autoCasts() {
    return case_condition instanceof Is is && is.e instanceof Name;
  }

  public IfCase(Stmt exp) {
    this.case_condition = exp;
  }

  @Override
  public String toString() {
    return case_condition.toString() + stmt;
  }
}
