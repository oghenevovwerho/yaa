package yaa.semantic.passes.fs6;

import yaa.ast.Throw;
import yaa.semantic.passes.fs6.results.ThrowResult;
import yaa.pojos.YaaInfo;
import yaa.pojos.GlobalData;

import static org.objectweb.asm.Opcodes.*;

public class F6Throw {
  public static YaaInfo handle$throw(Throw ctx) {
    var result = (ThrowResult) GlobalData.fs6.results.get(ctx);
    if (result.extendsThrowable) {
      ctx.e.visit(GlobalData.fs6);
      F6.mw().visitInsn(ATHROW);
    } else {
      F6.mw().visitTypeInsn(NEW, "java/lang/Exception");
      F6.mw().visitInsn(DUP);
      F6Utils.pushStringData(ctx.e);
      F6.mw().visitMethodInsn(
        INVOKESPECIAL,
        "java/lang/Exception",
        "<init>", "(Ljava/lang/String;)V", false
      );
      F6.mw().visitInsn(ATHROW);
    }
    return GlobalData.nothing;
  }
}
