package yaa.semantic.passes.fs6;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.TypePath;
import yaa.ast.NewClass;
import yaa.ast.Parameter;
import yaa.ast.VDeclaration;
import yaa.ast.VDefinition;
import yaa.pojos.*;
import yaa.semantic.passes.fs6.f6utils.InternalMetaGen;

import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.objectweb.asm.Opcodes.*;
import static yaa.pojos.GlobalData.*;
import static yaa.semantic.passes.fs6.F6.mw;
import static yaa.semantic.passes.fs6.F6Utils.saveClass4Writing;

public class F6NClass {
  public static void new$class(NewClass newClass) {
    fs6.pushTable(newClass);
    var clz = (YaaClz) fs6.getSymbol(newClass.placeOfUse());
    GlobalData.topClz.push(clz);

    fs6.push$fields();
    fs6.cw.push(new ClassWriter(ClassWriter.COMPUTE_FRAMES));

    if (clz.category == TypeCategory.trait_c) {
      //all interfaces extend Object, their real parent is given in implementedTrait
      fs6.cw.peek().visit(
          V17, clzCodeModifier(newClass, clz),
          clz.codeName, generateInterfaceSig(clz.traits.values()),
          "java/lang/Object",
          clz.traits.size() == 0 ? null : implemented(clz.traits.values())
      );
    } else {
      if (clz.parent == null) {
        fs6.cw.peek().visit(
            V17, clzCodeModifier(newClass, clz),
            clz.codeName, generateNewClzSig(newClass),
            "java/lang/Object",
            implemented(clz.traits.values())
        );
      } else {
        fs6.cw.peek().visit(
            V17, clzCodeModifier(newClass, clz),
            clz.codeName, generateNewClzSig(clz, newClass),
            clz.parent.codeName,
            implemented(clz.traits.values())
        );
      }
    }

    //for generic parameter annotations, e.g. Profile<@Meta T Object>{}, the @Meta
    if (newClass.typeParams.size() > 0) {
      int ref = 0;
      for (var type_param : newClass.typeParams) {
        for (var metaCall : type_param.metaCalls) {
          var meta_type = (YaaMeta) fs6.getSymbol(metaCall.name.content);
          F6MetaCall.visitArguments(metaCall, meta_type,
              fs6.cw.peek().visitTypeAnnotation(
                  ref, null, meta_type.descriptor(),
                  meta_type.retention == RetentionPolicy.RUNTIME
              ));
        }
        //refs for type params are incremented this way
        ref = ref + 65536;
      }
    }

    if (newClass.parent != null) {
      for (var metaCall : newClass.parent.metaCalls) {
        var meta_type = (YaaMeta) fs6.getSymbol(metaCall.name.content);
        F6MetaCall.visitArguments(
            metaCall, meta_type,
            fs6.cw.peek().visitTypeAnnotation(
                285212416, null, meta_type.descriptor(),
                meta_type.retention == RetentionPolicy.RUNTIME)
        );
      }

      //The type arguments for each parent
      if (newClass.parent.hasInternalMeta) {
        InternalMetaGen.nestedTypeArgs(
            newClass.parent.arguments, (fromString, meta, typeArg, metaCall) -> {
              F6MetaCall.visitArguments(
                  metaCall, meta,
                  fs6.cw.peek().visitTypeAnnotation(
                      MetaReference.PARENT_TYPE_INTERNAL_META_REF,
                      TypePath.fromString(fromString),
                      meta.descriptor(),
                      meta.retention == RetentionPolicy.RUNTIME
                  )
              );
            }
        );
      }
    }

    //for the annotations attached to the extends and implements
    //e.g. extends @Anno1 BaseClass implements @Anno2 Contract.
    for (var implementation : newClass.implementations) {
      if (implementation.type.metaCalls.size() > 0) {
        for (var metaCall : implementation.type.metaCalls) {
          var meta_type = (YaaMeta) fs6.getSymbol(metaCall.name.content);
          F6MetaCall.visitArguments(metaCall, meta_type,
              fs6.cw.peek().visitTypeAnnotation(
                  268435456, null, meta_type.descriptor(),
                  meta_type.retention == RetentionPolicy.RUNTIME
              )
          );
        }
      }

      //The type arguments for each implementation
      if (implementation.type.hasInternalMeta) {
        InternalMetaGen.nestedTypeArgs(
            implementation.type.arguments, (fromString, meta, typeArg, metaCall) -> {
              F6MetaCall.visitArguments(
                  metaCall, meta,
                  fs6.cw.peek().visitTypeAnnotation(
                      MetaReference.PARENT_TYPE_INTERNAL_META_REF,
                      TypePath.fromString(fromString),
                      meta.descriptor(),
                      meta.retention == RetentionPolicy.RUNTIME
                  )
              );
            }
        );
      }
    }

    if (newClass.metaCalls != null) {
      for (var metaCall : newClass.metaCalls) {
        var meta = (YaaMeta) fs6.getSymbol(metaCall.name.content);
        if (meta.name.equals(configMetaClzName)) {
          continue;
        }
        F6MetaCall.visitArguments(
            metaCall, meta,
            fs6.cw.peek().visitAnnotation(
                meta.descriptor(),
                meta.retention == RetentionPolicy.RUNTIME
            )
        );
      }
    }

    for (var v$dec : newClass.vDeclarations) {
      var f$name = v$dec.name.content;
      var field = (YaaField) fs6.getSymbol(f$name);
      fs6.clz$fields.peek().put(field);
      var field_visitor = fs6.cw.peek().visitField(
          fieldMod(v$dec), f$name,
          field.descriptor(),
          field.clzUseSignature(),
          null
      );
      for (var metaCall : v$dec.type.metaCalls) {
        var meta = (YaaMeta) fs6.getSymbol(metaCall.name.content);
        F6MetaCall.visitArguments(
            metaCall, meta,
            field_visitor.visitTypeAnnotation(
                318767104,
                null,
                meta.descriptor(),
                meta.retention == RetentionPolicy.RUNTIME
            ));
      }
      InternalMetaGen.nestedTypeArgs(v$dec.type.arguments, (fromString, meta, typeArg, metaCall) -> {
        F6MetaCall.visitArguments(
            metaCall, meta,
            field_visitor.visitTypeAnnotation(
                MetaReference.FIELD_INTERNAL_META_REF,
                TypePath.fromString(fromString),
                meta.descriptor(),
                meta.retention == RetentionPolicy.RUNTIME
            )
        );
      });
      field_visitor.visitEnd();
    }

    for (var v$def : newClass.vDefinitions) {
      var f$name = v$def.name.content;
      var field = (YaaField) fs6.getSymbol(f$name);
      fs6.clz$fields.peek().put(field);
      var field_visitor = fs6.cw.peek().visitField(
          fieldMod(v$def), f$name,
          field.descriptor(),
          field.clzUseSignature(),
          null
      );
      if (v$def.type != null) {
        for (var metaCall : v$def.type.metaCalls) {
          var meta = (YaaMeta) fs6.getSymbol(metaCall.name.content);
          F6MetaCall.visitArguments(
              metaCall, meta,
              field_visitor.visitTypeAnnotation(
                  318767104,
                  null,
                  meta.descriptor(),
                  meta.retention == RetentionPolicy.RUNTIME
              ));
        }
        InternalMetaGen.nestedTypeArgs(v$def.type.arguments, (fromString, meta, typeArg, metaCall) -> {
          F6MetaCall.visitArguments(
              metaCall, meta,
              field_visitor.visitTypeAnnotation(
                  MetaReference.FIELD_INTERNAL_META_REF,
                  TypePath.fromString(fromString),
                  meta.descriptor(),
                  meta.retention == RetentionPolicy.RUNTIME
              )
          );
        });
      }
      field_visitor.visitEnd();
    }

    if (clz.category != TypeCategory.trait_c) {
      if (newClass.init != null) {
        makeInit(newClass, clz);
        if (newClass.init.parameters.size() != 0) {
          //this is for JVM tools that require an empty initializer
          //this constructor does not initialize the class fields
          makePhantomInit(newClass, clz);
        }
      } else {
        makeStubInit(newClass, clz);
      }
    }

    fs6.cw.peek().visitSource(clz.codeName + ".yaa", null);

    for (var mtd_list : newClass.parentMtds.values()) {
      for (var mtd : mtd_list) {
        fs6.pushTable(mtd);
        var fun = (YaaFun) fs6.getSymbol(mtd.placeOfUse());
        fun.startCode(clz.decInfoMap.get(mtd.placeOfUse()));
        fun.initParam(mtd.parameters);
        mw().visitAnnotation("Ljava/lang/Override;", false);
        mtd.stmt.visit(fs6);
        fun.closeCode();
        fs6.popTable();
      }
    }

    for (var block : newClass.implementations) {
      fs6.pushTable(block);
      for (var mtd$pack : block.methods.values()) {
        for (var mtd : mtd$pack) {
          fs6.pushTable(mtd);
          var fun = (YaaFun) fs6.getSymbol(mtd.placeOfUse());
          fun.startCode(clz.decInfoMap.get(mtd.placeOfUse()));
          fun.initParam(mtd.parameters);
          mw().visitAnnotation("Ljava/lang/Override;", false);
          mtd.stmt.visit(fs6);
          fun.closeCode();
          fs6.popTable();
        }
      }
      fs6.popTable();
    }

    for (var mtd : newClass.methods) {
      fs6.pushTable(mtd);
      var fun = (YaaFun) fs6.getSymbol(mtd.placeOfUse());
      fun.startCode();

      int param_index = 0;
      for (var param : mtd.parameters) {
        if (param.type.metaCalls.size() > 0) {
          for (var metaCall : param.type.metaCalls) {
            var meta_type = (YaaMeta) fs6.getSymbol(metaCall.name.content);
            F6MetaCall.visitArguments(
                metaCall, meta_type,
                mw().visitTypeAnnotation(
                    369098752, null,
                    meta_type.descriptor(),
                    meta_type.retention == RetentionPolicy.RUNTIME
                ));
          }
        }

        for (var metaCall : param.type.metaCalls) {
          var meta = (YaaMeta) fs6.getSymbol(metaCall.name.content);
          F6MetaCall.visitArguments(metaCall, meta,
              mw().visitParameterAnnotation(
                  fun.closures.size() + param_index++,
                  meta.descriptor(),
                  meta.retention == RetentionPolicy.RUNTIME
              )
          );
        }
      }

      if (mtd.type != null && mtd.type.metaCalls.size() != 0) {
        for (var metaCall : mtd.type.metaCalls) {
          var meta_type = (YaaMeta) fs6.getSymbol(metaCall.name.content);
          F6MetaCall.visitArguments(
              metaCall, meta_type,
              mw().visitTypeAnnotation(
                  335544320, null, meta_type.descriptor(),
                  meta_type.retention == RetentionPolicy.RUNTIME
              )
          );
        }
      }

      var any_param_has_annotation = false;

      for (var param : mtd.parameters) {
        if (param.type.metaCalls.size() > 0) {
          any_param_has_annotation = true;
          break;
        }
      }

      if (any_param_has_annotation) {
        mw().visitAnnotableParameterCount(
            mtd.parameters.size() + fun.closures.size(), false
        );
      }

      for (var metaCall : mtd.metaCalls) {
        var meta = (YaaMeta) fs6.getSymbol(metaCall.name.content);
        if (meta.name.equals(configMetaClzName)) {
          continue;
        }
        F6MetaCall.visitArguments(
            metaCall, meta,
            mw().visitAnnotation(
                meta.descriptor(),
                meta.retention == RetentionPolicy.RUNTIME
            )
        );
      }

      mw().visitCode();
      fun.initParam(mtd.parameters);
      mtd.stmt.visit(fs6);
      fun.closeCode();
      fs6.popTable();
    }

    for (var inner$clz : newClass.classes) {
      inner$clz.visit(fs6);
    }

    for (var fInterface : newClass.fInterfaces) {
      fInterface.visit(fs6);
    }

    for (var inner$enum : newClass.enums) {
      inner$enum.visit(fs6);
    }

    if (newClass.runBlocks.size() > 0) {
      F6.mtdWriters.push(fs6.cw.peek().visitMethod(
          ACC_STATIC, "<clinit>", "()V", null, null
      ));
      fs6.push$variables();
      mw().visitCode();
      fs6.variables.peek().index = -1;
      for (var run_block : newClass.runBlocks) {
        fs6.pushTable(run_block);
        for (var stmt : run_block.stmts) {
          stmt.visit(fs6);
        }
        fs6.popTable();
      }
      mw().visitInsn(RETURN);
      fs6.pop$variables();
      mw().visitMaxs(0, 0);
      mw().visitEnd();
      F6.mtdWriters.pop();
    }

    fs6.pop$fields();
    fs6.cw.peek().visitEnd();
    saveClass4Writing(clz.codeName);
    fs6.cw.pop();
    GlobalData.topClz.pop();
    fs6.popTable();
  }

