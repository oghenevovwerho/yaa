package yaa.semantic.passes.fs6;

import org.objectweb.asm.*;
import yaa.ast.Init;
import yaa.ast.NewRecord;
import yaa.ast.Parameter;
import yaa.ast.VDefinition;
import yaa.pojos.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.objectweb.asm.Opcodes.*;
import static yaa.pojos.GlobalData.*;
import static yaa.semantic.passes.fs6.F6.mtdWriters;
import static yaa.semantic.passes.fs6.F6.mw;
import static yaa.semantic.passes.fs6.F6Utils.saveClass4Writing;

public class F6NRecord {
  public static void newRecord(NewRecord newRecord) {
    fs6.pushTable(newRecord);
    var clz = (YaaClz) fs6.getSymbol(newRecord.name.content);
    topClz.push(clz);

    fs6.push$fields();
    fs6.cw.push(new ClassWriter(ClassWriter.COMPUTE_FRAMES));

    fs6.cw.peek().visit(
        V17, recCodeModifier(newRecord),
        clz.codeName, null,
        clz.parent.codeName,
        implementedTraits(clz.traits.values())
    );

    if (newRecord.inits.size() == 1) {
      for (var param : newRecord.inits.get(0).parameters) {
        var f$name = param.name.content;
        var field = (YaaField) fs6.getSymbol(f$name);
        fs6.cw.peek().visitField(
            ACC_PUBLIC + ACC_FINAL, f$name,
            field.descriptor(),
            field.clzUseSignature(),
            null
        ).visitEnd();

        fs6.cw.peek().visitRecordComponent(
            f$name, field.descriptor(), field.clzUseSignature()
        );
      }

      for (int i = 0; i < newRecord.inits.size(); i++) {
        makeInit(newRecord.inits.get(i), newRecord, clz);
      }
    } else {
      if (clz.category != TypeCategory.trait_c) {
        stubInit(newRecord, clz, clz.inits.get(0));
      }
    }

    fs6.cw.peek().visitSource(clz.codeName + ".yaa", null);

    for (var block : newRecord.parents) {
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

    for (var mtd : newRecord.methods) {
      fs6.pushTable(mtd);
      var fun = (YaaFun) fs6.getSymbol(mtd.placeOfUse());
      fun.startCode();
      mw().visitCode();
      fun.initParam(mtd.parameters);
      mtd.stmt.visit(fs6);
      fun.closeCode();
      fs6.popTable();
    }

    var methods = new ArrayList<String>();
    methods.add("toString");
    methods.add("hashCode");
    methods.add("equals");

    for (var overBlock : newRecord.parents) {
      if (overBlock.isClass) {
        for (var pack_name : overBlock.methods.keySet()) {
          if (pack_name.equals("toString")) {
            methods.remove(pack_name);
          }
          if (pack_name.equals("hashCode")) {
            methods.remove(pack_name);
          }
          if (pack_name.equals("equals")) {
            methods.remove(pack_name);
          }
        }
      }
    }

    generateObjectMethods(clz, methods);

    for (var inner$clz : newRecord.classes) {
      inner$clz.visit(fs6);
    }

    for (var inner$enum : newRecord.enums) {
      inner$enum.visit(fs6);
    }

    if (newRecord.runBlocks.size() > 0) {
      F6.mtdWriters.push(fs6.cw.peek().visitMethod(
          ACC_STATIC, "<clinit>", "()V", null, null
      ));
      fs6.push$variables();
      mw().visitCode();
      fs6.variables.peek().index = -1;
      for (var run_block : newRecord.runBlocks) {
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
    topClz.pop();
    fs6.popTable();
  }

  private static void generateObjectMethods(YaaClz clz, ArrayList<String> methods) {
    var rec_fields = clz.instance$fields;

    Object[] object_arguments = new Object[2 + rec_fields.size()];
    object_arguments[0] = Type.getType(clz.descriptor());
    object_arguments[1] = appendFields(rec_fields.keySet().toArray());
    int index_in_arg = 2;
    for (var param : rec_fields.keySet()) {
      object_arguments[index_in_arg++] = new Handle(
          H_GETFIELD,
          clz.codeName,
          param,
          rec_fields.get(param).descriptor(),
          false
      );
    }

    //check before implementing any methods to avoid duplication
    //in case the user explicitly implements it already.

    //generate toString
    if (methods.contains("toString")) {
      mtdWriters.push(fs6.cw.peek().visitMethod(
          ACC_PUBLIC | ACC_FINAL,
          "toString",
          "()Ljava/lang/String;",
          null, null
      ));
      mw().visitCode();
      mw().visitVarInsn(ALOAD, 0);
      mw().visitInvokeDynamicInsn(
          "toString",
          "(" + clz.descriptor() + ")Ljava/lang/String;",
          new Handle(
              H_INVOKESTATIC,
              "java/lang/runtime/ObjectMethods",
              "bootstrap",
              "(" +
                  "Ljava/lang/invoke/MethodHandles$Lookup;" +
                  "Ljava/lang/String;" +
                  "Ljava/lang/invoke/TypeDescriptor;" +
                  "Ljava/lang/Class;" +
                  "Ljava/lang/String;" +
                  "[Ljava/lang/invoke/MethodHandle;" +
                  ")Ljava/lang/Object;",
              false
          ),
          object_arguments
      );
      mw().visitInsn(ARETURN);
      mw().visitMaxs(1, 1);
      mw().visitEnd();
      mtdWriters.pop();
    }

    //generate hashCode
    if (methods.contains("hashCode")) {
      mtdWriters.push(fs6.cw.peek().visitMethod(
          ACC_PUBLIC | ACC_FINAL,
          "hashCode",
          "()I",
          null,
          null
      ));
      mw().visitCode();
      mw().visitVarInsn(ALOAD, 0);
      mw().visitInvokeDynamicInsn(
          "hashCode",
          "(" + clz.descriptor() + ")I",
          new Handle(
              Opcodes.H_INVOKESTATIC,
              "java/lang/runtime/ObjectMethods",
              "bootstrap",
              "(" +
                  "Ljava/lang/invoke/MethodHandles$Lookup;" +
                  "Ljava/lang/String;" +
                  "Ljava/lang/invoke/TypeDescriptor;" +
                  "Ljava/lang/Class;" +
                  "Ljava/lang/String;" +
                  "[Ljava/lang/invoke/MethodHandle;" +
                  ")Ljava/lang/Object;",
              false
          ),
          object_arguments
      );
      mw().visitInsn(IRETURN);
      mw().visitMaxs(1, 1);
      mw().visitEnd();
      mtdWriters.pop();
    }

    //generate equals
    if (methods.contains("equals")) {
      mtdWriters.push(fs6.cw.peek().visitMethod(
          ACC_PUBLIC | ACC_FINAL,
          "equals",
          "(Ljava/lang/Object;)Z",
          null,
          null
      ));
      mw().visitCode();
      mw().visitVarInsn(ALOAD, 0);
      mw().visitVarInsn(ALOAD, 1);
      mw().visitInvokeDynamicInsn(
          "equals",
          "(" + clz.descriptor() + "Ljava/lang/Object;)Z",
          new Handle(
              Opcodes.H_INVOKESTATIC,
              "java/lang/runtime/ObjectMethods",
              "bootstrap",
              "("
                  + "Ljava/lang/invoke/MethodHandles$Lookup;"
                  + "Ljava/lang/String;"
                  + "Ljava/lang/invoke/TypeDescriptor;"
                  + "Ljava/lang/Class;"
                  + "Ljava/lang/String;"
                  + "[Ljava/lang/invoke/MethodHandle;"
                  + ")Ljava/lang/Object;",
              false
          ),
          object_arguments
      );
      mw().visitInsn(IRETURN);
      mw().visitMaxs(2, 2);
      mw().visitEnd();
      mtdWriters.pop();
    }
  }

  private static Object appendFields(Object[] parameters) {
    if (parameters.length == 0) {
      return "";
    }
    if (parameters.length == 1) {
      return parameters[0];
    }
    var sb = new StringBuilder();
    sb.append(parameters[0]);
    for (int i = 1; i < parameters.length; i++) {
      sb.append(";").append(parameters[i]);
    }
    return sb.toString();
  }

  private static int recCodeModifier(NewRecord newRecord) {
    var privacy = newRecord.privacy();
    var clz$modifier = ACC_PUBLIC;
    if (privacy == 1) {
      clz$modifier = ACC_PROTECTED;
    }
    if (privacy == 2) {
      clz$modifier = ACC_PRIVATE;
    }
    clz$modifier = clz$modifier +
        ACC_FINAL | ACC_SUPER | ACC_RECORD;
    return clz$modifier;
  }

  private static String[] implementedTraits(Collection<YaaClz> traits) {
    var trait_array = new String[traits.size()];
    int i = 0;
    for (var trait : traits) {
      trait_array[i] = trait.codeName;
      i++;
    }
    return trait_array;
  }

  private static void stubInit(NewRecord nc, YaaClz clz, YaaInit init) {
    fs6.push$variables();

    F6.mtdWriters.push(fs6.cw.peek().visitMethod(
        ACC_PUBLIC, "<init>",
        init.descriptor(),
        null, new String[]{}
    ));

    mw().visitCode();

    mw().visitVarInsn(ALOAD, 0);
    if (clz.parent == null) {
      mw().visitMethodInsn(
          INVOKESPECIAL,
          "java/lang/Object",
          "<init>", "()V", false
      );
    } else {
      mw().visitMethodInsn(
          INVOKESPECIAL,
          clz.parent.codeName,
          "<init>", "()V", false
      );
    }

    initializeDefFields(nc.vDefinitions, clz);

    mw().visitInsn(RETURN);
    mw().visitMaxs(0, 0);
    mw().visitEnd();
    F6.mtdWriters.pop();
    fs6.pop$variables();
  }

  private static void makeInit(Init init, NewRecord nc, YaaClz clz) {
    F6.variableMeta.push(new ArrayList<>());
    fs6.pushTable(init);
    var initInfo = (YaaInit) fs6.getSymbol(init.placeOfUse());
    fs6.push$variables();

    F6.mtdWriters.push(fs6.cw.peek().visitMethod(
        ACC_PUBLIC, "<init>",
        initInfo.descriptor(),
        null, new String[]{}
    ));

    mw().visitCode();

    handleParameters(init.parameters);
    initializeParent(clz);

    var first_init = nc.inits.get(0);
    for (int i = 0; i < first_init.parameters.size(); i++) {
      var param = first_init.parameters.get(i);
      var f$name = param.name.content;
      var field = (YaaField) fs6.getSymbol(f$name);
      mw().visitVarInsn(ALOAD, 0);
      switch (field.data.name) {
        case double$name -> mw().visitVarInsn(DLOAD, i + 1);
        case long$name -> mw().visitVarInsn(LLOAD, i + 1);
        case float$name -> mw().visitVarInsn(FLOAD, i + 1);
        default -> {
          if (field.data.isPrimitive()) {
            mw().visitVarInsn(ILOAD, i + 1);
          } else {
            mw().visitVarInsn(ALOAD, i + 1);
          }
        }
      }
      mw().visitFieldInsn(
          PUTFIELD, field.owner, f$name, field.descriptor()
      );
    }

    initializeDefFields(nc.vDefinitions, clz);

    init.stmt.visit(fs6);

    mw().visitInsn(RETURN);
    mw().visitMaxs(0, 0);
    mw().visitEnd();
    F6.mtdWriters.pop();
    fs6.popTable();
    fs6.pop$variables();
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

  private static void initializeParent(YaaClz clz) {
    mw().visitVarInsn(ALOAD, 0);
    mw().visitMethodInsn(
        INVOKESPECIAL,
        clz.parent.codeName,
        "<init>",
        "()V", false
    );
  }
}