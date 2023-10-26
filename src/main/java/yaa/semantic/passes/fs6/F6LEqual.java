package yaa.semantic.passes.fs6;

import yaa.ast.LEqual;
import yaa.semantic.passes.fs6.results.CallResult;
import yaa.pojos.YaaInfo;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;

import static yaa.pojos.GlobalData.*;
import static yaa.semantic.passes.fs6.F6Utils.itIsPrimitive;
import static org.objectweb.asm.Opcodes.*;

public class F6LEqual {
  public static YaaInfo lEqual(LEqual ctx) {
    var mtd = ((CallResult) results.get(fs6.path).get(ctx)).mtd;
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
          ctx.e1.visit(fs6);
          ctx.e2.visit(fs6);
          F6.mw().visitJumpInsn(Opcodes.IF_ICMPLE, bothEqualDestination);
          F6.mw().visitInsn(ICONST_0);
          F6.mw().visitJumpInsn(GOTO, finalDestination);
          F6.mw().visitLabel(bothEqualDestination);
          F6.mw().visitInsn(ICONST_1);
          F6.mw().visitLabel(finalDestination);
        }
        case double$name -> {
          var finalDestination = new Label();
          var bothEqualDestination = new Label();
          ctx.e1.visit(fs6);
          F6.mw().visitInsn(Opcodes.I2D);
          ctx.e2.visit(fs6);
          F6.mw().visitInsn(DCMPG);
          F6.mw().visitJumpInsn(IFLE, bothEqualDestination);
          F6.mw().visitInsn(ICONST_0);
          F6.mw().visitJumpInsn(GOTO, finalDestination);
          F6.mw().visitLabel(bothEqualDestination);
          F6.mw().visitInsn(ICONST_1);
          F6.mw().visitLabel(finalDestination);
        }
        case float$name -> {
          var finalDestination = new Label();
          var bothEqualDestination = new Label();
          ctx.e1.visit(fs6);
          F6.mw().visitInsn(Opcodes.I2F);
          ctx.e2.visit(fs6);
          F6.mw().visitInsn(FCMPG);
          F6.mw().visitJumpInsn(IFLE, bothEqualDestination);
          F6.mw().visitInsn(ICONST_0);
          F6.mw().visitJumpInsn(GOTO, finalDestination);
          F6.mw().visitLabel(bothEqualDestination);
          F6.mw().visitInsn(ICONST_1);
          F6.mw().visitLabel(finalDestination);
        }
        case long$name -> {
          var finalDestination = new Label();
          var bothEqualDestination = new Label();
          ctx.e1.visit(fs6);
          F6.mw().visitInsn(Opcodes.I2L);
          ctx.e2.visit(fs6);
          F6.mw().visitInsn(LCMP);
          F6.mw().visitJumpInsn(IFLE, bothEqualDestination);
          F6.mw().visitInsn(ICONST_0);
          F6.mw().visitJumpInsn(GOTO, finalDestination);
          F6.mw().visitLabel(bothEqualDestination);
          F6.mw().visitInsn(ICONST_1);
          F6.mw().visitLabel(finalDestination);
        }
      }
    }
    if (owner.equals(float$name)) {
      switch (right.name) {
        case float$name -> {
          var finalDestination = new Label();
          var bothEqualDestination = new Label();
          ctx.e1.visit(fs6);
          ctx.e2.visit(fs6);
          F6.mw().visitInsn(FCMPG);
          F6.mw().visitJumpInsn(IFLE, bothEqualDestination);
          F6.mw().visitInsn(ICONST_0);
          F6.mw().visitJumpInsn(GOTO, finalDestination);
          F6.mw().visitLabel(bothEqualDestination);
          F6.mw().visitInsn(ICONST_1);
          F6.mw().visitLabel(finalDestination);
        }
        case double$name -> {
          var finalDestination = new Label();
          var bothEqualDestination = new Label();
          ctx.e1.visit(fs6);
          F6.mw().visitInsn(Opcodes.F2D);
          ctx.e2.visit(fs6);
          F6.mw().visitInsn(DCMPG);
          F6.mw().visitJumpInsn(IFLE, bothEqualDestination);
          F6.mw().visitInsn(ICONST_0);
          F6.mw().visitJumpInsn(GOTO, finalDestination);
          F6.mw().visitLabel(bothEqualDestination);
          F6.mw().visitInsn(ICONST_1);
          F6.mw().visitLabel(finalDestination);
        }
        case int$name -> {
          var finalDestination = new Label();
          var bothEqualDestination = new Label();
          ctx.e1.visit(fs6);
          ctx.e2.visit(fs6);
          F6.mw().visitInsn(Opcodes.I2F);
          F6.mw().visitInsn(FCMPG);
          F6.mw().visitJumpInsn(IFLE, bothEqualDestination);
          F6.mw().visitInsn(ICONST_0);
          F6.mw().visitJumpInsn(GOTO, finalDestination);
          F6.mw().visitLabel(bothEqualDestination);
          F6.mw().visitInsn(ICONST_1);
          F6.mw().visitLabel(finalDestination);
        }
        case long$name -> {
          var finalDestination = new Label();
          var bothEqualDestination = new Label();
          ctx.e1.visit(fs6);
          ctx.e2.visit(fs6);
          F6.mw().visitInsn(Opcodes.L2F);
          F6.mw().visitInsn(FCMPG);
          F6.mw().visitJumpInsn(IFLE, bothEqualDestination);
          F6.mw().visitInsn(ICONST_0);
          F6.mw().visitJumpInsn(GOTO, finalDestination);
          F6.mw().visitLabel(bothEqualDestination);
          F6.mw().visitInsn(ICONST_1);
          F6.mw().visitLabel(finalDestination);
        }
      }
    }
    if (owner.equals(double$name)) {
      switch (right.name) {
        case double$name -> {
          var finalDestination = new Label();
          var bothEqualDestination = new Label();
          ctx.e1.visit(fs6);
          ctx.e2.visit(fs6);
          F6.mw().visitInsn(DCMPG);
          F6.mw().visitJumpInsn(IFLE, bothEqualDestination);
          F6.mw().visitInsn(ICONST_0);
          F6.mw().visitJumpInsn(GOTO, finalDestination);
          F6.mw().visitLabel(bothEqualDestination);
          F6.mw().visitInsn(ICONST_1);
          F6.mw().visitLabel(finalDestination);
        }
        case float$name -> {
          var finalDestination = new Label();
          var bothEqualDestination = new Label();
          ctx.e1.visit(fs6);
          ctx.e2.visit(fs6);
          F6.mw().visitInsn(Opcodes.F2D);
          F6.mw().visitInsn(DCMPG);
          F6.mw().visitJumpInsn(IFLE, bothEqualDestination);
          F6.mw().visitInsn(ICONST_0);
          F6.mw().visitJumpInsn(GOTO, finalDestination);
          F6.mw().visitLabel(bothEqualDestination);
          F6.mw().visitInsn(ICONST_1);
          F6.mw().visitLabel(finalDestination);
        }
        case int$name -> {
          var finalDestination = new Label();
          var bothEqualDestination = new Label();
          ctx.e1.visit(fs6);
          ctx.e2.visit(fs6);
          F6.mw().visitInsn(Opcodes.I2D);
          F6.mw().visitInsn(DCMPG);
          F6.mw().visitJumpInsn(IFLE, bothEqualDestination);
          F6.mw().visitInsn(ICONST_0);
          F6.mw().visitJumpInsn(GOTO, finalDestination);
          F6.mw().visitLabel(bothEqualDestination);
          F6.mw().visitInsn(ICONST_1);
          F6.mw().visitLabel(finalDestination);
        }
        case long$name -> {
          var finalDestination = new Label();
          var bothEqualDestination = new Label();
          ctx.e1.visit(fs6);
          ctx.e2.visit(fs6);
          F6.mw().visitInsn(Opcodes.L2D);
          F6.mw().visitInsn(DCMPG);
          F6.mw().visitJumpInsn(IFLE, bothEqualDestination);
          F6.mw().visitInsn(ICONST_0);
          F6.mw().visitJumpInsn(GOTO, finalDestination);
          F6.mw().visitLabel(bothEqualDestination);
          F6.mw().visitInsn(ICONST_1);
          F6.mw().visitLabel(finalDestination);
        }
      }
    }
    if (owner.equals(long$name)) {
      switch (right.name) {
        case long$name -> {
          var finalDestination = new Label();
          var bothEqualDestination = new Label();
          ctx.e1.visit(fs6);
          ctx.e2.visit(fs6);
          F6.mw().visitInsn(LCMP);
          F6.mw().visitJumpInsn(IFLE, bothEqualDestination);
          F6.mw().visitInsn(ICONST_0);
          F6.mw().visitJumpInsn(GOTO, finalDestination);
          F6.mw().visitLabel(bothEqualDestination);
          F6.mw().visitInsn(ICONST_1);
          F6.mw().visitLabel(finalDestination);
        }
        case double$name -> {
          var finalDestination = new Label();
          var bothEqualDestination = new Label();
          ctx.e1.visit(fs6);
          F6.mw().visitInsn(Opcodes.L2D);
          ctx.e2.visit(fs6);
          F6.mw().visitInsn(DCMPG);
          F6.mw().visitJumpInsn(IFLE, bothEqualDestination);
          F6.mw().visitInsn(ICONST_0);
          F6.mw().visitJumpInsn(GOTO, finalDestination);
          F6.mw().visitLabel(bothEqualDestination);
          F6.mw().visitInsn(ICONST_1);
          F6.mw().visitLabel(finalDestination);
        }
        case float$name -> {
          var finalDestination = new Label();
          var bothEqualDestination = new Label();
          ctx.e1.visit(fs6);
          F6.mw().visitInsn(Opcodes.L2F);
          ctx.e2.visit(fs6);
          F6.mw().visitInsn(FCMPG);
          F6.mw().visitJumpInsn(IFLE, bothEqualDestination);
          F6.mw().visitInsn(ICONST_0);
          F6.mw().visitJumpInsn(GOTO, finalDestination);
          F6.mw().visitLabel(bothEqualDestination);
          F6.mw().visitInsn(ICONST_1);
          F6.mw().visitLabel(finalDestination);
        }
        case int$name -> {
          var finalDestination = new Label();
          var bothEqualDestination = new Label();
          ctx.e1.visit(fs6);
          ctx.e2.visit(fs6);
          F6.mw().visitInsn(Opcodes.I2L);
          F6.mw().visitInsn(LCMP);
          F6.mw().visitJumpInsn(IFLE, bothEqualDestination);
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
