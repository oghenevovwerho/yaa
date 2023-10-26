package yaa.semantic.passes.fs6;

import yaa.ast.And;
import yaa.semantic.passes.fs6.results.CallResult;
import yaa.pojos.YaaInfo;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import yaa.pojos.GlobalData;

import static yaa.semantic.passes.fs6.F6Utils.itIsPrimitive;
import static org.objectweb.asm.Opcodes.*;

public class F6And {
  public static YaaInfo and(And ctx) {
    var mtd = ((CallResult) GlobalData.results.get(GlobalData.fs6.path).get(ctx)).mtd;
    var owner = mtd.owner;

    if (!F6Utils.itIsPrimitive(owner)) {
      return F6Utils.operatorMtd(ctx.e1, ctx.e2, mtd);
    }

    var lIsTrueDestination = new Label();
    var rIsTrueDestination = new Label();
    var finalDestination = new Label();
    ctx.e1.visit(GlobalData.fs6);
    F6.mw().visitJumpInsn(Opcodes.IFEQ, lIsTrueDestination);
    ctx.e2.visit(GlobalData.fs6);
    F6.mw().visitJumpInsn(Opcodes.IFNE, rIsTrueDestination);
    F6.mw().visitLabel(lIsTrueDestination);
    F6.mw().visitInsn(ICONST_0);
    F6.mw().visitJumpInsn(GOTO, finalDestination);
    F6.mw().visitLabel(rIsTrueDestination);
    F6.mw().visitInsn(ICONST_1);
    F6.mw().visitLabel(finalDestination);
    return mtd.type;
  }
}
