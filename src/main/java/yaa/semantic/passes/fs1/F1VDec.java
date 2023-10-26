package yaa.semantic.passes.fs1;

import yaa.ast.VDeclaration;
import yaa.pojos.*;

import javax.lang.model.SourceVersion;
import java.util.HashSet;
import java.util.Set;

import static yaa.pojos.GlobalData.*;

public class F1VDec {
  private static final Set<String> stmtProps = new HashSet<>(3);
  private static final Set<String> topProps = new HashSet<>(3);
  private static final Set<String> typeProps = new HashSet<>(3);

  static {
    stmtProps.add("");

    topProps.add("public");
    topProps.add("private");

    typeProps.add("final");
    typeProps.add("public");
    typeProps.add("protected");
    typeProps.add("private");
  }

  public static void f1topDec(VDeclaration dec) {
    var name = dec.name.content;
    if (SourceVersion.isKeyword(name)) {
      throw new YaaError(
          dec.name.placeOfUse(), "The java keyword \""
          + name + "\" can't be used for a variable declaration"
      );
    }

    for (var option : dec.options.entrySet()) {
      if (!topProps.contains(option.getKey())) {
        throw new YaaError(
            option.getValue().placeOfUse(), "A global scope variable " +
            "declaration cannot contain the option \"" + option.getKey() + "\""
        );
      }
    }

    var previouslyDefined = fs1.getAlreadyDefinedSymbolInPass1(name);
    if (previouslyDefined instanceof YaaField) {
      throw new YaaError(dec.placeOfUse(),
          "'" + name + "' has been used by another symbol at " + previouslyDefined.placeOfUSe());
    }
    var field = new YaaField(name);
    field.itIsWhat = dec.itIsWhat;
    field.startLine = dec.start.line;
    field.column = dec.start.column;
    field.itIsFinal = dec.options.get("final") != null;
    fs.putSymbol(name, field);
    field.privacy = dec.privacy();

    if (field.privacy == 0) {
      defineFieldGlobally(NameUtils.dottedStoreName(name), field);
    }
  }

  public static YaaInfo f1stmtDec(VDeclaration dec) {
    var name = dec.name.content;
    if (SourceVersion.isKeyword(name)) {
      throw new YaaError(
          dec.name.placeOfUse(), "The java keyword \""
          + name + "\" can't be used for a variable declaration"
      );
    }
    for (var option : dec.options.entrySet()) {
      if (!stmtProps.contains(option.getKey())) {
        throw new YaaError(
            option.getValue().placeOfUse(), "A local scope variable " +
            "declaration cannot contain the option \"" + option.getKey() + "\""
        );
      }
    }
    var previouslyDefined = fs1.getAlreadyDefinedSymbolInPass1(name);
    if (previouslyDefined instanceof YaaField) {
      throw new YaaError(dec.placeOfUse(),
          "'" + name + "' has been used by another symbol at " + previouslyDefined.placeOfUSe());
    }
    var field = new YaaField(name);
    field.itIsWhat = dec.itIsWhat;
    field.startLine = dec.start.line;
    field.column = dec.start.column;
    fs.putSymbol(name, field);
    return field;
  }

  public static void f1typeDec(VDeclaration dec) {
    var name = dec.name.content;
    if (SourceVersion.isKeyword(name)) {
      throw new YaaError(
          dec.name.placeOfUse(), "The java keyword \""
          + name + "\" can't be used for a variable declaration"
      );
    }
    for (var option : dec.options.entrySet()) {
      if (!typeProps.contains(option.getKey())) {
        throw new YaaError(
            option.getValue().placeOfUse(), "A type scope variable " +
            "declaration cannot contain the option \"" + option.getKey() + "\""
        );
      }
    }
    var previouslyDefined = fs1.getAlreadyDefinedSymbolInPass1(name);
    if (previouslyDefined instanceof YaaField) {
      throw new YaaError(dec.placeOfUse(),
          "'" + name + "' has been used by another symbol at " + previouslyDefined.placeOfUSe());
    }
    var field = new YaaField(name);
    field.itIsWhat = dec.itIsWhat;
    field.startLine = dec.start.line;
    field.column = dec.start.column;
    field.itIsFinal = dec.options.get("final") != null;
    fs.putSymbol(name, field);
  }
}
