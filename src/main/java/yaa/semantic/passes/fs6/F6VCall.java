package yaa.semantic.passes.fs6;

import yaa.ast.VCall;
import yaa.pojos.GlobalData;
import yaa.pojos.MtdIsWhat;
import yaa.pojos.YaaInfo;
import yaa.semantic.passes.fs6.results.CallResult;
import yaa.semantic.passes.fs6.results.InitResult;

import static org.objectweb.asm.Opcodes.*;
import static yaa.semantic.passes.fs6.F6.mw;

public class F6VCall {
  public static YaaInfo call(VCall call) {
    var result = GlobalData.fs6.results.get(call);
    if (result instanceof CallResult callResult) {
      if (callResult.isPredefined) {
        return F6PredefinedMtd.predefined(callResult, call);
      }
      var mtd = callResult.mtd;
      var mtd$name = mtd.name;
      if (mtd.mtdIsWhat == MtdIsWhat.classMtd) {
        if (mtd.privacy == 2) {
          mw().visitVarInsn(ALOAD, 0);
          F6Utils.runArguments(mtd.callInfo.parameters, call.arguments);
          if (mtd.theRemovedVarArgClz != null){
            //this is always an array
            F6Array.newArray(mtd.theRemovedVarArgClz);
          }
          mw().visitMethodInsn(
              INVOKESPECIAL, mtd.owner,
              mtd$name,
              mtd.callInfo.descriptor(mtd.theRemovedVarArgClz), false
          );
        } else {
          mw().visitVarInsn(ALOAD, 0);
          F6Utils.runArguments(mtd.callInfo.parameters, call.arguments);
          if (mtd.theRemovedVarArgClz != null){
            //this is always an array
            F6Array.newArray(mtd.theRemovedVarArgClz);
          }
          mw().visitMethodInsn(
              INVOKEVIRTUAL, mtd.owner,
              mtd$name,
              mtd.callInfo.descriptor(mtd.theRemovedVarArgClz), false
          );
        }
      } else {
        for (var closed$field : mtd.closures.values()) {
          F6Name.id(closed$field.field$name);
        }
        F6Utils.runArguments(mtd.callInfo.parameters, call.arguments);
        if (mtd.theRemovedVarArgClz != null){
          F6Array.newArray(mtd.theRemovedVarArgClz);
        }
        mw().visitMethodInsn(
            INVOKESTATIC, mtd.owner,
            mtd$name,
            mtd.callInfo.descriptor(mtd.theRemovedVarArgClz), mtd.itIsTraitMtd
        );
      }
      return mtd.type;
    } else {
      assert result instanceof InitResult;
      return F6ClzInit.init((InitResult) result, call);
    }
  }
}
