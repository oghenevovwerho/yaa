package yaa.semantic.passes.fs6;

import yaa.ast.EMtd;
import yaa.semantic.passes.fs6.results.CallResult;
import yaa.semantic.passes.fs6.results.InitResult;
import yaa.pojos.YaaClz;
import yaa.pojos.YaaInfo;

import static org.objectweb.asm.Opcodes.*;
import static yaa.pojos.GlobalData.*;

public class F6EMtd {
  public static YaaInfo eMtd(EMtd eMtd) {
    var any$result = fs6.results.get(eMtd);
    if (any$result instanceof InitResult result) {
      //for inner class calls
      var yaaClz = result.clz;
      var opParam = result.init.parameters;

      F6.mw().visitTypeInsn(NEW, yaaClz.codeName);
      F6.mw().visitInsn(DUP);

      var outer$clz = (YaaClz) eMtd.e.visit(fs6);
      for (int j = 0; j < opParam.size(); j++) {
        eMtd.arguments.get(j).visit(fs6);
      }

      F6.mw().visitMethodInsn(
          INVOKESPECIAL, yaaClz.codeName, "<init>",
          result.init.descriptor(outer$clz.descriptor()), false
      );

      return result.clz;
    }
    assert any$result instanceof CallResult;
    var result = (CallResult) any$result;
    var clz = result.clz;
    var mtd = result.mtd;
    var mtd$name = mtd.name;

    if (clz.isPrimitive()) {
      switch (mtd$name) {
        case "box" -> {
          eMtd.e.visit(fs6);
          F6Utils.boxPrimitive(clz.name);
        }
        case "toString" -> {
          eMtd.e.visit(fs6);
          F6PrimitiveMtds.toStringMtd(clz);
        }
        case "getClass" -> {
          F6PrimitiveMtds.getClassMtd(clz);
        }
        case "hashCode" -> {
          eMtd.e.visit(fs6);
          F6PrimitiveMtds.hashCodeMtd(clz);
        }
        case "equals" -> {
          eMtd.e.visit(fs6);
          F6PrimitiveMtds.equalsMtd(clz, eMtd.arguments);
        }
      }
      return mtd.type;
    }
    var info = eMtd.e.visit(fs6);
    if (clz.name.equals(array$name)) {
      F6ArrayMtds.arrayMtd(mtd$name, eMtd.arguments, clz);
      return mtd.type;
    }
    if (info.isUnboundedAndNotPrimitive()) {
      F6.mw().visitTypeInsn(CHECKCAST, info.codeName);
    }
    F6Utils.runArguments(mtd.callInfo.parameters, eMtd.arguments);
    if (mtd.theRemovedVarArgClz != null){
      F6Array.newArray(mtd.theRemovedVarArgClz);
    }
    if (mtd.itIsTraitMtd) {
      F6.mw().visitMethodInsn(
          INVOKEINTERFACE,
          mtd.owner,
          mtd$name,
          mtd.callInfo.descriptor(mtd.theRemovedVarArgClz),
          true
      );
    } else {
      F6.mw().visitMethodInsn(
          INVOKEVIRTUAL,
          mtd.owner,
          mtd$name,
          mtd.callInfo.descriptor(mtd.theRemovedVarArgClz),
          false
      );
    }
    return mtd.type;
  }
}
