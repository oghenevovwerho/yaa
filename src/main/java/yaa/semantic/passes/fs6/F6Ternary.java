package yaa.semantic.passes.fs6;

import yaa.ast.Ternary;
import yaa.semantic.passes.fs6.results.TernaryResult;
import yaa.pojos.YaaInfo;
import org.objectweb.asm.Label;
import yaa.pojos.GlobalData;

import static org.objectweb.asm.Opcodes.GOTO;
import static org.objectweb.asm.Opcodes.IFEQ;

public class F6Ternary {
  public static YaaInfo ternary(Ternary ternary) {
    var result = (TernaryResult) GlobalData.fs6.results.get(ternary);
    var fDestination = new Label();
    var finalDestination = new Label();
    ternary.cond.visit(GlobalData.fs6);
    F6.mw().visitJumpInsn(IFEQ, fDestination);
    var a1 = ternary.l.visit(GlobalData.fs6);
    if (a1.isPrimitive() && !result.type.isPrimitive()) {
      F6Utils.boxPrimitive(a1.name);
    }
    F6.mw().visitJumpInsn(GOTO, finalDestination);
    F6.mw().visitLabel(fDestination);
    var a2 = ternary.r.visit(GlobalData.fs6);
    if (a2.isPrimitive() && !result.type.isPrimitive()) {
      F6Utils.boxPrimitive(a2.name);
    }
    F6.mw().visitLabel(finalDestination);
    return result.type;
  }
}
