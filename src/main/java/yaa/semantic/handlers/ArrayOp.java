package yaa.semantic.handlers;

import yaa.ast.VCall;
import yaa.pojos.*;
import yaa.semantic.passes.fs6.results.InitResult;
import yaa.pojos.primitives.ArrayMold;

import java.util.ArrayList;
import java.util.List;

import static yaa.pojos.GlobalData.*;

public class ArrayOp {
  public static YaaClz arrayOp(VCall ctx, List<YaaInfo> expressions) {
    if (expressions.size() == 0) {
      var type$arguments = new ArrayList<YaaClz>(1);
      type$arguments.add(GlobalData.object$clz);
      var array$type = ArrayMold.newArray().changeCBounds(type$arguments);
      var result = new InitResult(new YaaInit(), array$type);
      results.get(fs.path).put(ctx, result);
      return array$type;
    }
    var type$arguments = new ArrayList<YaaClz>(1);
    var first$argument = expressions.get(0);
    if (!(first$argument instanceof YaaClz clz)) {
      throw new YaaError(
        ctx.arguments.get(0).placeOfUse(),
        "Only Yaa objects can be used for an array"
      );
    }
    if (first$argument.name.equals(null$name)) {
      throw new YaaError(
        ctx.arguments.get(0).placeOfUse(),
        "The \"null\" value is not allowed in an array expression"
      );
    }
    if (first$argument.name.equals(array$name)) {
      return handleArray1stCase(ctx, expressions);
    }
    if (first$argument.isPrimitive()) {
      return handlePrimitive1stCase(ctx, expressions);
    }
    var index = 0;
    for (var expression : expressions) {
      if (expression.name.equals(null$name)) {
        throw new YaaError(
          ctx.arguments.get(index).placeOfUse(),
          "The \"null\" value is not allowed in an array"
        );
      }
      if (!clz.isParentOf(expression)) {
        throw new YaaError(
          ctx.arguments.get(index).placeOfUse(), expression.toString(),
          "The type above must extend the type below",
          first$argument.toString()
        );
      }
      index = index + 1;
    }
    type$arguments.add(clz);
    var array$type = ArrayMold.newArray().changeCBounds(type$arguments);
    var result = new InitResult(new YaaInit(), array$type);
    results.get(fs.path).put(ctx, result);
    return array$type;
  }

  private static YaaClz handleArray1stCase(VCall ctx, List<YaaInfo> expressions) {
    var first = (YaaClz) expressions.get(0);
    var index = 0;
    for (var expression : expressions) {
      if (expression.name.equals(null$name)) {
        throw new YaaError(
          ctx.arguments.get(index).placeOfUse(),
          "The \"null\" value is not allowed in an array"
        );
      }
      if (!expression.name.equals(array$name)) {
        throw new YaaError(
          ctx.arguments.get(index).placeOfUse(), expression.toString(),
          "Only array types are allowed in an array with an array starter"
        );
      }
      index = index + 1;
    }
    var type$arguments = new ArrayList<YaaClz>(1);
    type$arguments.add(first);
    var array$type = ArrayMold.newArray().changeCBounds(type$arguments);
    var result = new InitResult(new YaaInit(), array$type);
    results.get(fs.path).put(ctx, result);
    return array$type;
  }

  private static YaaClz handlePrimitive1stCase(VCall ctx, List<YaaInfo> expressions) {
    var largest = double$clz;
    var has$double = false;
    var has$long = false;
    var has$float = false;
    var has$int = false;
    var has$short = false;
    var has$char = false;
    var index = 0;
    for (var expression : expressions) {
      if (expression.name.equals(null$name)) {
        throw new YaaError(
          ctx.arguments.get(index).placeOfUse(),
          "The \"null\" value is not allowed in an array expression"
        );
      }
      switch (expression.name) {
        case double$name -> {
          largest = double$clz;
          has$double = true;
        }
        case float$name -> {
          if (!has$double) {
            largest = float$clz;
          }
          has$float = true;
        }
        case long$name -> {
          if (!has$double && !has$float) {
            largest = long$clz;
          }
          has$long = true;
        }
        case int$name -> {
          if (!has$double && !has$float && !has$long) {
            largest = int$clz;
          }
          has$int = true;
        }
        case char$name -> {
          if (!has$double && !has$float && !has$long && !has$int) {
            largest = char$clz;
          }
          has$char = true;
        }
        case short$name -> {
          if (!has$double && !has$float && !has$long && !has$int && !has$char) {
            largest = short$clz;
          }
          has$short = true;
        }
        case byte$name, boole$name -> {
          if (!has$double && !has$float && !has$long
            && !has$int && !has$short && !has$char) {
            largest = byte$clz;
          }
        }
        default -> {
          throw new YaaError(
            ctx.arguments.get(index).placeOfUse(), expression.toString(),
            "Only primitive types are allowed in " +
              "an array with a primitive starter"
          );
        }
      }
      index = index + 1;
    }
    var type$arguments = new ArrayList<YaaClz>(1);
    type$arguments.add(largest);
    var array$type = ArrayMold.newArray().changeCBounds(type$arguments);
    var result = new InitResult(new YaaInit(), array$type);
    results.get(fs.path).put(ctx, result);
    return array$type;
  }
}