package yaa.semantic.passes.fs6.loops;

import yaa.ast.False;
import yaa.ast.Loop;
import yaa.pojos.VariableData;
import yaa.semantic.passes.fs6.F6;
import yaa.semantic.passes.fs6.F6Utils;
import org.objectweb.asm.Label;
import yaa.pojos.GlobalData;

import java.util.ArrayList;

import static yaa.pojos.GlobalData.boole$clz;
import static yaa.pojos.GlobalData.fs6;
import static yaa.semantic.passes.fs6.F6.mw;
import static org.objectweb.asm.Opcodes.*;

public class F6WLoop {
  public static void p6WhileLoop(Loop loop) {
    if (loop.condition instanceof False) {
      return;
    }
    var d1 = new Label();
    var d2 = new Label();
    var continue_label = new Label();
    var breakOut_label = new Label();

    String variableName;
    if (loop.value$name != null) {
      variableName = loop.value$name.content;
      var variables = fs6.variables.peek();

      String dataDescriptor = "I";
      if (loop.init$value != null) {
        var data = loop.init$value.visit(fs6);
        dataDescriptor = data.descriptor();
        var newFieldIndex = fs6.variables.peek().putVar(variableName);
        mw().visitVarInsn(ISTORE, newFieldIndex);
      } else {
        mw().visitInsn(ICONST_0);
        var newFieldIndex = fs6.variables.peek().putVar(variableName);
        mw().visitVarInsn(ISTORE, newFieldIndex);
      }

      var loopNameLabel = new Label();
      F6.mw().visitLabel(loopNameLabel);
      F6.mw().visitLineNumber(loop.start.line, loopNameLabel);

      F6.variableMeta.peek().add(
          new VariableData(
              variableName,
              loopNameLabel,
              dataDescriptor,
              boole$clz.clzUseSignature(),
              variables.index,
              new ArrayList<>(0),
              new ArrayList<>(0)
          )
      );
    } else {
      variableName = F6Utils.generateRandomName();
    }

    fs6.break$locations.push(new F6.Jump(variableName, breakOut_label));
    fs6.continue$locations.push(new F6.Jump(variableName, continue_label));

    mw().visitLabel(d1);
    loop.condition.visit(fs6);
    mw().visitJumpInsn(IFEQ, d2);
    loop.stmt.visit(fs6);
    mw().visitLabel(continue_label);
    if (loop.assign != null) {
      loop.assign.visit(fs6);
    }
    mw().visitJumpInsn(GOTO, d1);
    mw().visitLabel(d2);
    mw().visitLabel(breakOut_label);
    fs6.break$locations.pop();
    fs6.continue$locations.pop();
  }
}
