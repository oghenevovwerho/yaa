package yaa.semantic.passes.fs1;

import yaa.ast.Decimal;
import yaa.ast.False;
import yaa.ast.NewClass;
import yaa.ast.True;
import yaa.parser.TokenUtils;
import yaa.pojos.*;

import java.util.ArrayList;

import static yaa.pojos.GlobalData.fs1;
import static yaa.pojos.GlobalData.int$name;
import static yaa.pojos.NameUtils.dottedStoreName;

public class F1NClass {
  public static YaaInfo f1NewClass(NewClass newClass) {
    var simpleClzName = newClass.name.content;

    var dottedStoreName = dottedStoreName(simpleClzName);
    var fs1Class = new YaaClz(dottedStoreName);

    for (var metaCall : newClass.metaCalls) {
      var meta = fs1.getSymbol(metaCall.name.content);
      if (meta instanceof YaaMeta && meta.name.equals(GlobalData.configMetaClzName)) {
        for (var arg : metaCall.arguments.entrySet()) {
          var argument = arg.getKey();
          switch (argument.content) {
            case "privacy" -> {
              if (arg.getValue() instanceof Decimal decimal) {
                int value = TokenUtils.decimalValue(decimal.token);
                if (value == 0) {
                  fs1Class.privacy = 0;
                } else if (value == 1) {
                  throw new YaaError(
                      arg.getValue().placeOfUse(),
                      "The value of privacy for a type must be 0 or 2"
                  );
                } else if (value == 2) {
                  fs1Class.privacy = 2;
                } else {
                  throw new YaaError(
                      arg.getValue().placeOfUse(),
                      "The value of privacy for a type must be 0 or 2"
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
                fs1Class.isFinal = true;
              } else if (arg.getValue() instanceof False) {
                fs1Class.isFinal = false;
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
                  "This type cannot have the option \"" + argument.content + "\""
              );
            }
          }
        }
      }
    }

    if (newClass.isTrait) {
      fs1Class.category = TypeCategory.trait_c;
    }
    fs1Class.startLine = newClass.start.line;
    fs1Class.column = newClass.start.column;
    fs1Class.endLine = newClass.close.line;
    // Fs1Utils.isItDefined(simpleClzName, newClass.address());
    //define the class in the enclosing scope, so that, sibling constructs can find it
    fs1.putSymbol(newClass.placeOfUse(), fs1Class);
    fs1.putSymbol(simpleClzName, fs1Class);
    fs1.newTable();
    //the lowercase is necessary so that pool/Pool won't interfere with pool/pool
    GlobalData.usedClzNames.get(fs1.path).add(fs1Class.codeName.toLowerCase());

    if (newClass.init == null) {
      fs1Class.inits.add(new YaaInit());
    } else {
      fs1Class.inits.add(F1Init.f1Init(newClass.init));
    }

    for (var dec : newClass.vDeclarations) {
      F1VDec.f1typeDec(dec);
      var fieldName = dec.name.content;
      var field = (YaaField) fs1.getSymbol(fieldName);
      field.owner = fs1Class.codeName;
      fs1Class.instance$fields.put(fieldName, field);
    }

    for (var def : newClass.vDefinitions) {
      F1VDef.f1typeDef(def);
      var fieldName = def.name.content;
      var field = (YaaField) fs1.getSymbol(fieldName);
      field.owner = fs1Class.codeName;
      fs1Class.instance$fields.put(fieldName, field);
    }

    for (var mtd_list : newClass.parentMtds.values()) {
      for (var parentMtd : mtd_list) {
        F1ParentMtd.f1ParentMtd(parentMtd);
      }
    }

    for (var trait$clz : newClass.implementations) {
      F1BlockInClz.f1BlockInClz(trait$clz);
    }

    for (var block : newClass.runBlocks) {
      fs1.newTable();

      for (var stmt : block.stmts) {
        stmt.visit(fs1);
      }

      fs1.storeTable(block);
      fs1.popTable();
    }

    for (var method : newClass.methods) {
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
                        "The value of privacy for a method must be 0, or 1, or 2"
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
                  if (fs1Class.category == TypeCategory.trait_c) {
                    throw new YaaError(
                        argument.placeOfUse(),
                        "trait methods cannot have the final option"
                    );
                  }
                } else if (arg.getValue() instanceof False) {
                  newMethod.isFinal = false;
                  if (fs1Class.category == TypeCategory.trait_c) {
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
      newMethod.itIsTraitMtd = fs1Class.category == TypeCategory.trait_c;
      newMethod.owner = fs1Class.codeName;
      var definedMethods = fs1Class.instanceMethods.get(mtdName);
      if (definedMethods != null) {
        definedMethods.methods.add(newMethod);
      } else {
        var newMtdPack = new MtdPack(new ArrayList<>());
        newMtdPack.methods.add(newMethod);
        fs1Class.instanceMethods.put(mtdName, newMtdPack);
      }
    }

    for (var newIClass : newClass.classes) {
      var simple$name = newIClass.name.content;
      var internal$clz = (YaaClz) F1NClass.f1NewClass(newIClass);
      internal$clz.name = fs1Class.name + "." + simple$name;
      GlobalData.defineClassGlobally(internal$clz.name, internal$clz);
      fs1Class.staticInnerClasses.put(simple$name, internal$clz);
      newIClass.itIsTopLevelClz = newClass.itIsTopLevelClz;
    }

    for (var newIEnum : newClass.enums) {
      var simple$name = newIEnum.name.content;
      var internal$clz = (YaaClz) F1NEnum.f1NewEnum(newIEnum);
      internal$clz.name = fs1Class.name + "." + simple$name;
      GlobalData.defineClassGlobally(internal$clz.name, internal$clz);
      fs1Class.staticInnerClasses.put(simple$name, internal$clz);
      newIEnum.itIsTopLevelClz = newClass.itIsTopLevelClz;
    }

    for (var fInterface : newClass.fInterfaces) {
      var simple$name = fInterface.name.content;
      var internal$clz = (YaaClz) F1FInterface.f1FInterface(fInterface);
      internal$clz.name = fs1Class.name + "." + simple$name;
      GlobalData.defineClassGlobally(internal$clz.name, internal$clz);
      fs1Class.staticInnerClasses.put(simple$name, internal$clz);
    }

    fs1.storeTable(newClass);
    fs1.popTable();
    GlobalData.defineClassGlobally(dottedStoreName, fs1Class);
    if (newClass.itIsTopLevelClz) {
      GlobalData.defineImportableClass(dottedStoreName, fs1Class);
    }
    return fs1Class;
  }
}