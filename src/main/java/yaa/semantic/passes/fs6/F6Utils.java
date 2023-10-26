package yaa.semantic.passes.fs6;

import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import yaa.ast.*;
import yaa.pojos.*;
import yaa.semantic.passes.fs6.f6utils.F6CastUtils;

import java.util.List;

import static java.lang.Double.parseDouble;
import static java.lang.Float.parseFloat;
import static java.lang.Integer.parseInt;
import static java.lang.Long.parseLong;
import static org.objectweb.asm.Opcodes.*;
import static yaa.pojos.GlobalData.*;

public class F6Utils {
  public static void saveClass4Writing(String internalName) {
    var classFileData = new ClassFileData();
    classFileData.internalName = internalName;

    if (fs6.cw.peek() != null) {
      fs6.cw.peek().visitEnd();
      classFileData.classBytes = fs6.cw.peek().toByteArray();
    }
    compiledClasses.add(classFileData);
  }

  public static YaaInfo widest(YaaInfo info1, YaaInfo info2) {
    if (info1.name.equals(double$name)) {
      return info1;
    }
    if (info2.name.equals(double$name)) {
      return info2;
    }
    if (info1.name.equals(float$name)) {
      return info1;
    }
    if (info2.name.equals(float$name)) {
      return info2;
    }
    if (info1.name.equals(long$name)) {
      return info1;
    }
    if (info2.name.equals(long$name)) {
      return info2;
    }
    if (info1.name.equals(int$name)) {
      return info1;
    }
    if (info2.name.equals(int$name)) {
      return info2;
    }
    if (info1.name.equals(char$name)) {
      return info1;
    }
    if (info2.name.equals(char$name)) {
      return info2;
    }
    if (info1.name.equals(short$name)) {
      return info1;
    }
    if (info2.name.equals(short$name)) {
      return info2;
    }
    return byte$clz;
  }

  public static String[] widest(String info1, String info2) {
    if (info1.equals(double$name)) {
      return new String[]{info1, info2};
    }
    if (info2.equals(double$name)) {
      return new String[]{info2, info1};
    }
    if (info1.equals(float$name)) {
      return new String[]{info1, info2};
    }
    if (info2.equals(float$name)) {
      return new String[]{info2, info1};
    }
    if (info1.equals(long$name)) {
      return new String[]{info1, info2};
    }
    if (info2.equals(long$name)) {
      return new String[]{info2, info1};
    }
    if (info1.equals(int$name)) {
      return new String[]{info1, info2};
    }
    if (info2.equals(int$name)) {
      return new String[]{info2, info1};
    }
    if (info1.equals(char$name)) {
      return new String[]{info1, info2};
    }
    if (info2.equals(char$name)) {
      return new String[]{info2, info1};
    }
    if (info1.equals(short$name)) {
      return new String[]{info1, info2};
    }
    if (info2.equals(short$name)) {
      return new String[]{info2, info1};
    }
    return new String[]{byte$name, byte$name};
  }

  public static String castToWidest(String info1, String info2) {
    if (info1.equals(info2)) {
      return info1;
    }
    var results = widest(info1, info2);
    castTo(results[1], results[0]);
    return results[0];
  }

