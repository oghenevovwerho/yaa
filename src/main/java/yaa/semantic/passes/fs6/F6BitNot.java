package yaa.semantic.passes.fs6;

import yaa.ast.BitNot;
import yaa.semantic.passes.fs6.results.CallResult;
import yaa.pojos.YaaInfo;
import org.objectweb.asm.Opcodes;
import yaa.pojos.GlobalData;

import static yaa.pojos.GlobalData.bit_negate_op_name;
import static yaa.semantic.passes.fs6.F6.mw;
import static yaa.semantic.passes.fs6.F6Utils.itIsPrimitive;
import static org.objectweb.asm.Opcodes.*;

public class F6BitNot {
  public static YaaInfo not(BitNot ctx) {
    var mtd = ((CallResult) GlobalData.results.get(GlobalData.fs6.path).get(ctx)).mtd;
    var owner = mtd.owner;

    if (!itIsPrimitive(owner)) {
      ctx.e.visit(GlobalData.fs6);
      mw().visitMethodInsn(
        INVOKEVIRTUAL,
        mtd.owner,
        bit_negate_op_name,
        mtd.descriptor(),
        false
      );
      return mtd.type;
    }

    ctx.e.visit(GlobalData.fs6);
    if (owner.equals(GlobalData.int$name)) {
      mw().visitInsn(Opcodes.ICONST_M1);
      mw().visitInsn(IXOR);
    } else if (owner.equals(GlobalData.long$name)) {
      mw().visitLdcInsn(-1L);
      mw().visitInsn(LXOR);
    }

    return mtd.type;
  }
}
