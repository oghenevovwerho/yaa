package yaa.semantic.handlers;

import yaa.ast.Ternary;
import yaa.pojos.YaaClz;
import yaa.pojos.YaaError;
import yaa.pojos.YaaInfo;
import yaa.semantic.passes.fs6.F6Utils;
import yaa.semantic.passes.fs6.results.TernaryResult;

import static yaa.pojos.GlobalData.boole$name;
import static yaa.pojos.GlobalData.fs;
import static yaa.pojos.GlobalData.object$clz;
import static yaa.pojos.GlobalData.results;

public class TernaryOp {
  public static YaaInfo ternary(Ternary ternary) {
    var cond = ternary.cond.visit(fs);
    if (!cond.name.equals(boole$name)) {
      throw new YaaError(
        ternary.cond.placeOfUse(),
        "The condition of a ternary operation must be boolean"
      );
    }
    var then_e = ternary.l.visit(fs);
    var else_e = ternary.r.visit(fs);
    if (!(then_e instanceof YaaClz then_clz)) {
      throw new YaaError(
        ternary.l.placeOfUse(),
        "The then expression of a ternary must be a proper type"
      );
    }
    if (!(else_e instanceof YaaClz else_clz)) {
      throw new YaaError(
        ternary.r.placeOfUse(),
        "The else expression of a ternary must be a proper type"
      );
    }
    YaaInfo type;
    if (then_e.isPrimitive() && else_e.isPrimitive()) {
      type = F6Utils.widest(then_e, else_e);
    } else if (then_e.isPrimitive() && !else_e.isPrimitive()) {
      type = object$clz;
    } else if (!then_e.isPrimitive() && else_e.isPrimitive()) {
      type = object$clz;
    } else {
      if (then_clz.accepts(else_clz)) {
        if (then_clz.isUnboundedAndNotPrimitive()) {
          type = then_clz;
        } else if (else_clz.isUnboundedAndNotPrimitive()) {
          type = else_clz;
        } else {
          type = then_e;
        }
      } else if (then_clz.isParentOf(else_clz)) {
        type = then_clz;
      } else if (else_clz.isParentOf(then_clz)) {
        type = else_clz;
      } else if (then_clz.hasTrait(else_clz) != null) {
        type = then_clz;
      } else if (else_clz.hasTrait(then_clz) != null) {
        type = else_clz;
      } else {
        type = object$clz;
      }
    }
    results.get(fs.path).put(ternary, new TernaryResult(type));
    return type;
  }
}