  public static void castTo(String from, String to) {
    if (from.equals(to)) {
      return;
    }
    switch (from) {
      case double$name -> {
        switch (to) {
          case int$name -> F6.mw().visitInsn(Opcodes.D2I);
          case long$name -> F6.mw().visitInsn(Opcodes.D2L);
          case float$name -> F6.mw().visitInsn(Opcodes.D2F);
          case char$name -> {
            F6.mw().visitInsn(Opcodes.D2I);
            F6.mw().visitInsn(Opcodes.I2C);
          }
          case byte$name -> {
            F6.mw().visitInsn(Opcodes.D2I);
            F6.mw().visitInsn(Opcodes.I2B);
          }
          case short$name -> {
            F6.mw().visitInsn(Opcodes.D2I);
            F6.mw().visitInsn(Opcodes.I2S);
          }
        }
      }
      case float$name -> {
        switch (to) {
          case int$name -> F6.mw().visitInsn(Opcodes.F2I);
          case double$name -> F6.mw().visitInsn(Opcodes.F2D);
          case long$name -> F6.mw().visitInsn(Opcodes.F2L);
          case char$name -> {
            F6.mw().visitInsn(Opcodes.F2I);
            F6.mw().visitInsn(Opcodes.I2C);
          }
          case byte$name -> {
            F6.mw().visitInsn(Opcodes.F2I);
            F6.mw().visitInsn(Opcodes.I2B);
          }
          case short$name -> {
            F6.mw().visitInsn(Opcodes.F2I);
            F6.mw().visitInsn(Opcodes.I2S);
          }
        }
      }
      case int$name -> {
        switch (to) {
          case long$name -> F6.mw().visitInsn(Opcodes.I2L);
          case double$name -> F6.mw().visitInsn(Opcodes.I2D);
          case float$name -> F6.mw().visitInsn(Opcodes.I2F);
          case char$name -> F6.mw().visitInsn(Opcodes.I2C);
          case byte$name -> F6.mw().visitInsn(Opcodes.I2B);
          case short$name -> F6.mw().visitInsn(Opcodes.I2S);
        }
      }
      case long$name -> {
        switch (to) {
          case int$name -> F6.mw().visitInsn(Opcodes.L2I);
          case double$name -> F6.mw().visitInsn(Opcodes.L2D);
          case float$name -> F6.mw().visitInsn(Opcodes.L2F);
          case char$name -> {
            F6.mw().visitInsn(Opcodes.L2I);
            F6.mw().visitInsn(Opcodes.I2C);
          }
          case byte$name -> {
            F6.mw().visitInsn(Opcodes.L2I);
            F6.mw().visitInsn(Opcodes.I2B);
          }
          case short$name -> {
            F6.mw().visitInsn(Opcodes.L2I);
            F6.mw().visitInsn(Opcodes.I2S);
          }
        }
      }
      case char$name -> {
        switch (to) {
          case long$name -> F6.mw().visitInsn(Opcodes.I2L);
          case double$name -> F6.mw().visitInsn(Opcodes.I2D);
          case float$name -> F6.mw().visitInsn(Opcodes.I2F);
          case short$name -> F6.mw().visitInsn(Opcodes.I2S);
          case byte$name -> F6.mw().visitInsn(Opcodes.I2B);
        }
      }
      case short$name -> {
        switch (to) {
          case long$name -> F6.mw().visitInsn(Opcodes.I2L);
          case double$name -> F6.mw().visitInsn(Opcodes.I2D);
          case float$name -> F6.mw().visitInsn(Opcodes.I2F);
          case char$name -> F6.mw().visitInsn(Opcodes.I2C);
          case byte$name -> F6.mw().visitInsn(Opcodes.I2B);
        }
      }
      case byte$name -> {
        switch (to) {
          case long$name -> F6.mw().visitInsn(Opcodes.I2L);
          case double$name -> F6.mw().visitInsn(Opcodes.I2D);
          case float$name -> F6.mw().visitInsn(Opcodes.I2F);
          case char$name -> F6.mw().visitInsn(Opcodes.I2C);
          case short$name -> F6.mw().visitInsn(Opcodes.I2S);
        }
      }
    }
  }

  public static YaaInfo operatorMtd(Stmt e1, Stmt e2, YaaFun mtd) {
    e1.visit(fs6);
    e2.visit(fs6);
    F6.mw().visitMethodInsn(
        INVOKEVIRTUAL,
        mtd.owner,
        mtd.name,
        mtd.descriptor(),
        mtd.itIsTraitMtd
    );
    return mtd.type;
  }

  public static boolean itIsPrimitive(Stmt stmt) {
    if (stmt instanceof Pointed) {
      return true;
    }
    if (stmt instanceof Floated) {
      return true;
    }
    if (stmt instanceof Decimal) {
      return true;
    }
    if (stmt instanceof Shorted) {
      return true;
    }
    if (stmt instanceof Cha cha) {
      return !cha.itIsInterpolated;
    }
    return stmt instanceof Byted;
  }

