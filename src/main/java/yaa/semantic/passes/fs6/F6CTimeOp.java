package yaa.semantic.passes.fs6;

import yaa.ast.*;
import yaa.pojos.YaaInfo;
import yaa.pojos.GlobalData;

import static java.lang.Double.parseDouble;
import static java.lang.Float.parseFloat;
import static java.lang.Integer.parseInt;
import static java.lang.Long.parseLong;

public class F6CTimeOp {
  public static YaaInfo compOp(double value, YaaInfo type) {
    switch (type.name) {
      case GlobalData.double$name -> {
        F6Utils.generateDoubleCode(value);
      }
      case GlobalData.float$name -> {
        F6Utils.generateFloatCode((float) (value));
      }
      case GlobalData.long$name -> {
        F6Utils.generateLongCode((long) (value));
      }
      case GlobalData.int$name, GlobalData.short$name, GlobalData.byte$name, GlobalData.char$name -> {
        F6Utils.generateIntCode((int) (value));
      }
    }
    return type;
  }

  public static double compTimeDouble(Stmt e) {
    if (e instanceof Pointed pointed) {
      return parseDouble(pointed.token.content);
    }
    if (e instanceof Floated floated) {
      return parseFloat(floated.token.content);
    }
    if (e instanceof Longed longed) {
      return parseLong(longed.token.content);
    }
    if (e instanceof Decimal decimal) {
      return parseInt(decimal.token.content);
    }
    if (e instanceof Shorted shorted) {
      return parseInt(shorted.token.neededContent);
    }
    if (e instanceof Byted byted) {
      return parseInt(byted.token.neededContent);
    }
    return (Character.hashCode(((Cha) e).char$content));
  }
}