  private static String generateInterfaceSig(Collection<YaaClz> traits) {
    var sb = new StringBuilder();
    sb.append("Ljava/lang/Object;");
    for (var imp : traits) {
      sb.append(imp.clzUseSignature());
    }
    return sb.toString();
  }

  private static String generateNewClzSig(YaaClz clz, NewClass newClass) {
    if (newClass.typeParams.size() == 0) {
      return null;
    }
    var sb = new StringBuilder();
    sb.append("<");
    for (int i = 0; i < newClass.typeParams.size(); i++) {
      var param = newClass.typeParams.get(i).paramName.content;
      sb.append(param).append(":")
          .append(((YaaClz) fs6.getSymbol(param)).parent.descriptor());
    }
    sb.append(">").append(clz.parent.descriptor());
    return sb.toString();
  }

  private static String generateNewClzSig(NewClass newClass) {
    if (newClass.typeParams.size() == 0) {
      return null;
    }
    var sb = new StringBuilder();
    sb.append("<");
    for (int i = 0; i < newClass.typeParams.size(); i++) {
      var param = newClass.typeParams.get(i).paramName.content;
      sb.append(param).append(":")
          .append(((YaaClz) fs6.getSymbol(param)).parent.descriptor());
    }
    sb.append(">").append("Ljava/lang/Object;");
    return sb.toString();
  }