  public static boolean itIsBoxed(String name) {
    switch (name) {
      case int$boxed -> {
        return true;
      }
      case float$boxed -> {
        return true;
      }
      case long$boxed -> {
        return true;
      }
      case short$boxed -> {
        return true;
      }
      case boole$boxed -> {
        return true;
      }
      case byte$boxed -> {
        return true;
      }
      case char$boxed -> {
        return true;
      }
      case double$boxed -> {
        return true;
      }
      case void$boxed -> {
        return true;
      }
    }
    return false;
  }

  public static boolean itIsPrimitive(String name) {
    switch (name) {
      case int$name -> {
        return true;
      }
      case float$name -> {
        return true;
      }
      case long$name -> {
        return true;
      }
      case short$name -> {
        return true;
      }
      case boole$name -> {
        return true;
      }
      case byte$name -> {
        return true;
      }
      case char$name -> {
        return true;
      }
      case double$name -> {
        return true;
      }
      case void$name -> {
        return true;
      }
    }
    return false;
  }

  public static void generateIntCode(int value) {
    switch (value) {
      case 0 -> F6.mw().visitInsn(ICONST_0);
      case 1 -> F6.mw().visitInsn(ICONST_1);
      case 2 -> F6.mw().visitInsn(ICONST_2);
      case 3 -> F6.mw().visitInsn(ICONST_3);
      case 4 -> F6.mw().visitInsn(ICONST_4);
      case 5 -> F6.mw().visitInsn(ICONST_5);
      default -> {
        if (value < Byte.MAX_VALUE) {
          F6.mw().visitIntInsn(BIPUSH, value);
        } else if (value < Short.MAX_VALUE) {
          F6.mw().visitIntInsn(SIPUSH, value);
        } else {
          F6.mw().visitLdcInsn(value);
        }
      }
    }
  }

  public static void arraySetOperation(YaaClz array) {
    var type_argument = array.inputted.get(0);
    switch (type_argument.name) {
      case int$name, boole$name -> {
        F6.mw().visitInsn(Opcodes.IASTORE);
      }
      case short$name -> {
        F6.mw().visitInsn(Opcodes.SASTORE);
      }
      case byte$name -> {
        F6.mw().visitInsn(Opcodes.BASTORE);
      }
      case char$name -> {
        F6.mw().visitInsn(Opcodes.CASTORE);
      }
      case long$name -> {
        F6.mw().visitInsn(Opcodes.LASTORE);
      }
      case double$name -> {
        F6.mw().visitInsn(Opcodes.DASTORE);
      }
      case float$name -> {
        F6.mw().visitInsn(Opcodes.FASTORE);
      }
      default -> {
        F6.mw().visitInsn(Opcodes.AASTORE);
      }
    }
  }

  public static void arrayGetOperation(YaaClz array) {
    var type_argument = array.inputted.get(0);
    switch (type_argument.name) {
      case int$name, boole$name -> {
        F6.mw().visitInsn(Opcodes.IALOAD);
      }
      case short$name -> {
        F6.mw().visitInsn(Opcodes.SALOAD);
      }
      case byte$name -> {
        F6.mw().visitInsn(Opcodes.BALOAD);
      }
      case char$name -> {
        F6.mw().visitInsn(Opcodes.CALOAD);
      }
      case long$name -> {
        F6.mw().visitInsn(Opcodes.LALOAD);
      }
      case double$name -> {
        F6.mw().visitInsn(Opcodes.DALOAD);
      }
      case float$name -> {
        F6.mw().visitInsn(Opcodes.FALOAD);
      }
      default -> {
        F6.mw().visitInsn(Opcodes.AALOAD);
      }
    }
  }

