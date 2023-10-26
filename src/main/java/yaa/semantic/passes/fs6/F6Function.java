package yaa.semantic.passes.fs6;

import yaa.ast.NewFun;
import yaa.pojos.YaaClz;
import yaa.pojos.YaaFun;
import yaa.pojos.YaaInfo;
import org.objectweb.asm.ClassWriter;

import java.util.ArrayList;

import static yaa.pojos.GlobalData.*;

public class F6Function {
  public static YaaInfo function(NewFun newFun, ClassWriter tcw) {
    fs6.pushTable(newFun);
    var new$fn = (YaaFun) fs6.getSymbol(newFun.placeOfUse());
    F6.f6TopMtd.push(new$fn);
    fs6.push$variables();
    F6.variableMeta.push(new ArrayList<>(newFun.parameters.size()));
    F6.mtdWriters.push(tcw.visitMethod(
        new$fn.mtdCodeModifier(),
        new$fn.name, descriptor(new$fn),
        null, new String[]{}
    ));
    F6.mw().visitCode();
    fs6.variables.peek().index = -1;
    new$fn.initParam(newFun.parameters);
    newFun.stmt.visit(fs6);
    new$fn.closeCode();
    fs6.popTable();
    return nothing;
  }

  public static String descriptor(YaaFun fun) {
    String return$type = fun.type.descriptor();
    if (fun.type.isUnboundedAndNotPrimitive()) {
      return$type = ((YaaClz) fun.type).parent.descriptor();
    }
    var sb = new StringBuilder();
    sb.append("(");
    for (var field : fun.closures.values()) {
      sb.append(field.descriptor());
    }
    for (var parameter : fun.parameters) {
      if (parameter.isUnboundedAndNotPrimitive()) {
        sb.append(((YaaClz) parameter).parent.descriptor());
      } else {
        sb.append(parameter.descriptor());
      }
    }
    return sb.append(")").append(return$type).toString();
  }
}