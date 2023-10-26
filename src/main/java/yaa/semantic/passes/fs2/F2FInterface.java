package yaa.semantic.passes.fs2;

import yaa.ast.NewFunctionalInterface;
import yaa.pojos.BoundState;
import yaa.pojos.YaaClz;
import yaa.pojos.YaaClzVariance;
import yaa.pojos.YaaError;
import yaa.pojos.jMold.JMold;

import static yaa.pojos.GlobalData.fs2;

public class F2FInterface {
  public static void fInterface(NewFunctionalInterface ctx) {
    var currentClz = (YaaClz) fs2.getSymbol(ctx.placeOfUse());

    int clzInputIndex = 0;
    for (var type$param : ctx.typeParams) {
      var paramName = type$param.paramName.content;
      var inputtedClz = new YaaClz(paramName);
      inputtedClz.boundState = BoundState.clz_bound;
      if (type$param.type == null) {
        inputtedClz.parent = new JMold().newClz("java.lang.Object");
      } else {
        inputtedClz.parent = YaaClz.f2Clz(type$param.type);
      }
      inputtedClz.cbIndex = clzInputIndex;
      inputtedClz.variance = type$param.variance;
      currentClz.inputted.add(inputtedClz);
      fs2.putSymbol(paramName, inputtedClz);
      clzInputIndex = clzInputIndex + 1;
    }

    var newMtd = currentClz.instanceMethods.get(ctx.name.content).methods.get(0);

    for (int i = 0; i < ctx.parameters.size(); i++) {
      var param = ctx.parameters.get(i);
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
    if (ctx.type != null) {
      var mtdType = ctx.type.visit(fs2);
      if (mtdType.boundState == BoundState.clz_bound) {
        newMtd.hasClzTypeParam = true;
      }
      if (mtdType instanceof YaaClz clz && clz.variance == YaaClzVariance.contravariant) {
        throw new YaaError(
            ctx.type.placeOfUse(),
            "The type returned by the method resolves to a contravariant bound",
            "Only a method's parameter type can use a contravariant bound type"
        );
      }
      newMtd.type = mtdType;
    }
  }
}