  public static void runF6Stmts(List<Stmt> stmts) {
    for (var stmt : stmts) {
      if (stmt instanceof VDefinition def) {
        F6VDef.def(def);
        continue;
      }
      if (stmt instanceof Anonymous) {
        continue;
      }
      var label = new Label();
      F6.mw().visitLabel(label);
      var result = stmt.visit(fs6);
      F6.mw().visitLineNumber(stmt.start.line, label);
      if (result != null) {
        switch (result.name) {
          case double$name, long$name -> {
            F6.mw().visitInsn(POP2);
          }
          case void$name -> {
          }
          default -> {
            F6.mw().visitInsn(POP);
          }
        }
      }
    }
  }

  public static void runF6Stmt(Stmt stmt) {
    if (stmt instanceof VDefinition def) {
      F6VDef.def(def);
      return;
    }
    if (stmt instanceof Anonymous) {
      return;
    }
    var label = new Label();
    F6.mw().visitLabel(label);
    var result = stmt.visit(fs6);
    F6.mw().visitLineNumber(stmt.start.line, label);
    if (result != null) {
      switch (result.name) {
        case double$name, long$name -> {
          F6.mw().visitInsn(POP2);
        }
        case void$name -> {
        }
        default -> {
          F6.mw().visitInsn(POP);
        }
      }
    }
  }

  public static void runArguments(List<YaaInfo> parameters, List<Stmt> arguments) {
    for (var i = 0; i < parameters.size(); i++) {
      var parameter = parameters.get(i);
      matchInfo(parameter, arguments.get(i));
    }
  }

  public static YaaInfo matchInfo(YaaInfo declaredInfo, Stmt raw$argument) {
    if (declaredInfo.isUnboundedAndNotPrimitive()) {
      var argument = raw$argument.visit(fs6);
      if (argument.isPrimitive()) {
        boxPrimitive(argument.name);
      }
      return argument;
    } else {
      if (declaredInfo.isPrimitive()) {
        var argument = code4Match(declaredInfo, raw$argument);
        if (argument.isBoxed()) {
          unBox(argument.name);
          castTo(unboxedName(argument.name), declaredInfo.name);
        }
        return argument;
      }
      var argument = raw$argument.visit(fs6);
      if (declaredInfo.isBoxed() && argument.isBoxed()) {
        F6CastUtils.castBoxedToBoxed(argument.name, declaredInfo.name);
        return argument;
      }
      if (argument.isPrimitive()) {
        if (declaredInfo.isBoxed()) {
          castTo(argument.name, unboxedName(declaredInfo.name));
          boxPrimitive(unboxedName(declaredInfo.name));
        } else {
          boxPrimitive(argument.name);
        }
      }
      return argument;
    }
  }

