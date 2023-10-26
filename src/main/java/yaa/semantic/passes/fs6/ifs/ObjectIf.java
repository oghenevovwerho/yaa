package yaa.semantic.passes.fs6.ifs;

import yaa.ast.IfStmt;
import yaa.ast.RunBlock;
import yaa.semantic.passes.fs6.F6Utils;
import yaa.pojos.YaaInfo;
import org.objectweb.asm.Label;
import yaa.pojos.GlobalData;

import java.util.HashMap;
import java.util.HashSet;

import static yaa.semantic.passes.fs6.F6.mw;
import static org.objectweb.asm.Opcodes.*;
import static yaa.semantic.passes.fs6.F6Utils.*;

public class ObjectIf {
  public static void ifWithObjectTarget(IfStmt ctx, YaaInfo target) {
    var topName = generateRandomName();
    var newFieldIndex = GlobalData.fs6.variables.peek().putVar(topName);
    mw().visitVarInsn(ASTORE, newFieldIndex);

    var cases = ctx.cases;
    var labelled = new HashMap<String, Integer>(ctx.cases.size());
    var labels = new Label[ctx.cases.size()];
    for (int i = 0; i < cases.size(); i++) {
      var pre$case = cases.get(i);
      if (pre$case.caseLabel != null) {
        labelled.put(pre$case.caseLabel.content, i);
      }
      labels[i] = new Label();
    }

    var end$jump = new Label();

    var autoCasts = new HashSet<String>();

    for (int i = 0; i < cases.size(); i++) {
      var c$case = cases.get(i);
      GlobalData.fs6.pushTable(c$case);

      if (c$case.autoCasts() && c$case.caseLabel != null) {
        autoCasts.add(c$case.caseLabel.content);
      }

      GlobalData.fs6.variables.peek().load(target.name, topName);
      var case$offer = CondUtils.getCaseOffer(c$case);

      if (case$offer.isPrimitive()) {
        F6Utils.boxPrimitive(case$offer.name);
      }
      mw().visitMethodInsn(
          INVOKEVIRTUAL, target.codeName,
          "equals",
          "(Ljava/lang/Object;)Z", false
      );
      mw().visitJumpInsn(IFNE, labels[i]);
      GlobalData.fs6.popTable();
    }

    runF6Stmts(ctx.elseStmts);
    mw().visitJumpInsn(GOTO, end$jump);

    for (int i = 0; i < cases.size(); i++) {
      mw().visitLabel(labels[i]);
      var c$case = cases.get(i);
      if (c$case.caseLabel != null && autoCasts.contains(c$case.caseLabel.content)) {
        //This is to facilitate jumping to a block that auto-casts
        CondUtils.putCaseOffer(c$case);
      }
      var caseStmt = c$case.stmt;
      GlobalData.fs6.pushTable(c$case);
      if (c$case.targetName != null) {
        if (caseStmt instanceof RunBlock block) {
          F6Utils.runF6Stmts(block.stmts.subList(0, block.stmts.size() - 1));
          mw().visitJumpInsn(GOTO, labels[labelled.get(c$case.targetName)]);
        } else {
          //the statement is a jump
          mw().visitJumpInsn(GOTO, labels[labelled.get(c$case.targetName)]);
        }
      } else {
        runF6Stmt(caseStmt);
        mw().visitJumpInsn(GOTO, end$jump);
      }
      GlobalData.fs6.popTable();
    }
    mw().visitLabel(end$jump);
  }
}