package yaa.semantic.passes.fs6;

import yaa.ast.Stmt;
import yaa.pojos.YaaClz;
import yaa.pojos.GlobalData;

import java.util.List;

import static org.objectweb.asm.Opcodes.*;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;

public class F6PrimitiveMtds {
  public static void equalsMtd(YaaClz clz, List<Stmt> values) {
    F6Utils.boxPrimitive(clz.name);
    F6Utils.boxPrimitive(values.get(0).visit(GlobalData.fs6).name);
    switch (clz.name) {
      case GlobalData.int$name -> {
        F6.mw().visitMethodInsn(
          INVOKEVIRTUAL,
          "java/lang/Integer", "equals", "(Ljava/lang/Object;)Z", false
        );
      }
      case GlobalData.long$name -> {
        F6.mw().visitMethodInsn(
          INVOKEVIRTUAL,
          "java/lang/Long", "equals", "(Ljava/lang/Object;)Z", false
        );
      }
      case GlobalData.float$name -> {
        F6.mw().visitMethodInsn(
          INVOKEVIRTUAL,
          "java/lang/Float", "equals", "(Ljava/lang/Object;)Z", false
        );
      }
      case GlobalData.double$name -> {
        F6.mw().visitMethodInsn(
          INVOKEVIRTUAL,
          "java/lang/Double", "equals", "(Ljava/lang/Object;)Z", false
        );
      }
      case GlobalData.short$name -> {
        F6.mw().visitMethodInsn(
          INVOKEVIRTUAL,
          "java/lang/Short", "equals", "(Ljava/lang/Object;)Z", false
        );
      }
      case GlobalData.byte$name -> {
        F6.mw().visitMethodInsn(
          INVOKEVIRTUAL,
          "java/lang/Byte", "equals", "(Ljava/lang/Object;)Z", false
        );
      }
      case GlobalData.boole$name -> {
        F6.mw().visitMethodInsn(
          INVOKEVIRTUAL,
          "java/lang/Boolean", "equals", "(Ljava/lang/Object;)Z", false
        );
      }
    }
  }

  public static void getClassMtd(YaaClz clz) {
    switch (clz.name) {
      case GlobalData.int$name -> {
        F6.mw().visitFieldInsn(
          GETSTATIC, "java/lang/Integer", "TYPE", "Ljava/lang/Class;"
        );
      }
      case GlobalData.long$name -> {
        F6.mw().visitFieldInsn(
          GETSTATIC, "java/lang/Long", "TYPE", "Ljava/lang/Class;"
        );
      }
      case GlobalData.float$name -> {
        F6.mw().visitFieldInsn(
          GETSTATIC, "java/lang/Float", "TYPE", "Ljava/lang/Class;"
        );
      }
      case GlobalData.double$name -> {
        F6.mw().visitFieldInsn(
          GETSTATIC, "java/lang/Double", "TYPE", "Ljava/lang/Class;"
        );
      }
      case GlobalData.short$name -> {
        F6.mw().visitFieldInsn(
          GETSTATIC, "java/lang/Short", "TYPE", "Ljava/lang/Class;"
        );
      }
      case GlobalData.byte$name -> {
        F6.mw().visitFieldInsn(
          GETSTATIC, "java/lang/Byte", "TYPE", "Ljava/lang/Class;"
        );
      }
      case GlobalData.boole$name -> {
        F6.mw().visitFieldInsn(
          GETSTATIC, "java/lang/Boolean", "TYPE", "Ljava/lang/Class;"
        );
      }
    }
  }

  public static void hashCodeMtd(YaaClz clz) {
    switch (clz.name) {
      case GlobalData.int$name -> {
        F6.mw().visitMethodInsn(
          INVOKESTATIC,
          "java/lang/Integer", "hashCode", "(I)I", false
        );
      }
      case GlobalData.long$name -> {
        F6.mw().visitMethodInsn(
          INVOKESTATIC,
          "java/lang/Long", "hashCode", "(J)I", false
        );
      }
      case GlobalData.float$name -> {
        F6.mw().visitMethodInsn(
          INVOKESTATIC,
          "java/lang/Float", "hashCode", "(F)I", false
        );
      }
      case GlobalData.double$name -> {
        F6.mw().visitMethodInsn(
          INVOKESTATIC,
          "java/lang/Double", "hashCode", "(D)I", false
        );
      }
      case GlobalData.short$name -> {
        F6.mw().visitMethodInsn(
          INVOKESTATIC,
          "java/lang/Short", "hashCode", "(S)I", false
        );
      }
      case GlobalData.byte$name -> {
        F6.mw().visitMethodInsn(
          INVOKESTATIC,
          "java/lang/Byte", "hashCode", "(B)I", false
        );
      }
      case GlobalData.boole$name -> {
        F6.mw().visitMethodInsn(
          INVOKESTATIC,
          "java/lang/Boolean", "hashCode", "(Z)I", false
        );
      }
    }
  }

  public static void toStringMtd(YaaClz clz) {
    switch (clz.name) {
      case GlobalData.int$name -> {
        F6.mw().visitMethodInsn(
          INVOKESTATIC,
          "java/lang/Integer", "toString", "(I)Ljava/lang/String;", false
        );
      }
      case GlobalData.long$name -> {
        F6.mw().visitMethodInsn(
          INVOKESTATIC,
          "java/lang/Long", "toString", "(J)Ljava/lang/String;", false
        );
      }
      case GlobalData.float$name -> {
        F6.mw().visitMethodInsn(
          INVOKESTATIC,
          "java/lang/Float", "toString", "(F)Ljava/lang/String;", false
        );
      }
      case GlobalData.double$name -> {
        F6.mw().visitMethodInsn(
          INVOKESTATIC,
          "java/lang/Double", "toString", "(D)Ljava/lang/String;", false
        );
      }
      case GlobalData.short$name -> {
        F6.mw().visitMethodInsn(
          INVOKESTATIC,
          "java/lang/Short", "toString", "(S)Ljava/lang/String;", false
        );
      }
      case GlobalData.byte$name -> {
        F6.mw().visitMethodInsn(
          INVOKESTATIC,
          "java/lang/Byte", "toString", "(B)Ljava/lang/String;", false
        );
      }
      case GlobalData.boole$name -> {
        F6.mw().visitMethodInsn(
          INVOKESTATIC,
          "java/lang/Boolean", "toString", "(Z)Ljava/lang/String;", false
        );
      }
    }
  }
}
