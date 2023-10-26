package yaa.semantic.passes.fs5;

import yaa.ast.Loop;
import yaa.pojos.*;
import yaa.semantic.passes.fs6.results.LoopResult;

import static yaa.pojos.GlobalData.*;
import static yaa.semantic.passes.fs5.F5Callable.stmtGivesValue;

public class F5Loop {
  protected static boolean handleLoopStmt(Loop loop) {
    fs5.block.push(BlockKind.loop);
    fs5.pushTable(loop);

    //this is needed because the condition might need
    // the field value in the case of boolean conditions
    if (loop.init$value != null) {
      var fieldName = loop.value$name.content;
      var definedField = (YaaField) fs.getSymbol(fieldName);
      definedField.data = loop.init$value.visit(fs5);
    }

    var condition = loop.condition.visit(fs5);
    var result = new LoopResult();
    results.get(fs5.path).put(loop, result);

    if (condition.name.equals(boole$name)) {
      result.clz = (YaaClz) condition;
      result.loopIsWhat = LoopIsWhat.While;
    } else if (condition.name.equals(array$name)) {
      result.clz = (YaaClz) condition;
      if (loop.value$name != null) {
        if (loop.init$value != null) {
          throw new YaaError(
              loop.placeOfUse(),
              "An array based loop must not have an initial value"
          );
        }
        var fieldName = loop.value$name.content;
        var definedField = (YaaField) fs.getSymbol(fieldName);
        definedField.data = result.clz.inputted.get(0);
      } else {
        throw new YaaError(
            loop.placeOfUse(),
            "An array based loop must have a value reference name"
        );
      }
      result.loopIsWhat = LoopIsWhat.Array;
    } else {
      var cond_clz = ((YaaClz) condition);
      var iterator_pack = cond_clz.getMethod("iterator");
      if (iterator_pack == null) {
        throw new YaaError(
            loop.condition.placeOfUse(),
            "A loop's condition must be a type below",
            boole$name, array$name, "java.util.Iterator"
        );
      }
      iterator_pack = iterator_pack.changeCPack(cond_clz.inputted);
      var iterator = ((YaaClz) iterator_pack.methods.get(0).type);
      switch (iterator.name) {
        case "java.util.Iterator", "java.util.ListIterator" -> {
          if (loop.value$name != null) {
            if (loop.init$value != null) {
              throw new YaaError(
                  loop.placeOfUse(),
                  "An iterator based loop must not have an initial value"
              );
            }
            var fieldName = loop.value$name.content;
            var definedField = (YaaField) fs.getSymbol(fieldName);
            definedField.data = iterator.inputted.get(0);
            result.iteratorClz = iterator;
          } else {
            throw new YaaError(
                loop.placeOfUse(),
                "An iterator based loop must have a value reference name"
            );
          }
          result.clz = (YaaClz) condition;
          result.loopIsWhat = LoopIsWhat.Iterator;
        }
        case "java.util.PrimitiveIterator$OfInt",
            "java.util.PrimitiveIterator$OfLong",
            "java.util.PrimitiveIterator$OfDouble" -> {
          if (loop.value$name != null) {
            if (loop.init$value != null) {
              throw new YaaError(
                  loop.placeOfUse(),
                  "An iterator based loop must not have an initial value"
              );
            }
            var fieldName = loop.value$name.content;
            var definedField = (YaaField) fs.getSymbol(fieldName);
            definedField.data = iterator.getMethod("next").methods.get(0).type;
            result.iteratorClz = iterator;
          } else {
            throw new YaaError(
                loop.placeOfUse(),
                "An iterator based loop must have a value reference name"
            );
          }
          result.clz = (YaaClz) condition;
          result.loopIsWhat = LoopIsWhat.Iterator;
        }
        default -> {
          throw new YaaError(
              loop.condition.placeOfUse(),
              "A loops condition must be/do one of the following",
              boole$name, array$name, "return a value that " +
              "implements java.util.Iterator"
          );
        }
      }
    }
    if (loop.assign != null) {
      loop.assign.e2.visit(fs5);
    }
    var value = stmtGivesValue(loop.stmt);
    fs5.block.pop();
    fs5.popTable();
    return value;
  }
}
