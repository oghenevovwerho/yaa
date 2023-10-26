package yaa.semantic.passes.fs6;

import yaa.ast.EEqual;
import yaa.pojos.YaaClz;
import yaa.semantic.passes.fs6.results.CallResult;
import yaa.pojos.YaaInfo;
import org.objectweb.asm.Label;

import static yaa.pojos.GlobalData.*;
import static yaa.semantic.passes.fs6.F6.mw;
import static org.objectweb.asm.Opcodes.*;
import static yaa.semantic.passes.fs6.F6Utils.*;

public class F6Equal {
  public static YaaInfo eEqual(EEqual ctx) {
    var result = ((CallResult) results.get(fs6.path).get(ctx));
    var mtd = result.mtd;
    var owner = mtd.owner;
    var right = mtd.parameters.get(0);
    var rightName = right.name;

    if (result.arrayTypesAreDivergent) {
      mw().visitInsn(ICONST_0);
      return boole$clz;
    }

    if (owner.equals(null$name)) {
      if (right.isPrimitive()) {
        mw().visitInsn(ICONST_0);
        return mtd.type;
      }
      if (rightName.equals(null$name)) {
        mw().visitInsn(ICONST_1);
        return mtd.type;
      }
      var trueDestination = new Label();
      var finalDestination = new Label();
      ctx.e2.visit(fs6);
      mw().visitJumpInsn(IFNULL, trueDestination);
      mw().visitInsn(ICONST_0);
      mw().visitJumpInsn(GOTO, finalDestination);
      mw().visitLabel(trueDestination);
      mw().visitInsn(ICONST_1);
      mw().visitLabel(finalDestination);
      return mtd.type;
    }

    if (rightName.equals(null$name)) {
      if (itIsPrimitive(owner)) {
        mw().visitInsn(ICONST_0);
        return mtd.type;
      }
      var trueDestination = new Label();
      var finalDestination = new Label();
      ctx.e1.visit(fs6);
      mw().visitJumpInsn(IFNULL, trueDestination);
      mw().visitInsn(ICONST_0);
      mw().visitJumpInsn(GOTO, finalDestination);
      mw().visitLabel(trueDestination);
      mw().visitInsn(ICONST_1);
      mw().visitLabel(finalDestination);
      return mtd.type;
    }

    if (owner.equals(array$name) && rightName.equals(array$name)) {
      var left = (YaaClz) ctx.e1.visit(fs6);
      ctx.e2.visit(fs6);
      var descriptor = "(" + left.descriptor() + right.descriptor() + ")Z";
      //inconsistencies have been handled by pushZero
      if (left.inputted.get(0).isPrimitive()) {
        mw().visitMethodInsn(
          INVOKESTATIC, "java/util/Arrays",
          "equals", descriptor, false
        );
      } else {
        mw().visitMethodInsn(
          INVOKESTATIC, "java/util/Arrays",
          "deepEquals", descriptor, false
        );
      }
      return boole$clz;
    }

    if (owner.equals(array$name) || rightName.equals(array$name)) {
      mw().visitInsn(ICONST_0);
      return boole$clz;
    }

    if (itIsPrimitive(owner) && right.isBoxed()) {
      ctx.e1.visit(fs6);
      castTo(owner, unboxedName(rightName));
      boxPrimitive(unboxedName(rightName));
      ctx.e2.visit(fs6);

      mw().visitMethodInsn(
        INVOKEVIRTUAL,
        rightName.replace(".", "/"),
        "equals",
        "(Ljava/lang/Object;)Z",
        false
      );
      return boole$clz;
    }

    if (itIsBoxed(owner) && itIsPrimitive(rightName)) {
      ctx.e1.visit(fs6);
      ctx.e2.visit(fs6);
      castTo(rightName, unboxedName(owner));
      boxPrimitive(unboxedName(owner));

      mw().visitMethodInsn(
        INVOKEVIRTUAL,
        owner.replace(".", "/"),
        "equals",
        "(Ljava/lang/Object;)Z",
        false
      );
      return boole$clz;
    }

    if (!itIsPrimitive(owner) && !itIsPrimitive(rightName)) {
      ctx.e1.visit(fs6);
      var e2 = ctx.e2.visit(fs6);
      boxPrimitive(e2.name);
      mw().visitMethodInsn(
        INVOKEVIRTUAL,
        owner.replace(".", "/"), "equals",
        "(Ljava/lang/Object;)Z",
        false
      );
      return mtd.type;
    }

    if (itIsPrimitive(owner) && !itIsPrimitive(rightName)) {
      mw().visitInsn(ICONST_0);
      return mtd.type;
    }

    if (!itIsPrimitive(owner) && itIsPrimitive(rightName)) {
      mw().visitInsn(ICONST_0);
      return mtd.type;
    }

    F6EqualUtils.doEquals(ctx.e1, ctx.e2, owner, rightName);
    return mtd.type;
  }
}
