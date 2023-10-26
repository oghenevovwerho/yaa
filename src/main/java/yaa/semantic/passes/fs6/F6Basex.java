package yaa.semantic.passes.fs6;

import yaa.ast.Basex;
import yaa.pojos.YaaInfo;
import yaa.pojos.GlobalData;

import static java.lang.Byte.parseByte;
import static java.lang.Character.getNumericValue;
import static java.lang.Integer.parseInt;
import static java.lang.Long.parseLong;
import static java.lang.Short.parseShort;

public class F6Basex {
  public static YaaInfo basex(Basex basex) {
    var x_token = basex.xToken;
    var base = x_token.base;
    if (x_token.isFloated) {
      F6Utils.generateFloatCode(baseTen(x_token.number, base));
      return GlobalData.float$clz;
    }
    if (x_token.isLong) {
      F6Utils.generateLongCode(parseLong(x_token.number, base));
      return GlobalData.long$clz;
    }
    if (x_token.isByte) {
      F6Utils.generateIntCode(parseByte(x_token.number, base));
      return GlobalData.byte$clz;
    }
    if (x_token.isPointed) {
      F6Utils.generateDoubleCode(baseTen(x_token.number, base));
      return GlobalData.double$clz;
    }
    if (x_token.isShorted) {
      F6Utils.generateIntCode(parseShort(x_token.number, base));
      return GlobalData.short$clz;
    }
    F6Utils.generateIntCode(parseInt(x_token.number, base));
    return GlobalData.int$clz;
  }

  private static float baseTen(String number, int base) {
    var f_string = number.substring(number.indexOf(".") + 1);
    var f_part = 0.0F;
    for (int i = 0; i < f_string.length(); i++) {
      var current = getNumericValue(f_string.charAt(i));
      switch (current) {
        case 'a' -> {
          current = 10;
        }
        case 'b' -> {
          current = 11;
        }
        case 'c' -> {
          current = 12;
        }
        case 'd' -> {
          current = 13;
        }
        case 'e' -> {
          current = 14;
        }
        case 'f' -> {
          current = 15;
        }
      }
      f_part = (float) (f_part + (current * Math.pow(base, -(i + 1))));
    }
    var whole_part = number.substring(0, number.indexOf("."));
    return parseInt(whole_part, base) + f_part;
  }
}
