package yaa.semantic.passes.fs2;

import yaa.ast.NewFun;
import yaa.pojos.BoundState;
import yaa.pojos.MtdIsWhat;
import yaa.pojos.YaaClz;
import yaa.pojos.YaaError;
import yaa.pojos.*;

import static yaa.pojos.GlobalData.*;

public class F2NFun {
  public static void f2NewFunction(NewFun newFun) {
    //preventFunctionShadowing(newFun);
    fs2.pushTable(newFun);
    var newMtd = (YaaFun) fs2.getSymbol(newFun.placeOfUse());
    if (newFun.itIsWhat == MtdIsWhat.topMtd) {
      newMtd.owner = topClzCodeName.get(fs2.path);
    } else if (newFun.itIsWhat == MtdIsWhat.staticMtd) {
      newMtd.owner = topClzCodeName.get(fs2.path);
    }

    int mtdInputIndex = 0;
    for (var type$param : newFun.typeParams) {
      var paramName = type$param.paramName.content;
      var inputtedClz = new YaaClz(paramName);
      inputtedClz.boundState = BoundState.mtd_bound;
      inputtedClz.parent = YaaClz.f2Clz(type$param.type);
      inputtedClz.mbIndex = mtdInputIndex;
      inputtedClz.variance = type$param.variance;
      newMtd.inputted.add(inputtedClz);
      fs2.putSymbol(paramName, inputtedClz);
      mtdInputIndex = mtdInputIndex + 1;
    }

    for (int i = 0; i < newFun.parameters.size(); i++) {
      var param = newFun.parameters.get(i);
      var paramType = param.type.visit(fs2);
      newMtd.parameters.add(paramType);
      if (paramType.boundState == BoundState.clz_bound) {
        newMtd.hasClzTypeParam = true;
      }
      if (paramType instanceof YaaClz clz && clz.variance == YaaClzVariance.covariant) {
        throw new YaaError(
            param.type.placeOfUse(),
            "The type used by the parameter type resolves to a covariant bound",
            "Only method return types are allowed to use covariant bound types"
        );
      }
    }
    if (newFun.type != null) {
      var mtdType = newFun.type.visit(fs2);
      if (mtdType.boundState == BoundState.clz_bound) {
        newMtd.hasClzTypeParam = true;
      }
      if (mtdType instanceof YaaClz clz && clz.variance == YaaClzVariance.contravariant) {
        throw new YaaError(
            newFun.type.placeOfUse(),
            "The type returned by the method resolves to a contravariant bound",
            "Only a method's parameter type can use a contravariant bound type"
        );
      }
      newMtd.type = mtdType;
    }
    newFun.stmt.visit(fs2);
    fs2.popTable();
  }

//  private static void preventFunctionShadowing(NewFun newFun) {
//    var mtdName = newFun.name.content;
//    if (GlobalData.fs2.table.parent != null) {
//      var defined = GlobalData.fs2.table.parent.getFunction(mtdName);
//      if (defined != null && !defined.isPredefined) {
//        throw new YaaError(
//          newFun.address(), newFun.toString(),
//          "The function above shadows the one at "
//            + defined.methods.get(0).lineInfo,
//          "Function overloading is only allowed in the same scope"
//        );
//      }
//    }
//  }
}