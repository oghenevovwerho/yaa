package yaa.semantic.passes.fs6;

import yaa.ast.Stmt;
import yaa.pojos.YaaClz;
import org.objectweb.asm.Opcodes;
import yaa.pojos.GlobalData;

import java.util.List;

import static org.objectweb.asm.Opcodes.INVOKESTATIC;
import static yaa.pojos.GlobalData.fs6;
import static yaa.semantic.passes.fs6.F6.mw;

public class F6ArrayMtds {
  public static void arrayMtd(String name, List<Stmt> args, YaaClz arrayClz) {
    switch (name) {
      case "get" -> {
        args.get(0).visit(fs6);
        F6Utils.arrayGetOperation(arrayClz);
      }
      case "set" -> {
        //index
        args.get(0).visit(fs6);
        //value
        args.get(1).visit(fs6);
        F6Utils.arraySetOperation(arrayClz);
      }
      case "size" -> {
        mw().visitInsn(Opcodes.ARRAYLENGTH);
      }
      case "toString" -> {
        var descriptor = "([Ljava/lang/Object;)Ljava/lang/String;";
        if (arrayClz.inputted.get(0).name.equals(GlobalData.array$name)) {
          mw().visitMethodInsn(
            INVOKESTATIC, "java/util/Arrays",
            "deepToString", descriptor, false
          );
        } else {
          if (arrayClz.inputted.get(0).isPrimitive()) {
            descriptor = "(" + arrayClz.descriptor() + ")Ljava/lang/String;";
          }
          mw().visitMethodInsn(
            INVOKESTATIC, "java/util/Arrays",
            "toString", descriptor, false
          );
        }
      }
      case "hashCode" -> {
        var descriptor = "([Ljava/lang/Object;)I";
        if (arrayClz.inputted.get(0).isPrimitive()) {
          descriptor = "(" + arrayClz.descriptor() + ")I";
        }
        mw().visitMethodInsn(
          INVOKESTATIC, "java/util/Arrays",
          "hashCode", descriptor, false
        );
      }
    }
  }
}