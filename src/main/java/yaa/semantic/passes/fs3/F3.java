package yaa.semantic.passes.fs3;

import yaa.ast.*;
import yaa.pojos.YaaInfo;
import yaa.pojos.FileState;
import yaa.pojos.GlobalData;
import yaa.pojos.YaaClz;
import yaa.pojos.YaaError;

import java.util.List;

import static yaa.pojos.GlobalData.*;
import static yaa.pojos.GlobalData.nothing;

public class F3 extends FileState {
  public F3(List<Stmt> stmts, String filePath) {
    super(stmts, filePath);
  }

  @Override
  public YaaInfo $programOut(ProgramOut programOut) {
    fs = null;
    fs3 = null;
    return nothing;
  }

  @Override
  public YaaInfo $programIn(ProgramIn in) {
    this.tables = GlobalData.tables4File.get(in.path);
    this.table = tables.get(in);
    fs3 = this;
    fs = this;
    YaaError.filePath = in.path;
    return nothing;
  }

  public void pushTable(Stmt stmt) {
    this.table = tables.get(stmt);
  }

  public void popTable() {
    this.table = table.parent;
  }

  @Override
  public YaaInfo $fInterface(NewFunctionalInterface ctx) {
    F3FInterface.fInterface(ctx);
    return nothing;
  }

  @Override
  public YaaInfo $function(NewFun newFun) {
    F3NFun.f3NewFunction(newFun);
    return nothing;
  }

  @Override
  public YaaInfo $newClass(NewClass newClass) {
    F3NClass.newType(newClass);
    return nothing;
  }

  @Override
  public YaaInfo $newRecord(NewRecord newRecord) {
    F3NRecord.newRecord(newRecord);
    return nothing;
  }

  @Override
  public YaaInfo $newEnum(NewEnum newEnum) {
    F3NEnum.newEnum(newEnum);
    return nothing;
  }

  @Override
  public YaaInfo $objectType(ObjectType objectType) {
    return YaaClz.f3Clz(objectType);
  }

  @Override
  public YaaInfo $innerBlock(InnerBlock iBlock) {
    pushTable(iBlock);
    for (var stmt : iBlock.stmts) {
      stmt.visit(this);
    }
    popTable();
    return nothing;
  }

  @Override
  public YaaInfo $ifStmt(IfStmt ifStmt) {
    pushTable(ifStmt);
    for (var ifCase : ifStmt.cases) {
      F3IfCase.f3IfCase(ifCase);
    }
    for (var elseStmt : ifStmt.elseStmts) {
      elseStmt.visit(this);
    }
    popTable();
    return nothing;
  }

  @Override
  public YaaInfo $loop(Loop loop) {
    F3Loop.f3LoopStmt(loop);
    return nothing;
  }

  @Override
  public YaaInfo $main(MainFunction main) {
    F3Main.f3Main(main);
    return nothing;
  }

  @Override
  public YaaInfo $anonymous(Anonymous anonymous) {
    F3NoName.f3Main(anonymous);
    return nothing;
  }

  @Override
  public YaaInfo $tryCatch(TryCatch tryCatch) {
    F3Try.f3Try(tryCatch);
    return nothing;
  }
}