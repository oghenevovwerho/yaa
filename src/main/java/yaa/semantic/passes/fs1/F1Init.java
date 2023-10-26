package yaa.semantic.passes.fs1;

import yaa.ast.Init;
import yaa.pojos.*;

import static yaa.pojos.GlobalData.fs1;

public class F1Init {
  public static YaaInit f1Init(Init init) {
    fs1.newTable();
    var newInit = new YaaInit();
    fs1.putSymbol(init.placeOfUse(), newInit);
    for (var parameter : init.parameters) {
      var name = parameter.name.content;
      var previouslyDefined = fs1.getAlreadyDefinedSymbolInPass1(name);
      if (previouslyDefined instanceof YaaField) {
        throw new YaaError(parameter.placeOfUse(),
            "'" + name + "' has been used by another symbol at "
                + previouslyDefined.placeOfUSe());
      }
      var field = new YaaField(name, true);
      field.startLine = parameter.start.line;
      field.column = parameter.start.column;
      field.itIsWhat = FieldIsWhat.mtd$field;
      GlobalData.fs1.putSymbol(name, field);
    }

    init.stmt.visit(GlobalData.fs1);
    fs1.storeTable(init);
    fs1.popTable();
    return newInit;
  }
}