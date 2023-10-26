package yaa.semantic.passes.fs6;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import yaa.Yaa;
import yaa.ast.NewFunctionalInterface;
import yaa.ast.Parameter;
import yaa.pojos.*;

import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.objectweb.asm.Opcodes.*;
import static yaa.pojos.GlobalData.*;
import static yaa.semantic.passes.fs6.F6.mw;
import static yaa.semantic.passes.fs6.F6.variableMeta;
import static yaa.semantic.passes.fs6.F6Utils.saveClass4Writing;

public class F6FInterface {
  public static void fInterface(NewFunctionalInterface ctx) {
    var currentClz = (YaaClz) fs6.getSymbol(ctx.placeOfUse());

    fs6.push$fields();
    fs6.cw.push(new ClassWriter(ClassWriter.COMPUTE_FRAMES));

    fs6.cw.peek().visit(
        V17, clzCodeModifier(ctx),
        currentClz.codeName, generateNewClzSig(ctx),
        "java/lang/Object",
        implementedTraits(currentClz.traits.values())
    );

    //handleParameters(newFun.parameters);

    //for generic parameter annotations, e.g. Profile<@Meta T Object>{}, the @Meta
    if (ctx.typeParams.size() > 0) {
      int ref = 0;
      for (var type_param : ctx.typeParams) {
        for (var metaCall : type_param.metaCalls) {
          var meta = fs6.getSymbol(metaCall.name.content);
          if (meta instanceof YaaMeta meta_type) {
            F6MetaCall.visitArguments(
                metaCall, meta_type,
                fs6.cw.peek().visitTypeAnnotation(
                    ref, null, meta_type.descriptor(),
                    meta_type.retention == RetentionPolicy.RUNTIME
                )
            );
          } else {
            throw new YaaError(
                metaCall.placeOfUse(),
                metaCall.name.content + " must be a valid annotation"
            );
          }
        }
        //refs for type params are incremented this way
        ref = ref + 65536;
      }
    }

    fs6.cw.peek().visitSource(currentClz.codeName + ".yaa", null);

    var fun = currentClz.instanceMethods.get(currentClz.getSimpleName()).methods.get(0);
    //startAnonymousCode(fun);
    fun.startCode();
    //for parameter types with annotations, e.g. String@Ano()
    for (var param : ctx.parameters) {
      if (param.type.metaCalls.size() > 0) {
        for (var metaCall : param.type.metaCalls) {
          var meta = fs6.getSymbol(metaCall.name.content);
          if (meta instanceof YaaMeta meta_type) {
            F6MetaCall.visitArguments(
                metaCall, meta_type, mw().visitTypeAnnotation(
                    369098752, null, meta_type.descriptor(),
                    meta_type.retention == RetentionPolicy.RUNTIME
                )
            );
          } else {
            throw new YaaError(
                metaCall.placeOfUse(),
                metaCall.name.content + " must be a valid annotation"
            );
          }
        }
      }
    }

    if (ctx.type != null && ctx.type.metaCalls.size() != 0) {
      for (var metaCall : ctx.type.metaCalls) {
        var meta = fs6.getSymbol(metaCall.name.content);
        if (meta instanceof YaaMeta meta_type) {
          F6MetaCall.visitArguments(metaCall, meta_type,
              mw().visitTypeAnnotation(
                  335544320, null, meta_type.descriptor(),
                  meta_type.retention == RetentionPolicy.RUNTIME
              ));
        } else {
          throw new YaaError(
              metaCall.placeOfUse(),
              metaCall.name.content + " must be a valid annotation"
          );
        }
      }
    }

    var any_param_has_annotation = false;

    for (var param : ctx.parameters) {
      if (param.type.metaCalls.size() > 0) {
        any_param_has_annotation = true;
        break;
      }
    }

    if (any_param_has_annotation) {
      mw().visitAnnotableParameterCount(
          ctx.parameters.size() + fun.closures.size(), false
      );
    }

    mw().visitCode();
    fun.initParam(ctx.parameters);
    if (ctx.type != null) {
      ctx.retStmt.visit(fs6);
    }
    fun.closeCode();

    fs6.pop$fields();
    fs6.cw.peek().visitEnd();
    saveClass4Writing(currentClz.codeName);
    fs6.cw.pop();
  }

  private static void startAnonymousCode(YaaFun fun) {
    F6.f6TopMtd.push(fun);
    fs6.push$variables();
    variableMeta.push(new ArrayList<>());
    F6.mtdWriters.push(fs6.cw.peek().visitMethod(
        ACC_PUBLIC + ACC_ABSTRACT,
        fun.name, fun.descriptor(),
        fun.signature(), new String[]{}
    ));
  }

  private static String generateNewClzSig(NewFunctionalInterface newFun) {
    if (newFun.typeParams.size() == 0) {
      return null;
    }
    var sb = new StringBuilder();
    sb.append("<");
    for (int i = 0; i < newFun.typeParams.size(); i++) {
      var param = newFun.typeParams.get(i).paramName.content;
      sb.append(param).append(":")
          .append(((YaaClz) fs6.getSymbol(param)).parent.descriptor());
    }
    sb.append(">").append("Ljava/lang/Object;");
    return sb.toString();
  }

  protected static int clzCodeModifier(NewFunctionalInterface newFun) {
    var clz$modifier = ACC_PUBLIC;
    if (newFun.options.containsKey("final")) {
      clz$modifier = clz$modifier + ACC_FINAL;
    }
    clz$modifier = clz$modifier + ACC_ABSTRACT + ACC_INTERFACE;
    return clz$modifier;
  }

  protected static String[] implementedTraits(Collection<YaaClz> traits) {
    var trait_array = new String[traits.size()];
    int i = 0;
    for (var trait : traits) {
      trait_array[i] = trait.codeName;
      i++;
    }
    return trait_array;
  }

  private static void handleParameters(List<Parameter> parameters) {
    for (var param : parameters) {
      var variables = fs6.variables.peek();
      var param$name = param.name.content;
      var param$field = (YaaField) fs.getSymbol(param$name);
      var data = param$field.data;
      switch (data.name) {
        case long$name, GlobalData.double$name -> {
          variables.putWideVar(param$name);
        }
        default -> variables.putVar(param$name);
      }
      var label = new Label();
      mw().visitLabel(label);
      mw().visitLineNumber(param.start.line, label);
      var index = variables.index;
      F6.variableMeta.peek().add(new VariableData(
          param$name, label, data.descriptor(),
          data.clzUseSignature(), index, new ArrayList<>(0), new ArrayList<>(0)
      ));
    }
  }
}