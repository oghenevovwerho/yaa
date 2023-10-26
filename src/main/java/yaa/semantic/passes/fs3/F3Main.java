package yaa.semantic.passes.fs3;

import yaa.ast.MainFunction;

import static yaa.pojos.GlobalData.*;

public class F3Main {
  public static void f3Main(MainFunction main) {
    main.stmt.visit(fs3);
  }
}
