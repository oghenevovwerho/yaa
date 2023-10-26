package yaa.semantic.passes.fs2;

import yaa.ast.NewRecord;
import yaa.pojos.BoundState;
import yaa.pojos.YaaClz;

import static yaa.pojos.GlobalData.fs2;

public class F2NRecord {
  public static void newRecord(NewRecord newRecord) {
    fs2.pushTable(newRecord);
    var currentClz = (YaaClz) fs2.getSymbol(newRecord.placeOfUse());

    int clzInputIndex = 0;
    for (var type$param : newRecord.typeParams) {
      var paramName = type$param.paramName.content;
      var inputtedClz = new YaaClz(paramName);
      inputtedClz.boundState = BoundState.clz_bound;
      inputtedClz.parent = YaaClz.f2Clz(type$param.type);
      inputtedClz.cbIndex = clzInputIndex;
      inputtedClz.variance = type$param.variance;
      currentClz.inputted.add(inputtedClz);
      fs2.putSymbol(paramName, inputtedClz);
      clzInputIndex = clzInputIndex + 1;
    }

    for (var init : newRecord.inits) {
      F2Init.f2Init(init);
    }

    for (var fun : newRecord.methods) {
      F2NFun.f2NewFunction(fun);
    }

    for (var new$enum : newRecord.enums) {
      new$enum.visit(fs2);
    }

    for (var new$class : newRecord.classes) {
      new$class.visit(fs2);
    }

    for (var trait$clz : newRecord.parents) {
      F2BlockClz.f2BlockInClz(trait$clz);
    }

    for (var run_block : newRecord.runBlocks) {
      fs2.pushTable(run_block);
      for (var stmt : run_block.stmts) {
        stmt.visit(fs2);
      }
      fs2.popTable();
    }

    fs2.popTable();
  }
}
