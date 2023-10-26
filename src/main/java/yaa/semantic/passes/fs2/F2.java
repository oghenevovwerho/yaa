package yaa.semantic.passes.fs2;

import yaa.ast.*;
import yaa.pojos.YaaInfo;
import yaa.pojos.FileState;
import yaa.pojos.GlobalData;
import yaa.pojos.YaaClz;
import yaa.pojos.YaaError;

import java.util.List;

import static yaa.pojos.GlobalData.*;
import static yaa.pojos.GlobalData.nothing;
import static yaa.pojos.NameUtils.top$elements$clz$name;

public class F2 extends FileState {
  public F2(List<Stmt> stmts, String filePath) {
    super(stmts, filePath);
  }

  @Override
  public YaaInfo $programOut(ProgramOut programOut) {
    fs = null;
    fs2 = null;
    return nothing;
  }

  @Override
  public YaaInfo $programIn(ProgramIn in) {
    this.tables = GlobalData.tables4File.get(in.path);
    this.table = tables.get(in);
    fs2 = this;
    fs = this;
    YaaError.filePath = in.path;
    topClzCodeName.put(in.path, top$elements$clz$name());
    return nothing;
  }

  public void pushTable(Stmt stmt) {
    this.table = tables.get(stmt);
  }

  public void popTable() {
    this.table = table.parent;
  }

  @Override
  public YaaInfo $imports(Imports imp) {
    F2Imp.f2ImportsStmt(imp);
    return nothing;
  }

  @Override
  public YaaInfo $function(NewFun newFun) {
    F2NFun.f2NewFunction(newFun);
    return nothing;
  }

  @Override
  public YaaInfo $init(Init init) {
    F2Init.f2Init(init);
    return nothing;
  }

  @Override
  public YaaInfo $newClass(NewClass newClass) {
    F2NClass.newType(newClass);
    return nothing;
  }

  @Override
  public YaaInfo $fInterface(NewFunctionalInterface ctx) {
    F2FInterface.fInterface(ctx);
    return nothing;
  }

  @Override
  public YaaInfo $newRecord(NewRecord newRecord) {
    F2NRecord.newRecord(newRecord);
    return nothing;
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
  public YaaInfo $objectType(ObjectType objectType) {
    return YaaClz.f2Clz(objectType);
  }

  @Override
  public YaaInfo $anonymous(Anonymous anonymous) {
    F2NoName.f2NoName(anonymous);
    return nothing;
  }

  @Override
  public YaaInfo $ifStmt(IfStmt ifStmt) {
    pushTable(ifStmt);
    for (var ifCase : ifStmt.cases) {
      F2IfCase.f2IfCase(ifCase);
    }
    for (var elseStmt : ifStmt.elseStmts) {
      elseStmt.visit(this);
    }
    popTable();
    return nothing;
  }

  @Override
  public YaaInfo $loop(Loop loop) {
    F2Loop.f2LoopStmt(loop);
    return nothing;
  }

  @Override
  public YaaInfo $tryCatch(TryCatch tryCatch) {
    F2Try.f2Try(tryCatch);
    return nothing;
  }

  @Override
  public YaaInfo $main(MainFunction main) {
    F2Main.f2Main(main);
    return nothing;
  }

  @Override
  public YaaInfo $newEnum(NewEnum newEnum) {
    F2NEnum.newEnum(newEnum);
    return nothing;
  }
}
