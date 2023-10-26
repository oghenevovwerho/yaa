package yaa.semantic.passes.fs6;

import yaa.ast.EGet;
import yaa.semantic.passes.fs6.results.FieldResult;
import yaa.pojos.YaaInfo;
import yaa.pojos.GlobalData;

import static org.objectweb.asm.Opcodes.*;

public class F6EGet {
  public static YaaInfo get(EGet ctx) {
    var field = ((FieldResult) GlobalData.results.get(GlobalData.fs6.path).get(ctx)).field;
    var name = ctx.name.content;

    if (field.itIsStatic) {
      F6.mw().visitFieldInsn(GETSTATIC, field.owner, name, field.descriptor());
    } else {
      var info = ctx.e.visit(GlobalData.fs6);
      if (info.isUnboundedAndNotPrimitive()) {
        F6.mw().visitTypeInsn(CHECKCAST, info.codeName);
      }
      F6.mw().visitFieldInsn(GETFIELD, field.owner, name, field.descriptor());
    }

    return field.data;
  }
}
