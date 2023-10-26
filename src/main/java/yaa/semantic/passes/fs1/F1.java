package yaa.semantic.passes.fs1;

import yaa.ast.*;
import yaa.parser.TokenUtils;
import yaa.pojos.*;
import yaa.pojos.FileState;

import java.util.*;

import static yaa.pojos.GlobalData.*;
import static yaa.semantic.passes.fs1.F1NClass.f1NewClass;
import static yaa.semantic.passes.fs1.F1NRecord.f1NewRecord;

public class F1 extends FileState {
  public F1(List<Stmt> stmts, String filePath) {
    super(stmts, filePath);
  }

  public void newTable() {
    var parentTable = table;
    table = new YaaTable();
    table.parent = parentTable;
  }

  @Override
  public YaaInfo $programOut(ProgramOut programOut) {
    GlobalData.fs = null;
    fs1 = null;
    return nothing;
  }

  public void popTable() {
    table = table.parent;
  }

  public void storeTable(Stmt stmt) {
    tables.put(stmt, table);
  }

  private final YaaTable globalTable = new YaaTable();

  @Override
  public YaaInfo $programIn(ProgramIn in) {
    this.table = globalTable;
    GlobalData.tables4File.put(path, new HashMap<>());
    this.tables = GlobalData.tables4File.get(path);
    this.tables.put(in, this.table);
    GlobalData.fs = this;
    fs1 = this;
    YaaError.filePath = path;
    GlobalData.usedFnNames.put(path, new HashMap<>());
    GlobalData.usedClzNames.put(path, new HashSet<>());
    return nothing;
  }

  @Override
  public void execute() {
    for (var stmt : stmts) {
      if (stmt instanceof ProgramIn in) {
        //this must come first, because it initializes the context
        in.visit(this);
      } else if (stmt instanceof VDefinition def) {
        yaa.semantic.passes.fs1.F1VDef.f1topDef(def);
      } else if (stmt instanceof VDeclaration dec) {
        F1VDec.f1topDec(dec);
      } else if (stmt instanceof NewFun method) {
        var newMethod = yaa.semantic.passes.fs1.F1NFun.f1NewFun(method);
        for (var metaCall : method.metaCalls) {
          var meta = getSymbol(metaCall.name.content);
          if (meta instanceof YaaMeta && meta.name.equals(GlobalData.configMetaClzName)) {
            for (var arg : metaCall.arguments.entrySet()) {
              var argument = arg.getKey();
              switch (argument.content) {
                case "privacy" -> {
                  if (arg.getValue() instanceof Decimal decimal) {
                    int value = TokenUtils.decimalValue(decimal.token);
                    if (value == 0) {
                      newMethod.privacy = 0;
                      method.privacy = 0;
                    } else if (value == 2) {
                      newMethod.privacy = 2;
                      method.privacy = 2;
                    } else {
                      throw new YaaError(
                          arg.getValue().placeOfUse(),
                          "The value of privacy for a global function must be 0 or 2"
                      );
                    }
                  } else {
                    throw new YaaError(
                        arg.getValue().placeOfUse(),
                        "The value of the privacy parameter must be a literal of " + int$name
                    );
                  }
                }
                default -> {
                  throw new YaaError(
                      argument.placeOfUse(),
                      "A global scope method definition cannot " +
                          "contain the option \"" + argument.content + "\""
                  );
                }
              }
            }
          }
        }
      } else {
        stmt.visit(this);
      }
    }
  }

  @Override
  public YaaInfo $fInterface(NewFunctionalInterface ctx) {
    return F1FInterface.f1FInterface(ctx);
  }

  @Override
  public YaaInfo $newClass(NewClass newClass) {
    return f1NewClass(newClass);
  }

  @Override
  public YaaInfo $newRecord(NewRecord newRecord) {
    return f1NewRecord(newRecord);
  }

  @Override
  public YaaInfo $newEnum(NewEnum newEnum) {
    return yaa.semantic.passes.fs1.F1NEnum.f1NewEnum(newEnum);
  }

  @Override
  public YaaInfo $function(NewFun newFun) {
    yaa.semantic.passes.fs1.F1NFun.f1NewFun(newFun);
    for (var metaCall : newFun.metaCalls) {
      var meta = fs1.getSymbol(metaCall.name.content);
      if (meta instanceof YaaMeta && meta.name.equals(GlobalData.configMetaClzName)) {
        for (var arg : metaCall.arguments.entrySet()) {
          var argument = arg.getKey();
          throw new YaaError(
              argument.placeOfUse(),
              "A statement scope method definition cannot " +
                  "contain the option \"" + argument.content + "\""
          );
        }
      }
    }
    return nothing;
  }

  @Override
  public YaaInfo $loop(Loop loop) {
    yaa.semantic.passes.fs1.F1Loop.f1LoopStmt(loop);
    return nothing;
  }

  @Override
  public YaaInfo $innerBlock(InnerBlock iBlock) {
    newTable();
    for (var stmt : iBlock.stmts) {
      stmt.visit(this);
    }
    storeTable(iBlock);
    popTable();
    return nothing;
  }

  @Override
  public YaaInfo $vDef(VDefinition def) {
    yaa.semantic.passes.fs1.F1VDef.f1stmtDef(def);
    return nothing;
  }

  @Override
  public YaaInfo $vDec(VDeclaration dec) {
    return F1VDec.f1stmtDec(dec);
  }

  @Override
  public YaaInfo $ifStmt(IfStmt ifStmt) {
    yaa.semantic.passes.fs1.F1IfStmt.ifStmt(ifStmt);
    return nothing;
  }

  @Override
  public YaaInfo $anonymous(Anonymous anonymous) {
    F1NoName.anonymous(anonymous);
    return nothing;
  }

  @Override
  public YaaInfo $tryCatch(TryCatch tryCatch) {
    F1Try.f1Try(tryCatch);
    return nothing;
  }

  @Override
  public YaaInfo $main(MainFunction main) {
    F1Main.f1Main(main);
    return nothing;
  }

  @Override
  public YaaInfo $newMeta(NewMeta newMeta) {
    F1NewMeta.f1NewMeta(newMeta);
    return null;
  }
}