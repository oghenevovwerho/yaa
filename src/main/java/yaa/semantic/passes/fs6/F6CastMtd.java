package yaa.semantic.passes.fs6;

import yaa.ast.Cast;
import yaa.pojos.YaaError;
import yaa.pojos.YaaMeta;
import yaa.semantic.passes.fs6.results.CastResult;
import yaa.semantic.passes.fs6.results.IsResult;
import yaa.pojos.YaaInfo;
import yaa.pojos.GlobalData;

import java.lang.annotation.RetentionPolicy;

import static org.objectweb.asm.Opcodes.CHECKCAST;
import static yaa.pojos.GlobalData.fs6;
import static yaa.semantic.passes.fs6.F6.mw;

public class F6CastMtd {
  public static YaaInfo handleCastMtd(Cast cast) {
    cast.e.visit(GlobalData.fs6);
    var result = (CastResult) GlobalData.fs6.results.get(cast);

    if (result.from.isPrimitive()) {
      F6Utils.castTo(result.from.name, result.to.name);
    } else {
      mw().visitTypeInsn(CHECKCAST, result.to.codeName);
    }
    if (cast.type.metaCalls != null) {
      for (var metaCall : cast.type.metaCalls) {
        var meta = fs6.getSymbol(metaCall.name.content);
        if (meta instanceof YaaMeta meta_type) {
          F6MetaCall.visitArguments(
              metaCall, meta_type,
              fs6.cw.peek().visitTypeAnnotation(
                  1191182336, null, meta_type.descriptor(),
                  meta_type.retention == RetentionPolicy.RUNTIME
              )
          );
        } else {
          throw new YaaError(
              metaCall.placeOfUse(), metaCall.name + " must be a valid annotation"
          );
        }
      }
    }
    return result.to;
  }

  public static void doAutoCast(IsResult result) {
    if (result.type.isPrimitive()) {
      F6Utils.castTo(result.left.name, result.type.name);
    } else {
      mw().visitTypeInsn(CHECKCAST, result.type.codeName);
    }
  }
}
