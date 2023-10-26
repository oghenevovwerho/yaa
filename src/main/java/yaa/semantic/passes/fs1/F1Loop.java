package yaa.semantic.passes.fs1;

import yaa.ast.Loop;
import yaa.pojos.FieldIsWhat;
import yaa.pojos.YaaError;
import yaa.pojos.YaaField;

import static yaa.pojos.GlobalData.fs1;

public class F1Loop {
  public static void f1LoopStmt(Loop loop) {
    fs1.newTable();
    if (loop.value$name != null) {
      var name = loop.value$name.content;
      var previouslyDefined = fs1.getAlreadyDefinedSymbolInPass1(name);
      if (previouslyDefined instanceof YaaField){
        throw new YaaError(loop.value$name.placeOfUse(),
            "'" + name +"' has been used by another symbol at " + previouslyDefined.placeOfUSe());
      }
      var field = new YaaField(name, true);
      field.is4loop = true;
      field.itIsWhat = FieldIsWhat.mtd$field;
      field.startLine = loop.value$name.line;
      field.column = loop.value$name.column;
      fs1.putSymbol(name, field);
    }
    loop.stmt.visit(fs1);
    fs1.storeTable(loop);
    fs1.popTable();
  }
}
