package yaa.semantic.passes.fs5;

import yaa.ast.Name;
import yaa.pojos.*;

import static yaa.parser.YaaParser.astMain;
import static yaa.pojos.GlobalData.*;

public class F5NameOp {
  public static YaaInfo f5Name(Name name) {
    var nameContent = name.token.content;
    var info = fs5.getSymbol(nameContent);
    if (info == null) {
      throw new YaaError(
          name.placeOfUse(),
          "The referenced symbol \""
              + nameContent
              + "\" is not defined in scope"
      );
    }
    if (info instanceof YaaClz) {
      throw new YaaError(
          name.placeOfUse(),
          "\"" + nameContent + "\" cannot act as an expression"
      );
    }
    if (info.cbIndex > -1) {
      throw new YaaError(
          name.placeOfUse(),
          "\"" + nameContent + "\" is a bound" +
              ", type bounds cannot act as expressions"
      );
    }
    if (info instanceof YaaField field) {
      if (field.isPredefined) {
        if (nameContent.equals("cmd") && astMain == null) {
          throw new YaaError(
              name.placeOfUse(),
              "cmd is only reference-able " +
                  "when there is a defined entry point"
          );
        }
      } else if (field.data == null) {
        throw new YaaError(
            name.placeOfUse(),
            "The referenced symbol \"" + nameContent
                + "\" was referenced before initialization"
        );
      }
      if (field.itIsWhat != FieldIsWhat.top$field) {
        if (!topClz.isEmpty()) {
          var clz = topClz.peek();
          if (!field.itIsStatic) {
            if (field.startLine < clz.startLine || field.startLine > clz.endLine) {
              throw new YaaError(
                  name.placeOfUse(),
                  topClz.peek() + " is static, a static type cannot close over values",
                  "the closed over value is defined at line " + field.startLine
              );
            }
          }
        }
        if (!F5.topMtd.isEmpty()) {
          var mtd = F5.topMtd.peek();
          if (mtd.mtdIsWhat == MtdIsWhat.staticMtd && field.startLine < mtd.startLine) {
            mtd.closures.putIfAbsent(nameContent, field);
          }
        }
      }
      if (field.typeParam != null) {
        var data = field.data.cloneInfo();
        data.typeParam = field.typeParam;
        return data;
      }
      return field.data;
    }
    return info;
  }
}
