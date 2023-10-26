package yaa.semantic.passes.fs6;

import yaa.ast.RootTo;
import yaa.semantic.passes.fs6.results.CallResult;
import yaa.pojos.YaaInfo;
import org.objectweb.asm.Opcodes;
import yaa.pojos.GlobalData;

import static yaa.semantic.passes.fs6.F6.mw;
import static yaa.semantic.passes.fs6.F6CTimeOp.compOp;
import static yaa.semantic.passes.fs6.F6CTimeOp.compTimeDouble;
import static yaa.semantic.passes.fs6.F6Utils.*;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;

public class F6Root {
  public static YaaInfo root(RootTo root) {
    var mtd = ((CallResult) GlobalData.results.get(GlobalData.fs6.path).get(root)).mtd;
    var owner = mtd.owner;
    var right = mtd.parameters.get(0);

    if (itIsPrimitive(root.e1) && itIsPrimitive(root.e2)) {
      var v1 = compTimeDouble(root.e1);
      var v2 = compTimeDouble(root.e2);
      return compOp(Math.pow(v1, 1 / v2), mtd.type);
    }

    if (!itIsPrimitive(owner)) {
      return operatorMtd(root.e1, root.e2, mtd);
    }

    root.e1.visit(GlobalData.fs6);
    castTo(owner, GlobalData.double$name);

    mw().visitInsn(Opcodes.ICONST_1);
    castTo(GlobalData.int$name, GlobalData.double$name);
    root.e2.visit(GlobalData.fs6);
    castTo(right.name, GlobalData.double$name);
    mw().visitInsn(Opcodes.DDIV);

    mw().visitMethodInsn(INVOKESTATIC, "java/lang/Math", "pow", "(DD)D", false);
    return mtd.type;
  }
}
