package yaa.semantic.passes.fs2;

import yaa.ast.Import;
import yaa.ast.Imports;
import yaa.pojos.*;
import yaa.pojos.jMold.JMold;
import yaa.semantic.handlers.OpUtils;

import java.util.ArrayList;

import static yaa.pojos.GlobalData.*;

public class F2Imp {
  public static void f2ImportsStmt(Imports imports) {
    var notYetHandled = new ArrayList<Import>();
    for (var imp : imports.imports) {
      if (imp.is4Jvm) {
        notYetHandled.add(imp);
        continue;
      }
      if (!yaaContent(imp)) {
        notYetHandled.add(imp);
      }
    }
    for (var imp : notYetHandled) {
      if (!itIsJvmImp(imp)) {
        var meta = new JMold().newMeta(imp.fullName);
        if (meta != null) {
          switch (meta.name) {
            case "java.lang.FunctionalInterface" -> {
              throw new YaaError(
                  imp.placeOfUse(), meta.name + " is not allowed within Yaa code",
                  "use interface signatures to define functional interfaces"
              );
            }
            case "java.lang.Override", "java.lang.SafeVarargs", "java.lang.Deprecated" -> {
              throw new YaaError(
                  imp.placeOfUse(), meta.name + " is not allowed within Yaa code"
              );
            }
            case "java.lang.SuppressWarnings" -> {
              throw new YaaError(
                  imp.placeOfUse(), meta.name + " is not allowed within Yaa code",
                  "all of a program's configuration should be done from its settings file"
              );
            }
          }
          fs2.putSymbol(imp.refName, meta);
        } else {
          throw new YaaError(
              imp.placeOfUse(), imp.toString(),
              "The imported content above is not defined"
          );
        }
      }
    }
  }

  private static boolean itIsJvmImp(Import imp) {
    var jvmImport = new JMold().impObj(imp.fullName, imp.placeOfUse());
    if (jvmImport == null) {
      return false;
    }
    if (jvmImport instanceof YaaClz definedClz) {
      fs2.putSymbol(imp.refName, definedClz);
      return true;
    }
    if (jvmImport instanceof MtdPack mtdPack) {
      fs2.putSymbol(imp.refName, mtdPack);
      return true;
    }
    if (jvmImport instanceof YaaField fd) {
      fs2.putSymbol(fd.field$name, fd);
      return true;
    }
    return false;
  }

  private static boolean yaaContent(Import imp) {
    YaaInfo info = null;
    var index = -1;
    var sb = new StringBuilder();
    sb.append(imp.tokens.get(0).content);
    var possibleClass = getTouchedClass(imp.fullName);
    if (possibleClass != null) {
      OpUtils.checkForVisibility(imp.placeOfUse(), possibleClass);
      fs2.putSymbol(imp.refName, possibleClass);
      return true;
    }
    for (int i = 1; i < imp.tokens.size(); i++) {
      sb.append(".").append(imp.tokens.get(i).content);
      info = getGlobalSymbol(sb.toString());
      if (info != null) {
        index = i;
        break;
      }
    }

    if (index == -1) {
      return false;
    }

    for (int i = index; i < imp.tokens.size(); i++) {
      if (i == imp.tokens.size() - 1) {
        if (info instanceof YaaClz clz) {
          fs2.putSymbol(imp.refName, clz);
          return true;
        } else if (info instanceof YaaField field) {
          OpUtils.checkForVisibility(imp.placeOfUse(), field);
          fs2.putSymbol(imp.refName, field);
          return true;
        } else if (info instanceof MtdPack pack) {
          fs2.putSymbol(imp.refName, pack);
          return true;
        } else {
          return false;
        }
      } else {
        var next$name = imp.tokens.get(i + 1).content;
        if (info instanceof YaaClz clz) {
          info = clz.staticInnerClasses.get(next$name);
          if (info == null) {
            throw new YaaError(
                imp.tokens.get(i + 1).placeOfUse(),
                sb.append(".").append(next$name).toString(),
                clz + " does not define " + next$name
            );
          }
          OpUtils.checkForVisibility(imp.tokens.get(i + 1).placeOfUse(), (YaaClz) info);
        } else if (info instanceof YaaFun ignore) {
          if (i != imp.tokens.size() - 1) {
            var error$name = imp.tokens.get(i + 2).content;
            throw new YaaError(
                imp.placeOfUse(),
                sb.append(".").append(next$name).toString(),
                "The content \"" + error$name + "\" cannot " +
                    "be imported from a function"
            );
          }
        } else if (info instanceof YaaField ignore) {
          if (i != imp.tokens.size() - 1) {
            var error$name = imp.tokens.get(i + 2).content;
            throw new YaaError(
                imp.placeOfUse(),
                sb.append(".").append(next$name).toString(),
                "The content \"" + error$name + "\" cannot " +
                    "be imported from a field"
            );
          }
        } else {
          throw new YaaError(
              imp.placeOfUse(), sb.append(".").append(next$name).toString(),
              "The imported content is not recognised as a Yaa construct"
          );
        }
      }
    }
    return false;
  }
}
