package yaa.semantic.passes.fs5;

import yaa.ast.NewRecord;
import yaa.pojos.YaaClz;
import yaa.pojos.YaaError;

import static yaa.pojos.GlobalData.*;
import static yaa.pojos.TypeCategory.*;

public class F5NRecord {
  public static void newRecord(NewRecord newRecord) {
    fs5.pushTable(newRecord);
    var currentClz = (YaaClz) fs5.getSymbol(newRecord.placeOfUse());
    topClz.push(currentClz);

    for (var init : newRecord.inits) {
      init.visit(fs5);
    }

    for (var fun : newRecord.methods) {
      var mtd$name = fun.name.content;
      var parentResult = currentClz.getShadowMethod(mtd$name);
      if (parentResult != null) {
        var mtdWord = parentResult.pack.methods.size()
          == 1 ? "method" : "methods";
        var pronoun = parentResult.pack.methods.size()
          == 1 ? " is " : " are ";
        throw new YaaError(
          fun.placeOfUse(), fun.toString(),
          "The method above shadows the " + mtdWord + " below",
          parentResult.pack.toString(),
          "The shadowed " + mtdWord + pronoun + "defined in the "
            + what(parentResult.clz) + " below",
          parentResult.clz.toString()
        );
      }
      fun.visit(fs5);
    }

    for (var new$class : newRecord.classes) {
      new$class.visit(fs5);
    }

    for (var new$enum : newRecord.enums) {
      new$enum.visit(fs5);
    }

    for (var run_block : newRecord.runBlocks) {
      fs5.pushTable(run_block);
      for (var stmt : run_block.stmts) {
        stmt.visit(fs5);
      }
      fs5.popTable();
    }

    var blocks = newRecord.parents;
    for (int i = 0; i < blocks.size(); i++) {
      var block = blocks.get(i);
      var trait$clz = currentClz.clz$traits.get(i);
      if (!(trait$clz.category == enum_c || trait$clz.category == trait_c)) {
        F5Parent.f5parentBlock(block);
      } else {
        F5TraitBlock.f5TraitBlock(block);
      }
    }

    topClz.pop();
    fs5.popTable();
  }

  private static String what(YaaClz clz) {
    if (clz.category == trait_c) {
      return "trait";
    } else if (clz.category == enum_c) {
      return "enum";
    } else if (clz.category == record_c) {
      return "record";
    }
    return "class";
  }
}