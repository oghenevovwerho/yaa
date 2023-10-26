package yaa.semantic.passes.fs2;

import yaa.ast.OverBlock;
import yaa.ast.NewFun;
import yaa.pojos.*;

import static yaa.pojos.GlobalData.fs2;

public class F2BlockClz {
  public static void f2BlockInClz(OverBlock block) {
    fs2.pushTable(block);
    for (var parent$mtd$pack : block.methods.values()) {
      for (var parent$mtd : parent$mtd$pack) {
        overriddenMtd(parent$mtd);
      }
    }
    fs2.popTable();
  }

  //I am handling this here so that the overridden methods of
  //an enclosing class (which might also act as the parent),
  //can be overridden with a "method shadow error" being thrown
  //It prevents some redundant tests like if a method is top
  //level or not.
  private static void overriddenMtd(NewFun newFun) {
    fs2.pushTable(newFun);
    var newMtd = (YaaFun) fs2.getSymbol(newFun.placeOfUse());

    int mtdInputIndex = 0;
    for (var type$param : newFun.typeParams) {
      var paramName = type$param.paramName.content;
      var inputtedClz = new YaaClz(paramName);
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
}
