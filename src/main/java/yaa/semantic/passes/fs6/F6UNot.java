package yaa.semantic.passes.fs6;

import yaa.ast.UNot;
import yaa.semantic.passes.fs6.results.CallResult;
import yaa.pojos.YaaInfo;
import org.objectweb.asm.Label;
import yaa.pojos.GlobalData;

import static yaa.pojos.GlobalData.unary_not_op_name;
import static yaa.semantic.passes.fs6.F6.mw;
import static yaa.semantic.passes.fs6.F6Utils.itIsPrimitive;
import static org.objectweb.asm.Opcodes.*;
import static org.objectweb.asm.Opcodes.ICONST_0;

public class F6UNot {
  public static YaaInfo not(UNot ctx) {
    var mtd = ((CallResult) GlobalData.results.get(GlobalData.fs6.path).get(ctx)).mtd;
    var owner = mtd.owner;

    if (!itIsPrimitive(owner)) {
      ctx.e.visit(GlobalData.fs6);
      mw().visitMethodInsn(
        INVOKEVIRTUAL,
        mtd.owner,
        unary_not_op_name,
        mtd.descriptor(),
        false
      );
      return mtd.type;
    }

    if (owner.equals(GlobalData.boole$name)) {
      ctx.e.visit(GlobalData.fs6);
      Label equalResultDestination = new Label();
      Label afterNegateDestination = new Label();
      mw().visitJumpInsn(IFNE, equalResultDestination);
      mw().visitInsn(ICONST_1);
      mw().visitJumpInsn(GOTO, afterNegateDestination);
      mw().visitLabel(equalResultDestination);
      mw().visitInsn(ICONST_0);
      mw().visitLabel(afterNegateDestination);
    }
    return mtd.type;
  }
}
