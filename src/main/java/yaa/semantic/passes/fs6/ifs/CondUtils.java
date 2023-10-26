package yaa.semantic.passes.fs6.ifs;

import yaa.ast.IfCase;
import yaa.ast.IfStmt;
import yaa.ast.Is;
import yaa.ast.Name;
import yaa.pojos.YaaField;
import yaa.semantic.passes.fs6.F6CastMtd;
import yaa.semantic.passes.fs6.F6Is;
import yaa.semantic.passes.fs6.F6Name;
import yaa.semantic.passes.fs6.results.IsResult;
import yaa.pojos.YaaInfo;
import yaa.pojos.GlobalData;

import static yaa.pojos.GlobalData.fs6;
import static yaa.semantic.passes.fs6.F6.mw;
import static yaa.semantic.passes.fs6.F6Utils.generateRandomName;
import static yaa.semantic.passes.fs6.F6Utils.itIsPrimitive;
import static org.objectweb.asm.Opcodes.*;

public class CondUtils {
  protected static void putCaseOffer(IfCase c$case) {
    if (c$case.case_condition instanceof Is is && is.e instanceof Name name) {
      var result = (IsResult) fs6.results.get(c$case.case_condition);
      var nameContent = name.token.content;

      fs6.popTable();
      F6Name.id(nameContent);
      F6CastMtd.doAutoCast(result);
      fs6.pushTable(c$case);

      var cast$name = generateRandomName();
      var field = (YaaField) fs6.getSymbol(nameContent);
      field.cast$name = cast$name;
      var toName = result.type.name;
      switch (toName) {
        case GlobalData.double$name -> {
          mw().visitVarInsn(DSTORE, fs6.variables.peek().putWideVar(cast$name));
        }
        case GlobalData.long$name -> {
          mw().visitVarInsn(LSTORE, fs6.variables.peek().putWideVar(cast$name));
        }
        case GlobalData.float$name -> {
          mw().visitVarInsn(FSTORE, fs6.variables.peek().putWideVar(cast$name));
        }
        default -> {
          if (itIsPrimitive(toName)) {
            mw().visitVarInsn(ISTORE, fs6.variables.peek().putVar(cast$name));
          } else {
            mw().visitVarInsn(ASTORE, fs6.variables.peek().putVar(cast$name));
          }
        }
      }
    }
  }

  protected static YaaInfo getCaseOffer(IfCase c$case) {
    YaaInfo case$offer;
    if (c$case.case_condition instanceof Is is && is.e instanceof Name name) {
      var result = (IsResult) fs6.results.get(is);
      var nameContent = name.token.content;

      fs6.popTable();
      F6Name.id(nameContent);
      F6CastMtd.doAutoCast(result);
      fs6.pushTable(c$case);

      var cast$name = generateRandomName();
      var field = (YaaField) fs6.getSymbol(nameContent);
      field.cast$name = cast$name;
      var toName = result.type.name;
      switch (toName) {
        case GlobalData.double$name -> {
          mw().visitVarInsn(DSTORE, fs6.variables.peek().putWideVar(cast$name));
        }
        case GlobalData.long$name -> {
          mw().visitVarInsn(LSTORE, fs6.variables.peek().putWideVar(cast$name));
        }
        case GlobalData.float$name -> {
          mw().visitVarInsn(FSTORE, fs6.variables.peek().putWideVar(cast$name));
        }
        default -> {
          if (itIsPrimitive(toName)) {
            mw().visitVarInsn(ISTORE, fs6.variables.peek().putVar(cast$name));
          } else {
            mw().visitVarInsn(ASTORE, fs6.variables.peek().putVar(cast$name));
          }
        }
      }
      mw().visitVarInsn(ALOAD, fs6.variables.peek().indexOf(nameContent));
      F6Is.is(is, result, false);
      case$offer = GlobalData.boole$clz;
    } else {
      case$offer = c$case.case_condition.visit(fs6);
    }
    return case$offer;
  }

  public static YaaInfo getIfTarget(IfStmt ifStmt) {
    if (ifStmt.e instanceof Is is && is.e instanceof Name name) {
      var result = (IsResult) fs6.results.get(ifStmt.e);
      var nameContent = name.token.content;

      F6Name.id(nameContent);
      F6CastMtd.doAutoCast(result);

      fs6.pushTable(ifStmt);
      var cast$name = generateRandomName();
      var field = (YaaField) fs6.getSymbol(nameContent);
      field.cast$name = cast$name;
      var toName = result.type.name;
      switch (toName) {
        case GlobalData.double$name -> {
          mw().visitVarInsn(DSTORE, fs6.variables.peek().putWideVar(cast$name));
        }
        case GlobalData.long$name -> {
          mw().visitVarInsn(LSTORE, fs6.variables.peek().putWideVar(cast$name));
        }
        case GlobalData.float$name -> {
          mw().visitVarInsn(FSTORE, fs6.variables.peek().putWideVar(cast$name));
        }
        default -> {
          if (itIsPrimitive(toName)) {
            mw().visitVarInsn(ISTORE, fs6.variables.peek().putVar(cast$name));
          } else {
            mw().visitVarInsn(ASTORE, fs6.variables.peek().putVar(cast$name));
          }
        }
      }
      mw().visitVarInsn(ALOAD, fs6.variables.peek().indexOf(cast$name));
      F6Is.is(is, result, false);
      fs6.popTable();
      return GlobalData.boole$clz;
    } else {
      return ifStmt.e.visit(fs6);
    }
  }
}