package yaa.semantic.passes.fs6;

import org.objectweb.asm.Type;
import yaa.ast.VMtd;
import yaa.semantic.passes.fs6.results.CallResult;
import yaa.pojos.YaaInfo;
import yaa.pojos.GlobalData;

import static org.objectweb.asm.Opcodes.*;
import static yaa.pojos.GlobalData.array$name;
import static yaa.semantic.passes.fs6.F6.mw;

public class F6VMtd {
  public static YaaInfo vMtd(VMtd vMtd) {
    var name = vMtd.eName.content;

    var result = ((CallResult) GlobalData.fs6.results.get(vMtd));
    var clz = result.clz;
    var mtd = result.mtd;
    var mtd$name = mtd.name;


    if (mtd$name.equals("getClass")) {
      mw().visitLdcInsn(Type.getType(clz.descriptor()));
      return mtd.type;
    }

    if (mtd.itIsStatic) {
      F6Utils.runArguments(mtd.callInfo.parameters, vMtd.arguments);
      if (mtd.theRemovedVarArgClz != null) {
        //this is always an array
        F6Array.newArray(mtd.theRemovedVarArgClz);
      }
      mw().visitMethodInsn(
          INVOKESTATIC,
          mtd.owner,
          mtd$name,
          mtd.callInfo.descriptor(mtd.theRemovedVarArgClz),
          mtd.itIsTraitMtd
      );
      return mtd.type;
    }
    if (clz.isPrimitive()) {
      switch (mtd$name) {
        case "box" -> {
          F6Name.id(name);
          F6Utils.boxPrimitive(clz.name);
        }
        case "toString" -> {
          F6Name.id(name);
          F6PrimitiveMtds.toStringMtd(clz);
        }
        case "getClass" -> {
          F6PrimitiveMtds.getClassMtd(clz);
        }
        case "hashCode" -> {
          F6Name.id(name);
          F6PrimitiveMtds.hashCodeMtd(clz);
        }
        case "equals" -> {
          F6Name.id(name);
          F6PrimitiveMtds.equalsMtd(clz, vMtd.arguments);
        }
      }
      return mtd.type;
    }
    var info = F6Name.id(name);
    if (clz.name.equals(array$name)) {
      F6ArrayMtds.arrayMtd(mtd$name, vMtd.arguments, clz);
    } else {
      F6Utils.runArguments(mtd.callInfo.parameters, vMtd.arguments);
      if (mtd.theRemovedVarArgClz != null) {
        F6Array.newArray(mtd.theRemovedVarArgClz);
      }
      if (mtd.itIsTraitMtd) {
        mw().visitMethodInsn(
            INVOKEINTERFACE,
            mtd.owner,
            mtd$name,
            mtd.callInfo.descriptor(mtd.theRemovedVarArgClz),
            true
        );
      } else {
        mw().visitMethodInsn(
            INVOKEVIRTUAL,
            mtd.owner,
            mtd$name,
            mtd.callInfo.descriptor(mtd.theRemovedVarArgClz),
            false
        );
      }
    }
    return mtd.type;
  }
}
