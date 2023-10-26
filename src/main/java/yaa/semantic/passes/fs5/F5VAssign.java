package yaa.semantic.passes.fs5;

import yaa.ast.Assign;
import yaa.pojos.*;
import yaa.semantic.passes.fs6.results.FieldResult;

import static yaa.pojos.BlockKind.init;
import static yaa.pojos.FieldIsWhat.clz$field;
import static yaa.pojos.GlobalData.fs5;

public class F5VAssign {
  public static void f5VAssign(Assign nameAssign, String name) {
    YaaInfo symbol = fs5.getSymbol(name);
    if (symbol == null) {
      throw new YaaError(
        nameAssign.placeOfUse(), nameAssign.toString(),
        "The referenced name \"" + name + "\" " +
          "is not defined in scope"
      );
    }
    if (symbol instanceof YaaField fd) {
      if (fd.itIsFinal) {
        if (fs5.block.peek() != init || fd.itIsWhat != clz$field) {
          throw new YaaError(
            nameAssign.placeOfUse(), nameAssign.toString(),
            "The referenced variable \"" + name + "\" is defined as final",
            "Final variables cannot be reassigned once defined"
          );
        }
      }
      var newValue = nameAssign.e2.visit(fs5);
      if (fd.data.accepts(newValue)) {
        fd.data = newValue;
        var result = new FieldResult(fd);
        GlobalData.results.get(fs5.path).put(nameAssign, result);
        return;
      }
      if (fd.data instanceof YaaClz clz && clz.isParentOf(newValue)) {
        fd.data = newValue;
        var result = new FieldResult(fd);
        GlobalData.results.get(fs5.path).put(nameAssign, result);
        return;
      }
      throw new YaaError(
        nameAssign.placeOfUse(), fd.data.toString(),
        "You can not assign the type below to the type above",
        newValue.toString()
      );
    }
  }
}
