package yaa.semantic.handlers;

import yaa.ast.ObjectType;
import yaa.parser.BinaryStmt;
import yaa.ast.Stmt;
import yaa.pojos.*;
import yaa.semantic.passes.fs6.results.CallResult;

import java.util.ArrayList;
import java.util.List;

public class OpUtils {
  protected static YaaInfo binaryOp(String op_name, BinaryStmt ctx) {
    var l_value = ctx.e1.visit(GlobalData.fs);
    var r_value = ctx.e2.visit(GlobalData.fs);
    if (l_value instanceof YaaClz clz) {
      if (l_value.isPrimitive() && r_value.isBoxed()) {
        throw new YaaError(
            ctx.e2.placeOfUse(),
            "No method in " + l_value + " matched the given arguments",
            clz.getMethod(op_name).candidates()
        );
      }
      if (r_value.isBoxed() && l_value.isPrimitive()) {
        throw new YaaError(
            ctx.op.placeOfUse(), r_value.toString(),
            "The type above does not define the " + op_name + " method"
        );
      }
      var pack = clz.getMethod(op_name);
      if (pack != null) {
        var mtd = pack.choseOpMtd(r_value);
        if (mtd != null) {
          var result = new CallResult(mtd);
          GlobalData.results.get(GlobalData.fs.path).put(ctx, result);
          return mtd.type;
        }
        throw new YaaError(
            ctx.e2.placeOfUse(),
            "No method in " + l_value + " matched the given arguments",
            pack.candidates()
        );
      }
      throw new YaaError(
          ctx.op.placeOfUse(), l_value.toString(),
          "The type above does " +
              "not define the \"" + op_name + "\" method"
      );
    }
    throw new YaaError(
        ctx.op.placeOfUse(),
        "The attempted operation is illegal"
    );
  }

  protected static List<YaaClz> t$arguments(List<ObjectType> types) {
    var visited = new ArrayList<YaaClz>(types.size());
    for (var type : types) {
      visited.add((YaaClz) type.visit(GlobalData.fs));
    }
    return visited;
  }

  public static List<YaaInfo> v$arguments(List<Stmt> arguments) {
    var visited = new ArrayList<YaaInfo>(arguments.size());
    for (var argument : arguments) {
      var visited_argument = argument.visit(GlobalData.fs);
      visited.add(visited_argument);
    }
    return visited;
  }

  public static void checkForVisibility(String address, YaaField field) {
    if (field.owner != null) {
      if (field.privacy == 2) {
        if (GlobalData.topClz.isEmpty()) {
          throw new YaaError(
              address, "The field \"" + field.field$name + "\" is declared private in "
              + field.owner.replace("/", ".")
          );
        }
        if (!GlobalData.topClz.peek().codeName.equals(field.owner)) {
          throw new YaaError(
              address, "The field \"" + field.field$name + "\" is declared private in "
              + field.owner.replace("/", ".")
          );
        }
      }
      if (field.privacy == 1) {
        if (!field.path.equals(GlobalData.fs.path)) {
          throw new YaaError(
              address, "The field \"" + field.field$name + "\" is declared protected in "
              + field.owner.replace("/", ".")
          );
        }
      }
    }
    if (field.privacy == 2) {
      if (GlobalData.topClz.isEmpty()) {
        throw new YaaError(
            address, "The field \"" + field.field$name + "\" is declared private in "
            + field.path
        );
      }
      if (!GlobalData.topClz.peek().codeName.equals(field.owner)) {
        throw new YaaError(
            address, "The field \"" + field.field$name + "\" is declared private in "
            + field.path
        );
      }
    }
    if (field.privacy == 1) {
      if (!field.path.equals(GlobalData.fs.path)) {
        throw new YaaError(
            address, "The field \"" + field.field$name + "\" is declared protected in "
            + field.path
        );
      }
    }
  }

  public static void checkForVisibility(String address, YaaFun mtd) {
    if (mtd.privacy == 2) {
      if (GlobalData.topClz.isEmpty()) {
        throw new YaaError(
            address, mtd + " is declared private in "
            + mtd.owner.replace("/", "."),
            "Private methods can only be called " +
                "within the scope of their declaring type"
        );
      }
      if (!GlobalData.topClz.peek().codeName.equals(mtd.owner)) {
        throw new YaaError(
            address, mtd + " is declared private in "
            + mtd.owner.replace("/", "."),
            "Private methods can only be called " +
                "within the scope of their declaring type"
        );
      }
    }
    if (mtd.privacy == 1) {
      if (!mtd.path.equals(GlobalData.fs.path)) {
        throw new YaaError(
            address, mtd + " is declared protected in "
            + mtd.owner.replace("/", "."),
            "Protected methods can only be called " +
                "from the same source file"
        );
      }
    }
  }

  public static void checkForVisibility(String address, YaaClz clz) {
    if (clz.privacy == 2) {
      throw new YaaError(
          address, clz + " is declared private",
          "private types cannot be imported"
      );
    }
  }
}
