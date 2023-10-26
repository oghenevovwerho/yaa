package yaa.semantic.passes.fs1;

import yaa.ast.Anonymous;
import yaa.pojos.FieldIsWhat;
import yaa.pojos.MtdIsWhat;
import yaa.pojos.YaaField;
import yaa.pojos.YaaFun;
import yaa.pojos.*;

import java.util.ArrayList;

import static yaa.pojos.GlobalData.fs1;

public class F1NoName {
  public static void anonymous(Anonymous anonymous) {
    var no_name_fun = new YaaFun(NameUtils.generateName4lambda());
    no_name_fun.isAnonymous = true;
    no_name_fun.startLine = anonymous.start.line;
    no_name_fun.column = anonymous.start.column;
    no_name_fun.mtdIsWhat = MtdIsWhat.staticMtd;
    no_name_fun.parameters = new ArrayList<>(anonymous.parameters.size());
    fs1.putSymbol(anonymous.placeOfUse(), no_name_fun);
    fs1.newTable();

    no_name_fun.parameterNames = new ArrayList<>(3);
    for (var parameter : anonymous.parameters) {
      var name = parameter.content;
      var previouslyDefined = fs1.getAlreadyDefinedSymbolInPass1(name);
      if (previouslyDefined instanceof YaaField){
        throw new YaaError(parameter.placeOfUse(),
            "'" + name +"' has been used by another symbol at " + previouslyDefined.placeOfUSe());
      }
      var field = new YaaField(name, true);
      field.startLine = parameter.line;
      field.column = parameter.column;
      field.itIsWhat = FieldIsWhat.mtd$field;
      fs1.putSymbol(name, field);
      no_name_fun.parameterNames.add(name);
    }

    anonymous.stmt.visit(fs1);

    fs1.storeTable(anonymous);
    fs1.popTable();
  }
}