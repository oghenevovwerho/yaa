package yaa.semantic.passes.fs5;

import yaa.ast.NewClass;
import yaa.pojos.YaaClz;
import yaa.pojos.YaaError;
import yaa.semantic.handlers.MetaCallOp;
import yaa.semantic.handlers.OpUtils;

import java.lang.annotation.ElementType;
import java.util.ArrayList;

import static yaa.pojos.GlobalData.*;
import static yaa.pojos.TypeCategory.*;

public class F5NClass {
  public static void newType(NewClass newClass) {
    fs5.pushTable(newClass);
    var currentClz = (YaaClz) fs5.getSymbol(newClass.placeOfUse());
    topClz.push(currentClz);

    if (newClass.metaCalls != null && newClass.metaCalls.size() > 0) {
      currentClz.metas = MetaCallOp.metaCalls(newClass.metaCalls, ElementType.TYPE);
    }

    for (var def : newClass.vDefinitions) {
      def.visit(fs5);
    }

    for (var dec : newClass.vDeclarations) {
      dec.visit(fs5);
    }

    if (newClass.init != null) {
      if (newClass.init.parentCall != null && newClass.parent == null) {
        throw new YaaError(
            newClass.init.parentCall.placeOfUse(),
            currentClz + " has no defined parent, it therefore cannot call a parent initializer"
        );
      }
      if (newClass.init.parentCall == null && (newClass.parent != null && currentClz.parent.inits.size() > 0)) {
        throw new YaaError(
            newClass.placeOfUse(), currentClz.parent.toString(),
            currentClz + " must call a matching initializer from the type above",
            currentClz.parent.initCandidates(new ArrayList<>(0))
        );
      }
    } else {
      if (newClass.parent != null && currentClz.parent.inits.size() > 0) {
        throw new YaaError(
            newClass.placeOfUse(), currentClz.parent.toString(),
            currentClz + " must call a matching initializer from the type above",
            currentClz.parent.initCandidates(new ArrayList<>(0))
        );
      }
    }

    if (newClass.init != null) {
      fs5.pushTable(newClass.init);
      if (newClass.init.parentCall != null) {
        var values = OpUtils.v$arguments(newClass.init.parentCall.arguments);
        var matchedParentInit = false;
        for (var init : currentClz.parent.inits) {
          var acceptedInit = init.acceptsInit(values);
          if (acceptedInit != null) {
            newClass.parentCallDescriptor = acceptedInit.descriptor();
            matchedParentInit = true;
            break;
          }
        }
        if (!matchedParentInit) {
          throw new YaaError(
              newClass.init.parentCall.placeOfUse(), currentClz.parent.toString(),
              "The class above does not define " +
                  "any initializer with the given arguments",
              currentClz.parent.initCandidates(new ArrayList<>(0))
          );
        }
      }
      newClass.init.stmt.visit(fs5);
      fs5.popTable();
    }

    for (var mtd_list : newClass.parentMtds.values()) {
      for (var parentMtd : mtd_list) {
        parentMtd.visit(fs5);
      }
    }

    for (var mtd : newClass.methods) {
      var mtd$name = mtd.name.content;
      var parentResult = currentClz.getShadowMethod(mtd$name);
      if (parentResult != null) {
        var mtdWord = parentResult.pack.methods.size() == 1 ? "method" : "methods";
        var pronoun = parentResult.pack.methods.size() == 1 ? " is " : " are ";
        throw new YaaError(
            mtd.placeOfUse(), mtd.toString(),
            "The method above shadows the " + mtdWord + " below",
            parentResult.pack.toString(),
            "The shadowed " + mtdWord + pronoun + "defined in the "
                + what(parentResult.clz) + " below",
            parentResult.clz.toString()
        );
      }
      mtd.visit(fs5);
    }

    for (var new$class : newClass.classes) {
      new$class.visit(fs5);
    }

    for (var new$enum : newClass.enums) {
      new$enum.visit(fs5);
    }

    for (var run_block : newClass.runBlocks) {
      fs5.pushTable(run_block);
      for (var stmt : run_block.stmts) {
        stmt.visit(fs5);
      }
      fs5.popTable();
    }

    var blocks = newClass.implementations;
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