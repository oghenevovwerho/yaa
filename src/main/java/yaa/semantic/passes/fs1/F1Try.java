package yaa.semantic.passes.fs1;

import yaa.ast.TryCatch;
import yaa.ast.VDefinition;
import yaa.pojos.FieldIsWhat;
import yaa.pojos.YaaError;
import yaa.pojos.YaaField;

import static yaa.pojos.GlobalData.*;

public class F1Try {
  public static void f1Try(TryCatch tCatch) {
    fs1.newTable();
    fs1.newTable();
    for (var def : tCatch.tried.resources) {
      finalDef(def);
    }
    for (var stmt : tCatch.tried.stmts) {
      stmt.visit(fs1);
    }
    fs1.storeTable(tCatch.tried);
    fs1.popTable();

    for (var caught : tCatch.caught) {
      fs1.newTable();
      var name = caught.holder.content;
      var previouslyDefined = fs1.getAlreadyDefinedSymbolInPass1(name);
      if (previouslyDefined instanceof YaaField){
        throw new YaaError(caught.holder.placeOfUse(),
        "'" + name +"' has been used by another symbol at " + previouslyDefined.placeOfUSe());
      }
      var field = new YaaField(name);
      field.itIsWhat = FieldIsWhat.mtd$field;
      field.startLine = caught.holder.line;
      field.column = caught.holder.column;
      fs.putSymbol(name, field);
      for (var stmt : caught.stmts) {
        stmt.visit(fs1);
      }
      fs1.storeTable(caught);
      fs1.popTable();
    }

    fs1.newTable();
    for (var stmt : tCatch.finals.stmts) {
      stmt.visit(fs1);
    }
    fs1.storeTable(tCatch.finals);
    fs1.popTable();

    fs1.storeTable(tCatch);
    fs1.popTable();
  }

  public static void finalDef(VDefinition def) {
    var name = def.name.content;
    var previouslyDefined = fs1.getAlreadyDefinedSymbolInPass1(name);
    if (previouslyDefined instanceof YaaField){
      throw new YaaError(def.placeOfUse(),
          "'" + name +"' has been used by another symbol at " + previouslyDefined.placeOfUSe());
    }
    var field = new YaaField(name, true);
    field.itIsWhat = FieldIsWhat.mtd$field;
    field.startLine = def.start.line;
    field.column = def.start.column;
    fs.putSymbol(name, field);
  }
}
