package yaa.semantic.handlers;

import yaa.ast.EGet;
import yaa.pojos.*;
import yaa.semantic.passes.fs6.results.FieldResult;

public class EGetOp {
  public static YaaInfo eGet(EGet eGet) {
    var rName = eGet.name.content;
    var info = eGet.e.visit(GlobalData.fs);
    if (info instanceof YaaClz clz) {
      if (clz.category == TypeCategory.trait_c) {
        var field = clz.getStaticField(rName);
        if (field == null) {
          throw new YaaError(
            eGet.name.placeOfUse(),
            clz.toString(), "The type above does not define " +
            "any field with the name \"" + rName + "\""
          );
        }
        var result = new FieldResult(field);
        GlobalData.results.get(GlobalData.fs.path).put(eGet, result);
        return field.data;
      }
      if (clz.category != TypeCategory.enum_c) {
        var field = clz.getInstantField(rName);
        if (field == null) {
          throw new YaaError(
            eGet.name.placeOfUse(),
            clz.toString(), "The type above does not define " +
            "any field with the name \"" + rName + "\""
          );
        }
        if (field.data == null) {
          var touched_clz = GlobalData.getTouchedClass(clz.name);
          if (touched_clz.inputted.size() > 0) {
            touched_clz = touched_clz.changeCBounds(clz.inputted);
          }
          var touched_field = touched_clz.getInstantField(rName);
          var result = new FieldResult(touched_field);
          GlobalData.results.get(GlobalData.fs.path).put(eGet, result);
          return touched_field.data;
        }
        var result = new FieldResult(field);
        GlobalData.results.get(GlobalData.fs.path).put(eGet, result);
        return field.data;
      }
    }
    throw new YaaError(
      eGet.e.placeOfUse(), info.toString(),
      "The symbol above is not dot accessible"
    );
  }
}