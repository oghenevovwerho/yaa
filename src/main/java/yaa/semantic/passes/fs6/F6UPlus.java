package yaa.semantic.passes.fs6;

import yaa.ast.UPlus;
import yaa.semantic.passes.fs6.results.CallResult;
import yaa.pojos.YaaInfo;
import org.objectweb.asm.Opcodes;
import yaa.pojos.GlobalData;

import static yaa.pojos.GlobalData.unary_plus_op_name;
import static yaa.semantic.passes.fs6.F6.mw;
import static yaa.semantic.passes.fs6.F6Utils.itIsPrimitive;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;

public class F6UPlus {
  public static YaaInfo plus(UPlus ctx) {
    var mtd = ((CallResult) GlobalData.results.get(GlobalData.fs6.path).get(ctx)).mtd;
    var owner = mtd.owner;

    if (!itIsPrimitive(owner)) {
      ctx.e.visit(GlobalData.fs6);
      mw().visitMethodInsn(
        INVOKEVIRTUAL,
        mtd.owner,
        unary_plus_op_name,
        mtd.descriptor(),
        false
      );
      return mtd.type;
    }

    ctx.e.visit(GlobalData.fs6);
    switch (owner) {
      case GlobalData.int$name -> mw().visitMethodInsn(
        Opcodes.INVOKESTATIC,
        "java/lang/Math",
        "abs",
        "(I)I",
        false
      );
      case GlobalData.long$name -> mw().visitMethodInsn(
        Opcodes.INVOKESTATIC,
        "java/lang/Math",
        "abs",
        "(J)J",
        false
      );
      case GlobalData.double$name -> mw().visitMethodInsn(
        Opcodes.INVOKESTATIC,
        "java/lang/Math",
        "abs",
        "(D)D",
        false
      );
      case GlobalData.float$name -> mw().visitMethodInsn(
        Opcodes.INVOKESTATIC,
        "java/lang/Math",
        "abs",
        "(F)F",
        false
      );
    }
    return mtd.type;
  }
}
