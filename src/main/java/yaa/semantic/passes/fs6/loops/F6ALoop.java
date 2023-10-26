package yaa.semantic.passes.fs6.loops;

import yaa.ast.Loop;
import yaa.semantic.passes.fs6.F6;
import yaa.semantic.passes.fs6.F6Utils;
import yaa.semantic.passes.fs6.results.LoopResult;
import org.objectweb.asm.Label;

import static yaa.pojos.GlobalData.*;
import static yaa.semantic.passes.fs6.F6.mw;
import static org.objectweb.asm.Opcodes.*;

public class F6ALoop {
  public static void f6ArrayLoop(Loop ctx, LoopResult result) {
    var d1 = new Label();
    var continue_label = new Label();
    var breakOut_label = new Label();

    String variableName = ctx.value$name.content;

    String length$name = F6Utils.generateRandomName();
    ctx.condition.visit(fs6);
    mw().visitInsn(ARRAYLENGTH);
    mw().visitVarInsn(ISTORE, fs6.variables.peek().putVar(length$name));

    //jump to end if array size is 0
    mw().visitVarInsn(ILOAD, fs6.variables.peek().indexOf(length$name));
    mw().visitJumpInsn(IFEQ, breakOut_label);

    String index$name = F6Utils.generateRandomName();
    mw().visitInsn(ICONST_0);
    mw().visitVarInsn(ISTORE, fs6.variables.peek().putVar(index$name));

    fs6.break$locations.push(new F6.Jump(variableName, breakOut_label));
    fs6.continue$locations.push(new F6.Jump(variableName, continue_label));

    var array = result.clz;

    mw().visitLabel(d1);

    ctx.condition.visit(fs6);
    mw().visitVarInsn(ILOAD, fs6.variables.peek().indexOf(index$name));
    F6Utils.arrayGetOperation(array);

    var type_argument = array.inputted.get(0);
    switch (type_argument.name) {
      case long$name -> {
        mw().visitVarInsn(LSTORE, fs6.variables.peek().putVar(variableName));
      }
      case double$name -> {
        mw().visitVarInsn(DSTORE, fs6.variables.peek().putVar(variableName));
      }
      case float$name -> {
        mw().visitVarInsn(FSTORE, fs6.variables.peek().putVar(variableName));
      }
      default -> {
        if (type_argument.isPrimitive()) {
          mw().visitVarInsn(ISTORE, fs6.variables.peek().putVar(variableName));
        } else {
          mw().visitVarInsn(ASTORE, fs6.variables.peek().putVar(variableName));
        }
      }
    }

    ctx.stmt.visit(fs6);

    mw().visitLabel(continue_label);
    mw().visitVarInsn(ILOAD, fs6.variables.peek().indexOf(index$name));
    mw().visitInsn(ICONST_1);
    mw().visitInsn(IADD);
    mw().visitVarInsn(ISTORE, fs6.variables.peek().indexOf(index$name));

    mw().visitVarInsn(ILOAD, fs6.variables.peek().indexOf(length$name));
    mw().visitVarInsn(ILOAD, fs6.variables.peek().indexOf(index$name));
    mw().visitJumpInsn(IF_ICMPNE, d1);
    mw().visitLabel(breakOut_label);
    fs6.break$locations.pop();
    fs6.continue$locations.pop();
  }
}
