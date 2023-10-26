package yaa.semantic.passes.fs6;

import yaa.ast.Minus;
import yaa.semantic.passes.fs6.results.CallResult;
import yaa.pojos.YaaInfo;
import org.objectweb.asm.Opcodes;
import yaa.pojos.GlobalData;

import static yaa.semantic.passes.fs6.F6Utils.itIsPrimitive;

public class F6Minus {
  public static YaaInfo minus(Minus minus) {
    var mtd = ((CallResult) GlobalData.results.get(GlobalData.fs6.path).get(minus)).mtd;
    var owner = mtd.owner;
    var right = mtd.parameters.get(0);

    if (itIsPrimitive(minus.e1) && itIsPrimitive(minus.e2)) {
      var v1 = F6CTimeOp.compTimeDouble(minus.e1);
      var v2 = F6CTimeOp.compTimeDouble(minus.e2);
      return F6CTimeOp.compOp(v1 - v2, mtd.type);
    }

    if (!F6Utils.itIsPrimitive(owner)) {
      return F6Utils.operatorMtd(minus.e1, minus.e2, mtd);
    }

    if (owner.equals(GlobalData.int$name)) {
      var rightName = right.name;
      switch (rightName) {
        case GlobalData.int$name -> {
          minus.e1.visit(GlobalData.fs6);
          minus.e2.visit(GlobalData.fs6);
          F6.mw().visitInsn(Opcodes.ISUB);
        }
        case GlobalData.double$name -> {
          minus.e1.visit(GlobalData.fs6);
          F6.mw().visitInsn(Opcodes.I2D);
          minus.e2.visit(GlobalData.fs6);
          F6.mw().visitInsn(Opcodes.DSUB);
        }
        case GlobalData.float$name -> {
          minus.e1.visit(GlobalData.fs6);
          F6.mw().visitInsn(Opcodes.I2F);
          minus.e2.visit(GlobalData.fs6);
          F6.mw().visitInsn(Opcodes.FSUB);
        }
        case GlobalData.long$name -> {
          minus.e1.visit(GlobalData.fs6);
          F6.mw().visitInsn(Opcodes.I2L);
          minus.e2.visit(GlobalData.fs6);
          F6.mw().visitInsn(Opcodes.LSUB);
        }
      }
    }
    if (owner.equals(GlobalData.float$name)) {
      switch (right.name) {
        case GlobalData.float$name -> {
          minus.e1.visit(GlobalData.fs6);
          minus.e2.visit(GlobalData.fs6);
          F6.mw().visitInsn(Opcodes.FSUB);
        }
        case GlobalData.double$name -> {
          minus.e1.visit(GlobalData.fs6);
          F6.mw().visitInsn(Opcodes.F2D);
          minus.e2.visit(GlobalData.fs6);
          F6.mw().visitInsn(Opcodes.DSUB);
        }
        case GlobalData.int$name -> {
          minus.e1.visit(GlobalData.fs6);
          minus.e2.visit(GlobalData.fs6);
          F6.mw().visitInsn(Opcodes.I2F);
          F6.mw().visitInsn(Opcodes.FSUB);
        }
        case GlobalData.long$name -> {
          minus.e1.visit(GlobalData.fs6);
          minus.e2.visit(GlobalData.fs6);
          F6.mw().visitInsn(Opcodes.L2F);
          F6.mw().visitInsn(Opcodes.FSUB);
        }
      }
    }
    if (owner.equals(GlobalData.double$name)) {
      switch (right.name) {
        case GlobalData.double$name -> {
          minus.e1.visit(GlobalData.fs6);
          minus.e2.visit(GlobalData.fs6);
          F6.mw().visitInsn(Opcodes.DSUB);
        }
        case GlobalData.float$name -> {
          minus.e1.visit(GlobalData.fs6);
          minus.e2.visit(GlobalData.fs6);
          F6.mw().visitInsn(Opcodes.F2D);
          F6.mw().visitInsn(Opcodes.DSUB);
        }
        case GlobalData.int$name -> {
          minus.e1.visit(GlobalData.fs6);
          minus.e2.visit(GlobalData.fs6);
          F6.mw().visitInsn(Opcodes.I2D);
          F6.mw().visitInsn(Opcodes.DSUB);
        }
        case GlobalData.long$name -> {
          minus.e1.visit(GlobalData.fs6);
          minus.e2.visit(GlobalData.fs6);
          F6.mw().visitInsn(Opcodes.L2D);
          F6.mw().visitInsn(Opcodes.DSUB);
        }
      }
    }
    if (owner.equals(GlobalData.long$name)) {
      switch (right.name) {
        case GlobalData.long$name -> {
          minus.e1.visit(GlobalData.fs6);
          minus.e2.visit(GlobalData.fs6);
          F6.mw().visitInsn(Opcodes.LSUB);
        }
        case GlobalData.double$name -> {
          minus.e1.visit(GlobalData.fs6);
          F6.mw().visitInsn(Opcodes.L2D);
          minus.e2.visit(GlobalData.fs6);
          F6.mw().visitInsn(Opcodes.DSUB);
        }
        case GlobalData.float$name -> {
          minus.e1.visit(GlobalData.fs6);
          F6.mw().visitInsn(Opcodes.L2F);
          minus.e2.visit(GlobalData.fs6);
          F6.mw().visitInsn(Opcodes.FSUB);
        }
        case GlobalData.int$name -> {
          minus.e1.visit(GlobalData.fs6);
          minus.e2.visit(GlobalData.fs6);
          F6.mw().visitInsn(Opcodes.I2L);
          F6.mw().visitInsn(Opcodes.LSUB);
        }
      }
    }
    return mtd.type;
  }
}
