package yaa.semantic.passes.fs6;

import yaa.ast.MEqual;
import yaa.semantic.passes.fs6.results.CallResult;
import yaa.pojos.YaaInfo;
import org.objectweb.asm.Label;
import yaa.pojos.GlobalData;

import static yaa.semantic.passes.fs6.F6Utils.itIsPrimitive;
import static org.objectweb.asm.Opcodes.*;
import static org.objectweb.asm.Opcodes.ICONST_1;

public class F6MEqual {
  public static YaaInfo mEqual(MEqual mEqual) {
    var mtd = ((CallResult) GlobalData.results.get(GlobalData.fs6.path).get(mEqual)).mtd;
    var owner = mtd.owner;
    var right = mtd.parameters.get(0);

    if (owner.equals(GlobalData.null$name)) {
      if (right.isPrimitive()) {
        F6.mw().visitInsn(ICONST_0);
        return mtd.type;
      }
      if (right.name.equals(GlobalData.null$name)) {
        F6.mw().visitInsn(ICONST_1);
        return mtd.type;
      }
      var trueDestination = new Label();
      var finalDestination = new Label();
      mEqual.e2.visit(GlobalData.fs6);
      F6.mw().visitJumpInsn(IFNONNULL, trueDestination);
      F6.mw().visitInsn(ICONST_1);
      F6.mw().visitJumpInsn(GOTO, finalDestination);
      F6.mw().visitLabel(trueDestination);
      F6.mw().visitInsn(ICONST_0);
      F6.mw().visitLabel(finalDestination);
      return mtd.type;
    }

    if (right.name.equals(GlobalData.null$name)) {
      if (F6Utils.itIsPrimitive(owner)) {
        F6.mw().visitInsn(ICONST_0);
        return mtd.type;
      }
      var trueDestination = new Label();
      var finalDestination = new Label();
      mEqual.e1.visit(GlobalData.fs6);
      F6.mw().visitJumpInsn(IFNONNULL, trueDestination);
      F6.mw().visitInsn(ICONST_1);
      F6.mw().visitJumpInsn(GOTO, finalDestination);
      F6.mw().visitLabel(trueDestination);
      F6.mw().visitInsn(ICONST_0);
      F6.mw().visitLabel(finalDestination);
      return mtd.type;
    }

    if (F6Utils.itIsPrimitive(owner) || right.isPrimitive()) {
      F6.mw().visitInsn(ICONST_0);
      return mtd.type;
    }

    var finalDestination = new Label();
    var bothEqualDestination = new Label();
    mEqual.e1.visit(GlobalData.fs6);
    mEqual.e2.visit(GlobalData.fs6);
    F6.mw().visitJumpInsn(IF_ACMPEQ, bothEqualDestination);
    F6.mw().visitInsn(ICONST_0);
    F6.mw().visitJumpInsn(GOTO, finalDestination);
    F6.mw().visitLabel(bothEqualDestination);
    F6.mw().visitInsn(ICONST_1);
    F6.mw().visitLabel(finalDestination);
    return mtd.type;
  }
}
