package yaa.semantic.handlers;

import yaa.ast.NEqual;
import yaa.pojos.GlobalData;
import yaa.pojos.YaaClz;
import yaa.semantic.passes.fs6.results.CallResult;
import yaa.pojos.YaaFun;
import yaa.pojos.YaaInfo;

import static yaa.pojos.GlobalData.array$name;
import static yaa.pojos.GlobalData.fs;
import static yaa.pojos.GlobalData.results;

public class NEqualOp {
  public static YaaInfo nEqual(NEqual $not$equal) {
    var left = $not$equal.e1.visit(fs);
    var right = $not$equal.e2.visit(fs);

    boolean arrayTypesAreDivergent = false;

    if (left.name.equals(array$name) && right.name.equals(array$name)) {
      var l0 = ((YaaClz) left).inputted.get(0);
      var r0 = ((YaaClz) right).inputted.get(0);
      if (l0.isPrimitive()) {
        if (r0.isPrimitive()) {
          if (!l0.name.equals(r0.name)) {
            arrayTypesAreDivergent = true;
          }
        } else {
          arrayTypesAreDivergent = true;
        }
      } else {
        if (r0.isPrimitive()) {
          arrayTypesAreDivergent = true;
        }
      }
    }

    var equal$mtd = new YaaFun("equals", left.name);
    equal$mtd.parameters.add(right);
    equal$mtd.type = GlobalData.boole$clz;
    var result = new CallResult(equal$mtd);
    result.arrayTypesAreDivergent = arrayTypesAreDivergent;
    results.get(fs.path).put($not$equal, result);
    return equal$mtd.type;
  }
}
