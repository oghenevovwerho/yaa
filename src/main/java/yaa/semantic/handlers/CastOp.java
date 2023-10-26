package yaa.semantic.handlers;

import yaa.ast.Cast;
import yaa.semantic.passes.fs6.results.CastResult;
import yaa.pojos.YaaClz;
import yaa.pojos.YaaError;
import yaa.pojos.YaaInfo;

import java.lang.annotation.ElementType;

import static yaa.pojos.GlobalData.*;

public class CastOp {
  public static YaaInfo cast(Cast ctx) {
    var argument = ctx.e.visit(fs);
    var type = ctx.type.visit(fs);
    MetaCallOp.metaCalls(ctx.type, ElementType.TYPE_USE);
    if (argument.name.equals(boole$name)) {
      throw new YaaError(
          ctx.placeOfUse(), "boolean cannot be cast to "
          + type.toString()
      );
    }
    if (type.name.equals(boole$name)) {
      throw new YaaError(
          ctx.placeOfUse(), argument + " cannot be cast to boolean"
      );
    }
    if (!(type instanceof YaaClz type_clz)) {
      throw new YaaError(
          ctx.type.placeOfUse(), "Expressions can only be cast to Yaa types"
      );
    }
    if (!(argument instanceof YaaClz arg_clz)) {
      throw new YaaError(
          ctx.type.placeOfUse(), "Only Yaa expressions can be casted"
      );
    }
    if (type_clz.isPrimitive() && arg_clz.isPrimitive()) {
      results.get(fs.path).put(ctx, new CastResult(argument, type));
      return type;
    }
    if (type_clz.isParentOf(argument)) {
      results.get(fs.path).put(ctx, new CastResult(argument, type));
      return type;
    }
    if (type_clz.isParentOf(argument)) {
      results.get(fs.path).put(ctx, new CastResult(argument, type));
      return type;
    }
    if (type_clz.hasTrait(arg_clz) != null) {
      results.get(fs.path).put(ctx, new CastResult(argument, type));
      return type;
    }
    if (arg_clz.hasTrait(type_clz) != null) {
      results.get(fs.path).put(ctx, new CastResult(argument, type));
      return type;
    }
    throw new YaaError(
        ctx.type.placeOfUse(), argument + " cannot be cast to " + type
    );
  }
}
