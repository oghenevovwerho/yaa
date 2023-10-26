package yaa.semantic.passes.fs6;

import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import yaa.ast.Stmt;

import static org.objectweb.asm.Opcodes.*;
import static yaa.pojos.GlobalData.*;
import static yaa.semantic.passes.fs6.F6.mw;

public class F6EqualUtils {
  public static void doEquals(Stmt e1, Stmt e2, String owner, String rightName) {
    if (owner.equals(int$name) || owner.equals(short$name) ||
        owner.equals(char$name) || owner.equals(byte$name)) {
      switch (rightName) {
        case int$name, char$name, byte$name, short$name -> {
          var bothEqualDestination = new Label();
          var finalDestination = new Label();
          e1.visit(fs6);
          e2.visit(fs6);
          mw().visitJumpInsn(Opcodes.IF_ICMPEQ, bothEqualDestination);
          mw().visitInsn(ICONST_0);
          mw().visitJumpInsn(GOTO, finalDestination);
          mw().visitLabel(bothEqualDestination);
          mw().visitInsn(ICONST_1);
          mw().visitLabel(finalDestination);
        }
        case double$name -> {
          var finalDestination = new Label();
          var bothEqualDestination = new Label();
          e1.visit(fs6);
          mw().visitInsn(Opcodes.I2D);
          e2.visit(fs6);
          mw().visitInsn(DCMPG);
          mw().visitJumpInsn(IFEQ, bothEqualDestination);
          mw().visitInsn(ICONST_0);
          mw().visitJumpInsn(GOTO, finalDestination);
          mw().visitLabel(bothEqualDestination);
          mw().visitInsn(ICONST_1);
          mw().visitLabel(finalDestination);
        }
        case float$name -> {
          var finalDestination = new Label();
          var bothEqualDestination = new Label();
          e1.visit(fs6);
          mw().visitInsn(Opcodes.I2F);
          e2.visit(fs6);
          mw().visitInsn(FCMPG);
          mw().visitJumpInsn(IFEQ, bothEqualDestination);
          mw().visitInsn(ICONST_0);
          mw().visitJumpInsn(GOTO, finalDestination);
          mw().visitLabel(bothEqualDestination);
          mw().visitInsn(ICONST_1);
          mw().visitLabel(finalDestination);
        }
        case long$name -> {
          var finalDestination = new Label();
          var bothEqualDestination = new Label();
          e1.visit(fs6);
          mw().visitInsn(Opcodes.I2L);
          e2.visit(fs6);
          mw().visitInsn(LCMP);
          mw().visitJumpInsn(IFEQ, bothEqualDestination);
          mw().visitInsn(ICONST_0);
          mw().visitJumpInsn(GOTO, finalDestination);
          mw().visitLabel(bothEqualDestination);
          mw().visitInsn(ICONST_1);
          mw().visitLabel(finalDestination);
        }
      }
    }
    if (owner.equals(float$name)) {
      switch (rightName) {
        case float$name -> {
          var finalDestination = new Label();
          var bothEqualDestination = new Label();
          e1.visit(fs6);
          e2.visit(fs6);
          mw().visitInsn(FCMPG);
          mw().visitJumpInsn(IFEQ, bothEqualDestination);
          mw().visitInsn(ICONST_0);
          mw().visitJumpInsn(GOTO, finalDestination);
          mw().visitLabel(bothEqualDestination);
          mw().visitInsn(ICONST_1);
          mw().visitLabel(finalDestination);
        }
        case double$name -> {
          var finalDestination = new Label();
          var bothEqualDestination = new Label();
          e1.visit(fs6);
          mw().visitInsn(Opcodes.F2D);
          e2.visit(fs6);
          mw().visitInsn(DCMPG);
          mw().visitJumpInsn(IFEQ, bothEqualDestination);
          mw().visitInsn(ICONST_0);
          mw().visitJumpInsn(GOTO, finalDestination);
          mw().visitLabel(bothEqualDestination);
          mw().visitInsn(ICONST_1);
          mw().visitLabel(finalDestination);
        }
        case int$name -> {
          var finalDestination = new Label();
          var bothEqualDestination = new Label();
          e1.visit(fs6);
          e2.visit(fs6);
          mw().visitInsn(Opcodes.I2F);
          mw().visitInsn(FCMPG);
          mw().visitJumpInsn(IFEQ, bothEqualDestination);
          mw().visitInsn(ICONST_0);
          mw().visitJumpInsn(GOTO, finalDestination);
          mw().visitLabel(bothEqualDestination);
          mw().visitInsn(ICONST_1);
          mw().visitLabel(finalDestination);
        }
        case long$name -> {
          var finalDestination = new Label();
          var bothEqualDestination = new Label();
          e1.visit(fs6);
          e2.visit(fs6);
          mw().visitInsn(Opcodes.L2F);
          mw().visitInsn(FCMPG);
          mw().visitJumpInsn(IFEQ, bothEqualDestination);
          mw().visitInsn(ICONST_0);
          mw().visitJumpInsn(GOTO, finalDestination);
          mw().visitLabel(bothEqualDestination);
          mw().visitInsn(ICONST_1);
          mw().visitLabel(finalDestination);
        }
      }
    }
    if (owner.equals(double$name)) {
      switch (rightName) {
        case double$name -> {
          var finalDestination = new Label();
          var bothEqualDestination = new Label();
          e1.visit(fs6);
          e2.visit(fs6);
          mw().visitInsn(DCMPG);
          mw().visitJumpInsn(IFEQ, bothEqualDestination);
          mw().visitInsn(ICONST_0);
          mw().visitJumpInsn(GOTO, finalDestination);
          mw().visitLabel(bothEqualDestination);
          mw().visitInsn(ICONST_1);
          mw().visitLabel(finalDestination);
        }
        case float$name -> {
          var finalDestination = new Label();
          var bothEqualDestination = new Label();
          e1.visit(fs6);
          e2.visit(fs6);
          mw().visitInsn(Opcodes.F2D);
          mw().visitInsn(DCMPG);
          mw().visitJumpInsn(IFEQ, bothEqualDestination);
          mw().visitInsn(ICONST_0);
          mw().visitJumpInsn(GOTO, finalDestination);
          mw().visitLabel(bothEqualDestination);
          mw().visitInsn(ICONST_1);
          mw().visitLabel(finalDestination);
        }
        case int$name -> {
          var finalDestination = new Label();
          var bothEqualDestination = new Label();
          e1.visit(fs6);
          e2.visit(fs6);
          mw().visitInsn(Opcodes.I2D);
          mw().visitInsn(DCMPG);
          mw().visitJumpInsn(IFEQ, bothEqualDestination);
          mw().visitInsn(ICONST_0);
          mw().visitJumpInsn(GOTO, finalDestination);
          mw().visitLabel(bothEqualDestination);
          mw().visitInsn(ICONST_1);
          mw().visitLabel(finalDestination);
        }
        case long$name -> {
          var finalDestination = new Label();
          var bothEqualDestination = new Label();
          e1.visit(fs6);
          e2.visit(fs6);
          mw().visitInsn(Opcodes.L2D);
          mw().visitInsn(DCMPG);
          mw().visitJumpInsn(IFEQ, bothEqualDestination);
          mw().visitInsn(ICONST_0);
          mw().visitJumpInsn(GOTO, finalDestination);
          mw().visitLabel(bothEqualDestination);
          mw().visitInsn(ICONST_1);
          mw().visitLabel(finalDestination);
        }
      }
    }
    if (owner.equals(long$name)) {
      switch (rightName) {
        case long$name -> {
          var finalDestination = new Label();
          var bothEqualDestination = new Label();
          e1.visit(fs6);
          e2.visit(fs6);
          mw().visitInsn(LCMP);
          mw().visitJumpInsn(IFEQ, bothEqualDestination);
          mw().visitInsn(ICONST_0);
          mw().visitJumpInsn(GOTO, finalDestination);
          mw().visitLabel(bothEqualDestination);
          mw().visitInsn(ICONST_1);
          mw().visitLabel(finalDestination);
        }
        case double$name -> {
          var finalDestination = new Label();
          var bothEqualDestination = new Label();
          e1.visit(fs6);
          mw().visitInsn(Opcodes.L2D);
          e2.visit(fs6);
          mw().visitInsn(DCMPG);
          mw().visitJumpInsn(IFEQ, bothEqualDestination);
          mw().visitInsn(ICONST_0);
          mw().visitJumpInsn(GOTO, finalDestination);
          mw().visitLabel(bothEqualDestination);
          mw().visitInsn(ICONST_1);
          mw().visitLabel(finalDestination);
        }
        case float$name -> {
          var finalDestination = new Label();
          var bothEqualDestination = new Label();
          e1.visit(fs6);
          mw().visitInsn(Opcodes.L2F);
          e2.visit(fs6);
          mw().visitInsn(FCMPG);
          mw().visitJumpInsn(IFEQ, bothEqualDestination);
          mw().visitInsn(ICONST_0);
          mw().visitJumpInsn(GOTO, finalDestination);
          mw().visitLabel(bothEqualDestination);
          mw().visitInsn(ICONST_1);
          mw().visitLabel(finalDestination);
        }
        case int$name -> {
          var finalDestination = new Label();
          var bothEqualDestination = new Label();
          e1.visit(fs6);
          e2.visit(fs6);
          mw().visitInsn(Opcodes.I2L);
          mw().visitInsn(LCMP);
          mw().visitJumpInsn(IFEQ, bothEqualDestination);
          mw().visitInsn(ICONST_0);
          mw().visitJumpInsn(GOTO, finalDestination);
          mw().visitLabel(bothEqualDestination);
          mw().visitInsn(ICONST_1);
          mw().visitLabel(finalDestination);
        }
      }
    }
  }
}
