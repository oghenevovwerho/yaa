package yaa.semantic.passes.fs6;

import yaa.ast.GThan;
import yaa.semantic.passes.fs6.results.CallResult;
import yaa.pojos.YaaInfo;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import yaa.pojos.GlobalData;

import static yaa.semantic.passes.fs6.F6.mw;
import static yaa.semantic.passes.fs6.F6Utils.itIsPrimitive;
import static yaa.semantic.passes.fs6.F6Utils.operatorMtd;
import static org.objectweb.asm.Opcodes.*;

public class F6GThan {
  public static YaaInfo gThan(GThan ctx) {
    var mtd = ((CallResult) GlobalData.results.get(GlobalData.fs6.path).get(ctx)).mtd;
    var owner = mtd.owner;
    var right = mtd.parameters.get(0);

    if (!itIsPrimitive(owner)) {
      return operatorMtd(ctx.e1, ctx.e2, mtd);
    }

    if (owner.equals(GlobalData.int$name)) {
      var rightName = right.name;
      switch (rightName) {
        case GlobalData.int$name -> {
          var bothEqualDestination = new Label();
          var finalDestination = new Label();
          ctx.e1.visit(GlobalData.fs6);
          ctx.e2.visit(GlobalData.fs6);
          mw().visitJumpInsn(Opcodes.IF_ICMPGT, bothEqualDestination);
          mw().visitInsn(ICONST_0);
          mw().visitJumpInsn(GOTO, finalDestination);
          mw().visitLabel(bothEqualDestination);
          mw().visitInsn(ICONST_1);
          mw().visitLabel(finalDestination);
        }
        case GlobalData.double$name -> {
          var finalDestination = new Label();
          var bothEqualDestination = new Label();
          ctx.e1.visit(GlobalData.fs6);
          mw().visitInsn(Opcodes.I2D);
          ctx.e2.visit(GlobalData.fs6);
          mw().visitInsn(DCMPG);
          mw().visitJumpInsn(IFGT, bothEqualDestination);
          mw().visitInsn(ICONST_0);
          mw().visitJumpInsn(GOTO, finalDestination);
          mw().visitLabel(bothEqualDestination);
          mw().visitInsn(ICONST_1);
          mw().visitLabel(finalDestination);
        }
        case GlobalData.float$name -> {
          var finalDestination = new Label();
          var bothEqualDestination = new Label();
          ctx.e1.visit(GlobalData.fs6);
          mw().visitInsn(Opcodes.I2F);
          ctx.e2.visit(GlobalData.fs6);
          mw().visitInsn(FCMPG);
          mw().visitJumpInsn(IFGT, bothEqualDestination);
          mw().visitInsn(ICONST_0);
          mw().visitJumpInsn(GOTO, finalDestination);
          mw().visitLabel(bothEqualDestination);
          mw().visitInsn(ICONST_1);
          mw().visitLabel(finalDestination);
        }
        case GlobalData.long$name -> {
          var finalDestination = new Label();
          var bothEqualDestination = new Label();
          ctx.e1.visit(GlobalData.fs6);
          mw().visitInsn(Opcodes.I2L);
          ctx.e2.visit(GlobalData.fs6);
          mw().visitInsn(LCMP);
          mw().visitJumpInsn(IFGT, bothEqualDestination);
          mw().visitInsn(ICONST_0);
          mw().visitJumpInsn(GOTO, finalDestination);
          mw().visitLabel(bothEqualDestination);
          mw().visitInsn(ICONST_1);
          mw().visitLabel(finalDestination);
        }
      }
    }
    if (owner.equals(GlobalData.float$name)) {
      switch (right.name) {
        case GlobalData.float$name -> {
          var finalDestination = new Label();
          var bothEqualDestination = new Label();
          ctx.e1.visit(GlobalData.fs6);
          ctx.e2.visit(GlobalData.fs6);
          mw().visitInsn(FCMPG);
          mw().visitJumpInsn(IFGT, bothEqualDestination);
          mw().visitInsn(ICONST_0);
          mw().visitJumpInsn(GOTO, finalDestination);
          mw().visitLabel(bothEqualDestination);
          mw().visitInsn(ICONST_1);
          mw().visitLabel(finalDestination);
        }
        case GlobalData.double$name -> {
          var finalDestination = new Label();
          var bothEqualDestination = new Label();
          ctx.e1.visit(GlobalData.fs6);
          mw().visitInsn(Opcodes.F2D);
          ctx.e2.visit(GlobalData.fs6);
          mw().visitInsn(DCMPG);
          mw().visitJumpInsn(IFGT, bothEqualDestination);
          mw().visitInsn(ICONST_0);
          mw().visitJumpInsn(GOTO, finalDestination);
          mw().visitLabel(bothEqualDestination);
          mw().visitInsn(ICONST_1);
          mw().visitLabel(finalDestination);
        }
        case GlobalData.int$name -> {
          var finalDestination = new Label();
          var bothEqualDestination = new Label();
          ctx.e1.visit(GlobalData.fs6);
          ctx.e2.visit(GlobalData.fs6);
          mw().visitInsn(Opcodes.I2F);
          mw().visitInsn(FCMPG);
          mw().visitJumpInsn(IFGT, bothEqualDestination);
          mw().visitInsn(ICONST_0);
          mw().visitJumpInsn(GOTO, finalDestination);
          mw().visitLabel(bothEqualDestination);
          mw().visitInsn(ICONST_1);
          mw().visitLabel(finalDestination);
        }
        case GlobalData.long$name -> {
          var finalDestination = new Label();
          var bothEqualDestination = new Label();
          ctx.e1.visit(GlobalData.fs6);
          ctx.e2.visit(GlobalData.fs6);
          mw().visitInsn(Opcodes.L2F);
          mw().visitInsn(FCMPG);
          mw().visitJumpInsn(IFGT, bothEqualDestination);
          mw().visitInsn(ICONST_0);
          mw().visitJumpInsn(GOTO, finalDestination);
          mw().visitLabel(bothEqualDestination);
          mw().visitInsn(ICONST_1);
          mw().visitLabel(finalDestination);
        }
      }
    }
    if (owner.equals(GlobalData.double$name)) {
      switch (right.name) {
        case GlobalData.double$name -> {
          var finalDestination = new Label();
          var bothEqualDestination = new Label();
          ctx.e1.visit(GlobalData.fs6);
          ctx.e2.visit(GlobalData.fs6);
          mw().visitInsn(DCMPG);
          mw().visitJumpInsn(IFGT, bothEqualDestination);
          mw().visitInsn(ICONST_0);
          mw().visitJumpInsn(GOTO, finalDestination);
          mw().visitLabel(bothEqualDestination);
          mw().visitInsn(ICONST_1);
          mw().visitLabel(finalDestination);
        }
        case GlobalData.float$name -> {
          var finalDestination = new Label();
          var bothEqualDestination = new Label();
          ctx.e1.visit(GlobalData.fs6);
          ctx.e2.visit(GlobalData.fs6);
          mw().visitInsn(Opcodes.F2D);
          mw().visitInsn(DCMPG);
          mw().visitJumpInsn(IFGT, bothEqualDestination);
          mw().visitInsn(ICONST_0);
          mw().visitJumpInsn(GOTO, finalDestination);
          mw().visitLabel(bothEqualDestination);
          mw().visitInsn(ICONST_1);
          mw().visitLabel(finalDestination);
        }
        case GlobalData.int$name -> {
          var finalDestination = new Label();
          var bothEqualDestination = new Label();
          ctx.e1.visit(GlobalData.fs6);
          ctx.e2.visit(GlobalData.fs6);
          mw().visitInsn(Opcodes.I2D);
          mw().visitInsn(DCMPG);
          mw().visitJumpInsn(IFGT, bothEqualDestination);
          mw().visitInsn(ICONST_0);
          mw().visitJumpInsn(GOTO, finalDestination);
          mw().visitLabel(bothEqualDestination);
          mw().visitInsn(ICONST_1);
          mw().visitLabel(finalDestination);
        }
        case GlobalData.long$name -> {
          var finalDestination = new Label();
          var bothEqualDestination = new Label();
          ctx.e1.visit(GlobalData.fs6);
          ctx.e2.visit(GlobalData.fs6);
          mw().visitInsn(Opcodes.L2D);
          mw().visitInsn(DCMPG);
          mw().visitJumpInsn(IFGT, bothEqualDestination);
          mw().visitInsn(ICONST_0);
          mw().visitJumpInsn(GOTO, finalDestination);
          mw().visitLabel(bothEqualDestination);
          mw().visitInsn(ICONST_1);
          mw().visitLabel(finalDestination);
        }
      }
    }
    if (owner.equals(GlobalData.long$name)) {
      switch (right.name) {
        case GlobalData.long$name -> {
          var finalDestination = new Label();
          var bothEqualDestination = new Label();
          ctx.e1.visit(GlobalData.fs6);
          ctx.e2.visit(GlobalData.fs6);
          mw().visitInsn(LCMP);
          mw().visitJumpInsn(IFGT, bothEqualDestination);
          mw().visitInsn(ICONST_0);
          mw().visitJumpInsn(GOTO, finalDestination);
          mw().visitLabel(bothEqualDestination);
          mw().visitInsn(ICONST_1);
          mw().visitLabel(finalDestination);
        }
        case GlobalData.double$name -> {
          var finalDestination = new Label();
          var bothEqualDestination = new Label();
          ctx.e1.visit(GlobalData.fs6);
          mw().visitInsn(Opcodes.L2D);
          ctx.e2.visit(GlobalData.fs6);
          mw().visitInsn(DCMPG);
          mw().visitJumpInsn(IFGT, bothEqualDestination);
          mw().visitInsn(ICONST_0);
          mw().visitJumpInsn(GOTO, finalDestination);
          mw().visitLabel(bothEqualDestination);
          mw().visitInsn(ICONST_1);
          mw().visitLabel(finalDestination);
        }
        case GlobalData.float$name -> {
          var finalDestination = new Label();
          var bothEqualDestination = new Label();
          ctx.e1.visit(GlobalData.fs6);
          mw().visitInsn(Opcodes.L2F);
          ctx.e2.visit(GlobalData.fs6);
          mw().visitInsn(FCMPG);
          mw().visitJumpInsn(IFGT, bothEqualDestination);
          mw().visitInsn(ICONST_0);
          mw().visitJumpInsn(GOTO, finalDestination);
          mw().visitLabel(bothEqualDestination);
          mw().visitInsn(ICONST_1);
          mw().visitLabel(finalDestination);
        }
        case GlobalData.int$name -> {
          var finalDestination = new Label();
          var bothEqualDestination = new Label();
          ctx.e1.visit(GlobalData.fs6);
          ctx.e2.visit(GlobalData.fs6);
          mw().visitInsn(Opcodes.I2L);
          mw().visitInsn(LCMP);
          mw().visitJumpInsn(IFGT, bothEqualDestination);
          mw().visitInsn(ICONST_0);
          mw().visitJumpInsn(GOTO, finalDestination);
          mw().visitLabel(bothEqualDestination);
          mw().visitInsn(ICONST_1);
          mw().visitLabel(finalDestination);
        }
      }
    }
    return mtd.type;
  }
}
