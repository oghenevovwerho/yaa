package yaa.semantic.passes.fs6;

import yaa.ast.VCall;
import yaa.pojos.YaaInfo;
import yaa.semantic.passes.fs6.results.CallResult;

import static org.objectweb.asm.Opcodes.*;

public class F6PredefinedMtd {
  public static YaaInfo predefined(CallResult result, VCall ctx) {
    var mtd = result.mtd;
    switch (mtd.name) {
      case "println" -> {
        F6.mw().visitFieldInsn(
            GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;"
        );
        F6Utils.runArguments(mtd.callInfo.parameters, ctx.arguments);
        F6.mw().visitMethodInsn(
            INVOKEVIRTUAL, mtd.owner,
            "println",
            mtd.callInfo.descriptor(null),
            false
        );
      }
      case "print" -> {
        F6.mw().visitFieldInsn(
            GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;"
        );
        F6Utils.runArguments(mtd.callInfo.parameters, ctx.arguments);
        F6.mw().visitMethodInsn(
            INVOKEVIRTUAL, mtd.owner,
            "print",
            mtd.callInfo.descriptor(null), false
        );
      }
      case "getProperty" -> {
        F6Utils.runArguments(mtd.callInfo.parameters, ctx.arguments);
        F6.mw().visitMethodInsn(
            INVOKESTATIC, mtd.owner,
            "getProperty",
            mtd.callInfo.descriptor(null), false
        );
      }
    }
    return mtd.type;
  }
}
