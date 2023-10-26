package yaa.semantic.passes.fs6;

import yaa.ast.Xor;
import yaa.semantic.passes.fs6.results.CallResult;
import yaa.pojos.YaaInfo;
import org.objectweb.asm.Opcodes;
import yaa.pojos.GlobalData;

import static yaa.pojos.GlobalData.fs6;
import static yaa.semantic.passes.fs6.F6Utils.itIsPrimitive;
import static yaa.semantic.passes.fs6.F6Utils.operatorMtd;

public class F6Xor {
  public static YaaInfo xor(Xor ctx) {
    var mtd = ((CallResult) GlobalData.results.get(fs6.path).get(ctx)).mtd;
    var owner = mtd.owner;
    var right = mtd.parameters.get(0);

    if (!itIsPrimitive(owner)) {
      return operatorMtd(ctx.e1, ctx.e2, mtd);
    }

    if (owner.equals(GlobalData.int$name)) {
      if (right.name.equals(GlobalData.int$name)) {
        ctx.e1.visit(fs6);
        ctx.e2.visit(fs6);
        F6.mw().visitInsn(Opcodes.IXOR);
      } else {
        ctx.e1.visit(fs6);
        F6.mw().visitInsn(Opcodes.I2L);
        ctx.e2.visit(fs6);
        F6.mw().visitInsn(Opcodes.LXOR);
      }
    }

    if (owner.equals(GlobalData.long$name)) {
      if (right.name.equals(GlobalData.long$name)) {
        ctx.e1.visit(fs6);
        ctx.e2.visit(fs6);
        F6.mw().visitInsn(Opcodes.LXOR);
      } else {
        ctx.e1.visit(fs6);
        ctx.e2.visit(fs6);
        F6.mw().visitInsn(Opcodes.I2L);
        F6.mw().visitInsn(Opcodes.LXOR);
      }
    }
    return mtd.type;
  }
}