  private static YaaInfo code4Match(YaaInfo data, Stmt raw$value) {
    YaaInfo argument;
    switch (data.name) {
      case double$name -> {
        if (raw$value instanceof Pointed decimal) {
          generateDoubleCode(parseDouble(decimal.token.neededContent));
          argument = double$clz;
        } else if (raw$value instanceof Decimal decimal) {
          generateDoubleCode(parseInt(decimal.token.neededContent));
          argument = int$clz;
        } else if (raw$value instanceof Floated floated) {
          generateDoubleCode(parseFloat(floated.token.neededContent));
          argument = float$clz;
        } else if (raw$value instanceof Longed longed) {
          generateDoubleCode(parseLong(longed.token.neededContent));
          argument = long$clz;
        } else if (raw$value instanceof Shorted shorted) {
          generateDoubleCode(parseInt(shorted.token.neededContent));
          argument = short$clz;
        } else if (raw$value instanceof Byted byted) {
          generateDoubleCode(parseInt(byted.token.neededContent));
          argument = byte$clz;
        } else if (raw$value instanceof Cha cha) {
          if (cha.itIsInterpolated) {
            var e$name = raw$value.visit(fs6).name;
            castTo(e$name, double$name);
          } else {
            generateDoubleCode(
                Character.hashCode(cha.content.toString().charAt(0))
            );
          }
          argument = char$clz;
        } else {
          var e$name = raw$value.visit(fs6);
          if (!e$name.name.equals(double$name)) {
            castTo(e$name.name, double$name);
          }
          argument = e$name;
        }
      }
      case float$name -> {
        if (raw$value instanceof Decimal decimal) {
          generateFloatCode(parseInt(decimal.token.neededContent));
          argument = int$clz;
        } else if (raw$value instanceof Floated floated) {
          generateFloatCode(parseFloat(floated.token.neededContent));
          argument = float$clz;
        } else if (raw$value instanceof Longed longed) {
          generateFloatCode(parseLong(longed.token.neededContent));
          argument = long$clz;
        } else if (raw$value instanceof Shorted shorted) {
          var content = shorted.token.neededContent;
          var short$numb = parseInt(content.substring(0, content.length() - 1));
          generateFloatCode(short$numb);
          argument = short$clz;
        } else if (raw$value instanceof Byted byted) {
          generateFloatCode(parseInt(byted.token.neededContent));
          argument = byte$clz;
        } else if (raw$value instanceof Cha cha) {
          if (cha.itIsInterpolated) {
            var e$name = raw$value.visit(fs6).name;
            castTo(e$name, float$name);
          } else {
            generateFloatCode(Character.hashCode(cha.char$content));
          }
          argument = char$clz;
        } else {
          var e$name = raw$value.visit(fs6);
          if (!e$name.name.equals(float$name)) {
            castTo(e$name.name, float$name);
          }
          argument = e$name;
        }
      }
      case long$name -> {
        if (raw$value instanceof Decimal decimal) {
          generateLongCode(parseInt(decimal.token.neededContent));
          argument = int$clz;
        } else if (raw$value instanceof Longed longed) {
          var content = longed.token.neededContent;
          var long$numb = (content.substring(0, content.length() - 1));
          generateLongCode(parseLong(long$numb));
          argument = long$clz;
        } else if (raw$value instanceof Shorted shorted) {
          generateLongCode(parseInt(shorted.token.neededContent));
          argument = short$clz;
        } else if (raw$value instanceof Byted byted) {
          generateLongCode(parseInt(byted.token.neededContent));
          argument = byte$clz;
        } else if (raw$value instanceof Cha cha) {
          if (cha.itIsInterpolated) {
            var e$name = raw$value.visit(fs6).name;
            castTo(e$name, long$name);
          } else {
            generateLongCode(
                Character.hashCode(cha.content.toString().charAt(0))
            );
          }
          argument = char$clz;
        } else {
          var e$name = raw$value.visit(fs6);
          if (!e$name.name.equals(long$name)) {
            castTo(e$name.name, long$name);
          }
          argument = e$name;
        }
      }
      case int$name -> {
        if (raw$value instanceof Decimal decimal) {
          generateIntCode(parseInt(decimal.token.neededContent));
          argument = int$clz;
        } else if (raw$value instanceof Shorted shorted) {
          generateIntCode(parseInt(shorted.token.neededContent));
          argument = short$clz;
        } else if (raw$value instanceof Byted byted) {
          generateIntCode(parseInt(byted.token.neededContent));
          argument = byte$clz;
        } else if (raw$value instanceof Cha cha) {
          if (cha.itIsInterpolated) {
            var e$name = raw$value.visit(fs6).name;
            castTo(e$name, int$name);
          } else {
            generateIntCode(
                Character.hashCode(cha.content.toString().charAt(0))
            );
          }
          argument = char$clz;
        } else {
          var e$name = raw$value.visit(fs6);
          if (!e$name.name.equals(int$name)) {
            castTo(e$name.name, int$name);
          }
          argument = e$name;
        }
      }
      case char$name -> {
        if (raw$value instanceof Shorted shorted) {
          var content = shorted.token.neededContent;
          var short$numb = parseInt(removeLastChar(content));
          generateIntCode(short$numb);
          argument = short$clz;
        } else if (raw$value instanceof Byted byted) {
          generateIntCode(parseInt(byted.token.neededContent));
          argument = byte$clz;
        } else {
          var e$name = raw$value.visit(fs6);
          castTo(e$name.name, char$name);
          argument = e$name;
        }
      }
      case short$name -> {
        if (raw$value instanceof Shorted shorted) {
          generateIntCode(parseInt(shorted.token.neededContent));
          argument = short$clz;
        } else if (raw$value instanceof Byted byted) {
          generateIntCode(parseInt(byted.token.neededContent));
          argument = byte$clz;
        } else {
          var e$name = raw$value.visit(fs6);
          castTo(e$name.name, short$name);
          argument = e$name;
        }
      }
      case byte$name -> {
        raw$value.visit(fs6);
        argument = byte$clz;
      }
      case boole$name -> {
        raw$value.visit(fs6);
        argument = boole$clz;
      }
      default -> {
        argument = raw$value.visit(fs6);
      }
    }
    return argument;
  }

