package yaa.semantic.passes.fs3;

import yaa.ast.NewClass;
import yaa.ast.OverBlock;
import yaa.pojos.TypeCategory;
import yaa.pojos.YaaClz;
import yaa.pojos.YaaError;
import yaa.semantic.passes.fs2.F2Init;
import yaa.semantic.passes.fs4.F4Parent;

import java.util.HashMap;
import java.util.Map;

import static yaa.pojos.GlobalData.*;

public class F3NClass {
  public static void newType(NewClass newClass) {
    fs3.pushTable(newClass);
    var currentClz = (YaaClz) fs3.getSymbol(newClass.placeOfUse());

    if (newClass.init != null) {
      F3Init.f3Init(newClass.init);
    }

    for (var fun : newClass.methods) {
      F3NFun.f3NewFunction(fun);
    }

    for (var fInterface : newClass.fInterfaces) {
      fInterface.visit(fs3);
    }

    for (var new$class : newClass.classes) {
      new$class.visit(fs3);
    }

    for (var run_block : newClass.runBlocks) {
      fs3.pushTable(run_block);
      for (var stmt : run_block.stmts) {
        stmt.visit(fs3);
      }
      fs3.popTable();
    }

    for (var new$enum : newClass.enums) {
      new$enum.visit(fs3);
    }

    if (newClass.parent != null) {
      var parentClz = YaaClz.f3Clz(newClass.parent);
      currentClz.parent = parentClz;
      if (parentClz.category != TypeCategory.class_c) {
        throw new YaaError(
            newClass.parent.placeOfUse(),
            parentClz + " can not be extended by " + currentClz
        );
      }
      if (parentClz.isFinal) {
        throw new YaaError(
            newClass.parent.placeOfUse(),
            parentClz + " is final, a final type cannot be extended"
        );
      }
      if (newClass.typeParams.size() > 0) {
        if (parentClz.isChildOf(new YaaClz("java.lang.Throwable"))) {
          throw new YaaError(
              newClass.parent.placeOfUse(),
              "A type with generic parameters cannot be a subtype of java.lang.Throwable"
          );
        }
      }
    } else {
      currentClz.parent = object$clz;
    }

    for (var mtd_list : newClass.parentMtds.values()) {
      for (var parentMtd : mtd_list) {
        F3ParentMtd.f3ParentMtd(parentMtd);
      }
    }

    Map<String, OverBlock> alreadyImplemented = new HashMap<>();
    for (int i = 0; i < newClass.implementations.size(); i++) {
      var astTrait = newClass.implementations.get(i);
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