package yaa.semantic.passes.fs2;

import yaa.ast.NewEnum;

import static yaa.pojos.GlobalData.fs2;

public class F2NEnum {
  public static void newEnum(NewEnum newEnum) {
    fs2.pushTable(newEnum);

    for (var fun : newEnum.methods) {
      F2NFun.f2NewFunction(fun);
    }

    for (var init : newEnum.inits) {
      F2Init.f2Init(init);
    }

    for (var new$enum : newEnum.enums) {
      new$enum.visit(fs2);
    }

    for (var new$class : newEnum.classes) {
      new$class.visit(fs2);
    }

    if (newEnum.toStringParentMtd != null) {
      F2ParentMtd.f2ParentMtd(newEnum.toStringParentMtd);
    }

    for (var run_block : newEnum.runBlocks) {
      fs2.pushTable(run_block);
      for (var stmt : run_block.stmts) {
        stmt.visit(fs2);
      }
      fs2.popTable();
    }

    for (var trait$clz : newEnum.implementations) {
      F2BlockClz.f2BlockInClz(trait$clz);
    }

    fs2.popTable();
  }
}