  public static String removeLastChar(String originalString) {
    return originalString.substring(0, originalString.length() - 1);
  }

  public static String boxedName(String primitive$name) {
    switch (primitive$name) {
      case int$name -> {
        return int$boxed;
      }
      case double$name -> {
        return double$boxed;
      }
      case float$name -> {
        return float$boxed;
      }
      case char$name -> {
        return char$boxed;
      }
      case byte$name -> {
        return byte$boxed;
      }
      case short$name -> {
        return short$boxed;
      }
      case long$name -> {
        return long$boxed;
      }
      default -> {
        return boole$boxed;
      }
    }
  }

  public static String unboxedName(String boxed$name) {
    switch (boxed$name) {
      case int$boxed -> {
        return int$name;
      }
      case double$boxed -> {
        return double$name;
      }
      case float$boxed -> {
        return float$name;
      }
      case char$boxed -> {
        return char$name;
      }
      case byte$boxed -> {
        return byte$name;
      }
      case short$boxed -> {
        return short$name;
      }
      case long$boxed -> {
        return long$name;
      }
      default -> {
        return boole$name;
      }
    }
  }

  public static void loadDefaultData(String typeName) {
    switch (typeName) {
      case int$name,
          byte$name,
          boole$name,
          char$name,
          short$name -> F6.mw().visitInsn(ICONST_0);
      case long$name -> F6.mw().visitInsn(LCONST_0);
      case double$name -> F6.mw().visitInsn(DCONST_0);
      case float$name -> F6.mw().visitInsn(FCONST_0);
      default -> F6.mw().visitInsn(ACONST_NULL);
    }
  }

  public static void pushStringData(Stmt stmt) {
    var data = stmt.visit(fs6);
    if (!data.name.equals("java.lang.String")) {
      switch (data.name) {
        case int$name -> {
          F6.mw().visitMethodInsn(
              INVOKESTATIC,
              "java/lang/Integer",
              "toString",
              "(" + data.descriptor() + ")Ljava/lang/String;",
              false
          );
        }
        case byte$name -> {
          F6.mw().visitMethodInsn(
              INVOKESTATIC,
              "java/lang/Byte",
              "toString",
              "(" + data.descriptor() + ")Ljava/lang/String;",
              false
          );
        }
        case boole$name -> {
          F6.mw().visitMethodInsn(
              INVOKESTATIC,
              "java/lang/Boolean",
              "toString",
              "(" + data.descriptor() + ")Ljava/lang/String;",
              false
          );
        }
        case char$name -> {
          F6.mw().visitMethodInsn(
              INVOKESTATIC,
              "java/lang/Character",
              "toString",
              "(" + data.descriptor() + ")Ljava/lang/String;",
              false
          );
        }
        case short$name -> {
          F6.mw().visitMethodInsn(
              INVOKESTATIC,
              "java/lang/Short",
              "toString",
              "(" + data.descriptor() + ")Ljava/lang/String;",
              false
          );
        }
        case long$name -> {
          F6.mw().visitMethodInsn(
              INVOKESTATIC,
              "java/lang/Long",
              "toString",
              "(" + data.descriptor() + ")Ljava/lang/String;",
              false
          );
        }
        case double$name -> {
          F6.mw().visitMethodInsn(
              INVOKESTATIC,
              "java/lang/Double",
              "toString",
              "(" + data.descriptor() + ")Ljava/lang/String;",
              false
          );
        }
        case float$name -> {
          F6.mw().visitMethodInsn(
              INVOKESTATIC,
              "java/lang/Float",
              "toString",
              "(" + data.descriptor() + ")Ljava/lang/String;",
              false
          );
        }
        default -> {
          F6.mw().visitMethodInsn(
              INVOKESTATIC,
              "java/util/Objects",
              "toString",
              "(" + data.descriptor() + ")Ljava/lang/String;",
              false
          );
        }
      }
    }
  }

