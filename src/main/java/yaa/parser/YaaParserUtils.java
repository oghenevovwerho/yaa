package yaa.parser;

import yaa.ast.NewClass;
import yaa.ast.NewEnum;
import yaa.pojos.YaaError;

import java.util.*;

public class YaaParserUtils {
  public static Set<String> classWords = new HashSet<>(3);

  static {
    classWords.add("final");
    classWords.add("public");
    classWords.add("private");
    classWords.add("protected");
  }

  public static Set<String> acceptedInStmt = new HashSet<>(3);

  static {
    acceptedInStmt.add("final");
    acceptedInStmt.add("data");
  }

  public static Set<String> acceptedInTop = new HashSet<>(3);

  static {
    acceptedInTop.add("final");
    acceptedInTop.add("data");
    acceptedInTop.add("public");
    acceptedInTop.add("protected");
    acceptedInTop.add("private");
  }

  public static Set<String> acceptedInClz = new HashSet<>(3);

  static {
    acceptedInClz.add("data");
    acceptedInClz.add("final");
    acceptedInClz.add("public");
    acceptedInClz.add("protected");
  }

  public static Set<String> acceptedInTrait = new HashSet<>(3);

  static {
    acceptedInTrait.add("final");
    acceptedInTrait.add("data");
    acceptedInTrait.add("public");
    acceptedInTrait.add("protected");
  }

  public static Set<String> traitWordsInStmt = new HashSet<>(3);

  public static Set<String> traitWordsAtTop = new HashSet<>(3);

  static {
    traitWordsAtTop.add("public");
    traitWordsAtTop.add("protected");
    traitWordsAtTop.add("private");
  }

  public static Set<String> traitWordsInClz = new HashSet<>(3);

  static {
    traitWordsInClz.add("public");
    traitWordsInClz.add("protected");
  }

  public static Set<String> traitWordsInTrait = new HashSet<>(3);

  static {
    traitWordsInTrait.add("public");
    traitWordsInTrait.add("protected");
  }

  public static Set<String> traitWords = new HashSet<>(3);

  static {
    traitWords.add("public");
    traitWords.add("private");
    traitWords.add("protected");
  }

  public static Set<String> enumWordsInStmt = new HashSet<>(3);

  public static Set<String> enumWordsAtTop = new HashSet<>(3);

  static {
    enumWordsAtTop.add("public");
    enumWordsAtTop.add("protected");
    enumWordsAtTop.add("private");
  }

  public static Set<String> enumWordsInClz = new HashSet<>(3);

  static {
    enumWordsInClz.add("public");
    enumWordsInClz.add("protected");
  }

  public static Set<String> enumWordsInTrait = new HashSet<>(3);

  static {
    enumWordsInTrait.add("public");
    enumWordsInTrait.add("protected");
  }

  public static Set<String> enumWords = new HashSet<>(3);

  static {
    enumWords.add("public");
    enumWords.add("private");
    enumWords.add("protected");
  }

  static void checkEnumPropInStmt(NewEnum object) {
    for (var option : object.options.values()) {
      if (!enumWords.contains(option.content)) {
        throw new YaaError(
            option.placeOfUse(), "\"" + option.content
            + "\" is not a valid enum option"
        );
      }
      if (!enumWordsInStmt.contains(option.content)) {
        throw new YaaError(
            option.placeOfUse(), "\"" + option.content
            + "\" is not accepted in a enum declared as a statement"
        );
      }
    }
  }

  static void checkEnumPropAtTop(NewEnum object) {
    for (var option : object.options.values()) {
      if (!enumWords.contains(option.content)) {
        throw new YaaError(
            option.placeOfUse(), "\"" + option.content
            + "\" is not a valid enum option"
        );
      }
      if (!enumWordsAtTop.contains(option.content)) {
        throw new YaaError(
            option.placeOfUse(), "\"" + option.content
            + "\" is not accepted in a enum declared at the global scope"
        );
      }
    }
  }

  static void checkEnumPropInTrait(NewEnum object) {
    for (var option : object.options.values()) {
      if (!enumWords.contains(option.content)) {
        throw new YaaError(
            option.placeOfUse(), "\"" + option.content
            + "\" is not a valid enum option"
        );
      }
      if (!enumWordsInTrait.contains(option.content)) {
        throw new YaaError(
            option.placeOfUse(), "\"" + option.content
            + "\" is not accepted in a enum declared at the global scope"
        );
      }
    }
  }

  static void checkEnumPropInClz(NewEnum object) {
    for (var option : object.options.values()) {
      if (!enumWords.contains(option.content)) {
        throw new YaaError(
            option.placeOfUse(), "\"" + option.content
            + "\" is not a valid enum option"
        );
      }
      if (!enumWordsInClz.contains(option.content)) {
        throw new YaaError(
            option.placeOfUse(), "\"" + option.content
            + "\" is not accepted in a enum declared within a class"
        );
      }
    }
  }
}
