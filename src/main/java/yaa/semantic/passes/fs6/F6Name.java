package yaa.semantic.passes.fs6;

import yaa.pojos.GlobalData;
import yaa.pojos.YaaField;
import yaa.pojos.YaaInfo;

import static org.objectweb.asm.Opcodes.CHECKCAST;
import static org.objectweb.asm.Opcodes.GETSTATIC;
import static yaa.semantic.passes.fs6.F6.mw;

public class F6Name {
  public static YaaInfo id(String name) {
    var info = GlobalData.fs6.getSymbol(name);
    var field = (YaaField) info;
    //enum constants floating in namespace
    if (field.isEnumField) {
      mw().visitFieldInsn(
        GETSTATIC,
        field.data.codeName,
        name,
        field.descriptor()
      );
      return field.data;
    } else if (field.isPredefined) {
      return F6PredefinedFields.field(field);
    }
    field.generateReferenceCode();
    if (field.data.cbIndex > -1) {
      return field.data;
    }
    if (field.data.mbIndex > -1) {
      return field.data;
    }
    if (field.isPrimitive()) {
      return field.data;
    }
    if (field.isUnboundedAndNotPrimitive()) {
      mw().visitTypeInsn(CHECKCAST, field.data.codeName);
    }
    return field.data;
  }
}