  public static void setTopFields() {
    fs6.push$fields();
    F6.mtdWriters.push(fs6.cw.peek().visitMethod(
        ACC_STATIC, "<clinit>", "()V", null, null
    ));
    F6.mw().visitCode();
    for (var stmt : fs6.stmts) {
      if (stmt instanceof VDefinition def) {
        var name = def.name.content;
        var field = (YaaField) fs6.getSymbol(name);
        var mod = ACC_PUBLIC + ACC_STATIC;
        fs6.cw.peek().visitField(
            mod, name, field.descriptor(), null, null
        ).visitEnd();
        fs6.clz$fields.peek().put(field);

        def.value.visit(fs6);
        F6.mw().visitFieldInsn(
            PUTSTATIC, field.owner, name, field.descriptor()
        );
      } else if (stmt instanceof VDeclaration dec) {
        var name = dec.name.content;
        var field = (YaaField) fs6.getSymbol(name);
        var mod = ACC_PUBLIC + ACC_STATIC;
        fs6.cw.peek().visitField(
            mod, name, field.descriptor(), null, null
        ).visitEnd();
        fs6.clz$fields.peek().put(field);
      }
    }
    F6.mw().visitInsn(RETURN);
    F6.mw().visitMaxs(0, 0);
    F6.mw().visitEnd();
    F6.mtdWriters.pop();
    fs6.pop$fields();
  }

  public static void negateTopValue() {
    var zero$case = new Label();
    var end$point = new Label();
    F6.mw().visitJumpInsn(IFEQ, zero$case);
    F6.mw().visitInsn(ICONST_0);
    F6.mw().visitJumpInsn(GOTO, end$point);
    F6.mw().visitLabel(zero$case);
    F6.mw().visitInsn(ICONST_1);
    F6.mw().visitLabel(end$point);
  }

  public static String generateRandomName() {
    return "name" + System.nanoTime();
  }

  public static String generateRandomName(String prefix) {
    return prefix + System.nanoTime();
  }

  public static void unBox(String boxed$name) {
    switch (boxed$name) {
      case int$boxed -> {
        F6.mw().visitMethodInsn(
            INVOKEVIRTUAL,
            "java/lang/Integer",
            "intValue",
            "()I",
            false
        );
      }
      case double$boxed -> {
        F6.mw().visitMethodInsn(
            INVOKEVIRTUAL,
            "java/lang/Double",
            "doubleValue",
            "()D",
            false
        );
      }
      case float$boxed -> {
        F6.mw().visitMethodInsn(
            INVOKEVIRTUAL,
            "java/lang/Float",
            "floatValue",
            "()F",
            false
        );
      }
      case char$boxed -> {
        F6.mw().visitMethodInsn(
            INVOKEVIRTUAL,
            "java/lang/Character",
            "charValue",
            "()C",
            false
        );
      }
      case byte$boxed -> {
        F6.mw().visitMethodInsn(
            INVOKEVIRTUAL,
            "java/lang/Byte",
            "byteValue",
            "()B",
            false
        );
      }
      case short$boxed -> {
        F6.mw().visitMethodInsn(
            INVOKEVIRTUAL,
            "java/lang/Short",
            "shortValue",
            "()S",
            false
        );
      }
      case long$boxed -> {
        F6.mw().visitMethodInsn(
            INVOKEVIRTUAL,
            "java/lang/Long",
            "longValue",
            "()J",
            false
        );
      }
      case boole$boxed -> {
        F6.mw().visitMethodInsn(
            INVOKEVIRTUAL,
            "java/lang/Boolean",
            "byteValue",
            "()Z",
            false
        );
      }
    }
  }

