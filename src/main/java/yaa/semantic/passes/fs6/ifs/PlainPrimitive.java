package yaa.semantic.passes.fs6.ifs;

import yaa.ast.IfStmt;
import yaa.ast.RunBlock;
import yaa.semantic.passes.fs6.F6Utils;
import yaa.pojos.YaaInfo;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import yaa.pojos.GlobalData;

import java.util.HashMap;
import java.util.HashSet;

import static yaa.semantic.passes.fs6.F6.mw;
import static yaa.semantic.passes.fs6.F6Utils.*;
import static yaa.semantic.passes.fs6.ifs.CondUtils.getCaseOffer;
import static yaa.semantic.passes.fs6.ifs.CondUtils.putCaseOffer;
import static org.objectweb.asm.Opcodes.*;

public class PlainPrimitive {
  public static void handlePlainPrimitive(IfStmt ctx, YaaInfo target) {
    var topName = F6Utils.generateRandomName();
    switch (target.name) {
      case GlobalData.long$name -> {
        var newFieldIndex = GlobalData.fs6.variables.peek().putWideVar(topName);
        mw().visitVarInsn(LSTORE, newFieldIndex);
      }
      case GlobalData.double$name -> {
        var newFieldIndex = GlobalData.fs6.variables.peek().putWideVar(topName);
        mw().visitVarInsn(DSTORE, newFieldIndex);
      }
      case GlobalData.float$name -> {
        var newFieldIndex = GlobalData.fs6.variables.peek().putVar(topName);
        mw().visitVarInsn(FSTORE, newFieldIndex);
      }
      default -> {
        var newFieldIndex = GlobalData.fs6.variables.peek().putVar(topName);
        mw().visitVarInsn(ISTORE, newFieldIndex);
      }
    }

    var cases = ctx.cases;
    var labels = new Label[cases.size()];
    var end$jump = new Label();
    var labelled = new HashMap<String, Integer>(ctx.cases.size());

    for (int i = 0; i < cases.size(); i++) {
      var c$case = cases.get(i);
      if (c$case.caseLabel != null) {
        labelled.put(c$case.caseLabel.content, i);
      }
      labels[i] = new Label();
    }

    var autoCasts = new HashSet<String>();

    for (int i = 0; i < cases.size(); i++) {
      var c$case = cases.get(i);
      GlobalData.fs6.pushTable(c$case);
      if (c$case.isNotPrimitive) {
        GlobalData.fs6.variables.peek().load(target.name, topName);
        var boxedName = boxPrimitive(target.name);
        getCaseOffer(c$case);

        mw().visitMethodInsn(
            INVOKEVIRTUAL, codeName(boxedName),
            "equals",
            "(Ljava/lang/Object;)Z", false
        );
        mw().visitJumpInsn(IFNE, labels[i]);
      } else {
        GlobalData.fs6.variables.peek().load(target.name, topName);
        if (c$case.resultName.equals(GlobalData.boole$name) || target.name.equals(GlobalData.boole$name)) {
          if (c$case.autoCasts() && c$case.caseLabel != null) {
            autoCasts.add(c$case.caseLabel.content);
          }
          if (c$case.resultName.equals(target.name)) {
            getCaseOffer(c$case);
            mw().visitJumpInsn(IF_ICMPEQ, labels[i]);
          } else {
            continue;
          }
        } else {
          String widest$name = null;
          if (widest(target.name, c$case.resultName)[0].equals(c$case.resultName)) {
            castTo(target.name, c$case.resultName);
            widest$name = c$case.resultName;
          }
          var case$offer = getCaseOffer(c$case);
          if (case$offer.isBoxed()) {
            unBox(case$offer.name);
          }

          if (widest$name == null) {
            widest$name = castToWidest(target.name, case$offer.name);
          }

          switch (widest$name) {
            case GlobalData.long$name -> {
              mw().visitInsn(Opcodes.LCMP);
              mw().visitJumpInsn(IFEQ, labels[i]);
            }
            case GlobalData.double$name -> {
              mw().visitInsn(Opcodes.DCMPG);
              mw().visitJumpInsn(IFEQ, labels[i]);
            }
            case GlobalData.float$name -> {
              mw().visitInsn(Opcodes.FCMPG);
              mw().visitJumpInsn(IFEQ, labels[i]);
            }
            default -> {
              mw().visitJumpInsn(IF_ICMPEQ, labels[i]);
            }
          }
        }
      }
      GlobalData.fs6.popTable();
    }

    runF6Stmts(ctx.elseStmts);
    mw().visitJumpInsn(GOTO, end$jump);

    for (int i = 0; i < cases.size(); i++) {
      mw().visitLabel(labels[i]);
      var c$case = cases.get(i);
      if (c$case.caseLabel != null && autoCasts.contains(c$case.caseLabel.content)) {
        //This is to facilitate jumping to a block that auto-casts
        putCaseOffer(c$case);
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
