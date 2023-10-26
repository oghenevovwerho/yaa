package yaa.semantic.handlers;

import yaa.ast.MNEqual;
import yaa.semantic.passes.fs6.results.CallResult;
import yaa.pojos.GlobalData;
import yaa.pojos.YaaFun;
import yaa.pojos.YaaInfo;

import static yaa.pojos.GlobalData.fs;
import static yaa.pojos.GlobalData.results;

public class MNEqualOp {
  public static YaaInfo mnEqual(MNEqual $mnequal) {
    var equal$mtd = new YaaFun("equals", $mnequal.e1.visit(fs).name);
    equal$mtd.parameters.add($mnequal.e2.visit(GlobalData.fs));
    equal$mtd.type = GlobalData.boole$clz;
    var result = new CallResult(equal$mtd);
    results.get(fs.path).put($mnequal, result);
    return equal$mtd.type;
  }
}
