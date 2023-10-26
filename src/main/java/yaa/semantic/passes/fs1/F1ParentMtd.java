package yaa.semantic.passes.fs1;

import yaa.ast.Decimal;
import yaa.ast.NewFun;
import yaa.parser.TokenUtils;
import yaa.pojos.GlobalData;
import yaa.pojos.YaaError;
import yaa.pojos.YaaFun;
import yaa.pojos.YaaMeta;

import static yaa.pojos.GlobalData.fs1;
import static yaa.pojos.GlobalData.int$name;

public class F1ParentMtd {
  public static void f1ParentMtd(NewFun method) {
    var newMethod = (YaaFun) F1NFun.f1NewFun(method);
    for (var metaCall : method.metaCalls) {
      var meta = fs1.getSymbol(metaCall.name.content);
      if (meta instanceof YaaMeta && meta.name.equals(GlobalData.configMetaClzName)) {
        for (var arg : metaCall.arguments.entrySet()) {
          var argument = arg.getKey();
          if (argument.content.equals("privacy")) {
            if (arg.getValue() instanceof Decimal decimal) {
              int value = TokenUtils.decimalValue(decimal.token);
              if (value == 0) {
                newMethod.privacy = 0;
                method.privacy = 0;
              } else if (value == 1) {
                newMethod.privacy = 1;
                method.privacy = 1;
              } else {
                throw new YaaError(
                    arg.getValue().placeOfUse(),
                    "The value of the privacy parameter for a parent method must be 0, or 1"
                );
              }
            } else {
              throw new YaaError(
                  arg.getValue().placeOfUse(),
                  "The value of the privacy parameter must be a literal of " + int$name
              );
            }
          } else {
            throw new YaaError(
                argument.placeOfUse(),
                "A parent scope method definition cannot " +
                    "contain the option \"" + argument.content + "\""
            );
          }
        }
      }
    }
  }
}
