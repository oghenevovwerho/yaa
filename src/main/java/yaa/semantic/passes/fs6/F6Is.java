package yaa.semantic.passes.fs6;

import yaa.ast.Is;
import yaa.pojos.YaaMeta;
import yaa.semantic.passes.fs6.results.IsResult;
import yaa.pojos.YaaInfo;
import yaa.pojos.GlobalData;

import java.lang.annotation.RetentionPolicy;

import static org.objectweb.asm.Opcodes.*;
import static yaa.pojos.GlobalData.fs6;

public class F6Is {
  public static YaaInfo is(Is is, IsResult result, boolean shouldVisit) {
    var argument = result.left;
    var firstIn = result.type;
    if (argument.isPrimitive() || firstIn.isPrimitive()) {
      if (firstIn.name.equals(argument.name)) {
        F6.mw().visitInsn(ICONST_1);
      } else {
        F6.mw().visitInsn(ICONST_0);
      }
    } else {
      if (shouldVisit) {
        is.e.visit(GlobalData.fs6);
      }
      F6.mw().visitTypeInsn(INSTANCEOF, firstIn.codeName);
    }
    if (is.type.metaCalls != null) {
      for (var metaCall : is.type.metaCalls) {
        var meta_type = (YaaMeta) fs6.getSymbol(metaCall.name.content);
        F6MetaCall.visitArguments(
            metaCall, meta_type,
            fs6.cw.peek().visitTypeAnnotation(
                1124073472, null, meta_type.descriptor(),
                meta_type.retention == RetentionPolicy.RUNTIME
            )
        );
      }
    }
    return GlobalData.boole$clz;
  }
}