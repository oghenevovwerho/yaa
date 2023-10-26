package yaa.semantic.passes.fs5;

import yaa.ast.Catch;
import yaa.ast.Final;
import yaa.ast.Tried;
import yaa.ast.TryCatch;
import yaa.pojos.YaaClz;
import yaa.pojos.YaaError;
import yaa.pojos.YaaField;
import yaa.pojos.jMold.JMold;
import yaa.semantic.handlers.VDefOp;
import yaa.semantic.passes.fs6.results.TypeResult;

import static yaa.pojos.GlobalData.*;
import static yaa.pojos.GlobalData.results;
import static yaa.semantic.passes.fs5.F5Callable.stmtGivesValue;

public class F5Try {
  protected static boolean tryCatchStmt(TryCatch tCatch) {
    var b1$true = true;
    var b2$true = true;
    fs5.pushTable(tCatch);

    if (!tryBlock(tCatch.tried)) {
      b1$true = false;
    }

    for (var caught : tCatch.caught) {
      if (!catchBlock(caught)) {
        b2$true = false;
      }
    }

    if (finalBlock(tCatch.finals)) {
      return true;
    } else if (tCatch.finals.stmts.size() > 0) {
      fs5.popTable();
      return false;
    }

    fs5.popTable();
    return b1$true && b2$true;
  }

  private static boolean catchBlock(Catch caught) {
    fs5.pushTable(caught);
    var stmts = caught.stmts;
    var field = (YaaField) fs5.getSymbol(caught.holder.content);
    if (caught.types.size() > 1) {
      for (var caught$type : caught.types) {
        var type = (YaaClz) caught$type.visit(fs5);
        results.get(fs.path).put(caught$type, new TypeResult(type));
      }
      field.data = new JMold().newClz("java.lang.Exception");
    } else {
      var caught$type = caught.types.get(0);
      var type = (YaaClz) caught$type.visit(fs5);
      results.get(fs.path).put(caught$type, new TypeResult(type));
      field.data = type;
    }
    for (int i = 0; i < stmts.size(); i++) {
      if (stmtGivesValue(stmts.get(i))) {
        if (i < stmts.size() - 1) {
          throw new YaaError(
              stmts.get(i + 1).placeOfUse(),
              "Unreachable statement after return " +
                  "from \"" + fs5.fn$name.peek() + "\""
          );
        }
        fs5.popTable();
        return true;
      }
    }
    fs5.popTable();
    return false;
  }

  private static boolean finalBlock(Final finals) {
    fs5.pushTable(finals);
    var stmts = finals.stmts;
    if (stmts.size() == 0) {
      return false;
    }
    for (int i = 0; i < stmts.size(); i++) {
      if (stmtGivesValue(stmts.get(i))) {
        if (i < stmts.size() - 1) {
          throw new YaaError(
              stmts.get(i + 1).placeOfUse(),
              "Unreachable statement after return " +
                  "from \"" + fs5.fn$name.peek() + "\""
          );
        }
        fs5.popTable();
        return true;
      }
    }
    fs5.popTable();
    return false;
  }

  private static boolean tryBlock(Tried tried) {
    fs5.pushTable(tried);
    for (var def : tried.resources) {
      VDefOp.defOp(def);
      var field = (YaaField) fs5.getSymbol(def.name.content);
      if (field.data instanceof YaaClz clz) {
        if (clz.hasTrait(new YaaClz("java.lang.AutoCloseable")) == null) {
          throw new YaaError(
              def.placeOfUse(), "The try-with-resource " +
              "value must implement java.lang.AutoCloseable",
              clz.name + " does not implement it"
          );
        }
      } else {
        throw new YaaError(
            def.placeOfUse(), "The value of a try with resource must be a type"
        );
      }
    }
    var stmts = tried.stmts;
    for (int i = 0; i < stmts.size(); i++) {
      if (stmtGivesValue(stmts.get(i))) {
        if (i < stmts.size() - 1) {
          throw new YaaError(
              stmts.get(i + 1).placeOfUse(),
              "Unreachable statement after return " +
                  "from \"" + fs5.fn$name.peek() + "\""
          );
        }
        fs5.popTable();
        return true;
      }
    }
    fs5.popTable();
    return false;
  }
}
