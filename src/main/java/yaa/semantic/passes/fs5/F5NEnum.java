package yaa.semantic.passes.fs5;

import yaa.ast.NewEnum;
import yaa.pojos.YaaClz;
import yaa.pojos.YaaError;
import yaa.pojos.GlobalData;

import static yaa.pojos.GlobalData.fs5;
import static yaa.pojos.TypeCategory.*;

public class F5NEnum {
  public static void newEnum(NewEnum newEnum) {
    fs5.pushTable(newEnum);
    var currentClz = (YaaClz) fs5.getSymbol(newEnum.placeOfUse());
    GlobalData.topClz.push(currentClz);

    for (var def : newEnum.vDefinitions) {
      def.visit(fs5);
    }

    for (var dec : newEnum.vDeclarations) {
      dec.visit(fs5);
    }

    for (var fun : newEnum.methods) {
      fun.visit(fs5);
    }

    for (var init : newEnum.inits) {
      init.visit(fs5);
    }

    for (var new$class : newEnum.classes) {
      new$class.visit(fs5);
    }

    for (var run_block : newEnum.runBlocks) {
      fs5.pushTable(run_block);
      for (var stmt : run_block.stmts) {
        stmt.visit(fs5);
      }
      fs5.popTable();
    }

    for (var new$enum : newEnum.enums) {
      new$enum.visit(fs5);
    }

    if (newEnum.toStringParentMtd != null) {
      newEnum.toStringParentMtd.visit(fs5);
    }

    for (var option : newEnum.enumOptions) {
      if (option.arguments == null) {
        continue;
      } else {
        if (newEnum.inits.size() == 0) {
          //to prevent things like Option()
          //without a no-argument initializer
          throw new YaaError(
              option.name.placeOfUse(),
              "\"" + currentClz.name + "\" " +
                  "has no matching initializer for \""
                  + option.name.content + "\""
          );
        }
      }
      var option_accepted = false;
      init_loop:
      for (var init : currentClz.inits) {
        if (option.arguments.size() == init.parameters.size()) {
          for (int i = 0; i < init.parameters.size(); i++) {
            var arg = option.arguments.get(i);
            var param = init.parameters.get(i);
            if (!param.accepts(arg.visit(fs5))) {
              continue init_loop;
            }
          }
          option_accepted = true;
        }
      }
      if (!option_accepted) {
        throw new YaaError(
            option.name.placeOfUse(),
            "\"" + currentClz.name + "\" " +
                "has no matching initializer for \""
                + option.name.content + "\""
        );
      }
    }

    var blocks = newEnum.implementations;
    for (int i = 0; i < blocks.size(); i++) {
      var block = blocks.get(i);
      var trait$clz = currentClz.clz$traits.get(i);
      if (!(trait$clz.category == enum_c || trait$clz.category == trait_c)) {
        F5Parent.f5parentBlock(block);
      } else {
        F5TraitBlock.f5TraitBlock(block);
      }
    }

    GlobalData.topClz.pop();
    fs5.popTable();
  }
}
