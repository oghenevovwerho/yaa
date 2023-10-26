package yaa.semantic.passes.fs4;

import yaa.ast.NewClass;
import yaa.ast.NewFun;
import yaa.pojos.TypeCategory;
import yaa.pojos.YaaError;
import yaa.semantic.handlers.VDefOp;
import yaa.pojos.YaaClz;

import java.util.List;
import java.util.Map;

import static yaa.pojos.GlobalData.*;
import static yaa.pojos.GlobalData.no_override_object_mtds;

public class F4NClass {
  public static void newType(NewClass newClass) {
    fs4.pushTable(newClass);
    var clzName = newClass.name.content;
    var currentClz = (YaaClz) fs4.getSymbol(newClass.placeOfUse());
    topClz.push(currentClz);

    if (newClass.init != null) {
      fs4.pushTable(newClass.init);
      newClass.init.stmt.visit(fs4);
      fs4.popTable();
    }

    for (var def : newClass.vDefinitions) {
      VDefOp.defOp(def);
    }

    for (var run_block : newClass.runBlocks) {
      fs4.pushTable(run_block);
      for (var stmt : run_block.stmts) {
        stmt.visit(fs4);
      }
      fs4.popTable();
    }

    for (var vDec : newClass.vDeclarations) {
      vDec.visit(fs4);
    }

    for (var fun : newClass.methods) {
      fun.visit(fs4);
    }

    for (var new$class : newClass.classes) {
      new$class.visit(fs4);
    }

    for (var new$enum : newClass.enums) {
      new$enum.visit(fs4);
    }

    if (newClass.parent != null) {
      currentClz.parent = YaaClz.fsClz(newClass.parent);
    }

    if (newClass.parentMtds.size() > 0) {
      checkObjectMtds(newClass.parentMtds);
      F4Parent.doParentMtdCheck(newClass.parentMtds, currentClz, currentClz.parent);
    }

    var blocks = newClass.implementations;
    for (int i = 0; i < blocks.size(); i++) {
      var block = blocks.get(i);
      var stored$block = currentClz.clz$traits.get(i);
      YaaClz.fsClz(block.type);//check type
      //for interfaces implemented by an interface, you do not check for method compliance
      //this is what java does
      if (currentClz.category != TypeCategory.trait_c) {
        F4Parent.implementBlock(blocks.get(i), currentClz, stored$block);
      }
    }

    topClz.pop();
    fs4.popTable();
  }

  public static void checkObjectMtds(Map<String, List<NewFun>> methods) {
    for (var mtd_name : no_override_object_mtds) {
      if (methods.get(mtd_name) != null) {
        throw new YaaError(
            methods.get(mtd_name).get(0).placeOfUse(),
            "The java.lang.Object method " +
                "\"" + mtd_name + "\" can not be overridden"
        );
      }
    }

    if (methods.get("equals") != null) {
      if (methods.get("hashCode") == null) {
        throw new YaaError(
            methods.get("equals").get(0).placeOfUse(),
            topClz.peek() + " does not override " +
                "the java.lang.Object method \"hashCode\"",
            "The java.lang.Object method " +
                "\"equals\" must be overridden alongside \"hashCode\""
        );
      }
    }
  }
}