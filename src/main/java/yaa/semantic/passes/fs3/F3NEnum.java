package yaa.semantic.passes.fs3;

import yaa.ast.OverBlock;
import yaa.ast.NewEnum;
import yaa.pojos.TypeCategory;
import yaa.pojos.YaaClz;
import yaa.pojos.YaaError;

import java.util.HashMap;
import java.util.Map;

import static yaa.pojos.GlobalData.fs3;

public class F3NEnum {
  public static void newEnum(NewEnum newEnum) {
    fs3.pushTable(newEnum);
    var currentClz = (YaaClz) fs3.getSymbol(newEnum.placeOfUse());

    for (var fun : newEnum.methods) {
      F3NFun.f3NewFunction(fun);
    }

    for (var init : newEnum.inits) {
      F3Init.f3Init(init);
    }

    for (var run_block : newEnum.runBlocks) {
      fs3.pushTable(run_block);
      for (var stmt : run_block.stmts) {
        stmt.visit(fs3);
      }
      fs3.popTable();
    }

    for (var new$class : newEnum.classes) {
      new$class.visit(fs3);
    }

    for (var new$enum : newEnum.enums) {
      new$enum.visit(fs3);
    }

    if (newEnum.toStringParentMtd != null) {
      F3ParentMtd.f3ParentMtd(newEnum.toStringParentMtd);
    }

    Map<String, OverBlock> alreadyImplemented = new HashMap<>();
    for (int i = 0; i < newEnum.implementations.size(); i++) {
      var astTrait = newEnum.implementations.get(i);
      var implementedObject = YaaClz.f3Clz(astTrait.type);
      if (implementedObject.category != TypeCategory.trait_c) {
        throw new YaaError(
            astTrait.type.placeOfUse(),
            implementedObject + " is not an interface, only interfaces can be implemented"
        );
      }
      if (implementedObject.isFinal) {
        throw new YaaError(
            astTrait.type.placeOfUse(),
            implementedObject + " is final, a final type cannot be implemented"
        );
      }
      var defined$trait = alreadyImplemented.get(implementedObject.name);
      if (defined$trait != null) {
        if (currentClz.category == TypeCategory.trait_c) {
          throw new YaaError(
              astTrait.type.placeOfUse(),
              "The current trait already extends " +
                  "\"" + defined$trait.type.typeName.content + "\" at "
                  + defined$trait.placeOfUse(),
              "A trait cannot extend the same trait more than once"
          );
        } else {
          throw new YaaError(
              astTrait.type.placeOfUse(),
              "The current type already implements " +
                  "\"" + defined$trait.type.typeName.content + "\" at "
                  + defined$trait.placeOfUse(),
              "A type cannot implement the same trait more than once"
          );
        }
      }
      var trait$name = implementedObject.name;
      alreadyImplemented.put(trait$name, astTrait);
      currentClz.traits.put(trait$name, implementedObject);
      currentClz.clz$traits.add(implementedObject);
      F3BlockInClz.f3BlockInClz(astTrait);
    }

    fs3.popTable();
  }
}
