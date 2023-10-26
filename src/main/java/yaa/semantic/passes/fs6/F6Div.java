package yaa.semantic.passes.fs6;

import yaa.ast.Divide;
import yaa.semantic.passes.fs6.results.CallResult;
import yaa.pojos.YaaInfo;
import org.objectweb.asm.Opcodes;
import yaa.pojos.GlobalData;

import static yaa.semantic.passes.fs6.F6Utils.itIsPrimitive;

public class F6Div {
  public static YaaInfo div(Divide div) {
    var mtd = ((CallResult) GlobalData.results.get(GlobalData.fs6.path).get(div)).mtd;
    var owner = mtd.owner;
    var right = mtd.parameters.get(0);

    if (itIsPrimitive(div.e1) && itIsPrimitive(div.e2)) {
      var v1 = F6CTimeOp.compTimeDouble(div.e1);
      var v2 = F6CTimeOp.compTimeDouble(div.e2);
      return F6CTimeOp.compOp(v1 / v2, mtd.type);
    }

    if (!F6Utils.itIsPrimitive(owner)) {
      return F6Utils.operatorMtd(div.e1, div.e2, mtd);
    }

    if (owner.equals(GlobalData.int$name)) {
      var rightName = right.name;
      switch (rightName) {
        case GlobalData.int$name -> {
          div.e1.visit(GlobalData.fs6);
          div.e2.visit(GlobalData.fs6);
          F6.mw().visitInsn(Opcodes.IDIV);
        }
        case GlobalData.double$name -> {
          div.e1.visit(GlobalData.fs6);
          F6.mw().visitInsn(Opcodes.I2D);
          div.e2.visit(GlobalData.fs6);
          F6.mw().visitInsn(Opcodes.DDIV);
        }
        case GlobalData.float$name -> {
          div.e1.visit(GlobalData.fs6);
          F6.mw().visitInsn(Opcodes.I2F);
          div.e2.visit(GlobalData.fs6);
          F6.mw().visitInsn(Opcodes.FDIV);
        }
        case GlobalData.long$name -> {
          div.e1.visit(GlobalData.fs6);
          F6.mw().visitInsn(Opcodes.I2L);
          div.e2.visit(GlobalData.fs6);
          F6.mw().visitInsn(Opcodes.LDIV);
        }
      }
    }
    if (owner.equals(GlobalData.float$name)) {
      switch (right.name) {
        case GlobalData.float$name -> {
          div.e1.visit(GlobalData.fs6);
          div.e2.visit(GlobalData.fs6);
          F6.mw().visitInsn(Opcodes.FDIV);
        }
        case GlobalData.double$name -> {
          div.e1.visit(GlobalData.fs6);
          F6.mw().visitInsn(Opcodes.F2D);
          div.e2.visit(GlobalData.fs6);
          F6.mw().visitInsn(Opcodes.DDIV);
        }
        case GlobalData.int$name -> {
          div.e1.visit(GlobalData.fs6);
          div.e2.visit(GlobalData.fs6);
          F6.mw().visitInsn(Opcodes.I2F);
          F6.mw().visitInsn(Opcodes.FDIV);
        }
        case GlobalData.long$name -> {
          div.e1.visit(GlobalData.fs6);
          div.e2.visit(GlobalData.fs6);
          F6.mw().visitInsn(Opcodes.L2F);
          F6.mw().visitInsn(Opcodes.FDIV);
        }
      }
    }
    if (owner.equals(GlobalData.double$name)) {
      switch (right.name) {
        case GlobalData.double$name -> {
          div.e1.visit(GlobalData.fs6);
          div.e2.visit(GlobalData.fs6);
          F6.mw().visitInsn(Opcodes.DDIV);
        }
        case GlobalData.float$name -> {
          div.e1.visit(GlobalData.fs6);
          div.e2.visit(GlobalData.fs6);
          F6.mw().visitInsn(Opcodes.F2D);
          F6.mw().visitInsn(Opcodes.DDIV);
        }
        case GlobalData.int$name -> {
          div.e1.visit(GlobalData.fs6);
          div.e2.visit(GlobalData.fs6);
          F6.mw().visitInsn(Opcodes.I2D);
          F6.mw().visitInsn(Opcodes.DDIV);
        }
        case GlobalData.long$name -> {
          div.e1.visit(GlobalData.fs6);
          div.e2.visit(GlobalData.fs6);
          F6.mw().visitInsn(Opcodes.L2D);
          F6.mw().visitInsn(Opcodes.DDIV);
        }
      }
    }
    if (owner.equals(GlobalData.long$name)) {
      switch (right.name) {
        case GlobalData.long$name -> {
          div.e1.visit(GlobalData.fs6);
          div.e2.visit(GlobalData.fs6);
          F6.mw().visitInsn(Opcodes.LDIV);
        }
        case GlobalData.double$name -> {
          div.e1.visit(GlobalData.fs6);
          F6.mw().visitInsn(Opcodes.L2D);
          div.e2.visit(GlobalData.fs6);
          F6.mw().visitInsn(Opcodes.DDIV);
        }
        case GlobalData.float$name -> {
          div.e1.visit(GlobalData.fs6);
          F6.mw().visitInsn(Opcodes.L2F);
          div.e2.visit(GlobalData.fs6);
          F6.mw().visitInsn(Opcodes.FDIV);
        }
        case GlobalData.int$name -> {
          div.e1.visit(GlobalData.fs6);
          div.e2.visit(GlobalData.fs6);
          F6.mw().visitInsn(Opcodes.I2L);
          F6.mw().visitInsn(Opcodes.LDIV);
        }
      }
    }
    return mtd.type;
  }
}
