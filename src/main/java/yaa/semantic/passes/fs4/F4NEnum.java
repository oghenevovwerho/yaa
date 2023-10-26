package yaa.semantic.passes.fs4;

import yaa.ast.NewEnum;
import yaa.semantic.handlers.VDefOp;
import yaa.pojos.YaaClz;
import yaa.pojos.GlobalData;

import static yaa.pojos.GlobalData.fs4;
import static yaa.semantic.passes.fs4.F4NClass.checkObjectMtds;

public class F4NEnum {
  public static void newEnum(NewEnum newEnum) {
    fs4.pushTable(newEnum);
    var currentClz = (YaaClz) fs4.getSymbol(newEnum.placeOfUse());
    GlobalData.topClz.push(currentClz);

    for (var fun : newEnum.methods) {
      fun.visit(fs4);
    }

    for (var run_block : newEnum.runBlocks) {
      fs4.pushTable(run_block);
      for (var stmt : run_block.stmts) {
        stmt.visit(fs4);
      }
      fs4.popTable();
    }

    for (var def : newEnum.vDefinitions) {
      VDefOp.defOp(def);
    }

    for (var new$class : newEnum.classes) {
      new$class.visit(fs4);
    }

    for (var new$enum : newEnum.enums) {
      new$enum.visit(fs4);
    }

    for (var vDec : newEnum.vDeclarations) {
      vDec.visit(fs4);
    }

    for (var init : newEnum.inits) {
      init.visit(fs4);
    }

    if (newEnum.toStringParentMtd != null) {
      newEnum.toStringParentMtd.visit(fs4);
    }

    var blocks = newEnum.implementations;
    for (int i = 0; i < blocks.size(); i++) {
      var block = blocks.get(i);
      var stored$block = currentClz.clz$traits.get(i);
      YaaClz.fsClz(block.type);//check type
      F4Parent.implementBlock(blocks.get(i), currentClz, stored$block);

      if (block.isClass) {
        checkObjectMtds(block.methods);
      }
    }

    GlobalData.topClz.pop();
    fs4.popTable();
  }
}
