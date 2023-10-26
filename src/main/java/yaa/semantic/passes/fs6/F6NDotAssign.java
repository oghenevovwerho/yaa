package yaa.semantic.passes.fs6;

import yaa.ast.Assign;
import yaa.ast.VGet;
import yaa.semantic.passes.fs6.results.FieldResult;
import yaa.pojos.GlobalData;

import static yaa.semantic.passes.fs6.F6.mw;
import static org.objectweb.asm.Opcodes.*;

public class F6NDotAssign {
  public static void nameDotAssign(Assign assign, VGet vGet) {
    var result = (FieldResult) GlobalData.results.get(GlobalData.fs6.path).get(assign);
    var field = result.field;

    if (field.itIsStatic) {
      assign.e2.visit(GlobalData.fs6);
      mw().visitFieldInsn(
        PUTSTATIC,
        field.data.codeName,
        field.field$name,
        field.descriptor()
      );
    } else {
      F6Name.id(vGet.n1.content);
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
