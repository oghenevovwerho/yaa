package yaa.semantic.passes.fs5;

import yaa.ast.AstString;
import yaa.ast.NewMeta;
import yaa.ast.YaaMetaCall;
import yaa.pojos.*;
import yaa.semantic.handlers.VDefOp;

import java.util.HashMap;

import static yaa.pojos.GlobalData.array$name;
import static yaa.pojos.GlobalData.fs5;

public class F5NewMeta {
  public static void newMeta(NewMeta newMeta) {
    fs5.pushTable(newMeta);

    if (newMeta.metaCalls.size() > 0) {
      var alreadyCalledMetas = new HashMap<String, YaaMetaCall>(1);
      for (var annotation : newMeta.metaCalls) {
        var meta = (YaaMeta) annotation.visit(fs5);
        var defined_meta = alreadyCalledMetas.get(meta.name);
        if (defined_meta != null && !meta.isRepeatable) {
          throw new YaaError(annotation.placeOfUse(), meta.name
            + " has already been called at " + defined_meta.placeOfUse(),
            meta.name + " is not repeatable"
          );
        }
        alreadyCalledMetas.put(meta.name, annotation);
      }
    }

    for (var def : newMeta.vDefinitions) {
      if (def.value instanceof AstString string && string.itIsInterpolated) {
        throw new YaaError(
          def.value.placeOfUse(),
          "Interpolated strings are not allowed as annotation values"
        );
      }
      VDefOp.defOp(def);
      var field = (YaaField) fs5.getSymbol(def.name.content);
      if (field.data instanceof YaaClz clz) {
        if (!isPermissibleMetaValue(clz)) {
          throw new YaaError(
            def.value.placeOfUse(),
            "The value of an annotations field must be one of the following",
            "A String", "A primitive", "An enum", "An array of the above"
          );
        }
      } else {
        throw new YaaError(
          def.value.placeOfUse(),
          "The value of an annotations field must be one of the following",
          "A String", "A primitive", "An enum", "An array of the above"
        );
      }
    }

    for (var vDec : newMeta.vDeclarations) {
      vDec.visit(fs5);
      var field = (YaaField) fs5.getSymbol(vDec.name.content);
      if (field.data instanceof YaaClz clz) {
        if (!isPermissibleMetaValue(clz)) {
          throw new YaaError(
            vDec.type.placeOfUse(),
            "The value of an annotations field must be one of the following",
            "A String", "A primitive", "An enum", "An array of the above"
          );
        }
      } else {
        throw new YaaError(
          vDec.type.placeOfUse(),
          "The value of an annotations field must be one of the following",
          "A String", "A primitive", "An enum", "An array of the above"
        );
      }
    }
    fs5.popTable();
  }

  public static boolean isPermissibleMetaValue(YaaClz clz) {
    if (clz.isPrimitive() || clz.name.equals("java.lang.String")) {
      return true;
    }
    if (clz.category == TypeCategory.enum_c) {
      return true;
    }
    if (clz.name.equals(array$name)) {
      var array_input = clz.inputted.get(0);
      if (array_input.isPrimitive()) {
        return true;
      }
      if (array_input.name.equals("java.lang.String")) {
        return true;
      }
      return array_input.category == TypeCategory.enum_c;
    }
    return false;
  }
}
