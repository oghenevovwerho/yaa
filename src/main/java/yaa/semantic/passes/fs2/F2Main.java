package yaa.semantic.passes.fs2;

import yaa.Yaa;
import yaa.ast.MainFunction;

import static yaa.pojos.GlobalData.*;

public class F2Main {
  public static void f2Main(MainFunction main) {
    Yaa.main$clz$name = topClzCodeName.get(fs2.path);
    var mainMtd = mainFunction;
    Yaa.main$fun$name = mainMtd.name;
    main.stmt.visit(fs2);
  }
}
