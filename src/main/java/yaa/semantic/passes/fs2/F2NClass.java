package yaa.semantic.passes.fs2;

import yaa.ast.NewClass;
import yaa.pojos.BoundState;
import yaa.pojos.YaaClz;
import yaa.pojos.jMold.JMold;

import static yaa.pojos.GlobalData.fs2;

public class F2NClass {
  public static void newType(NewClass newClass) {
    fs2.pushTable(newClass);

    if (newClass.typeParams.size() > 0) {
      var currentClz = (YaaClz) fs2.getSymbol(newClass.placeOfUse());
      int clzInputIndex = 0;
      for (var type$param : newClass.typeParams) {
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
    }

    if (newClass.init != null) {
      F2Init.f2Init(newClass.init);
    }

    for (var fun : newClass.methods) {
      F2NFun.f2NewFunction(fun);
    }

    for (var new$enum : newClass.enums) {
      new$enum.visit(fs2);
    }

    for (var new$class : newClass.classes) {
      new$class.visit(fs2);
    }

    for (var fInterface : newClass.fInterfaces) {
      fInterface.visit(fs2);
    }

    for (var mtd_list : newClass.parentMtds.values()) {
      for (var parentMtd : mtd_list) {
        F2ParentMtd.f2ParentMtd(parentMtd);
      }
    }

    for (var trait$clz : newClass.implementations) {
      F2BlockClz.f2BlockInClz(trait$clz);
    }

    for (var run_block : newClass.runBlocks) {
      fs2.pushTable(run_block);
      for (var stmt : run_block.stmts) {
        stmt.visit(fs2);
      }
      fs2.popTable();
    }

    fs2.popTable();
  }
}