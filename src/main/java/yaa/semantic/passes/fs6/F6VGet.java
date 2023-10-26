package yaa.semantic.passes.fs6;

import yaa.ast.VGet;
import yaa.semantic.passes.fs6.results.FieldResult;
import yaa.pojos.YaaClz;
import yaa.pojos.YaaInfo;
import yaa.pojos.jMold.JMold;
import yaa.pojos.GlobalData;

import static org.objectweb.asm.Opcodes.*;

public class F6VGet {
  public static YaaInfo get(VGet ctx) {
    var result = ((FieldResult) GlobalData.results.get(GlobalData.fs6.path).get(ctx));
    var field = result.field;
    var name = ctx.n2.content;
    if (field.isEnumField) {
      F6.mw().visitFieldInsn(
          GETSTATIC,
          field.data.codeName,
          name,
          field.descriptor()
      );
      return field.data;
    } else if (name.equals("class")) {
      if (field.isPrimitive()) {
        F6PrimitiveMtds.getClassMtd((YaaClz) field.data);
      } else {
        F6.mw().visitFieldInsn(
            GETSTATIC,
            field.data.codeName,
            "TYPE",
            "Ljava/lang/Class;"
        );
      }
      return new JMold().newClz("java.lang.Class");
    } else if (field.itIsStatic) {
      F6.mw().visitFieldInsn(
          GETSTATIC, field.owner, name, field.descriptor()
      );
    } else {
      F6Name.id(ctx.n1.content);
      F6.mw().visitFieldInsn(GETFIELD, field.owner, name, field.descriptor());
    }

    return field.data;
  }
}
