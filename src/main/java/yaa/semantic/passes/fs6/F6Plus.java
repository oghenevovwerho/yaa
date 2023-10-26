package yaa.semantic.passes.fs6;

import yaa.ast.Cha;
import yaa.ast.Plus;
import yaa.semantic.passes.fs6.results.CallResult;
import yaa.pojos.YaaFun;
import yaa.pojos.YaaInfo;
import org.objectweb.asm.Opcodes;
import yaa.pojos.GlobalData;

import static yaa.semantic.passes.fs6.F6CTimeOp.compOp;
import static yaa.semantic.passes.fs6.F6CTimeOp.compTimeDouble;
import static yaa.semantic.passes.fs6.F6.mw;
import static yaa.semantic.passes.fs6.F6Utils.itIsPrimitive;
import static yaa.semantic.passes.fs6.F6Utils.operatorMtd;
import static org.objectweb.asm.Opcodes.*;

public class F6Plus {
  public static YaaInfo plus(Plus plus) {
    var mtd = ((CallResult) GlobalData.results.get(GlobalData.fs6.path).get(plus)).mtd;
    var owner = mtd.owner;
    var right = mtd.parameters.get(0);

    if (plus.e1 instanceof Cha && plus.e2 instanceof Cha) {
      //The addition of two chars has special semantics
      return handleCharConcat(mtd, plus);
    } else if (itIsPrimitive(plus.e1) && itIsPrimitive(plus.e2)) {
      var v1 = compTimeDouble(plus.e1);
      var v2 = compTimeDouble(plus.e2);
      return compOp(v1 + v2, mtd.type);
    }

    if (owner.equals("java/lang/String")) {
      return handleStringConcat(mtd, plus);
    }

    if (!itIsPrimitive(owner)) {
      return operatorMtd(plus.e1, plus.e2, mtd);
    }

    if (owner.equals(GlobalData.int$name)) {
      var rightName = right.name;
      switch (rightName) {
        case GlobalData.int$name -> {
          plus.e1.visit(GlobalData.fs6);
          plus.e2.visit(GlobalData.fs6);
          mw().visitInsn(Opcodes.IADD);
        }
        case GlobalData.double$name -> {
          plus.e1.visit(GlobalData.fs6);
          mw().visitInsn(Opcodes.I2D);
          plus.e2.visit(GlobalData.fs6);
          mw().visitInsn(Opcodes.DADD);
        }
        case GlobalData.float$name -> {
          plus.e1.visit(GlobalData.fs6);
          mw().visitInsn(Opcodes.I2F);
          plus.e2.visit(GlobalData.fs6);
          mw().visitInsn(Opcodes.FADD);
        }
        case GlobalData.long$name -> {
          plus.e1.visit(GlobalData.fs6);
          mw().visitInsn(Opcodes.I2L);
          plus.e2.visit(GlobalData.fs6);
          mw().visitInsn(Opcodes.LADD);
        }
      }
    }
    if (owner.equals(GlobalData.float$name)) {
      switch (right.name) {
        case GlobalData.float$name -> {
          plus.e1.visit(GlobalData.fs6);
          plus.e2.visit(GlobalData.fs6);
          mw().visitInsn(Opcodes.FADD);
        }
        case GlobalData.double$name -> {
          plus.e1.visit(GlobalData.fs6);
          mw().visitInsn(Opcodes.F2D);
          plus.e2.visit(GlobalData.fs6);
          mw().visitInsn(Opcodes.DADD);
        }
        case GlobalData.int$name -> {
          plus.e1.visit(GlobalData.fs6);
          plus.e2.visit(GlobalData.fs6);
          mw().visitInsn(Opcodes.I2F);
          mw().visitInsn(Opcodes.FADD);
        }
        case GlobalData.long$name -> {
          plus.e1.visit(GlobalData.fs6);
          plus.e2.visit(GlobalData.fs6);
          mw().visitInsn(Opcodes.L2F);
          mw().visitInsn(Opcodes.FADD);
        }
      }
    }
    if (owner.equals(GlobalData.double$name)) {
      switch (right.name) {
        case GlobalData.double$name -> {
          plus.e1.visit(GlobalData.fs6);
          plus.e2.visit(GlobalData.fs6);
          mw().visitInsn(Opcodes.DADD);
        }
        case GlobalData.float$name -> {
          plus.e1.visit(GlobalData.fs6);
          plus.e2.visit(GlobalData.fs6);
          mw().visitInsn(Opcodes.F2D);
          mw().visitInsn(Opcodes.DADD);
        }
        case GlobalData.int$name -> {
          plus.e1.visit(GlobalData.fs6);
          plus.e2.visit(GlobalData.fs6);
          mw().visitInsn(Opcodes.I2D);
          mw().visitInsn(Opcodes.DADD);
        }
        case GlobalData.long$name -> {
          plus.e1.visit(GlobalData.fs6);
          plus.e2.visit(GlobalData.fs6);
          mw().visitInsn(Opcodes.L2D);
          mw().visitInsn(Opcodes.DADD);
        }
      }
    }
    if (owner.equals(GlobalData.long$name)) {
      switch (right.name) {
        case GlobalData.long$name -> {
          plus.e1.visit(GlobalData.fs6);
          plus.e2.visit(GlobalData.fs6);
          mw().visitInsn(Opcodes.LADD);
        }
        case GlobalData.double$name -> {
          plus.e1.visit(GlobalData.fs6);
          mw().visitInsn(Opcodes.L2D);
          plus.e2.visit(GlobalData.fs6);
          mw().visitInsn(Opcodes.DADD);
        }
        case GlobalData.float$name -> {
          plus.e1.visit(GlobalData.fs6);
          mw().visitInsn(Opcodes.L2F);
          plus.e2.visit(GlobalData.fs6);
          mw().visitInsn(Opcodes.FADD);
        }
        case GlobalData.int$name -> {
          plus.e1.visit(GlobalData.fs6);
          plus.e2.visit(GlobalData.fs6);
          mw().visitInsn(Opcodes.I2L);
          mw().visitInsn(Opcodes.LADD);
        }
      }
    }
    return mtd.type;
  }

