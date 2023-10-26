package yaa.semantic.passes.fs3;

import yaa.ast.NewRecord;
import yaa.ast.OverBlock;
import yaa.pojos.GlobalData;
import yaa.pojos.TypeCategory;
import yaa.pojos.YaaClz;
import yaa.pojos.YaaError;

import java.util.HashMap;
import java.util.Map;

import static yaa.pojos.GlobalData.*;
import static yaa.pojos.TypeCategory.class_c;

public class F3NRecord {
  public static void newRecord(NewRecord newRecord) {
    var typeName = newRecord.name.content;
    fs3.pushTable(newRecord);
    var currentClz = (YaaClz) fs3.getSymbol(newRecord.placeOfUse());

    for (var init : newRecord.inits) {
      F3Init.f3Init(init);
    }

    for (var fun : newRecord.methods) {
      F3NFun.f3NewFunction(fun);
    }

    for (var new$class : newRecord.classes) {
      new$class.visit(fs3);
    }

    for (var run_block : newRecord.runBlocks) {
      fs3.pushTable(run_block);
      for (var stmt : run_block.stmts) {
        stmt.visit(fs3);
      }
      fs3.popTable();
    }

    for (var new$enum : newRecord.enums) {
      new$enum.visit(fs3);
    }

    OverBlock parent = null;
    Map<String, OverBlock> defined$traits = new HashMap<>();
    for (int i = 0; i < newRecord.parents.size(); i++) {
      var trait$clz = newRecord.parents.get(i);
      var block$object = YaaClz.f3Clz(trait$clz.type);
      if (block$object.category == TypeCategory.trait_c) {
        var defined$trait = defined$traits.get(block$object.name);
        if (defined$trait != null) {
          throw new YaaError(
              trait$clz.type.placeOfUse(),
              "The current record already implements " +
                  "\"" + defined$trait.type.typeName.content + "\" at "
                  + defined$trait.placeOfUse(),
              "A record cannot implement the same trait more than once"
          );
        }
        var trait$name = block$object.name;
        defined$traits.put(trait$name, trait$clz);
        currentClz.traits.put(trait$name, block$object);
        currentClz.clz$traits.add(block$object);
        F3BlockInClz.f3BlockInClz(trait$clz);
        continue;
      }
      if (block$object.category == class_c) {
        if (parent != null) {
          throw new YaaError(
              trait$clz.type.placeOfUse(),
              "The current class already extends \""
                  + parent.type.typeName.content + "\" at " + parent.placeOfUse(),
              "A record cannot have more than one parent class at one time",
              "The first parent is defined at " + parent.placeOfUse()
          );
        }
        if (block$object.name.equals(object$name)) {
          currentClz.clz$traits.add(block$object);
          currentClz.parent = object$clz;
          trait$clz.isClass = true;
          parent = trait$clz;
        } else {
          throw new YaaError(
              trait$clz.type.placeOfUse(),
              "A record type can only extend " + object$name
          );
        }
      }

      F3BlockInClz.f3BlockInClz(trait$clz);
    }

    fs3.popTable();
  }
}
