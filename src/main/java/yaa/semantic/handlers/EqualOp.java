package yaa.semantic.handlers;

import yaa.ast.EEqual;
import yaa.pojos.*;
import yaa.semantic.passes.fs6.results.CallResult;

import static yaa.pojos.GlobalData.*;
import static yaa.pojos.GlobalData.fs;
import static yaa.pojos.GlobalData.results;

public class EqualOp {
  public static YaaInfo eEqual(EEqual $equal) {
    var left = $equal.e1.visit(fs);
    var right = $equal.e2.visit(fs);

    //this is especially useful for arrays
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
    equal$mtd.type = boole$clz;
    var result = new CallResult(equal$mtd);
    result.arrayTypesAreDivergent = arrayTypesAreDivergent;
    results.get(fs.path).put($equal, result);
    return equal$mtd.type;
  }
}