  protected static int clzCodeModifier(NewClass newClass, YaaClz clz) {
    var privacy = clz.privacy;
    var clz$modifier = ACC_PUBLIC;
    if (newClass.itIsTopLevelClz) {
      //inner classes are enforced to be private by the compiler
      //,but they are declared public for code generation
      if (privacy == 1) {
        clz$modifier = ACC_PROTECTED;
      }
      if (privacy == 2) {
        clz$modifier = ACC_PRIVATE;
      }
    }
    if (clz.isFinal) {
      clz$modifier = clz$modifier + ACC_FINAL;
    }
    if (clz.category == TypeCategory.trait_c) {
      clz$modifier = clz$modifier + ACC_ABSTRACT + ACC_INTERFACE;
    }
    return clz$modifier;
  }

  protected static int fieldMod(VDefinition v$def) {
    var privacy = v$def.privacy();
    var field$modifier = ACC_PUBLIC;
    if (privacy == 1) {
      field$modifier = ACC_PROTECTED;
    }
    if (privacy == 2) {
      field$modifier = ACC_PRIVATE;
    }
    if (v$def.options.containsKey("final")) {
      field$modifier = field$modifier + ACC_FINAL;
    }
    if (v$def.options.containsKey("transient")) {
      field$modifier = field$modifier + ACC_TRANSIENT;
    }
    return field$modifier;
  }

