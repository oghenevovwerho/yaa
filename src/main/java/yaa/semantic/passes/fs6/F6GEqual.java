package yaa.semantic.passes.fs6;

import yaa.ast.GEqual;
import yaa.semantic.passes.fs6.results.CallResult;
import yaa.pojos.YaaInfo;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import yaa.pojos.GlobalData;

import static yaa.pojos.GlobalData.byte$name;
import static yaa.pojos.GlobalData.char$name;
import static yaa.pojos.GlobalData.int$name;
import static yaa.pojos.GlobalData.short$name;
import static yaa.semantic.passes.fs6.F6Utils.itIsPrimitive;
import static org.objectweb.asm.Opcodes.*;
import static org.objectweb.asm.Opcodes.ICONST_1;

public class F6GEqual {
  public static YaaInfo gEqual(GEqual ctx) {
    var mtd = ((CallResult) GlobalData.results.get(GlobalData.fs6.path).get(ctx)).mtd;
    var owner = mtd.owner;
    var right = mtd.parameters.get(0);

    if (!F6Utils.itIsPrimitive(owner)) {
      return F6Utils.operatorMtd(ctx.e1, ctx.e2, mtd);
    }

    if (owner.equals(int$name) || owner.equals(short$name) ||
      owner.equals(char$name) || owner.equals(byte$name)) {
      var rightName = right.name;
      switch (rightName) {
        case int$name, char$name, byte$name, short$name -> {
          var bothEqualDestination = new Label();
          var finalDestination = new Label();
          ctx.e1.visit(GlobalData.fs6);
          ctx.e2.visit(GlobalData.fs6);
          F6.mw().visitJumpInsn(Opcodes.IF_ICMPGE, bothEqualDestination);
          F6.mw().visitInsn(ICONST_0);
          F6.mw().visitJumpInsn(GOTO, finalDestination);
          F6.mw().visitLabel(bothEqualDestination);
          F6.mw().visitInsn(ICONST_1);
          F6.mw().visitLabel(finalDestination);
        }
        case GlobalData.double$name -> {
          var finalDestination = new Label();
          var bothEqualDestination = new Label();
          ctx.e1.visit(GlobalData.fs6);
          F6.mw().visitInsn(Opcodes.I2D);
          ctx.e2.visit(GlobalData.fs6);
          F6.mw().visitInsn(DCMPG);
          F6.mw().visitJumpInsn(IFGE, bothEqualDestination);
          F6.mw().visitInsn(ICONST_0);
          F6.mw().visitJumpInsn(GOTO, finalDestination);
          F6.mw().visitLabel(bothEqualDestination);
          F6.mw().visitInsn(ICONST_1);
          F6.mw().visitLabel(finalDestination);
        }
        case GlobalData.float$name -> {
          var finalDestination = new Label();
          var bothEqualDestination = new Label();
          ctx.e1.visit(GlobalData.fs6);
          F6.mw().visitInsn(Opcodes.I2F);
          ctx.e2.visit(GlobalData.fs6);
          F6.mw().visitInsn(FCMPG);
          F6.mw().visitJumpInsn(IFGE, bothEqualDestination);
          F6.mw().visitInsn(ICONST_0);
          F6.mw().visitJumpInsn(GOTO, finalDestination);
          F6.mw().visitLabel(bothEqualDestination);
          F6.mw().visitInsn(ICONST_1);
          F6.mw().visitLabel(finalDestination);
        }
        case GlobalData.long$name -> {
          var finalDestination = new Label();
          var bothEqualDestination = new Label();
          ctx.e1.visit(GlobalData.fs6);
          F6.mw().visitInsn(Opcodes.I2L);
          ctx.e2.visit(GlobalData.fs6);
          F6.mw().visitInsn(LCMP);
          F6.mw().visitJumpInsn(IFGE, bothEqualDestination);
          F6.mw().visitInsn(ICONST_0);
          F6.mw().visitJumpInsn(GOTO, finalDestination);
          F6.mw().visitLabel(bothEqualDestination);
          F6.mw().visitInsn(ICONST_1);
          F6.mw().visitLabel(finalDestination);
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
          F6.mw().visitInsn(FCMPG);
          F6.mw().visitJumpInsn(IFGE, bothEqualDestination);
          F6.mw().visitInsn(ICONST_0);
          F6.mw().visitJumpInsn(GOTO, finalDestination);
          F6.mw().visitLabel(bothEqualDestination);
          F6.mw().visitInsn(ICONST_1);
          F6.mw().visitLabel(finalDestination);
        }
        case GlobalData.double$name -> {
          var finalDestination = new Label();
          var bothEqualDestination = new Label();
          ctx.e1.visit(GlobalData.fs6);
          F6.mw().visitInsn(Opcodes.F2D);
          ctx.e2.visit(GlobalData.fs6);
          F6.mw().visitInsn(DCMPG);
          F6.mw().visitJumpInsn(IFGE, bothEqualDestination);
          F6.mw().visitInsn(ICONST_0);
          F6.mw().visitJumpInsn(GOTO, finalDestination);
          F6.mw().visitLabel(bothEqualDestination);
          F6.mw().visitInsn(ICONST_1);
          F6.mw().visitLabel(finalDestination);
        }
        case GlobalData.int$name -> {
          var finalDestination = new Label();
          var bothEqualDestination = new Label();
          ctx.e1.visit(GlobalData.fs6);
          ctx.e2.visit(GlobalData.fs6);
          F6.mw().visitInsn(Opcodes.I2F);
          F6.mw().visitInsn(FCMPG);
          F6.mw().visitJumpInsn(IFGE, bothEqualDestination);
          F6.mw().visitInsn(ICONST_0);
          F6.mw().visitJumpInsn(GOTO, finalDestination);
          F6.mw().visitLabel(bothEqualDestination);
          F6.mw().visitInsn(ICONST_1);
          F6.mw().visitLabel(finalDestination);
        }
        case GlobalData.long$name -> {
          var finalDestination = new Label();
          var bothEqualDestination = new Label();
          ctx.e1.visit(GlobalData.fs6);
          ctx.e2.visit(GlobalData.fs6);
          F6.mw().visitInsn(Opcodes.L2F);
          F6.mw().visitInsn(FCMPG);
          F6.mw().visitJumpInsn(IFGE, bothEqualDestination);
          F6.mw().visitInsn(ICONST_0);
          F6.mw().visitJumpInsn(GOTO, finalDestination);
          F6.mw().visitLabel(bothEqualDestination);
          F6.mw().visitInsn(ICONST_1);
          F6.mw().visitLabel(finalDestination);
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
          F6.mw().visitInsn(DCMPG);
          F6.mw().visitJumpInsn(IFGE, bothEqualDestination);
          F6.mw().visitInsn(ICONST_0);
          F6.mw().visitJumpInsn(GOTO, finalDestination);
          F6.mw().visitLabel(bothEqualDestination);
          F6.mw().visitInsn(ICONST_1);
          F6.mw().visitLabel(finalDestination);
        }
        case GlobalData.float$name -> {
          var finalDestination = new Label();
          var bothEqualDestination = new Label();
          ctx.e1.visit(GlobalData.fs6);
          ctx.e2.visit(GlobalData.fs6);
          F6.mw().visitInsn(Opcodes.F2D);
          F6.mw().visitInsn(DCMPG);
          F6.mw().visitJumpInsn(IFGE, bothEqualDestination);
          F6.mw().visitInsn(ICONST_0);
          F6.mw().visitJumpInsn(GOTO, finalDestination);
          F6.mw().visitLabel(bothEqualDestination);
          F6.mw().visitInsn(ICONST_1);
          F6.mw().visitLabel(finalDestination);
        }
        case GlobalData.int$name -> {
          var finalDestination = new Label();
          var bothEqualDestination = new Label();
          ctx.e1.visit(GlobalData.fs6);
          ctx.e2.visit(GlobalData.fs6);
          F6.mw().visitInsn(Opcodes.I2D);
          F6.mw().visitInsn(DCMPG);
          F6.mw().visitJumpInsn(IFGE, bothEqualDestination);
          F6.mw().visitInsn(ICONST_0);
          F6.mw().visitJumpInsn(GOTO, finalDestination);
          F6.mw().visitLabel(bothEqualDestination);
          F6.mw().visitInsn(ICONST_1);
          F6.mw().visitLabel(finalDestination);
        }
        case GlobalData.long$name -> {
          var finalDestination = new Label();
          var bothEqualDestination = new Label();
          ctx.e1.visit(GlobalData.fs6);
          ctx.e2.visit(GlobalData.fs6);
          F6.mw().visitInsn(Opcodes.L2D);
          F6.mw().visitInsn(DCMPG);
          F6.mw().visitJumpInsn(IFGE, bothEqualDestination);
          F6.mw().visitInsn(ICONST_0);
          F6.mw().visitJumpInsn(GOTO, finalDestination);
          F6.mw().visitLabel(bothEqualDestination);
          F6.mw().visitInsn(ICONST_1);
          F6.mw().visitLabel(finalDestination);
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
          F6.mw().visitInsn(LCMP);
          F6.mw().visitJumpInsn(IFGE, bothEqualDestination);
          F6.mw().visitInsn(ICONST_0);
          F6.mw().visitJumpInsn(GOTO, finalDestination);
          F6.mw().visitLabel(bothEqualDestination);
          F6.mw().visitInsn(ICONST_1);
          F6.mw().visitLabel(finalDestination);
        }
        case GlobalData.double$name -> {
          var finalDestination = new Label();
          var bothEqualDestination = new Label();
          ctx.e1.visit(GlobalData.fs6);
          F6.mw().visitInsn(Opcodes.L2D);
          ctx.e2.visit(GlobalData.fs6);
          F6.mw().visitInsn(DCMPG);
          F6.mw().visitJumpInsn(IFGE, bothEqualDestination);
          F6.mw().visitInsn(ICONST_0);
          F6.mw().visitJumpInsn(GOTO, finalDestination);
          F6.mw().visitLabel(bothEqualDestination);
          F6.mw().visitInsn(ICONST_1);
          F6.mw().visitLabel(finalDestination);
        }
        case GlobalData.float$name -> {
          var finalDestination = new Label();
          var bothEqualDestination = new Label();
          ctx.e1.visit(GlobalData.fs6);
          F6.mw().visitInsn(Opcodes.L2F);
          ctx.e2.visit(GlobalData.fs6);
          F6.mw().visitInsn(FCMPG);
          F6.mw().visitJumpInsn(IFGE, bothEqualDestination);
          F6.mw().visitInsn(ICONST_0);
          F6.mw().visitJumpInsn(GOTO, finalDestination);
          F6.mw().visitLabel(bothEqualDestination);
          F6.mw().visitInsn(ICONST_1);
          F6.mw().visitLabel(finalDestination);
        }
        case GlobalData.int$name -> {
          var finalDestination = new Label();
          var bothEqualDestination = new Label();
          ctx.e1.visit(GlobalData.fs6);
          ctx.e2.visit(GlobalData.fs6);
          F6.mw().visitInsn(Opcodes.I2L);
          F6.mw().visitInsn(LCMP);
          F6.mw().visitJumpInsn(IFGE, bothEqualDestination);
          F6.mw().visitInsn(ICONST_0);
          F6.mw().visitJumpInsn(GOTO, finalDestination);
          F6.mw().visitLabel(bothEqualDestination);
          F6.mw().visitInsn(ICONST_1);
          F6.mw().visitLabel(finalDestination);
        }
      }
    }
    return mtd.type;
  }
}
