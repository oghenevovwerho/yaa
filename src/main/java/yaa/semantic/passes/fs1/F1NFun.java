package yaa.semantic.passes.fs1;

import yaa.ast.NewFun;
import yaa.pojos.*;

import javax.lang.model.SourceVersion;
import java.util.ArrayList;

import static yaa.pojos.GlobalData.*;
import static yaa.pojos.NameUtils.dottedStoreName;

public class F1NFun {
  public static YaaInfo f1NewFun(NewFun newFun) {
    var simpleMtdName = newFun.name.content;
    if (SourceVersion.isKeyword(simpleMtdName)) {
      throw new YaaError(
          newFun.name.placeOfUse(), "The java keyword \""
          + simpleMtdName + "\" can't be used as a function's name"
      );
    }
    var fs1Mtd = new YaaFun();
    fs1Mtd.path = fs1.path;
    fs1Mtd.parameters = new ArrayList<>(newFun.parameters.size());
    fs1Mtd.name = mtdCodeName(simpleMtdName);
    fs1Mtd.startLine = newFun.start.line;
    fs1Mtd.column = newFun.start.column;
    fs1.putSymbol(newFun.placeOfUse(), fs1Mtd);
    fs1Mtd.mtdIsWhat = newFun.itIsWhat;
    fs1.newTable();

    for (var parameter : newFun.parameters) {
      var name = parameter.name.content;
      //isItDefined(name, parameter.address());
      var previouslyDefined = fs1.getAlreadyDefinedSymbolInPass1(name);
      if (previouslyDefined instanceof YaaField){
        throw new YaaError(parameter.placeOfUse(),
            "'" + name +"' has been used by another symbol at "
                + previouslyDefined.placeOfUSe());
      }
      var field = new YaaField(name, true);
      field.startLine = parameter.start.line;
      field.column = parameter.start.column;
      field.itIsWhat = FieldIsWhat.mtd$field;
      fs1.putSymbol(name, field);
    }

    newFun.stmt.visit(fs1);

    var defined$pack = fs1.getSymbol(newFun.placeOfUse());
    if (defined$pack instanceof MtdPack pack) {
      pack.methods.add(fs1Mtd);
    } else {
      var newMtdPack = new MtdPack(new ArrayList<>(), simpleMtdName);
      newMtdPack.methods.add(fs1Mtd);
      fs1.table.parent.putSymbol(simpleMtdName, newMtdPack);
    }

    if (newFun.itIsWhat == MtdIsWhat.topMtd) {
      var defined$pack_g = getGlobalFunction(dottedStoreName(simpleMtdName));
      if (defined$pack_g != null) {
        defined$pack_g.methods.add(fs1Mtd);
      } else {
        if (fs1Mtd.privacy == 0) {
          var pubNewMtdPack = new MtdPack(new ArrayList<>(), simpleMtdName);
          pubNewMtdPack.methods.add(fs1Mtd);
          defineFunGlobally(dottedStoreName(simpleMtdName), pubNewMtdPack);
        }
      }
    }

    fs1.storeTable(newFun);
    fs1.popTable();
    return fs1Mtd;
  }

  private static String mtdCodeName(String simpleName) {
    StringBuilder name = new StringBuilder(simpleName);
    var funNames = usedFnNames.get(fs1.path);
    var names = funNames.get(name.toString());
    while (names != null) {
      name.append(StateUtils.fun$count++);
      names = funNames.get(name.toString());
    }
    return name.toString();
  }
}