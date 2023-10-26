package yaa.semantic.handlers;

import yaa.ast.Is;
import yaa.pojos.YaaClz;
import yaa.pojos.YaaError;
import yaa.pojos.YaaInfo;
import yaa.semantic.passes.fs6.results.IsResult;

import java.lang.annotation.ElementType;

import static yaa.pojos.GlobalData.*;

public class IsOp {
  public static YaaInfo is(Is is) {
    var left = is.e.visit(fs);
    var name = is.type.typeName.content;
    MetaCallOp.metaCalls(is.type, ElementType.TYPE_USE);
    var gottenClz = fs.getSymbol(name);
    if (gottenClz instanceof YaaClz clz) {
      if (!isSuitable4InstanceCheck(clz, is)) {
        throw new YaaError(
            is.type.placeOfUse(),
            "Only reified types can be used for instance checks"
        );
      }
      //do not check type arguments, so that checks like names => List can pass
      results.get(fs.path).put(is, new IsResult(left, clz));
      return boole$clz;
    }
    throw new YaaError(
        is.type.placeOfUse(),
        "There is no type with the name \"" + name + "\" in scope"
    );
  }

  public static boolean isSuitable4InstanceCheck(YaaClz clz, Is is) {
    if (clz.isPrimitive()) {
      return true;
    }
    if (clz.inputted.size() == 0) {
      //for plain types, name => String
      return true;
    }
    if (is.type.arguments.size() > 0) {
      if (clz.name.equals(array$name)) {
        if (isSuitable4InstanceCheck(clz.inputted.get(0), is)) {
          //for arrays of suitable types, String[], List[], or Map<?, ?>[]
          return true;
        }
      }
      throw new YaaError(
          is.type.placeOfUse(),
          "The type of an instance check cannot be generic"
      );
    } else {
      if (clz.name.equals(array$name)) {
        throw new YaaError(
            is.type.placeOfUse(), "The array used for instance check must be given its type argument"
        );
      }
      //for raw types, names => List
      return true;
    }
  }
}
