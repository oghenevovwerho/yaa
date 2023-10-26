package yaa.semantic.handlers;

import yaa.ast.ThisField;
import yaa.semantic.passes.fs6.results.FieldResult;
import yaa.pojos.GlobalData;
import yaa.pojos.YaaError;
import yaa.pojos.YaaInfo;

import static yaa.pojos.GlobalData.fs;

public class ThisFieldOp {
  public static YaaInfo thisField(ThisField ctx) {
    var field_name = ctx.name.content;
    var clz = GlobalData.topClz.peek();
    if (clz == null) {
      throw new YaaError(
        ctx.name.placeOfUse(), "\"this\" must be used from within a type"
      );
    }
    var instantField = clz.getInstantField(field_name);
    if (instantField == null) {
      throw new YaaError(
        ctx.name.placeOfUse(),
        clz.toString(), "The type above does not define " +
        "any fields with the name \"" + field_name + "\""
      );
    }
    GlobalData.results.get(fs.path).put(ctx, new FieldResult(instantField));
    return instantField.data;
  }
}