  public static String boxPrimitive(String primitive$name) {
    switch (primitive$name) {
      case int$name -> {
        F6.mw().visitMethodInsn(
            INVOKESTATIC,
            "java/lang/Integer",
            "valueOf",
            "(I)Ljava/lang/Integer;",
            false
        );
        return "java.lang.Integer";
      }
      case double$name -> {
        F6.mw().visitMethodInsn(
            INVOKESTATIC,
            "java/lang/Double",
            "valueOf",
            "(D)Ljava/lang/Double;",
            false
        );
        return "java.lang.Double";
      }
      case float$name -> {
        F6.mw().visitMethodInsn(
            INVOKESTATIC,
            "java/lang/Float",
            "valueOf",
            "(F)Ljava/lang/Float;",
            false
        );
        return "java.lang.Float";
      }
      case char$name -> {
        F6.mw().visitMethodInsn(
            INVOKESTATIC,
            "java/lang/Character",
            "valueOf",
            "(C)Ljava/lang/Character;",
            false
        );
        return "java.lang.Character";
      }
      case byte$name -> {
        F6.mw().visitMethodInsn(
            INVOKESTATIC,
            "java/lang/Byte",
            "valueOf",
            "(B)Ljava/lang/Byte;",
            false
        );
        return "java.lang.Byte";
      }
      case short$name -> {
        F6.mw().visitMethodInsn(
            INVOKESTATIC,
            "java/lang/Short",
            "valueOf",
            "(S)Ljava/lang/Short;",
            false
        );
        return "java.lang.Short";
      }
      case long$name -> {
        F6.mw().visitMethodInsn(
            INVOKESTATIC,
            "java/lang/Long",
            "valueOf",
            "(J)Ljava/lang/Long;",
            false
        );
        return "java.lang.Long";
      }
      case boole$name -> {
        F6.mw().visitMethodInsn(
            INVOKESTATIC,
            "java/lang/Boolean",
            "valueOf",
            "(Z)Ljava/lang/Boolean;",
            false
        );
        return "java.lang.Boolean";
      }
    }
    return "java.lang.Integer";
  }

  public static String codeName(String name) {
    return switch (name) {
      case boole$boxed -> "java/lang/Boolean";
      case long$boxed -> "java/lang/Long";
      case short$boxed -> "java/lang/Short";
      case float$boxed -> "java/lang/Float";
      case double$boxed -> "java/lang/Double";
      case byte$boxed -> "java/lang/Byte";
      case char$boxed -> "java/lang/Character";
      default -> "java/lang/Integer";
    };
  }

  public static void generateLongCode(long value) {
    if (value == 0L) {
      F6.mw().visitInsn(LCONST_0);
    } else if (value == 1L) {
      F6.mw().visitInsn(LCONST_1);
    } else {
      F6.mw().visitLdcInsn(value);
    }
  }

  public static void generateDoubleCode(double value) {
    if (value == 0.0d) {
      F6.mw().visitInsn(DCONST_0);
    } else if (value == 1.0d) {
      F6.mw().visitInsn(DCONST_1);
    } else {
      F6.mw().visitLdcInsn(value);
    }
  }

  public static void generateFloatCode(float value) {
    if (value == 0.0) {
      F6.mw().visitInsn(FCONST_0);
    } else if (value == 1.0) {
      F6.mw().visitInsn(FCONST_1);
    } else if (value == 2.0) {
      F6.mw().visitInsn(FCONST_2);
    } else {
      F6.mw().visitLdcInsn(value);
    }
  }
}
