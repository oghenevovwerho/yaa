package yaa.semantic.passes.fs1;

import yaa.ast.MainFunction;
import yaa.pojos.GlobalData;
import yaa.pojos.MtdIsWhat;
import yaa.pojos.YaaFun;

import static yaa.pojos.GlobalData.*;

public class F1Main {
  public static void f1Main(MainFunction main) {
    var main$function = new YaaFun(main.name.content);
    main$function.mtdIsWhat = MtdIsWhat.mainMtd;
    main$function.startLine = main.start.line;
    main$function.column = main.start.column;
    GlobalData.mainFunction = main$function;

    main.stmt.visit(fs1);
  }
}
