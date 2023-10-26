package yaa.semantic.passes.fs6;

import yaa.ast.Modulo;
import yaa.semantic.passes.fs6.results.CallResult;
import yaa.pojos.YaaInfo;
import org.objectweb.asm.Opcodes;
import yaa.pojos.GlobalData;

import static yaa.semantic.passes.fs6.F6.mw;
import static yaa.semantic.passes.fs6.F6CTimeOp.compOp;
import static yaa.semantic.passes.fs6.F6CTimeOp.compTimeDouble;
import static yaa.semantic.passes.fs6.F6Utils.itIsPrimitive;
import static yaa.semantic.passes.fs6.F6Utils.operatorMtd;

public class F6Mod {
  public static YaaInfo mod(Modulo mod) {
    var mtd = ((CallResult) GlobalData.results.get(GlobalData.fs6.path).get(mod)).mtd;
    var owner = mtd.owner;
    var right = mtd.parameters.get(0);

    if (itIsPrimitive(mod.e1) && itIsPrimitive(mod.e2)) {
      var v1 = compTimeDouble(mod.e1);
      var v2 = compTimeDouble(mod.e2);
      return compOp(v1 % v2, mtd.type);
    }

    if (!itIsPrimitive(owner)) {
      return operatorMtd(mod.e1, mod.e2, mtd);
    }

    if (owner.equals(GlobalData.int$name)) {
      var rightName = right.name;
      switch (rightName) {
        case GlobalData.int$name -> {
          mod.e1.visit(GlobalData.fs6);
          mod.e2.visit(GlobalData.fs6);
          mw().visitInsn(Opcodes.IREM);
        }
        case GlobalData.double$name -> {
          mod.e1.visit(GlobalData.fs6);
          mw().visitInsn(Opcodes.I2D);
          mod.e2.visit(GlobalData.fs6);
          mw().visitInsn(Opcodes.DREM);
        }
        case GlobalData.float$name -> {
          mod.e1.visit(GlobalData.fs6);
          mw().visitInsn(Opcodes.I2F);
          mod.e2.visit(GlobalData.fs6);
          mw().visitInsn(Opcodes.FREM);
        }
        case GlobalData.long$name -> {
          mod.e1.visit(GlobalData.fs6);
          mw().visitInsn(Opcodes.I2L);
          mod.e2.visit(GlobalData.fs6);
          mw().visitInsn(Opcodes.LREM);
        }
      }
    }
    if (owner.equals(GlobalData.float$name)) {
      switch (right.name) {
        case GlobalData.float$name -> {
          mod.e1.visit(GlobalData.fs6);
          mod.e2.visit(GlobalData.fs6);
          mw().visitInsn(Opcodes.FREM);
        }
        case GlobalData.double$name -> {
          mod.e1.visit(GlobalData.fs6);
          mw().visitInsn(Opcodes.F2D);
          mod.e2.visit(GlobalData.fs6);
          mw().visitInsn(Opcodes.DREM);
        }
        case GlobalData.int$name -> {
          mod.e1.visit(GlobalData.fs6);
          mod.e2.visit(GlobalData.fs6);
          mw().visitInsn(Opcodes.I2F);
          mw().visitInsn(Opcodes.FREM);
        }
        case GlobalData.long$name -> {
          mod.e1.visit(GlobalData.fs6);
          mod.e2.visit(GlobalData.fs6);
          mw().visitInsn(Opcodes.L2F);
          mw().visitInsn(Opcodes.FREM);
        }
      }
    }
    if (owner.equals(GlobalData.double$name)) {
      switch (right.name) {
        case GlobalData.double$name -> {
          mod.e1.visit(GlobalData.fs6);
          mod.e2.visit(GlobalData.fs6);
          mw().visitInsn(Opcodes.DREM);
        }
        case GlobalData.float$name -> {
          mod.e1.visit(GlobalData.fs6);
          mod.e2.visit(GlobalData.fs6);
          mw().visitInsn(Opcodes.F2D);
          mw().visitInsn(Opcodes.DREM);
        }
        case GlobalData.int$name -> {
          mod.e1.visit(GlobalData.fs6);
          mod.e2.visit(GlobalData.fs6);
          mw().visitInsn(Opcodes.I2D);
          mw().visitInsn(Opcodes.DREM);
        }
        case GlobalData.long$name -> {
          mod.e1.visit(GlobalData.fs6);
          mod.e2.visit(GlobalData.fs6);
          mw().visitInsn(Opcodes.L2D);
          mw().visitInsn(Opcodes.DREM);
        }
      }
    }
    if (owner.equals(GlobalData.long$name)) {
      switch (right.name) {
        case GlobalData.long$name -> {
          mod.e1.visit(GlobalData.fs6);
          mod.e2.visit(GlobalData.fs6);
          mw().visitInsn(Opcodes.LREM);
        }
        case GlobalData.double$name -> {
          mod.e1.visit(GlobalData.fs6);
          mw().visitInsn(Opcodes.L2D);
          mod.e2.visit(GlobalData.fs6);
          mw().visitInsn(Opcodes.DREM);
        }
        case GlobalData.float$name -> {
          mod.e1.visit(GlobalData.fs6);
          mw().visitInsn(Opcodes.L2F);
          mod.e2.visit(GlobalData.fs6);
          mw().visitInsn(Opcodes.FREM);
        }
        case GlobalData.int$name -> {
          mod.e1.visit(GlobalData.fs6);
          mod.e2.visit(GlobalData.fs6);
          mw().visitInsn(Opcodes.I2L);
          mw().visitInsn(Opcodes.LREM);
        }
      }
    }
    return mtd.type;
  }
}
