package yaa.semantic.passes.fs6;

import yaa.ast.VCall;
import yaa.pojos.YaaClz;
import yaa.pojos.YaaInfo;
import yaa.semantic.passes.fs6.results.InitResult;

import static org.objectweb.asm.Opcodes.*;
import static yaa.pojos.GlobalData.*;
import static yaa.semantic.passes.fs6.F6.mw;
import static yaa.semantic.passes.fs6.F6Utils.arraySetOperation;
import static yaa.semantic.passes.fs6.F6Utils.generateIntCode;

public class F6Array {
  public static void newArray(YaaClz array) {
    generateIntCode(0);//size of the array
    var type_argument = array.inputted.get(0);
    switch (type_argument.name) {
      case int$name -> {
        mw().visitIntInsn(NEWARRAY, T_INT);
      }
      case float$name -> {
        mw().visitIntInsn(NEWARRAY, T_FLOAT);
      }
      case double$name -> {
        mw().visitIntInsn(NEWARRAY, T_DOUBLE);
      }
      case short$name -> {
        mw().visitIntInsn(NEWARRAY, T_SHORT);
      }
      case byte$name -> {
        mw().visitIntInsn(NEWARRAY, T_BYTE);
      }
      case char$name -> {
        mw().visitIntInsn(NEWARRAY, T_CHAR);
      }
      case boole$name -> {
        mw().visitIntInsn(NEWARRAY, T_BOOLEAN);
      }
      case long$name -> {
        mw().visitIntInsn(NEWARRAY, T_LONG);
      }
      default -> {
        if (type_argument.name.equals(array$name)) {
          mw().visitTypeInsn(ANEWARRAY, type_argument.descriptor());
        } else {
          mw().visitTypeInsn(ANEWARRAY, type_argument.codeName);
        }
      }
    }
  }

  public static YaaInfo newArray(VCall ctx) {
    var array = ((InitResult) results.get(fs6.path).get(ctx)).clz;
    generateIntCode(ctx.arguments.size());//size of the array
    var type_argument = array.inputted.get(0);
    switch (type_argument.name) {
      case int$name -> {
        mw().visitIntInsn(NEWARRAY, T_INT);
      }
      case float$name -> {
        mw().visitIntInsn(NEWARRAY, T_FLOAT);
      }
      case double$name -> {
        mw().visitIntInsn(NEWARRAY, T_DOUBLE);
      }
      case short$name -> {
        mw().visitIntInsn(NEWARRAY, T_SHORT);
      }
      case byte$name -> {
        mw().visitIntInsn(NEWARRAY, T_BYTE);
      }
      case char$name -> {
        mw().visitIntInsn(NEWARRAY, T_CHAR);
      }
      case boole$name -> {
        mw().visitIntInsn(NEWARRAY, T_BOOLEAN);
      }
      case long$name -> {
        mw().visitIntInsn(NEWARRAY, T_LONG);
      }
      default -> {
        if (type_argument.name.equals(array$name)) {
          mw().visitTypeInsn(ANEWARRAY, type_argument.descriptor());
        } else {
          mw().visitTypeInsn(ANEWARRAY, type_argument.codeName);
        }
      }
    }

    if (type_argument.isPrimitive()) {
      for (int i = 0; i < ctx.arguments.size(); i++) {
        mw().visitInsn(DUP);
        generateIntCode(i);
        var from = ctx.arguments.get(i).visit(fs6);
        F6Utils.castTo(from.name, type_argument.name);
        arraySetOperation(array);
      }
    } else {
      for (int i = 0; i < ctx.arguments.size(); i++) {
        mw().visitInsn(DUP);
        generateIntCode(i);
        ctx.arguments.get(i).visit(fs6);
        arraySetOperation(array);
      }
    }
    return array;
  }
}
