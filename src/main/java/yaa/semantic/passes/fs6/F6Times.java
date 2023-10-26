package yaa.semantic.passes.fs6;

import yaa.ast.Times;
import yaa.semantic.passes.fs6.results.CallResult;
import yaa.pojos.YaaInfo;
import org.objectweb.asm.Opcodes;
import yaa.pojos.GlobalData;

import static yaa.semantic.passes.fs6.F6.mw;
import static yaa.semantic.passes.fs6.F6CTimeOp.compOp;
import static yaa.semantic.passes.fs6.F6CTimeOp.compTimeDouble;
import static yaa.semantic.passes.fs6.F6Utils.itIsPrimitive;
import static yaa.semantic.passes.fs6.F6Utils.operatorMtd;

public class F6Times {
  public static YaaInfo times(Times times) {
    var mtd = ((CallResult) GlobalData.results.get(GlobalData.fs6.path).get(times)).mtd;
    var owner = mtd.owner;
    var right = mtd.parameters.get(0);

    if (itIsPrimitive(times.e1) && itIsPrimitive(times.e2)) {
      var v1 = compTimeDouble(times.e1);
      var v2 = compTimeDouble(times.e2);
      return compOp(v1 * v2, mtd.type);
    }

    if (!itIsPrimitive(owner)) {
      return operatorMtd(times.e1, times.e2, mtd);
    }

    if (owner.equals(GlobalData.int$name)) {
      var rightName = right.name;
      switch (rightName) {
        case GlobalData.int$name -> {
          times.e1.visit(GlobalData.fs6);
          times.e2.visit(GlobalData.fs6);
          mw().visitInsn(Opcodes.IMUL);
        }
        case GlobalData.double$name -> {
          times.e1.visit(GlobalData.fs6);
          mw().visitInsn(Opcodes.I2D);
          times.e2.visit(GlobalData.fs6);
          mw().visitInsn(Opcodes.DMUL);
        }
        case GlobalData.float$name -> {
          times.e1.visit(GlobalData.fs6);
          mw().visitInsn(Opcodes.I2F);
          times.e2.visit(GlobalData.fs6);
          mw().visitInsn(Opcodes.FMUL);
        }
        case GlobalData.long$name -> {
          times.e1.visit(GlobalData.fs6);
          mw().visitInsn(Opcodes.I2L);
          times.e2.visit(GlobalData.fs6);
          mw().visitInsn(Opcodes.LMUL);
        }
      }
    }
    if (owner.equals(GlobalData.float$name)) {
      switch (right.name) {
        case GlobalData.float$name -> {
          times.e1.visit(GlobalData.fs6);
          times.e2.visit(GlobalData.fs6);
          mw().visitInsn(Opcodes.FMUL);
        }
        case GlobalData.double$name -> {
          times.e1.visit(GlobalData.fs6);
          mw().visitInsn(Opcodes.F2D);
          times.e2.visit(GlobalData.fs6);
          mw().visitInsn(Opcodes.DMUL);
        }
        case GlobalData.int$name -> {
          times.e1.visit(GlobalData.fs6);
          times.e2.visit(GlobalData.fs6);
          mw().visitInsn(Opcodes.I2F);
          mw().visitInsn(Opcodes.FMUL);
        }
        case GlobalData.long$name -> {
          times.e1.visit(GlobalData.fs6);
          times.e2.visit(GlobalData.fs6);
          mw().visitInsn(Opcodes.L2F);
          mw().visitInsn(Opcodes.FMUL);
        }
      }
    }
    if (owner.equals(GlobalData.double$name)) {
      switch (right.name) {
        case GlobalData.double$name -> {
          times.e1.visit(GlobalData.fs6);
          times.e2.visit(GlobalData.fs6);
          mw().visitInsn(Opcodes.DMUL);
        }
        case GlobalData.float$name -> {
          times.e1.visit(GlobalData.fs6);
          times.e2.visit(GlobalData.fs6);
          mw().visitInsn(Opcodes.F2D);
          mw().visitInsn(Opcodes.DMUL);
        }
        case GlobalData.int$name -> {
          times.e1.visit(GlobalData.fs6);
          times.e2.visit(GlobalData.fs6);
          mw().visitInsn(Opcodes.I2D);
          mw().visitInsn(Opcodes.DMUL);
        }
        case GlobalData.long$name -> {
          times.e1.visit(GlobalData.fs6);
          times.e2.visit(GlobalData.fs6);
          mw().visitInsn(Opcodes.L2D);
          mw().visitInsn(Opcodes.DMUL);
        }
      }
    }
    if (owner.equals(GlobalData.long$name)) {
      switch (right.name) {
        case GlobalData.long$name -> {
          times.e1.visit(GlobalData.fs6);
          times.e2.visit(GlobalData.fs6);
          mw().visitInsn(Opcodes.LMUL);
        }
        case GlobalData.double$name -> {
          times.e1.visit(GlobalData.fs6);
          mw().visitInsn(Opcodes.L2D);
          times.e2.visit(GlobalData.fs6);
          mw().visitInsn(Opcodes.DMUL);
        }
        case GlobalData.float$name -> {
          times.e1.visit(GlobalData.fs6);
          mw().visitInsn(Opcodes.L2F);
          times.e2.visit(GlobalData.fs6);
          mw().visitInsn(Opcodes.FMUL);
        }
        case GlobalData.int$name -> {
          times.e1.visit(GlobalData.fs6);
          times.e2.visit(GlobalData.fs6);
          mw().visitInsn(Opcodes.I2L);
          mw().visitInsn(Opcodes.LMUL);
        }
      }
    }
    return mtd.type;
  }
}
