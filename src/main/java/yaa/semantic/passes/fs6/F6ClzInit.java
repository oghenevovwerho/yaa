package yaa.semantic.passes.fs6;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.TypePath;
import yaa.ast.VCall;
import yaa.pojos.*;
import yaa.pojos.jMold.JMold;
import yaa.semantic.passes.fs6.f6utils.InternalMetaGen;
import yaa.semantic.passes.fs6.results.InitResult;

import java.lang.annotation.RetentionPolicy;

import static org.objectweb.asm.Opcodes.*;
import static yaa.pojos.GlobalData.fs6;
import static yaa.pojos.GlobalData.null$name;
import static yaa.semantic.passes.fs6.F6.mw;

public class F6ClzInit {
  public static YaaInfo init(InitResult result, VCall call) {
    var yaaClz = result.clz;
    if (yaaClz.name.equals(GlobalData.array$name)) {
      return handleArrayInit(result, call);
    } else if (yaaClz.name.equals(null$name)) {
      mw().visitInsn(ACONST_NULL);
      return yaaClz;
    }

    mw().visitTypeInsn(Opcodes.NEW, yaaClz.codeName);

    if (call.metaCalls.size() > 0) {
      for (var meta_call : call.metaCalls) {
        var meta_type = fs6.getSymbol(meta_call.name.content);
        if (meta_type instanceof YaaMeta meta) {
          F6MetaCall.visitArguments(
              meta_call, meta,
              mw().visitInsnAnnotation(MetaReference.TYPE_CALL,
                  null, meta.descriptor(),
                  meta.retention == RetentionPolicy.RUNTIME
              )
          );
        } else {
          throw new YaaError(
              meta_call.placeOfUse(), meta_call.name.content + " must be a valid annotation"
          );
        }
      }
    }

    //e.g. Profile<String@Ano()>(), the @Ano()
    InternalMetaGen.nestedTypeArgs(call.types, (fromString, meta, typeArg, metaCall) -> {
      F6MetaCall.visitArguments(
          metaCall, meta,
          mw().visitInsnAnnotation(
              MetaReference.TYPE_CALL_INTERNAL_META_REF,
              TypePath.fromString(fromString),
              meta.descriptor(), meta.retention == RetentionPolicy.RUNTIME
          ));
    });

    mw().visitInsn(DUP);

    F6Utils.runArguments(result.init.callInfo.parameters, call.arguments);
    mw().visitMethodInsn(
        INVOKESPECIAL,
        yaaClz.codeName,
        "<init>",
        result.init.callInfo.descriptor(),
        false
    );

    return yaaClz;
  }

  private static YaaInfo handleArrayInit(InitResult result, VCall call) {
    if (call.types.size() == 0) {
      return F6Array.newArray(call);
    }
    var array = result.clz;
    if (call.arguments.size() == 0) {
      mw().visitInsn(ICONST_0);
    } else {
      call.arguments.get(0).visit(fs6);
    }
    var type_argument = array.inputted.get(0);
    switch (type_argument.name) {
      case GlobalData.int$name -> {
        mw().visitIntInsn(NEWARRAY, T_INT);
      }
      case GlobalData.float$name -> {
        mw().visitIntInsn(NEWARRAY, T_FLOAT);
      }
      case GlobalData.double$name -> {
        mw().visitIntInsn(NEWARRAY, T_DOUBLE);
      }
      case GlobalData.short$name -> {
        mw().visitIntInsn(NEWARRAY, T_SHORT);
      }
      case GlobalData.byte$name -> {
        mw().visitIntInsn(NEWARRAY, T_BYTE);
      }
      case GlobalData.char$name -> {
        mw().visitIntInsn(NEWARRAY, T_CHAR);
      }
      case GlobalData.boole$name -> {
        mw().visitIntInsn(NEWARRAY, T_BOOLEAN);
      }
      case GlobalData.long$name -> {
        mw().visitIntInsn(NEWARRAY, T_LONG);
      }
      default -> {
        if (type_argument.name.equals(GlobalData.array$name)) {
          mw().visitTypeInsn(ANEWARRAY, type_argument.descriptor());
        } else {
          mw().visitTypeInsn(ANEWARRAY, type_argument.codeName);
        }
      }
    }
    return array;
  }
}
