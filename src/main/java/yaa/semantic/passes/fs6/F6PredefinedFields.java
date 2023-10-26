package yaa.semantic.passes.fs6;

import yaa.pojos.YaaField;
import yaa.pojos.YaaInfo;

import static org.objectweb.asm.Opcodes.*;
import static yaa.Yaa.main$clz$name;
import static yaa.semantic.passes.fs6.F6.mw;

public class F6PredefinedFields {
  public static YaaInfo field(YaaField field) {
    if (field.field$name.equals("cmd")) {
      if (main$clz$name == null) {
        mw().visitInsn(ICONST_0);
        mw().visitTypeInsn(ANEWARRAY, "java/lang/String");
      } else {
        mw().visitFieldInsn(
            GETSTATIC, main$clz$name, "cmd", "[Ljava/lang/String;"
        );
      }
    }
    return field.data;
  }
}
