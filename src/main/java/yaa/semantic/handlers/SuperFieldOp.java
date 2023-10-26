package yaa.semantic.handlers;

import yaa.ast.SuperField;
import yaa.semantic.passes.fs6.results.FieldResult;
import yaa.pojos.GlobalData;
import yaa.pojos.YaaError;
import yaa.pojos.YaaInfo;

import static yaa.pojos.GlobalData.fs;

public class SuperFieldOp {
  public static YaaInfo superField(SuperField superField) {
    var field_name = superField.name.content;
    var clz = GlobalData.topClz.peek().parent;
    var instantField = clz.getInstantField(field_name);
    if (instantField == null) {
      throw new YaaError(
        superField.name.placeOfUse(),
        clz.toString(), "The type above does not define " +
        "any fields with the name \"" + field_name + "\""
      );
    }
    var result = new FieldResult(instantField);
    GlobalData.results.get(fs.path).put(superField, result);
    return instantField.data;
  }
}
