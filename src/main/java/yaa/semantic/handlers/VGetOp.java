package yaa.semantic.handlers;

import yaa.ast.VGet;
import yaa.pojos.*;
import yaa.semantic.passes.fs6.results.FieldResult;
import yaa.pojos.jMold.JMold;

import static yaa.pojos.GlobalData.fs;

public class VGetOp {
  public static YaaInfo vGet(VGet vGet) {
    var field_name = vGet.n2.content;
    var lName = vGet.n1.content;
    var info = fs.getSymbol(lName);
    if (info == null) {
      throw new YaaError(
        vGet.placeOfUse(),
        "There is no symbol in scope with the name \"" + lName + "\""
      );
    }
    if (info instanceof YaaClz clz) {
      if (field_name.equals("class")) {
        var class_field = new YaaField("class");
        class_field.data = clz;
        var result = new FieldResult(class_field);
        GlobalData.results.get(fs.path).put(vGet, result);
        return new JMold().newClz("java.lang.Class");
      }
      var field = clz.getStaticField(field_name);
      if (field == null) {
        if (clz.category == TypeCategory.enum_c) {
          field = clz.instance$fields.get(field_name);
        }
        if (field == null) {
          throw new YaaError(
            vGet.n2.placeOfUse(), clz.toString(), "The static field \"" +
            field_name + "\" is not defined in the type above"
          );
        }
      }
      var result = new FieldResult(field);
      GlobalData.results.get(fs.path).put(vGet, result);
      return field.data;
    } else if (info instanceof YaaField field) {
      if (field.data instanceof YaaClz clz) {
        var instantField = clz.getInstantField(field_name);
        if (instantField == null) {
          throw new YaaError(
            vGet.placeOfUse(),
            clz.toString(), "The type above does not define " +
            "any fields with the name \"" + field_name + "\""
          );
        }
        var result = new FieldResult(instantField);
        GlobalData.results.get(fs.path).put(vGet, result);
        if (instantField.typeParam != null) {
          var data = instantField.data.cloneInfo();
          data.typeParam = instantField.typeParam;
          return data;
        }
        return instantField.data;
      }
    }
    throw new YaaError(
      vGet.placeOfUse(), info.toString(),
      "The symbol above is not dot accessible"
    );
  }
}
