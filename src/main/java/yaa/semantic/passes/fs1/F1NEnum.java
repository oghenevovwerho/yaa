package yaa.semantic.passes.fs1;

import yaa.ast.Decimal;
import yaa.ast.False;
import yaa.ast.NewEnum;
import yaa.ast.True;
import yaa.parser.TokenUtils;
import yaa.pojos.*;
import yaa.pojos.jMold.JMold;

import java.util.*;

import static yaa.pojos.GlobalData.fs1;
import static yaa.pojos.GlobalData.int$name;
import static yaa.pojos.NameUtils.dottedStoreName;

public class F1NEnum {
  private static final Set<String> enumFunProps = new HashSet<>(3);

  static {
    enumFunProps.add("privacy");
  }

  public static YaaInfo f1NewEnum(NewEnum newEnum) {
    var simpleClzName = newEnum.name.content;

    var dottedStoreName = dottedStoreName(simpleClzName);
    var fs1Enum = new YaaClz(dottedStoreName);
    fs1Enum.isFinal = true;
    var list = new ArrayList<YaaClz>(1);
    list.add(fs1Enum);
    var clz = new JMold().newClz("java.lang.Enum");
    fs1Enum.parent = changeClzBounds(clz, list);
    fs1Enum.category = TypeCategory.enum_c;
    fs1Enum.startLine = newEnum.start.line;
    fs1Enum.column = newEnum.start.column;
    fs1Enum.endLine = newEnum.close.line;
    // Fs1Utils.isItDefined(simpleClzName, newClass.address());
    //define the class in the enclosing scope, so that, sibling constructs can find it
    fs1.putSymbol(simpleClzName, fs1Enum);
    fs1.putSymbol(newEnum.placeOfUse(), fs1Enum);
    fs1.newTable();
    //the lowercase is necessary so that pool/Pool won't interfere with pool/pool
    GlobalData.usedClzNames.get(fs1.path).add(fs1Enum.codeName.toLowerCase());

    for (var dec : newEnum.vDeclarations) {
      F1VDec.f1stmtDec(dec);
      var fieldName = dec.name.content;
      var field = (YaaField) fs1.getSymbol(fieldName);
      field.owner = fs1Enum.codeName;
      fs1Enum.instance$fields.put(fieldName, field);
    }

    for (var def : newEnum.vDefinitions) {
      F1VDef.f1typeDef(def);
      var fieldName = def.name.content;
      var field = (YaaField) fs1.getSymbol(fieldName);
      field.owner = fs1Enum.codeName;
      fs1Enum.instance$fields.put(fieldName, field);
    }

    for (int i = 0; i < newEnum.enumOptions.size(); i++) {
      var option = newEnum.enumOptions.get(i);
      var name = option.name.content;
      var previouslyDefined = fs1.getAlreadyDefinedSymbolInPass1(name);
      if (previouslyDefined instanceof YaaField){
        throw new YaaError(option.name.placeOfUse(),
            "'" + name +"' has been used by another symbol at "
                + previouslyDefined.placeOfUSe());
      }
      var enum_field = new YaaField(name, true);
      enum_field.itIsWhat = FieldIsWhat.top$field;
      fs1Enum.enumIndices.put(name, i);
      enum_field.isEnumField = true;
      enum_field.data = fs1Enum;
      fs1Enum.instance$fields.put(name, enum_field);
    }

    for (var trait$clz : newEnum.implementations) {
      F1BlockInClz.f1BlockInClz(trait$clz);
    }

    for (var block : newEnum.runBlocks) {
      fs1.newTable();

      for (var stmt : block.stmts) {
        stmt.visit(fs1);
      }

      fs1.storeTable(block);
      fs1.popTable();
    }

    if (newEnum.inits.size() == 0) {
      var stubInit = new YaaInit();
      fs1Enum.inits.add(stubInit);
    } else {
      for (var init : newEnum.inits) {
        fs1Enum.inits.add(F1Init.f1Init(init));
      }
    }

    for (var method : newEnum.methods) {
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
                  if (fs1Enum.category == TypeCategory.trait_c) {
                    throw new YaaError(
                        argument.placeOfUse(),
                        "trait methods cannot have the final option"
                    );
                  }
                } else if (arg.getValue() instanceof False) {
                  newMethod.isFinal = false;
                  if (fs1Enum.category == TypeCategory.trait_c) {
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
            }
            if (!enumFunProps.contains(argument.content)) {
              throw new YaaError(
                  argument.placeOfUse(),
                  "A type scope method definition cannot " +
                      "contain the option \"" + argument.content + "\""
              );
            }
          }
        }
      }
      newMethod.owner = fs1Enum.codeName;
      var definedMethods = fs1Enum.instanceMethods.get(mtdName);
      if (definedMethods != null) {
        definedMethods.methods.add(newMethod);
      } else {
        var newMtdPack = new MtdPack(new ArrayList<>());
        newMtdPack.methods.add(newMethod);
        fs1Enum.instanceMethods.put(mtdName, newMtdPack);
      }
    }

    if (newEnum.toStringParentMtd != null) {
      F1ParentMtd.f1ParentMtd(newEnum.toStringParentMtd);
    }

    for (var newIClass : newEnum.classes) {
      var simple$name = newIClass.name.content;
      var internal$clz = (YaaClz) F1NClass.f1NewClass(newIClass);
      internal$clz.name = fs1Enum.name + "." + simple$name;
      GlobalData.defineClassGlobally(internal$clz.name, internal$clz);
      fs1Enum.staticInnerClasses.put(simple$name, internal$clz);
      newIClass.itIsTopLevelClz = newEnum.itIsTopLevelClz;
    }

    for (var newIEnum : newEnum.enums) {
      var simple$name = newIEnum.name.content;
      var internal$clz = (YaaClz) F1NEnum.f1NewEnum(newIEnum);
      internal$clz.name = fs1Enum.name + "." + simple$name;
      GlobalData.defineClassGlobally(internal$clz.name, internal$clz);
      fs1Enum.staticInnerClasses.put(simple$name, internal$clz);
      newIEnum.itIsTopLevelClz = newEnum.itIsTopLevelClz;
    }

    fs1.storeTable(newEnum);
    fs1.popTable();
    GlobalData.defineClassGlobally(dottedStoreName, fs1Enum);
    if (newEnum.itIsTopLevelClz) {
      GlobalData.defineImportableClass(dottedStoreName, fs1Enum);
    }
    return fs1Enum;
  }

  public static YaaClz changeClzBounds(YaaClz eClz, List<YaaClz> args) {
    var new_clz = (YaaClz) eClz.cloneInfo();
    for (int i = 0; i < new_clz.inputted.size(); i++) {
      var type_param = new_clz.inputted.get(i);
      if (type_param.inputted.size() > 0) {
        new_clz.inputted.set(i, type_param.changeCBounds(args));
        continue;
      }
      var cb_index = type_param.cbIndex;
      if (cb_index > -1) {
        var newIn = (YaaClz) args.get(cb_index).cloneInfo();
        if (newIn.cbIndex > -1) {
          newIn = newIn.parent;
        }
        newIn.boundState = BoundState.clz_bound;
        newIn.variance = type_param.variance;
        newIn.typeParam = type_param;
        new_clz.inputted.set(i, newIn);
        continue;
      }
      var mb_index = type_param.mbIndex;
      if (mb_index > -1) {
        var newIn = (YaaClz) args.get(mb_index).cloneInfo();
        if (newIn.mbIndex > -1) {
          newIn = newIn.parent;
        }
        newIn.typeParam = type_param;
        newIn.boundState = BoundState.mtd_bound;
        newIn.variance = type_param.variance;
        new_clz.inputted.set(i, newIn);
      }
    }

    changeMethods(new_clz.instanceMethods.values(), new_clz);
    return new_clz;
  }

  private static void changeMethods(Collection<MtdPack> packs, YaaClz new_clz) {
    for (var pack : packs) {
      for (var new_mtd : pack.methods) {
        if (!new_mtd.hasClzTypeParam) {
          continue;
        }
        var parameters = new_mtd.parameters;
        new_mtd.raw_parameters = parameters;
        var new$parameters = new ArrayList<YaaInfo>(parameters.size());
        for (var param : parameters) {
          if (param.cbIndex > -1) {
            new$parameters.add(new_clz.inputted.get(param.cbIndex));
          } else {
            new$parameters.add(param);
          }
        }
        new_mtd.parameters = new$parameters;
      }
    }
  }
}
