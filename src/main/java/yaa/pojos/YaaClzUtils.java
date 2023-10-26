package yaa.pojos;

import java.util.List;
import java.util.Stack;

import static yaa.pojos.GlobalData.array$name;

public class YaaClzUtils {
  //TypeSignature: Z | C | B | S | I | F | J | D | FieldTypeSignature
  //FieldTypeSignature: ClassTypeSignature | [ TypeSignature | TypeVar
  //ClassTypeSignature: L Id ( / Id )* TypeArgs? ( . Id TypeArgs? )* ;
  //TypeArgs: < TypeArg+ >
  //TypeArg: * | ( + | - )? FieldTypeSignature
  //TypeVar: T Id ;

  public static String typeUseSignature(YaaClz clz) {
    if (clz.isPrimitive()) {
      return null;
    }
    return fieldTypeSignature(clz);
  }

  private static String fieldTypeSignature(YaaClz clz) {
    if (clz.name.equals(array$name)) {
      return "[" + typeUseSignature(clz.inputted.get(0));
    }
    if (clz.isIBounded()) {
      return typeVar(clz);
    }
    return classTypeSignature(clz);
  }

  private static Stack<YaaClz> classStack(YaaClz clz) {
    var stack = new Stack<YaaClz>();
    //so that the topmost class comes last
    var topmost_clz = clz;
    stack.push(topmost_clz);
    while (topmost_clz.outerClz != null) {
      stack.push(topmost_clz);
      topmost_clz = topmost_clz.outerClz;
    }
    return stack;
  }

  //ClassTypeSignature: L Id ( / Id )* TypeArgs? ( . Id TypeArgs? )* ;
  private static String classTypeSignature(YaaClz clz) {
    var clz_stack = classStack(clz);
    var topmost_clz = clz_stack.pop();
    var sb = new StringBuilder();
    sb.append("L");
    sb.append(topmost_clz.codeName);
    if (topmost_clz.inputted.size() > 0) {
      sb.append(typeArgs(topmost_clz.inputted));
    }

    while (!clz_stack.isEmpty()) {
      var current_clz = clz_stack.pop();
      sb.append(".");
      sb.append(current_clz.name);
      if (current_clz.inputted.size() > 0) {
        sb.append(typeArgs(current_clz.inputted));
      }
    }

    return sb.append(";").toString();
  }

  private static String typeArgs(List<YaaClz> args) {
    var sb = new StringBuilder();
    sb.append("<");
    for (var arg : args) {
      sb.append(typeArg(arg));
    }
    sb.append(">");
    return sb.toString();
  }

  private static String typeArg(YaaClz arg) {
    //typeArg: * | ( + | - )? FieldTypeSignature
    //System.out.println(arg + "  " + arg.variance);
    if (arg.name.equals("?")) {//List<?>
      return "*";
    }
    if (arg.variance == YaaClzVariance.contravariant) {
      return "-" + arg.descriptor();
    }
    if (arg.variance == YaaClzVariance.covariant) {
      return "+" + arg.descriptor();
    }
    if (arg.variance == YaaClzVariance.invariant) {
      return arg.descriptor();
    }
    return arg.clzUseSignature();
  }

  private static String typeVar(YaaClz clz) {
    return "T" + clz.getSimpleName() + ";";
  }
}
