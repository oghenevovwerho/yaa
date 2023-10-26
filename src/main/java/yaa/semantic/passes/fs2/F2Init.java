package yaa.semantic.passes.fs2;

import yaa.ast.Init;
import yaa.pojos.*;

import static yaa.pojos.GlobalData.fs2;

public class F2Init {
  public static void f2Init(Init init) {
    fs2.pushTable(init);
    var newInit = (YaaInit) fs2.getSymbol(init.placeOfUse());
    for (var param : init.parameters) {
      var paramType = param.type.visit(fs2);
      newInit.parameters.add(paramType);
      if (paramType instanceof YaaClz tp) {
        if (tp.cbIndex > -1 || tp.mbIndex > -1) {
          if (tp.variance == YaaClzVariance.covariant) {
            throw new YaaError(
                param.type.placeOfUse(),
                "The type used by the parameter " +
                    "type resolves to a covariant bound",
                "Only method return types are allowed " +
                    "to use covariant bound types"
            );
          }
        }
      }
    }

    init.stmt.visit(fs2);
    fs2.popTable();
  }
}