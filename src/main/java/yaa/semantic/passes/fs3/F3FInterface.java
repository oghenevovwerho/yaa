package yaa.semantic.passes.fs3;

import yaa.ast.NewFunctionalInterface;
import yaa.pojos.YaaClz;

import static yaa.pojos.GlobalData.fs3;

public class F3FInterface {
  public static void fInterface(NewFunctionalInterface ctx) {
    var f3Clz = (YaaClz) fs3.getSymbol(ctx.placeOfUse());

    var newMtd = f3Clz.instanceMethods.get(ctx.name.content).methods.get(0);
    for (int i = 0; i < newMtd.parameters.size(); i++) {
      var param = ctx.parameters.get(i);
      var paramType = param.type.visit(fs3);
      newMtd.parameters.set(i, paramType);
    }
    if (ctx.type != null) {
      newMtd.type = ctx.type.visit(fs3);
    }
  }
}