package yaa.semantic.passes.fs6.ifs;

import org.objectweb.asm.Label;
import yaa.ast.IfStmt;
import yaa.ast.RunBlock;
import yaa.pojos.GlobalData;
import yaa.pojos.YaaClz;
import yaa.semantic.passes.fs6.F6Utils;

import java.util.HashMap;
import java.util.TreeMap;

import static org.objectweb.asm.Opcodes.GOTO;
import static yaa.semantic.passes.fs6.F6.mw;

public class EnumSwitch {
  public static void handleEnumSwitch(IfStmt ctx, YaaClz target) {
    if (ctx.cases.size() == target.enumIndices.size()) {
      //this means all the enum cases were handled
      //so table switching will work
      handleTableSwitch(ctx, target);
      return;
    }
    var cases = ctx.cases;
    var labels = new SwitchLabeller(cases.size());

    var end$jump = new Label();
    var keys = new int[cases.size()];

    //The elements of a look-up switch must be sorted by their keys
    var values = new TreeMap<Integer, Integer>();

    int o = 0;
    for (var option : ctx.enumOptions) {
      values.put(target.enumIndices.get(option), o);
      labels.putLabel(new Label());
      o++;
    }

    var i = 0;
    for (var value : values.entrySet()) {
      keys[i++] = value.getKey();
    }

    var defaultJump = new Label();
    var jumpIndices = new HashMap<String, Integer>();

    mw().visitLookupSwitchInsn(defaultJump, keys, labels.labels);

    int j = 0;
    for (var value : values.entrySet()) {
      var c$case = cases.get(value.getValue());

      var case$holder = c$case.caseLabel;
      if (case$holder != null) {
        jumpIndices.put(case$holder.content, j);
      }
      j++;
    }

    int k = 0;
    for (var value : values.entrySet()) {
      var caseIndex = value.getValue();
      var c$case = cases.get(caseIndex);

      GlobalData.fs6.pushTable(c$case);
      mw().visitLabel(labels.labels[k]);
      if (c$case.targetName != null) {
        if (c$case.stmt instanceof RunBlock block) {
          F6Utils.runF6Stmts(block.stmts.subList(0, block.stmts.size() - 1));
          mw().visitJumpInsn(GOTO, labels.labels[jumpIndices.get(c$case.targetName)]);
        } else {
          //the statement must be a jump
          mw().visitJumpInsn(GOTO, labels.labels[jumpIndices.get(c$case.targetName)]);
        }
      } else {
        F6Utils.runF6Stmt(c$case.stmt);
        mw().visitJumpInsn(GOTO, end$jump);
      }
      GlobalData.fs6.popTable();
      k++;
    }

    mw().visitLabel(defaultJump);
    F6Utils.runF6Stmts(ctx.elseStmts);
    mw().visitLabel(end$jump);
  }

  private static void handleTableSwitch(IfStmt ctx, YaaClz target) {
    var cases = ctx.cases;
    var labels = new SwitchLabeller(cases.size());

    var finalDestination = new Label();

    //The elements of a look-up switch must be sorted by their keys
    var values = new TreeMap<Integer, Integer>();

    for (int i = 0; i < cases.size(); i++) {
      values.put(target.enumIndices.get(ctx.enumOptions.get(i)), i);
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
