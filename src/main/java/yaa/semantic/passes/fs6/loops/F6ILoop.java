package yaa.semantic.passes.fs6.loops;

import org.objectweb.asm.Label;
import yaa.ast.Loop;
import yaa.pojos.TypeCategory;
import yaa.pojos.YaaClz;
import yaa.pojos.YaaField;
import yaa.semantic.passes.fs6.F6;
import yaa.semantic.passes.fs6.F6Utils;
import yaa.semantic.passes.fs6.results.LoopResult;

import static org.objectweb.asm.Opcodes.*;
import static yaa.pojos.GlobalData.*;
import static yaa.semantic.passes.fs6.F6.mw;

public class F6ILoop {
  public static void f6ILoop(Loop loop, LoopResult result) {
    var condition = new Label();
    var jump$body = new Label();

    loop.condition.visit(fs6);
    if (result.clz.category == TypeCategory.trait_c) {
      mw().visitMethodInsn(
          INVOKEINTERFACE, result.clz.codeName,
          "iterator", "()" + result.iteratorClz.descriptor(), true
      );
    } else {
      mw().visitMethodInsn(
          INVOKEVIRTUAL, result.clz.codeName,
          "iterator", "()" + result.iteratorClz.descriptor(), false
      );
    }

    var iIndex = fs6.variables.peek().putVar(F6Utils.generateRandomName());
    mw().visitVarInsn(ASTORE, iIndex);

    var v$name = loop.value$name.content;
    var variableIndex = fs6.variables.peek().putVar(v$name);

    fs6.continue$locations.push(new F6.Jump(v$name, condition));
    fs6.break$locations.push(new F6.Jump(v$name, jump$body));

    mw().visitLabel(condition);

    mw().visitVarInsn(ALOAD, iIndex);
    mw().visitMethodInsn(
        INVOKEINTERFACE,
        result.iteratorClz.codeName,
        "hasNext", "()Z", true
    );

    mw().visitJumpInsn(IFEQ, jump$body);

    mw().visitVarInsn(ALOAD, iIndex);
    var loop_field = (YaaField) fs6.getSymbol(v$name);
    var data_name = loop_field.data.name;
    if (loop_field.data.isUnboundedAndNotPrimitive()) {
      data_name = ((YaaClz) loop_field.data).parent.name;
    }
    switch (data_name) {
      case int$name, int$boxed -> {
        mw().visitMethodInsn(
            INVOKEINTERFACE,
            result.iteratorClz.codeName,
            "next", "()Ljava/lang/Integer;", true
        );
      }
      case long$name, long$boxed -> {
        mw().visitMethodInsn(
            INVOKEINTERFACE,
            result.iteratorClz.codeName,
            "next", "()Ljava/lang/Long;", true
        );
      }
      case double$name, double$boxed -> {
        mw().visitMethodInsn(
            INVOKEINTERFACE,
            result.iteratorClz.codeName,
            "next", "()Ljava/lang/Double;", true
        );
      }
      default -> {
        mw().visitMethodInsn(
            INVOKEINTERFACE,
            result.iteratorClz.codeName,
            "next", "()Ljava/lang/Object;", true
        );
      }
    }

    mw().visitVarInsn(ASTORE, variableIndex);

    loop.stmt.visit(fs6);

    if (loop.assign != null) {
      loop.assign.visit(fs6);
    }
    mw().visitJumpInsn(GOTO, condition);
    mw().visitLabel(jump$body);
    fs6.continue$locations.pop();
    fs6.break$locations.pop();
  }
}
