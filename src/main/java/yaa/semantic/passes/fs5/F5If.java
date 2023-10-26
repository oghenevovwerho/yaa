package yaa.semantic.passes.fs5;

import yaa.ast.*;
import yaa.pojos.*;
import yaa.semantic.passes.fs1.Fs1Utils;
import yaa.semantic.passes.fs6.results.IsResult;

import java.util.ArrayList;
import java.util.TreeMap;

import static java.lang.Integer.parseInt;
import static yaa.pojos.BlockKind.case$block;
import static yaa.pojos.TypeCategory.enum_c;
import static yaa.semantic.passes.fs5.F5Callable.stmtGivesValue;

public class F5If {
  static boolean f5IfStmt(IfStmt ctx) {
    GlobalData.fs5.pushTable(ctx);
    if (ctx.e instanceof Is is && is.e instanceof Name name) {
      var argument = is.e.visit(GlobalData.fs5);
      var type = YaaClz.fsClz(is.type);
      var call$result = new IsResult(argument, type);
      GlobalData.results.get(GlobalData.fs.path).put(is, call$result);
      ctx.hasPrimitiveTarget = true;

      var nameContent = name.token.content;
      var field = new YaaField(nameContent);
      field.data = type;
      GlobalData.fs5.putSymbol(nameContent, field);
      //GlobalData.fs5.replaceField(nameContent, field);
    } else {
      var condition = ctx.e.visit(GlobalData.fs5);
      ctx.hasPrimitiveTarget = condition.isPrimitive();
      if (condition instanceof YaaClz clz && clz.category == enum_c) {
        var all$case$true = runEnumCases(ctx, clz);
        ctx.isEnumSwitch = true;
        var e$stmts = ctx.elseStmts;
        if (all$case$true && e$stmts.size() == 0) {
          return false;
        }

        for (int i = 0; i < e$stmts.size(); i++) {
          if (stmtGivesValue(e$stmts.get(i))) {
            if (i < e$stmts.size() - 1) {
              throw new YaaError(
                  e$stmts.get(i + 1).placeOfUse(),
                  "Unreachable statement after " +
                      "return from \"" + GlobalData.fs5.fn$name.peek() + "\""
              );
            }
            return all$case$true;
          }
        }
        return false;
      }
    }
    var all$case$true = runNonEnumCases(ctx);

    var e$stmts = ctx.elseStmts;
    if (all$case$true && e$stmts.size() == 0) {
      return false;
    }

    for (int i = 0; i < e$stmts.size(); i++) {
      if (stmtGivesValue(e$stmts.get(i))) {
        if (i < e$stmts.size() - 1) {
          throw new YaaError(
              e$stmts.get(i + 1).placeOfUse(),
              "Unreachable statement after " +
                  "return from \"" + GlobalData.fs5.fn$name.peek() + "\""
          );
        }
        return all$case$true;
      }
    }
    GlobalData.fs5.popTable();
    return false;
  }

  private static boolean runEnumCases(IfStmt ctx, YaaClz enumClz) {
    var all$case$true = true;
    var defined_constants = new TreeMap<String, Stmt>();
    var enumOptions = new ArrayList<String>();
    for (var e_case : ctx.cases) {
      if (e_case.case_condition instanceof Name name) {
        var nameContent = name.token.content;
        var defined = defined_constants.get(nameContent);
        if (defined != null) {
          throw new YaaError(
              e_case.placeOfUse(), nameContent
              + " is already handled at " + defined.placeOfUse()
          );
        }
        var field = enumClz.instance$fields.get(nameContent);
        if (field == null) {
          if (enumClz.enumIndices.size() == 0) {
            throw new YaaError(
                e_case.placeOfUse(), "The enum \""
                + enumClz.name + "\" does not define " + nameContent
            );
          }
          throw new YaaError(
              e_case.placeOfUse(), "The enum \""
              + enumClz.name + "\" does not define " + nameContent,
              "The following are the valid enum constants",
              Fs1Utils.stringOfCollection(enumClz.enumIndices.keySet().toArray())
          );
        }
        enumOptions.add(nameContent);
        defined_constants.put(nameContent, e_case);
      } else {
        throw new YaaError(
            e_case.placeOfUse(), "An enum case must be the " +
            "unqualified name of an enumeration constant"
        );
      }
      if (!handleIfCase(e_case)) {
        all$case$true = false;
      }
    }
    ctx.enumOptions = enumOptions;
    return all$case$true;
  }

