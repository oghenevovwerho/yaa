package yaa.semantic.passes.fs1;

import yaa.ast.VDefinition;
import yaa.pojos.*;

import javax.lang.model.SourceVersion;
import java.util.HashSet;
import java.util.Set;

import static yaa.pojos.GlobalData.*;
import static yaa.pojos.GlobalData.fs;

public class F1VDef {
  private static final Set<String> stmtProps = new HashSet<>(3);
  private static final Set<String> topProps = new HashSet<>(3);
  private static final Set<String> typeProps = new HashSet<>(3);

  static {
    stmtProps.add("final");

    topProps.add("final");
    topProps.add("public");
    topProps.add("private");

    typeProps.add("final");
    typeProps.add("public");
    typeProps.add("protected");
    typeProps.add("private");
  }

  public static void f1topDef(VDefinition def) {
    var name = def.name.content;
    if (SourceVersion.isKeyword(name)) {
      throw new YaaError(
          def.name.placeOfUse(), "The java keyword \""
          + name + "\" can't be used for a variable definition"
      );
    }
    for (var option : def.options.entrySet()) {
      if (!topProps.contains(option.getKey())) {
        throw new YaaError(
            option.getValue().placeOfUse(), "A global scope variable " +
            "definition cannot contain the option \"" + option.getKey() + "\""
        );
      }
    }
    var previouslyDefined = fs1.getAlreadyDefinedSymbolInPass1(name);
    if (previouslyDefined instanceof YaaField) {
      throw new YaaError(def.placeOfUse(),
          "'" + name + "' has been used by another symbol at " + previouslyDefined.placeOfUSe());
    }
    var field = new YaaField(name);
    field.itIsWhat = def.itIsWhat;
    field.startLine = def.start.line;
    field.column = def.start.column;
    field.itIsFinal = def.options.get("final") != null;
    fs.putSymbol(name, field);
    field.privacy = def.privacy();

    if (field.privacy == 0) {
      field.path = fs1.path;
      defineFieldGlobally(NameUtils.dottedStoreName(name), field);
    }
  }

  public static void f1typeDef(VDefinition def) {
    var name = def.name.content;
    if (SourceVersion.isKeyword(name)) {
      throw new YaaError(
          def.name.placeOfUse(), "The java keyword \""
          + name + "\" can't be used for a variable definition"
      );
    }
    for (var option : def.options.entrySet()) {
      if (!typeProps.contains(option.getKey())) {
        throw new YaaError(
            option.getValue().placeOfUse(), "A type scope variable " +
            "definition cannot contain the option \"" + option.getKey() + "\""
        );
      }
    }
    var previouslyDefined = fs1.getAlreadyDefinedSymbolInPass1(name);
    if (previouslyDefined instanceof YaaField) {
      throw new YaaError(def.placeOfUse(), "'" + name + "' has been used by another symbol at " + previouslyDefined.placeOfUSe());
    }
    var field = new YaaField(name);
    field.itIsWhat = def.itIsWhat;
    field.startLine = def.start.line;
    field.column = def.start.column;
    field.path = fs1.path;
    field.itIsFinal = def.options.get("final") != null;
    fs.putSymbol(name, field);
  }

  public static void f1stmtDef(VDefinition def) {
    var name = def.name.content;
    if (SourceVersion.isKeyword(name)) {
      throw new YaaError(
          def.name.placeOfUse(), "The java keyword \""
          + name + "\" can't be used for a variable definition"
      );
    }
    for (var option : def.options.entrySet()) {
      if (!stmtProps.contains(option.getKey())) {
        throw new YaaError(
            option.getValue().placeOfUse(), "A local scope variable " +
            "definition cannot contain the option \"" + option.getKey() + "\""
        );
      }
    }
    var previouslyDefined = fs1.getAlreadyDefinedSymbolInPass1(name);
    if (previouslyDefined instanceof YaaField) {
      throw new YaaError(def.placeOfUse(),
          "'" + name + "' has been used by another symbol at " + previouslyDefined.placeOfUSe());
    }
    var field = new YaaField(name);
    field.itIsWhat = def.itIsWhat;
    field.startLine = def.start.line;
    field.column = def.start.column;
    field.path = fs1.path;
    field.itIsFinal = def.options.get("final") != null;
    fs.putSymbol(name, field);
  }
}
