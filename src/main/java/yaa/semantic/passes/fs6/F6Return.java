package yaa.semantic.passes.fs6;

import yaa.ast.Return;
import yaa.pojos.YaaInfo;

import static org.objectweb.asm.Opcodes.*;
import static yaa.pojos.GlobalData.*;
import static yaa.semantic.passes.fs6.F6Utils.unboxedName;

public class F6Return {
  public static YaaInfo handle$return(Return ret) {
    var expected_value = F6.f6TopMtd.peek().type;
    var given_value = ret.e.visit(fs6);
    if (expected_value.isPrimitive()) {
      if (given_value.isBoxed()) {
        F6Utils.unBox(given_value.name);
      }
      switch (expected_value.name) {
        case int$name, byte$name, boole$name, char$name, short$name -> {
          F6.mw().visitInsn(IRETURN);
        }
        case long$name -> {
          F6Utils.castTo(unboxedName(given_value.name), expected_value.name);
          F6.mw().visitInsn(LRETURN);
        }
        case double$name -> {
          F6Utils.castTo(unboxedName(given_value.name), expected_value.name);
          F6.mw().visitInsn(DRETURN);
        }
        case float$name -> {
          F6Utils.castTo(unboxedName(given_value.name), expected_value.name);
          F6.mw().visitInsn(FRETURN);
        }
      }
    } else {
      F6.mw().visitInsn(ARETURN);
    }
    return nothing;
  }
}