  private static boolean runNonEnumCases(IfStmt ctx) {
    var all$case$true = true;
    var allCasesAreIntegral = true;
    var values = new TreeMap<Integer, Stmt>();
    GlobalData.fs5.catchHolders.push(ctx.catchHolders);
    for (var caseBlock : ctx.cases) {
      GlobalData.fs5.pushTable(caseBlock);
      YaaInfo case$offer;

      if (caseBlock.case_condition instanceof Is is && is.e instanceof Name name) {
        var argument = is.e.visit(GlobalData.fs5);
        var type = YaaClz.fsClz(is.type);
        var call$result = new IsResult(argument, type);
        GlobalData.results.get(GlobalData.fs.path).put(caseBlock.case_condition, call$result);
        var nameContent = name.token.content;
        var field = new YaaField(nameContent);
        field.data = type;
        GlobalData.fs5.putSymbol(nameContent, field);
        //GlobalData.fs5.replaceField(nameContent, field);
        case$offer = GlobalData.boole$clz;
      } else {
        case$offer = caseBlock.case_condition.visit(GlobalData.fs5);
      }

      var case$e = caseBlock.case_condition;
      if (case$e instanceof Decimal decimal) {
        var int$value = parseInt(decimal.token.content);
        var defined = values.get(int$value);
        if (defined != null) {
          throw new YaaError(
              case$e.placeOfUse(), case$e + " at " + case$e.placeOfUse(),
              "The case value above and the one below " +
                  "resolve to the same values at compile time",
              defined + " at " + defined.placeOfUse()
          );
        }
        values.put(int$value, case$e);
      } else if (case$e instanceof Shorted shorted) {
        var short$numb = parseInt(shorted.token.neededContent);
        var defined = values.get(short$numb);
        if (defined != null) {
          throw new YaaError(
              case$e.placeOfUse(), case$e + " at " + case$e.placeOfUse(),
              "The case value above and the one below " +
                  "resolve to the same values at compile time",
              defined + " at " + defined.placeOfUse()
          );
        }
        values.put(short$numb, case$e);
      } else if (case$e instanceof Basex basex &&
          (basex.xToken.isShorted || basex.xToken.isInt
              || basex.xToken.isLong || basex.xToken.isByte)) {
        var content = basex.xToken.content;
        var number = parseInt(content, basex.xToken.base);
        var defined = values.get(number);
        if (defined != null) {
          throw new YaaError(
              case$e.placeOfUse(), case$e + " at " + case$e.placeOfUse(),
              "The case value above and the one below " +
                  "resolve to the same values at compile time",
              defined + " at " + defined.placeOfUse()
          );
        }
        values.put(number, case$e);
      } else if (case$e instanceof Byted byted) {
        var byte$numb = parseInt(byted.token.neededContent);
        var defined = values.get(byte$numb);
        if (defined != null) {
          throw new YaaError(
              case$e.placeOfUse(), case$e + " at " + case$e.placeOfUse(),
              "The case value above and the one below " +
                  "resolve to the same values at compile time",
              defined + " at " + defined.placeOfUse()
          );
        }
        values.put(byte$numb, case$e);
      } else if (case$e instanceof Cha cha) {
        if (cha.itIsInterpolated) {
          allCasesAreIntegral = false;
        } else {
          var char$value = Character.hashCode(cha.char$content);
          var defined = values.get(char$value);
          if (defined != null) {
            throw new YaaError(
                case$e.placeOfUse(), case$e + " at " + case$e.placeOfUse(),
                "The case value above and the one below " +
                    "resolve to the same values at compile time",
                defined + " at " + defined.placeOfUse()
            );
          }
          values.put(char$value, case$e);
        }
      } else {
        allCasesAreIntegral = false;
      }
      assert case$offer != null;
      caseBlock.resultName = case$offer.name;
      if (case$offer.isPrimitive()) {
        caseBlock.isNotPrimitive = false;
      } else {
        caseBlock.isNotPrimitive = !case$offer.isBoxed();
      }
      if (!handleIfCase(caseBlock)) {
        all$case$true = false;
      }
      var lastJump = caseBlock.lastJump;
      if (lastJump != null) {
        if (lastJump.name != null) {
          var targetName = lastJump.name.content;
          if (ctx.catchHolders.get(targetName) != null) {
            caseBlock.targetName = targetName;
          }
        }
      }
      GlobalData.fs5.popTable();
    }
    GlobalData.fs5.catchHolders.pop();
    if (allCasesAreIntegral) {
      ctx.allCasesAreIntegral = true;
      ctx.canBeTableSwitched = canBeTableSwitched(values.keySet().toArray());
    }
    return all$case$true;
  }

  private static boolean canBeTableSwitched(Object[] case$values) {
    if (case$values.length == 1) {
      return false;
    }
    for (int i = 0; i < case$values.length - 1; i++) {
      var number1 = (Integer) case$values[i];
      var number2 = (Integer) case$values[i + 1];
      if (number2 - number1 > 1) {
        return false;
      }
    }
    return true;
  }

  private static boolean handleIfCase(IfCase caseBlock) {
    GlobalData.fs5.block.push(case$block);
    var result = stmtGivesValue(caseBlock.stmt);
    GlobalData.fs5.block.pop();
    return result;
  }
}