  private static YaaInfo handleCharConcat(YaaFun mtd, Plus ctx) {
    var right = mtd.parameters.get(0);

    mw().visitTypeInsn(NEW, "java/lang/StringBuilder");
    mw().visitInsn(DUP);

    mw().visitMethodInsn(
      INVOKESPECIAL, "java/lang/StringBuilder",
      "<init>", "()V", false
    );

    ctx.e1.visit(GlobalData.fs6);
    mw().visitMethodInsn(
      INVOKEVIRTUAL, "java/lang/StringBuilder", "append",
      "(C)Ljava/lang/StringBuilder;", false
    );

    ctx.e2.visit(GlobalData.fs6);
    mw().visitMethodInsn(
      INVOKEVIRTUAL, "java/lang/StringBuilder", "append",
      "(" + right.descriptor() + ")Ljava/lang/StringBuilder;",
      false
    );

    mw().visitMethodInsn(
      INVOKEVIRTUAL,
      "java/lang/StringBuilder",
      "toString",
      "()Ljava/lang/String;",
      false
    );

    return mtd.type;
  }

  private static YaaInfo handleStringConcat(YaaFun mtd, Plus ctx) {
    var right = mtd.parameters.get(0);

    mw().visitTypeInsn(NEW, "java/lang/StringBuilder");
    mw().visitInsn(DUP);

    mw().visitMethodInsn(
      INVOKESPECIAL, "java/lang/StringBuilder",
      "<init>", "()V", false
    );

    ctx.e1.visit(GlobalData.fs6);
    mw().visitMethodInsn(
      INVOKEVIRTUAL, "java/lang/StringBuilder", "append",
      "(Ljava/lang/String;)Ljava/lang/StringBuilder;",
      false
    );

    ctx.e2.visit(GlobalData.fs6);
    mw().visitMethodInsn(
      INVOKEVIRTUAL, "java/lang/StringBuilder", "append",
      "(" + right.descriptor() + ")Ljava/lang/StringBuilder;",
      false
    );

    mw().visitMethodInsn(
      INVOKEVIRTUAL,
      "java/lang/StringBuilder",
      "toString",
      "()Ljava/lang/String;",
      false
    );

    return mtd.type;
  }
}
