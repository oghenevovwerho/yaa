package yaa.semantic.passes.fs6;

import yaa.ast.Power;
import yaa.semantic.passes.fs6.results.CallResult;
import yaa.pojos.YaaInfo;
import yaa.pojos.GlobalData;

import static yaa.semantic.passes.fs6.F6Utils.*;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;

public class F6Pow {
  public static YaaInfo pow(Power pow) {
    var mtd = ((CallResult) GlobalData.results.get(GlobalData.fs6.path).get(pow)).mtd;
    var owner = mtd.owner;
    var right = mtd.parameters.get(0);

    if (itIsPrimitive(pow.e1) && itIsPrimitive(pow.e2)) {
      var v1 = F6CTimeOp.compTimeDouble(pow.e1);
      var v2 = F6CTimeOp.compTimeDouble(pow.e2);
      return F6CTimeOp.compOp(Math.pow(v1, v2), mtd.type);
    }

    if (!F6Utils.itIsPrimitive(owner)) {
      return operatorMtd(pow.e1, pow.e2, mtd);
    }

    pow.e1.visit(GlobalData.fs6);
    castTo(owner, GlobalData.double$name);
    pow.e2.visit(GlobalData.fs6);
    castTo(right.name, GlobalData.double$name);
    F6.mw().visitMethodInsn(INVOKESTATIC, "java/lang/Math", "pow", "(DD)D", false);
    return mtd.type;
  }
}