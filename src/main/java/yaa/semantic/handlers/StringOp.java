package yaa.semantic.handlers;

import yaa.ast.AstString;
import yaa.ast.Stmt;
import yaa.pojos.YaaClz;
import yaa.pojos.YaaError;
import yaa.pojos.YaaField;
import yaa.semantic.passes.fs6.results.TypeResult;
import yaa.pojos.YaaInfo;
import yaa.pojos.jMold.JMold;

import static yaa.pojos.GlobalData.fs;
import static yaa.pojos.GlobalData.results;

public class StringOp {
  public static YaaInfo string(AstString string) {
    for (var item : string.content) {
      if (item instanceof Stmt stmt$item) {
        var stmt = stmt$item.visit(fs);
        if (!(stmt instanceof YaaField || stmt instanceof YaaClz)) {
          throw new YaaError(
            stmt$item.placeOfUse(),
            stmt.toString(),
            "The expression above is illegal for string interpolation"
          );
        }
      }
    }
    var clz = new JMold().newClz("java.lang.String");
    results.get(fs.path).put(string, new TypeResult(clz));
    return clz;
  }
}
