package yaa.semantic.passes.fs6;

import yaa.ast.Decimal;
import yaa.ast.UMinus;
import yaa.semantic.passes.fs6.results.CallResult;
import yaa.pojos.YaaInfo;
import org.objectweb.asm.Opcodes;
import yaa.pojos.GlobalData;

import static yaa.pojos.GlobalData.unary_minus_op_name;
import static yaa.semantic.passes.fs6.F6.mw;
import static yaa.semantic.passes.fs6.F6Utils.itIsPrimitive;
import static java.lang.Integer.parseInt;
import static org.objectweb.asm.Opcodes.ICONST_M1;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;

public class F6UMinus {
  public static YaaInfo minus(UMinus ctx) {
    var mtd = ((CallResult) GlobalData.results.get(GlobalData.fs6.path).get(ctx)).mtd;
    var owner = mtd.owner;

    if (ctx.e instanceof Decimal decimal) {
      if (parseInt(decimal.token.content) == 1) {
        mw().visitInsn(ICONST_M1);
        return mtd.type;
      }
    }

    if (!itIsPrimitive(owner)) {
      ctx.e.visit(GlobalData.fs6);
      mw().visitMethodInsn(
        INVOKEVIRTUAL,
        mtd.owner,
        unary_minus_op_name,
        mtd.descriptor(),
        false
      );
      return mtd.type;
    }

    ctx.e.visit(GlobalData.fs6);
    switch (owner) {
      case GlobalData.int$name -> mw().visitInsn(Opcodes.INEG);
      case GlobalData.long$name -> mw().visitInsn(Opcodes.LNEG);
      case GlobalData.double$name -> mw().visitInsn(Opcodes.DNEG);
      case GlobalData.float$name -> mw().visitInsn(Opcodes.FNEG);
    }
    return mtd.type;
  }
}
