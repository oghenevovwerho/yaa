package yaa.semantic.passes.fs1;

import yaa.ast.NewMeta;
import yaa.ast.VDeclaration;
import yaa.ast.VDefinition;
import yaa.pojos.GlobalData;
import yaa.pojos.YaaError;
import yaa.pojos.YaaField;
import yaa.pojos.YaaMeta;

import javax.lang.model.SourceVersion;

import static yaa.pojos.GlobalData.fs1;
import static yaa.pojos.NameUtils.clzCodeName;
import static yaa.pojos.NameUtils.dottedStoreName;

public class F1NewMeta {
  public static void f1NewMeta(NewMeta newMeta) {
    var simpleClzName = newMeta.name.content;

    var class$name = dottedStoreName(simpleClzName);
    var fs1Meta = new YaaMeta();
    fs1Meta.name = class$name;
    fs1Meta.startLine = newMeta.start.line;
    fs1Meta.column = newMeta.start.column;
    // Fs1Utils.isItDefined(simpleClzName, newClass.address());
    fs1.putSymbol(newMeta.placeOfUse(), fs1Meta);
    fs1.putSymbol(simpleClzName, fs1Meta);
    fs1.newTable();

    var codeName = clzCodeName(simpleClzName);
    fs1Meta.codeName = codeName;
    //the lowercase is necessary so that pool/Pool won't interfere with pool/pool
    GlobalData.usedClzNames.get(fs1.path).add(codeName.toLowerCase());

    for (var dec : newMeta.vDeclarations) {
      f1MetaDec(dec);
      var fieldName = dec.name.content;
      var field = (YaaField) fs1.getSymbol(fieldName);
      field.owner = fs1Meta.codeName;
      fs1Meta.requiredFields.put(fieldName, field);
    }

    for (var def : newMeta.vDefinitions) {
      f1MetaDef(def);
      var fieldName = def.name.content;
      var field = (YaaField) fs1.getSymbol(fieldName);
      field.owner = fs1Meta.codeName;
      fs1Meta.defaultFields.put(fieldName, field);
    }

    fs1.storeTable(newMeta);
    fs1.popTable();
    if (newMeta.itIsTopLevelClz) {
      GlobalData.defineMetaGlobally(class$name, fs1Meta);
    }
  }

  public static void f1MetaDef(VDefinition def) {
    var name = def.name.content;
    if (SourceVersion.isKeyword(name)) {
      throw new YaaError(
          def.name.placeOfUse(), "The java keyword \""
          + name + "\" can't be used for a variable definition"
      );
    }
    var previouslyDefined = fs1.getAlreadyDefinedSymbolInPass1(name);
    if (previouslyDefined instanceof YaaField){
      throw new YaaError(def.placeOfUse(),
          "'" + name +"' has been used by another symbol at " + previouslyDefined.placeOfUSe());
    }
    var field = new YaaField(name);
    field.itIsWhat = def.itIsWhat;
    field.startLine = def.start.line;
    field.column = def.start.column;
    field.path = fs1.path;
    fs1.putSymbol(name, field);
  }

  public static void f1MetaDec(VDeclaration dec) {
    var name = dec.name.content;
    if (SourceVersion.isKeyword(name)) {
      throw new YaaError(
          dec.name.placeOfUse(), "The java keyword \""
          + name + "\" can't be used for a variable declaration"
      );
    }
    var previouslyDefined = fs1.getAlreadyDefinedSymbolInPass1(name);
    if (previouslyDefined instanceof YaaField){
      throw new YaaError(dec.placeOfUse(),
          "'" + name +"' has been used by another symbol at " + previouslyDefined.placeOfUSe());
    }
    var field = new YaaField(name);
    field.itIsWhat = dec.itIsWhat;
    field.startLine = dec.start.line;
    field.column = dec.start.column;
    field.path = fs1.path;
    fs1.putSymbol(name, field);
  }
}