  protected static int fieldMod(VDeclaration v$def) {
    var privacy = v$def.privacy();
    var field$modifier = ACC_PUBLIC;
    if (privacy == 1) {
      field$modifier = ACC_PROTECTED;
    }
    if (privacy == 2) {
      field$modifier = ACC_PRIVATE;
    }
    if (v$def.options.containsKey("final")) {
      field$modifier = field$modifier + ACC_FINAL;
    }
    if (v$def.options.containsKey("transient")) {
      field$modifier = field$modifier + ACC_TRANSIENT;
    }
    return field$modifier;
  }

  protected static String[] implemented(Collection<YaaClz> traits) {
    var trait_array = new String[traits.size()];
    int i = 0;
    for (var trait : traits) {
      trait_array[i] = trait.codeName;
      i++;
    }
    return trait_array;
  }

  private static void makePhantomInit(NewClass nc, YaaClz clz) {
    fs6.push$variables();

    F6.mtdWriters.push(fs6.cw.peek().visitMethod(
        ACC_PUBLIC, "<init>",
        "()V",
        null, new String[]{}
    ));

    mw().visitCode();

    mw().visitVarInsn(ALOAD, 0);
    if (nc.parent == null) {
      mw().visitMethodInsn(
          INVOKESPECIAL,
          "java/lang/Object",
          "<init>",
          "()V",
          false
      );
    } else {
      var parentStubInit = clz.parent.inits.get(0);
      //also check if the parent has an init with zero arguments
      //if it does, use that one. Otherwise, continue with the first init
      for (int i = 0; i < clz.parent.inits.size(); i++) {
        var initHasZeroArgs = clz.parent.inits.get(i).parameters.size() == 0;
        if (initHasZeroArgs) {
          parentStubInit = clz.parent.inits.get(i);
          break;
        }
      }
      var parentCallArgs = parentStubInit.raw_parameters;
      if (parentCallArgs == null) {
        parentCallArgs = parentStubInit.parameters;
      }
      for (var arg : parentCallArgs) {
        F6Utils.loadDefaultData(arg.name);
      }
      mw().visitMethodInsn(
          INVOKESPECIAL,
          clz.parent.codeName,
          "<init>",
          parentStubInit.descriptor(),
          false
      );
    }

    mw().visitInsn(RETURN);
    mw().visitMaxs(0, 0);
    mw().visitEnd();
    F6.mtdWriters.pop();
    fs6.pop$variables();
  }

