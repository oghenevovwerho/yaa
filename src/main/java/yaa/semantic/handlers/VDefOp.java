package yaa.semantic.handlers;

import yaa.ast.VDefinition;
import yaa.pojos.YaaClz;
import yaa.pojos.YaaError;
import yaa.pojos.YaaField;

import static yaa.pojos.GlobalData.*;

public class VDefOp {
  public static YaaField defOp(VDefinition vd) {
    var fieldName = vd.name.content;
    var definedField = (YaaField) fs.getSymbol(fieldName);
    if (vd.type != null) {
      var type$value = vd.type.visit(fs);
      var exp$value = vd.value.visit(fs);
      if (exp$value.name.equals(null$name)) {
        throw new YaaError(
            vd.value.placeOfUse(),
            "The assigned expression returned " + null$name,
            null$name + " cannot be assigned to a variable",
            "Change the operation to a declaration instead"
        );
      } else if (exp$value.name.equals(void$name)) {
        throw new YaaError(
            vd.value.placeOfUse(),
            "The assigned expression returned " + void$name,
            void$name + " cannot be assigned to a variable"
        );
      }
      if (type$value.accepts(exp$value)) {
        definedField.data = type$value;
        return definedField;
      }
      if (type$value instanceof YaaClz l && exp$value instanceof YaaClz r) {
        if (l.isParentOf(r)) {
          definedField.data = type$value;
          return definedField;
        }
      }
      throw new YaaError(
          vd.placeOfUse(), type$value + " = " + exp$value, vd.toString(),
          "The type of the variable and its value are not compatible"
      );
    }
    var exp$value = vd.value.visit(fs);
    if (exp$value.name.equals(void$name)) {
      throw new YaaError(
          vd.value.placeOfUse(),
          "The assigned expression returned " + void$name,
          void$name + " cannot be assigned to a variable"
      );
    }
    definedField.data = exp$value;
    return definedField;
  }
}