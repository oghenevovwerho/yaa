package yaa.semantic.passes.fs5;

import yaa.ast.*;
import yaa.pojos.*;
import yaa.semantic.passes.fs6.results.FieldResult;

import static yaa.pojos.GlobalData.fs5;

public class F5EAssign {
  public static void f5EAssign(Assign assign) {
    var e1 = assign.e1;
    if (e1 instanceof VGet vGet) {
      handleVGetAssign(assign, vGet);
    } else if (e1 instanceof EGet eGet) {
      handleEGetAssign(assign, eGet);
    } else if (e1 instanceof Name name) {
      F5VAssign.f5VAssign(assign, name.token.content);
    } else if (e1 instanceof SuperField superField) {
      var rName = superField.name.content;
      var top$clz = GlobalData.topClz.peek();
      if (top$clz == null) {
        throw new YaaError(
            assign.placeOfUse(),
            "A parent can only be referenced in a type"
        );
      }
      var clz = top$clz.parent;
      if (clz == null) {
        throw new YaaError(
            assign.placeOfUse(), GlobalData.topClz.peek() + " has no parent"
        );
      }
      var instantField = clz.getInstantField(rName);
      if (instantField == null) {
        throw new YaaError(
            superField.name.placeOfUse(),
            clz.toString(), "The type above does not define " +
            "any fields with the name \"" + rName + "\""
        );
      }
      compareValues(instantField, assign);
      var result = new FieldResult(instantField);
      //result.itParentAccess = true;
      GlobalData.results.get(GlobalData.fs.path).put(assign, result);
    } else if (e1 instanceof ThisField thisField) {
      var rName = thisField.name.content;
      var top$clz = GlobalData.topClz.peek();
      if (top$clz == null) {
        throw new YaaError(
            assign.placeOfUse(),
            "A parent can only be referenced in a type"
        );
      }
      var instantField = top$clz.getInstantField(rName);
      if (instantField == null) {
        throw new YaaError(
            thisField.name.placeOfUse(),
            top$clz.toString(), "The type above does not define " +
            "any fields with the name \"" + rName + "\""
        );
      }
      compareValues(instantField, assign);
      var result = new FieldResult(instantField);
      //result.itParentAccess = true;
      GlobalData.results.get(GlobalData.fs.path).put(assign, result);
    } else {
      throw new YaaError(
          assign.placeOfUse(), assign.toString(),
          "The expression below is not valid as an assignment target",
          assign.e1.toString()
      );
    }
  }

  private static void handleEGetAssign(Assign assign, EGet eGet) {
    var rName = eGet.name.content;
    var info = eGet.e.visit(GlobalData.fs);
    if (info instanceof YaaClz clz) {
      if (clz.category == TypeCategory.enum_c) {
        throw new YaaError(
            eGet.placeOfUse(),
            clz.toString(), "The fields of an enum are not assignable"
        );
      }
      if (clz.category != TypeCategory.trait_c) {
        var instantField = clz.getInstantField(rName);
        if (instantField == null) {
          throw new YaaError(
              eGet.placeOfUse(),
              clz.toString(), "The type above does not define " +
              "any field with the name \"" + rName + "\""
          );
        }
        compareValues(instantField, assign);
        var result = new FieldResult(instantField);
        GlobalData.results.get(GlobalData.fs.path).put(assign, result);
      }
    } else if (info instanceof YaaField field) {
      if (field.data instanceof YaaClz clz) {
        var instantField = clz.getInstantField(rName);
        if (instantField == null) {
          throw new YaaError(
              eGet.placeOfUse(),
              clz.toString(), "The type above does not define " +
              "any fields with the name \"" + rName + "\""
          );
        }
        if (instantField.itIsFinal) {
          throw new YaaError(
              eGet.name.placeOfUse(), rName + " is final in " + instantField.owner
          );
        }
        compareValues(instantField, assign);
        var result = new FieldResult(instantField);
        GlobalData.results.get(GlobalData.fs.path).put(assign, result);
      }
    } else {
      throw new YaaError(
          eGet.placeOfUse(), info.toString(),
          "The symbol above is not dot accessible"
      );
    }
  }

  private static void handleVGetAssign(Assign assign, VGet vGet) {
    var rName = vGet.n2.content;
    var lName = vGet.n1.content;
    var info = GlobalData.fs.getSymbol(lName);
    if (info == null) {
      throw new YaaError(
          vGet.placeOfUse(),
          "There is no symbol in scope with the name \"" + lName + "\""
      );
    }
    if (info instanceof YaaClz clz) {
      var staticField = clz.getStaticField(rName);
      if (staticField == null) {
        throw new YaaError(
            vGet.placeOfUse(),
            clz.toString(), "The type above does not define " +
            "any field with the name \"" + rName + "\""
        );
      }
      if (staticField.itIsFinal) {
        throw new YaaError(
            vGet.n2.placeOfUse(), rName + " is final in " + staticField.owner
        );
      }
      compareValues(staticField, assign);
      var result = new FieldResult(staticField);
      GlobalData.results.get(GlobalData.fs.path).put(assign, result);
    } else if (info instanceof YaaField field) {
      if (field.data instanceof YaaClz clz) {
        var instantField = clz.getInstantField(rName);
        if (instantField == null) {
          throw new YaaError(
              vGet.placeOfUse(),
              clz.toString(), "The type above does not define " +
              "any fields with the name \"" + rName + "\""
          );
        }
        if (instantField.itIsFinal) {
          throw new YaaError(
              vGet.n2.placeOfUse(), rName + " is final in " + instantField.owner
          );
        }
        compareValues(instantField, assign);
        var result = new FieldResult(instantField);
        GlobalData.results.get(GlobalData.fs.path).put(assign, result);
      }
    } else {
      throw new YaaError(
          vGet.placeOfUse(), info.toString(),
          "The symbol above is not dot accessible"
      );
    }
  }

  private static void compareValues(YaaField field, Assign assign) {
    var old_data = field.data;
    var newValue = assign.e2.visit(fs5);
    if (field.data.accepts(newValue)) {
      field.data = newValue;
      return;
    }
    if (old_data instanceof YaaClz l && newValue instanceof YaaClz r) {
      if (l.isParentOf(r)) {
        field.data = newValue;
        return;
      }
    }
    throw new YaaError(
        assign.placeOfUse(), old_data.toString(),
        "You can not assign the type below to the type above",
        newValue.toString()
    );
  }
}
