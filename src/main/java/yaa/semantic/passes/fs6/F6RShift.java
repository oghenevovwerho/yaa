package yaa.semantic.passes.fs6;

import yaa.ast.RShift;
import yaa.semantic.passes.fs6.results.CallResult;
import yaa.pojos.YaaInfo;
import org.objectweb.asm.Opcodes;
import yaa.pojos.GlobalData;

import static yaa.semantic.passes.fs6.F6Utils.itIsPrimitive;

public class F6RShift {
  public static YaaInfo rShift(RShift ctx) {
    var mtd = ((CallResult) GlobalData.results.get(GlobalData.fs6.path).get(ctx)).mtd;
    var owner = mtd.owner;
    var right = mtd.parameters.get(0);

    if (!F6Utils.itIsPrimitive(owner)) {
      return F6Utils.operatorMtd(ctx.e1, ctx.e2, mtd);
    }

    if (owner.equals(GlobalData.int$name)) {
      if (right.name.equals(GlobalData.int$name)) {
        ctx.e1.visit(GlobalData.fs6);
        ctx.e2.visit(GlobalData.fs6);
        F6.mw().visitInsn(Opcodes.ISHR);
      } else {
        F6.mw().visitInsn(Opcodes.I2L);
        ctx.e1.visit(GlobalData.fs6);
        ctx.e2.visit(GlobalData.fs6);
        F6.mw().visitInsn(Opcodes.LSHR);
      }
    }

    if (owner.equals(GlobalData.long$name)) {
      if (right.name.equals(GlobalData.long$name)) {
        ctx.e1.visit(GlobalData.fs6);
        ctx.e2.visit(GlobalData.fs6);
        F6.mw().visitInsn(Opcodes.LSHR);
      } else {
        ctx.e1.visit(GlobalData.fs6);
        ctx.e2.visit(GlobalData.fs6);
        F6.mw().visitInsn(Opcodes.I2L);
        F6.mw().visitInsn(Opcodes.LSHR);
      }
    }
    return mtd.type;
  }
}
