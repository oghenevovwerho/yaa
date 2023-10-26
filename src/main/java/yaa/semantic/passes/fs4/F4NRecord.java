package yaa.semantic.passes.fs4;

import yaa.ast.NewRecord;
import yaa.pojos.YaaClz;
import yaa.semantic.handlers.VDefOp;

import static yaa.pojos.GlobalData.*;
import static yaa.pojos.GlobalData.fs4;

public class F4NRecord {
  public static void newRecord(NewRecord newRecord) {
    fs4.pushTable(newRecord);
    var currentClz = (YaaClz) fs4.getSymbol(newRecord.placeOfUse());
    topClz.push(currentClz);

    for (var def : newRecord.vDefinitions) {
      VDefOp.defOp(def);
    }

    for (var run_block : newRecord.runBlocks) {
      fs4.pushTable(run_block);
      for (var stmt : run_block.stmts) {
        stmt.visit(fs4);
      }
      fs4.popTable();
    }

    for (var vDec : newRecord.vDeclarations) {
      vDec.visit(fs4);
    }

    for (var init : newRecord.inits) {
      init.visit(fs4);
    }

    for (var fun : newRecord.methods) {
      fun.visit(fs4);
    }

    for (var new$class : newRecord.classes) {
      new$class.visit(fs4);
    }

    for (var new$enum : newRecord.enums) {
      new$enum.visit(fs4);
    }

    var blocks = newRecord.parents;
    for (int i = 0; i < blocks.size(); i++) {
      var block = blocks.get(i);
      var stored$block = currentClz.clz$traits.get(i);
      YaaClz.fsClz(block.type);//check type
      F4Parent.implementBlock(blocks.get(i), currentClz, stored$block);

      if (block.isClass) {
        F4NClass.checkObjectMtds(block.methods);
      }
    }

    topClz.pop();
    fs4.popTable();
  }
}