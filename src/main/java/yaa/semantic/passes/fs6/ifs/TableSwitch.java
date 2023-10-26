package yaa.semantic.passes.fs6.ifs;

import yaa.ast.*;
import yaa.semantic.passes.fs6.F6Utils;
import org.objectweb.asm.Label;
import yaa.pojos.GlobalData;

import java.util.HashMap;
import java.util.TreeMap;

import static yaa.semantic.passes.fs6.F6.mw;
import static java.lang.Integer.parseInt;
import static org.objectweb.asm.Opcodes.GOTO;

public class TableSwitch {
  public static void handleTableSwitch(IfStmt ctx) {
    var cases = ctx.cases;
    var labels = new SwitchLabeller(cases.size());

    var finalDestination = new Label();

    //The elements of a look-up switch must be sorted by their keys
    var values = new TreeMap<Integer, Integer>();

    for (int i = 0; i < cases.size(); i++) {
      var e_value = cases.get(i).case_condition;
      if (e_value instanceof Decimal decimal) {
        values.put(parseInt(decimal.token.content), i);
      } else if (e_value instanceof Shorted shorted) {
        values.put(parseInt(shorted.token.neededContent), i);
      } else if (e_value instanceof Byted byted) {
        values.put(parseInt(byted.token.neededContent), i);
      } else if (e_value instanceof Basex basex &&
          (basex.xToken.isShorted || basex.xToken.isInt
              || basex.xToken.isLong || basex.xToken.isByte)) {
        values.put(parseInt(basex.xToken.content, basex.xToken.base), i);
      } else if (e_value instanceof Cha cha) {
        values.put(Character.hashCode(cha.content.toString().charAt(0)), i);
      }
      labels.putLabel(new Label());
    }

    var elseJump = new Label();
    mw().visitTableSwitchInsn(values.firstKey(), values.lastKey(), elseJump, labels.labels);
    var jumpIndices = new HashMap<String, Integer>();

    int j = 0;
    for (var value : values.entrySet()) {
      var c$case = cases.get(value.getValue());

      var case$holder = c$case.caseLabel;
      if (case$holder != null) {
        jumpIndices.put(case$holder.content, j);
      }
      j++;
    }

    int i = 0;
    for (var value : values.entrySet()) {
      var caseIndex = value.getValue();
      var c$case = cases.get(caseIndex);
      GlobalData.fs6.pushTable(c$case);

      mw().visitLabel(labels.labels[i]);
      if (c$case.targetName != null) {
        if (c$case.stmt instanceof RunBlock block) {
          F6Utils.runF6Stmts(block.stmts.subList(0, block.stmts.size() - 1));
          mw().visitJumpInsn(GOTO, labels.labels[jumpIndices.get(c$case.targetName)]);
        } else {
          //the statement is a jump
          mw().visitJumpInsn(GOTO, labels.labels[jumpIndices.get(c$case.targetName)]);
        }
      } else {
        F6Utils.runF6Stmt(c$case.stmt);
        mw().visitJumpInsn(GOTO, finalDestination);
      }
      GlobalData.fs6.popTable();
      i++;
    }

    mw().visitLabel(elseJump);
    F6Utils.runF6Stmts(ctx.elseStmts);
    mw().visitLabel(finalDestination);
  }
}
