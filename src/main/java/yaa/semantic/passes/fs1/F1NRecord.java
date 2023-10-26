package yaa.semantic.passes.fs1;

import yaa.ast.*;
import yaa.parser.TokenUtils;
import yaa.pojos.*;
import yaa.pojos.jMold.JMold;

import java.util.ArrayList;

import static yaa.pojos.GlobalData.fs1;
import static yaa.pojos.GlobalData.int$name;
import static yaa.pojos.NameUtils.dottedStoreName;

public class F1NRecord {
  public static YaaInfo f1NewRecord(NewRecord newRecord) {
    var simpleClzName = newRecord.name.content;

    var dottedStoreName = dottedStoreName(simpleClzName);
    var fs1Record = new YaaClz(dottedStoreName);
    fs1Record.isFinal = true;
    fs1Record.parent = new JMold().newClz("java.lang.Record");
    fs1Record.startLine = newRecord.start.line;
    fs1Record.column = newRecord.start.column;
    fs1Record.endLine = newRecord.close.line;
    // Fs1Utils.isItDefined(simpleClzName, newClass.address());
    fs1.putSymbol(simpleClzName, fs1Record);
    fs1.putSymbol(newRecord.placeOfUse(), fs1Record);
    fs1.newTable();
    //the lowercase is necessary so that pool/Pool won't interfere with pool/pool
    GlobalData.usedClzNames.get(fs1.path).add(fs1Record.codeName.toLowerCase());

    for (var dec : newRecord.vDeclarations) {
      F1VDec.f1typeDec(dec);
      var fieldName = dec.name.content;
      var field = (YaaField) fs1.getSymbol(fieldName);
      field.owner = fs1Record.codeName;
      fs1Record.instance$fields.put(fieldName, field);
    }

    for (var def : newRecord.vDefinitions) {
      F1VDef.f1typeDef(def);
      var fieldName = def.name.content;
      var field = (YaaField) fs1.getSymbol(fieldName);
      field.owner = fs1Record.codeName;
      fs1Record.instance$fields.put(fieldName, field);
    }

    if (newRecord.inits.size() == 0) {
      var stubInit = new YaaInit();
      fs1Record.inits.add(stubInit);
    } else {
      for (var init : newRecord.inits) {
        var newInit = new YaaInit();
        fs1.putSymbol(init.placeOfUse(), newInit);
        fs1.newTable();
        f1RecordInit(init, fs1Record);
        fs1Record.inits.add((YaaInit) fs1.getSymbol(init.placeOfUse()));
        fs1.storeTable(init);
        fs1.popTable();
      }
    }

    for (var trait$clz : newRecord.parents) {
      F1BlockInClz.f1BlockInClz(trait$clz);
    }

    for (var block : newRecord.runBlocks) {
      fs1.newTable();

      for (var stmt : block.stmts) {
        stmt.visit(fs1);
      }

      fs1.storeTable(block);
      fs1.popTable();
    }

    for (var method : newRecord.methods) {
      var mtdName = method.name.content;
      var newMethod = (YaaFun) F1NFun.f1NewFun(method);
      for (var metaCall : method.metaCalls) {
        var meta = fs1.getSymbol(metaCall.name.content);
        if (meta instanceof YaaMeta && meta.name.equals(GlobalData.configMetaClzName)) {
          for (var arg : metaCall.arguments.entrySet()) {
            var argument = arg.getKey();
            switch (argument.content) {
              case "privacy" -> {
                if (arg.getValue() instanceof Decimal decimal) {
                  int value = TokenUtils.decimalValue(decimal.token);
                  if (value == 0) {
                    newMethod.privacy = 0;
                    method.privacy = 0;
                  } else if (value == 1) {
                    newMethod.privacy = 1;
                    method.privacy = 1;
                  } else if (value == 2) {
                    newMethod.privacy = 2;
                    method.privacy = 2;
                  } else {
                    throw new YaaError(
                        arg.getValue().placeOfUse(),
                        "The value of the privacy parameter must be 0, or 1, or 2"
                    );
                  }
                } else {
                  throw new YaaError(
                      arg.getValue().placeOfUse(),
                      "The value of the privacy parameter must be a literal of " + int$name
                  );
                }
              }
              case "final" -> {
                if (arg.getValue() instanceof True) {
                  newMethod.isFinal = true;
                  if (fs1Record.category == TypeCategory.trait_c) {
                    throw new YaaError(
                        argument.placeOfUse(),
                        "trait methods cannot have the final option"
                    );
                  }
                } else if (arg.getValue() instanceof False) {
                  newMethod.isFinal = false;
                  if (fs1Record.category == TypeCategory.trait_c) {
                    throw new YaaError(
                        argument.placeOfUse(),
                        "trait methods cannot have the final option"
                    );
                  }
                } else {
                  throw new YaaError(
                      arg.getValue().placeOfUse(),
                      "The value of the final parameter must be a boolean literal"
                  );
                }
              }
              default -> {
                throw new YaaError(
                    argument.placeOfUse(),
                    "A type scope method definition cannot " +
                        "contain the option \"" + argument.content + "\""
                );
              }
            }
          }
        }
      }
      newMethod.owner = fs1Record.codeName;
      var definedMethods = fs1Record.instanceMethods.get(mtdName);
      if (definedMethods != null) {
        definedMethods.methods.add(newMethod);
      } else {
        var newMtdPack = new MtdPack(new ArrayList<>());
        newMtdPack.methods.add(newMethod);
        fs1Record.instanceMethods.put(mtdName, newMtdPack);
      }
    }

    for (var newIClass : newRecord.classes) {
      var simple$name = newIClass.name.content;
      var internal$clz = (YaaClz) F1NClass.f1NewClass(newIClass);
      internal$clz.name = fs1Record.name + "." + simple$name;
      GlobalData.defineClassGlobally(internal$clz.name, internal$clz);
      fs1Record.staticInnerClasses.put(simple$name, internal$clz);
      newIClass.itIsTopLevelClz = newRecord.itIsTopLevelClz;
    }

    for (var newIEnum : newRecord.enums) {
      var simple$name = newIEnum.name.content;
      var internal$clz = (YaaClz) F1NEnum.f1NewEnum(newIEnum);
      internal$clz.name = fs1Record.name + "." + simple$name;
      GlobalData.defineClassGlobally(internal$clz.name, internal$clz);
      fs1Record.staticInnerClasses.put(simple$name, internal$clz);
      newIEnum.itIsTopLevelClz = newRecord.itIsTopLevelClz;
    }

    fs1.storeTable(newRecord);
    fs1.popTable();
    GlobalData.defineClassGlobally(dottedStoreName, fs1Record);
    if (newRecord.itIsTopLevelClz) {
      GlobalData.defineImportableClass(dottedStoreName, fs1Record);
    }
    return fs1Record;
  }

  public static void f1RecordInit(Init init, YaaClz clz) {
    for (var parameter : init.parameters) {
      var name = parameter.name.content;
      var previouslyDefined = fs1.getAlreadyDefinedSymbolInPass1(name);
      if (previouslyDefined instanceof YaaField){
        throw new YaaError(parameter.placeOfUse(),
            "'" + name +"' has been used by another symbol at " + previouslyDefined.placeOfUSe());
      }
      var field = new YaaField(name, true);
      field.isRecordField = true;
      field.owner = clz.codeName;
      field.startLine = parameter.start.line;
      field.column = parameter.start.column;
      field.itIsWhat = FieldIsWhat.mtd$field;
      clz.instance$fields.put(name, field);
      fs1.table.parent.putSymbol(name, field);
    }

    init.stmt.visit(GlobalData.fs1);
  }
}