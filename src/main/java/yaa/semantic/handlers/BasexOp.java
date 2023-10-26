package yaa.semantic.handlers;

import yaa.ast.Basex;
import yaa.pojos.YaaInfo;

import static yaa.pojos.GlobalData.*;

public class BasexOp {
  public static YaaInfo basex(Basex basex) {
    var x_token = basex.xToken;
    if (x_token.isFloated) {
      return float$clz;
    }
    if (x_token.isPointed) {
      return double$clz;
    }
    if (x_token.isLong) {
      return long$clz;
    }
    if (x_token.isShorted) {
      return short$clz;
    }
    if (x_token.isByte) {
      return byte$clz;
    }
    return int$clz;
  }
}
