package yaa.semantic.passes.fs6;

import yaa.ast.URShift;
import yaa.semantic.passes.fs6.results.CallResult;
import yaa.pojos.YaaInfo;
import org.objectweb.asm.Opcodes;
import yaa.pojos.GlobalData;

import static yaa.semantic.passes.fs6.F6Utils.itIsPrimitive;
import static yaa.semantic.passes.fs6.F6Utils.operatorMtd;

public class F6URShift {
  public static YaaInfo uRShift(URShift ctx) {
    var mtd = ((CallResult) GlobalData.results.get(GlobalData.fs6.path).get(ctx)).mtd;
    var owner = mtd.owner;
    var right = mtd.parameters.get(0);

    if (!itIsPrimitive(owner)) {
      return operatorMtd(ctx.e1, ctx.e2, mtd);
    }

    if (owner.equals(GlobalData.int$name)) {
      if (right.name.equals(GlobalData.int$name)) {
        ctx.e1.visit(GlobalData.fs6);
        ctx.e2.visit(GlobalData.fs6);
        F6.mw().visitInsn(Opcodes.IUSHR);
      } else {
        F6.mw().visitInsn(Opcodes.I2L);
        ctx.e1.visit(GlobalData.fs6);
        ctx.e2.visit(GlobalData.fs6);
        F6.mw().visitInsn(Opcodes.LUSHR);
      }
    }

    if (owner.equals(GlobalData.long$name)) {
      if (right.name.equals(GlobalData.long$name)) {
        ctx.e1.visit(GlobalData.fs6);
        ctx.e2.visit(GlobalData.fs6);
        F6.mw().visitInsn(Opcodes.LUSHR);
      } else {
        ctx.e1.visit(GlobalData.fs6);
        ctx.e2.visit(GlobalData.fs6);
        F6.mw().visitInsn(Opcodes.I2L);
        F6.mw().visitInsn(Opcodes.LUSHR);
      }
    }
    return mtd.type;
  }
}