  private static void makeStubInit(NewClass nc, YaaClz clz) {
    fs6.push$variables();

    F6.mtdWriters.push(fs6.cw.peek().visitMethod(
        ACC_PUBLIC, "<init>",
        "()V",
        null, new String[]{}
    ));

    mw().visitCode();

    mw().visitVarInsn(ALOAD, 0);
    if (nc.parent == null) {
      mw().visitMethodInsn(
          INVOKESPECIAL,
          "java/lang/Object",
          "<init>",
          "()V",
          false
      );
    } else {
      var parentStubInit = clz.parent.inits.get(0);
      //also check if the parent has an init with zero arguments
      //if it does, use that one. Otherwise, continue with the first init
      for (int i = 0; i < clz.parent.inits.size(); i++) {
        var initHasZeroArgs = clz.parent.inits.get(i).parameters.size() == 0;
        if (initHasZeroArgs) {
          parentStubInit = clz.parent.inits.get(i);
          break;
        }
      }
      var parentCallArgs = parentStubInit.raw_parameters;
      if (parentCallArgs == null) {
        parentCallArgs = parentStubInit.parameters;
      }
      for (var arg : parentCallArgs) {
        F6Utils.loadDefaultData(arg.name);
      }
      mw().visitMethodInsn(
          INVOKESPECIAL,
          clz.parent.codeName,
          "<init>",
          parentStubInit.descriptor(),
          false
      );
    }

    initializeDefFields(nc.vDefinitions, clz);
    initializeDecFields(nc.vDeclarations, clz);

    mw().visitInsn(RETURN);
    mw().visitMaxs(0, 0);
    mw().visitEnd();
    F6.mtdWriters.pop();
    fs6.pop$variables();
  }

  public static void makeInit(NewClass nc, YaaClz clz) {
    fs6.pushTable(nc.init);
    F6.variableMeta.push(new ArrayList<>());
    fs6.push$variables();

    F6.mtdWriters.push(fs6.cw.peek().visitMethod(
        ACC_PUBLIC, "<init>",
        clz.inits.get(0).descriptor(),
        null, new String[]{}
    ));

    mw().visitCode();
    handleParameters(nc.init.parameters);

    mw().visitVarInsn(ALOAD, 0);
    if (nc.parent == null) {
      mw().visitMethodInsn(
          INVOKESPECIAL,
          "java/lang/Object",
          "<init>",
          "()V",
          false
      );
    } else {
      var parentCallArgs = nc.init.parentCall.arguments;
      for (var arg : parentCallArgs) {
        arg.visit(fs6);
      }
      mw().visitMethodInsn(
          INVOKESPECIAL,
          clz.parent.codeName,
          "<init>",
          nc.parentCallDescriptor,
          false
      );
    }

    //this must come before the statement body is executed
    initializeDefFields(nc.vDefinitions, clz);
    initializeDecFields(nc.vDeclarations, clz);

    if (nc.init != null) {
      nc.init.stmt.visit(fs6);
    }

    mw().visitInsn(RETURN);
    mw().visitMaxs(0, 0);
    mw().visitEnd();
    F6.mtdWriters.pop();
    fs6.pop$variables();
    fs6.popTable();
    F6.variableMeta.pop();
  }

  private static void handleParameters(List<Parameter> parameters) {
    for (var param : parameters) {
      var variables = fs6.variables.peek();
      var param$name = param.name.content;
      var param$field = (YaaField) fs.getSymbol(param$name);
      var data = param$field.data;
      switch (data.name) {
        case long$name, double$name -> {
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

  private static void initializeDefFields(List<VDefinition> def$fields, YaaClz clz) {
    for (var v$def : def$fields) {
      mw().visitVarInsn(ALOAD, 0);
      var f$name = v$def.name.content;
      var field = (YaaField) fs6.getSymbol(f$name);
      v$def.value.visit(fs6);
      mw().visitFieldInsn(PUTFIELD, clz.codeName, f$name, field.descriptor());
    }
  }

  private static void initializeDecFields(List<VDeclaration> dec$fields, YaaClz clz) {
    for (var v$def : dec$fields) {
      mw().visitVarInsn(ALOAD, 0);
      var f$name = v$def.name.content;
      var field = (YaaField) fs6.getSymbol(f$name);
      F6Utils.loadDefaultData(field.data.name);
      mw().visitFieldInsn(PUTFIELD, clz.codeName, f$name, field.descriptor());
    }
  }
}