package yaa.semantic.handlers;

import yaa.ast.Name;
import yaa.pojos.*;

import static yaa.pojos.GlobalData.fs;

public class NameOp {
  public static YaaInfo name(Name name) {
    var nameContent = name.token.content;
    var info = fs.getSymbol(nameContent);
    if (info == null) {
      throw new YaaError(
          name.placeOfUse(),
          "The referenced symbol \"" + nameContent
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
      if (field.data == null) {
        throw new YaaError(
            name.placeOfUse(),
            "The referenced symbol \"" + nameContent
                + "\" was referenced before initialization"
        );
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
