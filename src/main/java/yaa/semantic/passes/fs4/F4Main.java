package yaa.semantic.passes.fs4;

import yaa.ast.MainFunction;

import static yaa.pojos.GlobalData.*;

public class F4Main {
  public static void f4Main(MainFunction main) {
    F4.runF4Stmt(main.stmt);
  }
}
