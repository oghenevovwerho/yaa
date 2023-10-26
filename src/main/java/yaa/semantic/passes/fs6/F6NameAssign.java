package yaa.semantic.passes.fs6;

import yaa.ast.Assign;
import yaa.pojos.YaaField;
import yaa.pojos.FieldIsWhat;
import yaa.pojos.GlobalData;

import static yaa.semantic.passes.fs6.F6.mw;
import static org.objectweb.asm.Opcodes.*;

public class F6NameAssign {
  public static void plain$assign(Assign assign, String variableName) {
    var newFieldIndex = GlobalData.fs6.variables.peek().indexOf(variableName);
    if (newFieldIndex != null) {
      var fieldValue = assign.e2.visit(GlobalData.fs6);
      switch (fieldValue.name) {
        case GlobalData.long$name -> mw().visitVarInsn(LSTORE, newFieldIndex);
        case GlobalData.double$name -> mw().visitVarInsn(DSTORE, newFieldIndex);
        case GlobalData.float$name -> mw().visitVarInsn(FSTORE, newFieldIndex);
        default -> {
          if (fieldValue.isPrimitive()) {
            mw().visitVarInsn(ISTORE, newFieldIndex);
          } else {
            mw().visitVarInsn(ASTORE, newFieldIndex);
          }
        }
      }
      return;
    }
    var potentialField = GlobalData.fs6.getSymbol(variableName);
    if (potentialField instanceof YaaField field) {
      if (field.itIsWhat == FieldIsWhat.top$field) {
        assign.e2.visit(GlobalData.fs6);
        mw().visitFieldInsn(
          PUTSTATIC,
          field.owner,
          field.field$name,
          field.descriptor()
        );
      } else {
        mw().visitVarInsn(ALOAD, 0);
        assign.e2.visit(GlobalData.fs6);
        mw().visitFieldInsn(
          PUTFIELD,
          field.owner,
          field.field$name,
          field.descriptor()
        );
      }
    }
  }
}
