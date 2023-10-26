package yaa.semantic.passes.fs5;

import yaa.ast.*;
import yaa.pojos.YaaClz;
import yaa.pojos.YaaError;
import yaa.semantic.passes.fs6.results.ThrowResult;

import static yaa.pojos.GlobalData.*;

public class F5Callable {
  public static void f5Callable() {
    if (!stmtGivesValue(fs5.fn$stmt.peek()) && !fs5.fn$type.peek().name.equals(void$name)) {
      throw new YaaError(fs5.fn$address.peek(),
          fs5.fn$name.peek() +
              " has to return a value of the type given below",
          fs5.fn$type.peek().toString(),
          "its control flow graph gave no value after traversal"
      );
    }
  }

  protected static boolean stmtGivesValue(Stmt stmt) {
    if (stmt instanceof Throw throw_stmt) {
      return handleThrowStmt(throw_stmt);
    }
    if (stmt instanceof Return ret) {
      return handleReturn(ret);
    }
    if (stmt instanceof Leave leave) {
      return handleLeave(leave);
    }
    if (stmt instanceof IfStmt ifStmt) {
      return F5If.f5IfStmt(ifStmt);
    }
    if (stmt instanceof Loop loop) {
      return F5Loop.handleLoopStmt(loop);
    }
    if (stmt instanceof TryCatch tryCatch) {
      return F5Try.tryCatchStmt(tryCatch);
    }
    if (stmt instanceof InnerBlock innerBlock) {
      return iBlock(innerBlock);
    }
    stmt.visit(fs5);
    return false;
  }

  public static boolean wasExit = false;

  public static boolean iBlock(InnerBlock iBlock) {
    if (iBlock.stmts.size() == 0) {
      return false;
    }
    fs5.pushTable(iBlock);
    var stmts = iBlock.stmts;
    for (int i = 0; i < stmts.size(); i++) {
      if (stmtGivesValue(stmts.get(i))) {
        if (i < stmts.size() - 1) {
          throw new YaaError(
              stmts.get(i + 1).placeOfUse(),
              "Unreachable statement after " +
                  "return from \"" + fs5.fn$name.peek() + "\""
          );
        }
        fs5.popTable();
        return true;
      }
    }
    fs5.popTable();
    return false;
  }

  private static boolean handleLeave(Leave leave) {
    var fnName = fs5.fn$name.peek();
    if (!fs5.fn$type.peek().name.equals(void$name)) {
      throw new YaaError(
          leave.placeOfUse(), fnName + " must return a " +
          "value of type " + fs5.fn$type.peek()
      );
    }
    return true;
  }

  private static boolean handleReturn(Return ret) {
    if (fs5.fn$type.peek().name.equals(void$name)) {
      throw new YaaError(
          ret.placeOfUse(), fs5.fn$name.peek() + " cannot return a value"
      );
    }
    var expected = fs5.fn$type.peek();
    var given = ret.e.visit(fs5);
    if (expected.accepts(given)) {
      return true;
    } else if (expected instanceof YaaClz clz) {
      if (given instanceof YaaClz gotten) {
        if (clz.isParentOf(gotten)) {
          return true;
        }
      }
    }
    throw new YaaError(
        ret.placeOfUse(),
        "Incompatibility between declared type " +
            "and returned type in \"" + fs5.fn$name.peek() + "\"",
        expected.toString(),
        "Expected the type given above, but " +
            "got the type given below", given.toString()
    );
  }

  private static boolean handleThrowStmt(Throw ctx) {
    var thrownObject = ctx.e.visit(fs5);
    if (thrownObject instanceof YaaClz clz) {
      var result = new ThrowResult(clz, clz.isChildOf(
          new YaaClz("java.lang.Throwable")
      ));
      results.get(fs5.path).put(ctx, result);
      return true;
    }
    throw new YaaError(
        ctx.placeOfUse(), thrownObject +
        " is not a Yaa object, only Yaa objects can be thrown"
    );
  }
}