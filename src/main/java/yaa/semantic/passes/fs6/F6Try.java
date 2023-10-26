package yaa.semantic.passes.fs6;

import org.objectweb.asm.Label;
import yaa.ast.TryCatch;
import yaa.pojos.VariableData;
import yaa.pojos.YaaField;
import yaa.pojos.YaaInfo;
import yaa.semantic.passes.fs6.results.TypeResult;

import java.util.ArrayList;
import java.util.List;

import static org.objectweb.asm.Opcodes.*;
import static yaa.pojos.GlobalData.fs6;
import static yaa.pojos.GlobalData.nothing;
import static yaa.semantic.passes.fs6.F6.mw;
import static yaa.semantic.passes.fs6.F6.variableMeta;
import static yaa.semantic.passes.fs6.F6Utils.runF6Stmts;

public class F6Try {
  public static YaaInfo tryCatch(TryCatch ctx) {
    if (ctx.finals.stmts.size() > 0) {
      tryFinally(ctx);
      return nothing;
    }
    fs6.pushTable(ctx);
    var catchDestination = new Label();
    var catchStart = new Label();
    var catchEnd = new Label();

    fs6.pushTable(ctx.tried);
    var fields = new ArrayList<YaaField>(ctx.tried.resources.size());
    for (var def : ctx.tried.resources) {
      F6VDef.def(def);
      var field = (YaaField) fs6.getSymbol(def.name.content);
      fields.add(field);
    }
    mw().visitLabel(catchStart);
    runF6Stmts(ctx.tried.stmts);
    freeAllResources(fields);
    mw().visitLabel(catchEnd);
    fs6.popTable();

    for (var caught : ctx.caught) {
      fs6.pushTable(caught);

      mw().visitJumpInsn(GOTO, catchDestination);
      var catchHandler = new Label();
      mw().visitLabel(catchHandler);
      for (var type : caught.types) {
        var clz$result = (TypeResult) fs6.results.get(type);
        mw().visitTryCatchBlock(
            catchStart,
            catchEnd,
            catchHandler,
            clz$result.type.codeName
        );
      }

      var eName = caught.holder.content;
      var index = fs6.variables.peek().putVar(eName);
      mw().visitVarInsn(ASTORE, index);

      F6.mw().visitLineNumber(caught.start.line, catchStart);

      F6.variableMeta.peek().add(
          new VariableData(
              eName,
              catchStart,
              "Ljava/lang/Exception;",
              null,
              index,
              new ArrayList<>(0),
              new ArrayList<>(0)
          )
      );

      runF6Stmts(caught.stmts);
      freeCatchResources(fields);
      fs6.popTable();
    }
    mw().visitLabel(catchDestination);
    fs6.popTable();
    return nothing;
  }

  private static void freeCatchResources(ArrayList<YaaField> fields) {
    if (fields.size() > 0) {
      var catchDestination = new Label();
      var catchStart = new Label();
      var catchEnd = new Label();

      mw().visitLabel(catchStart);
      for (var field : fields) {
        fs6.variables.peek().load(field.data.name, field.field$name);
        mw().visitMethodInsn(
            INVOKEINTERFACE,
            "java/lang/AutoCloseable",
            "close", "()V", true
        );
      }
      mw().visitLabel(catchEnd);

      mw().visitJumpInsn(GOTO, catchDestination);
      var catchHandler = new Label();
      mw().visitLabel(catchHandler);
      mw().visitTryCatchBlock(
          catchStart,
          catchEnd,
          catchHandler,
          "java/lang/Throwable"
      );

      var eName = F6Utils.generateRandomName("thrown");
      var index = fs6.variables.peek().putVar(eName);
      mw().visitVarInsn(ASTORE, index);
      mw().visitLabel(catchDestination);
    }
  }

  private static void freeAllResources(ArrayList<YaaField> fields) {
    for (var field : fields) {
      fs6.variables.peek().load(field.data.name, field.field$name);
      mw().visitMethodInsn(
          INVOKEINTERFACE,
          "java/lang/AutoCloseable",
          "close", "()V", true
      );
    }
  }

  private static void tryFinally(TryCatch ctx) {
    fs6.pushTable(ctx);
    var caught_size = ctx.caught.size();
    var caught_labels = new Label[caught_size * 2];
    var startBlock = new Label();
    var closeBlock = new Label();
    var finalBlock = new Label();
    var leaveBlock = new Label();

    for (int i = 0; i < caught_size; i++) {
      caught_labels[i] = new Label(); //the open label is here
      caught_labels[i + caught_size] = new Label();//the close
    }

    for (int i = 0; i < caught_size; i++) {
      var caught = ctx.caught.get(i);
      for (var type : caught.types) {
        var clz$result = (TypeResult) fs6.results.get(type);
        mw().visitTryCatchBlock(
            startBlock,
            closeBlock,
            caught_labels[i],
            clz$result.type.codeName
        );
      }
    }

    mw().visitTryCatchBlock(startBlock, closeBlock, finalBlock, null);

    for (int i = 0; i < caught_size; i++) {
      mw().visitTryCatchBlock(
          caught_labels[i],
          caught_labels[i + caught_size],
          finalBlock, null
      );
    }

    fs6.pushTable(ctx.tried);
    var fields = new ArrayList<YaaField>(ctx.tried.resources.size());
    for (var def : ctx.tried.resources) {
      F6VDef.def(def);
      var field = (YaaField) fs6.getSymbol(def.name.content);
      fields.add(field);
    }
    mw().visitLabel(startBlock);
    runF6Stmts(ctx.tried.stmts);
    freeAllResources(fields);
    mw().visitLabel(closeBlock);
    fs6.popTable();

    fs6.pushTable(ctx.finals);
    runF6Stmts(ctx.finals.stmts);
    mw().visitJumpInsn(GOTO, leaveBlock);
    fs6.popTable();

    for (int i = 0; i < caught_size; i++) {
      var caught = ctx.caught.get(i);
      fs6.pushTable(caught);
      mw().visitLabel(caught_labels[i]);
      var holder = caught.holder;
      var index = fs6.variables.peek().putVar(holder.content);
      mw().visitVarInsn(ASTORE, index);

      var label = new Label();
      mw().visitLabel(label);
      mw().visitLineNumber(holder.line, label);
      var clz$result = (TypeResult) fs6.results.get(caught.types.get(0));
      variableMeta.peek().add(new VariableData(
          holder.content, label, clz$result.type.descriptor(),
          clz$result.type.clzUseSignature(), index, List.of(), List.of()
      ));

      runF6Stmts(caught.stmts);
      freeCatchResources(fields);
      mw().visitLabel(caught_labels[i + caught_size]);
      fs6.popTable();
      fs6.pushTable(ctx.finals);
      runF6Stmts(ctx.finals.stmts);
      mw().visitJumpInsn(GOTO, leaveBlock);
      fs6.popTable();
    }

    mw().visitLabel(finalBlock);
    mw().visitVarInsn(ASTORE, fs6.variables.peek().index + 1);
    fs6.pushTable(ctx.finals);
    runF6Stmts(ctx.finals.stmts);
    fs6.popTable();

    mw().visitVarInsn(ALOAD, fs6.variables.peek().index + 1);
    mw().visitInsn(ATHROW);
    mw().visitLabel(leaveBlock);
    fs6.popTable();
  }
}