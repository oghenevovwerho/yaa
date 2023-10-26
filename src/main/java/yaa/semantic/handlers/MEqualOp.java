package yaa.semantic.handlers;

import yaa.ast.MEqual;
import yaa.pojos.GlobalData;
import yaa.semantic.passes.fs6.results.CallResult;
import yaa.pojos.YaaFun;
import yaa.pojos.YaaInfo;

import static yaa.pojos.GlobalData.fs;
import static yaa.pojos.GlobalData.results;

public class MEqualOp {
  public static YaaInfo mEqual(MEqual $mequal) {
    var equal$mtd = new YaaFun("equals", $mequal.e1.visit(GlobalData.fs).name);
    equal$mtd.parameters.add($mequal.e2.visit(GlobalData.fs));
    equal$mtd.type = GlobalData.boole$clz;
    var result = new CallResult(equal$mtd);
    results.get(fs.path).put($mequal, result);
    return equal$mtd.type;
  }
}
