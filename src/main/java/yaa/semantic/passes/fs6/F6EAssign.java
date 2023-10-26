package yaa.semantic.passes.fs6;

import yaa.ast.Assign;
import yaa.ast.EGet;
import yaa.semantic.passes.fs6.results.FieldResult;
import yaa.pojos.GlobalData;

import static org.objectweb.asm.Opcodes.PUTFIELD;

public class F6EAssign {
  public static void e$assign(Assign assign, EGet eGet) {
    var result = (FieldResult) GlobalData.results.get(GlobalData.fs6.path).get(assign);
    var field = result.field;

    eGet.e.visit(GlobalData.fs6);
    assign.e2.visit(GlobalData.fs6);
    F6.mw().visitFieldInsn(
      PUTFIELD,
      field.owner,
      field.field$name,
      field.descriptor()
    );
  }
}
