package yaa.semantic.passes.fs6;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.Type;
import yaa.ast.Init;
import yaa.ast.NewEnum;
import yaa.ast.Parameter;
import yaa.ast.VDefinition;
import yaa.pojos.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.objectweb.asm.Opcodes.*;
import static yaa.pojos.GlobalData.fs6;
import static yaa.semantic.passes.fs6.F6.mw;

public class F6NEnum {
  public static void newEnum(NewEnum newEnum) {
    fs6.pushTable(newEnum);
    var clz = (YaaClz) fs6.getSymbol(newEnum.placeOfUse());
    GlobalData.topClz.push(clz);
    var stub_value_name = F6Utils.generateRandomName("value");

    fs6.push$fields();
    fs6.cw.push(new ClassWriter(ClassWriter.COMPUTE_FRAMES));

    fs6.cw.peek().visit(
        V17, ACC_PUBLIC | ACC_FINAL | ACC_SUPER | ACC_ENUM,
        clz.codeName, enumSignature(clz), "java/lang/Enum",
        implementedTraits(clz.traits.values())
    );

    for (var v$dec : newEnum.vDeclarations) {
      var f$name = v$dec.name.content;
      var field = (YaaField) fs6.getSymbol(f$name);
      fs6.clz$fields.peek().put(field);
      fs6.cw.peek().visitField(
          F6NClass.fieldMod(v$dec),
          f$name,
          field.descriptor(),
          field.clzUseSignature(),
          null
      ).visitEnd();
    }

    for (var v$def : newEnum.vDefinitions) {
      var f$name = v$def.name.content;
      var field = (YaaField) fs6.getSymbol(f$name);
      fs6.clz$fields.peek().put(field);
      fs6.cw.peek().visitField(
          F6NClass.fieldMod(v$def),
          f$name,
          field.descriptor(),
          field.clzUseSignature(),
          null
      ).visitEnd();
    }

    if (newEnum.inits.size() > 0) {
      for (int i = 0; i < newEnum.inits.size(); i++) {
        enumInit(newEnum.inits.get(i), clz, clz.inits.get(i), newEnum);
      }
    } else {
      enumInit(clz, newEnum);
    }

    fs6.cw.peek().visitField(
        ACC_PRIVATE | ACC_FINAL | ACC_STATIC | ACC_SYNTHETIC,
        stub_value_name,
        "[" + clz.descriptor(),
        null,
        null
    ).visitEnd();

    var bodyMtd = fs6.cw.peek().visitMethod(
        ACC_STATIC, "<clinit>", "()V", null, null
    );

    F6.mtdWriters.push(bodyMtd);

    int index = 0;
    for (var option : newEnum.enumOptions) {
      var option_name = option.name.content;
      fs6.cw.peek().visitField(
          ACC_PUBLIC | ACC_FINAL | ACC_STATIC | ACC_ENUM,
          option_name,
          clz.descriptor(),
          null,
          null
      ).visitEnd();
      bodyMtd.visitTypeInsn(NEW, clz.codeName);
      bodyMtd.visitInsn(DUP);
      bodyMtd.visitLdcInsn(option_name);
      F6Utils.generateIntCode(index++);
      var bd = new StringBuilder();
      bd.append("(");
      bd.append("Ljava/lang/String;I");
      if (option.arguments != null) {
        for (var arg : option.arguments) {
          var result = arg.visit(fs6);
          bd.append(result.descriptor());
        }
      }
      bd.append(")V");
      bodyMtd.visitMethodInsn(
          INVOKESPECIAL,
          clz.codeName,
          "<init>",
          bd.toString(),
          false
      );
      bodyMtd.visitFieldInsn(
          PUTSTATIC,
          clz.codeName,
          option_name,
          clz.descriptor()
      );
    }

    bodyMtd.visitMethodInsn(
        INVOKESTATIC,
        clz.codeName,
        "$values",
        "()[" + clz.descriptor(),
        false
    );

    bodyMtd.visitFieldInsn(
        PUTSTATIC,
        clz.codeName,
        stub_value_name,
        "[" + clz.descriptor()
    );

    bodyMtd.visitInsn(RETURN);
    bodyMtd.visitMaxs(0, 0);
    bodyMtd.visitEnd();
    F6.mtdWriters.pop();

    var valuesMtd = fs6.cw.peek().visitMethod(
        ACC_PUBLIC | ACC_STATIC | ACC_SYNTHETIC,
        "$values",
        "()[" + clz.descriptor(),
        null,
        null
    );

    F6.mtdWriters.push(valuesMtd);

    valuesMtd.visitCode();
    F6Utils.generateIntCode(newEnum.enumOptions.size());
    valuesMtd.visitTypeInsn(ANEWARRAY, clz.codeName);
    valuesMtd.visitInsn(DUP);
    int i = 0;
    for (var option : newEnum.enumOptions) {
      var option_name = option.name.content;
      F6Utils.generateIntCode(i++);
      valuesMtd.visitFieldInsn(
          GETSTATIC, clz.codeName,
          option_name,
          clz.descriptor()
      );
      valuesMtd.visitInsn(AASTORE);
      valuesMtd.visitInsn(DUP);
    }
    valuesMtd.visitInsn(ARETURN);
    valuesMtd.visitMaxs(0, 0);
    valuesMtd.visitEnd();
    F6.mtdWriters.pop();

    var valueOfMtd = fs6.cw.peek().visitMethod(
        ACC_PUBLIC | ACC_STATIC, "valueOf",
        "(Ljava/lang/String;)" + clz.descriptor(),
        null, new String[0]
    );

    valueOfMtd.visitCode();
    valueOfMtd.visitLdcInsn(Type.getType(clz.descriptor()));
    valueOfMtd.visitVarInsn(ALOAD, 0);
    valueOfMtd.visitMethodInsn(
        INVOKESTATIC,
        "java/lang/Enum",
        "valueOf",
        "(Ljava/lang/Class;Ljava/lang/String;)Ljava/lang/Enum;",
        false
    );
    valueOfMtd.visitTypeInsn(CHECKCAST, clz.codeName);
    valueOfMtd.visitInsn(ARETURN);
    valueOfMtd.visitMaxs(0, 0);
    valueOfMtd.visitEnd();

    fs6.cw.peek().visitSource(clz.codeName + ".yaa", null);

    if (newEnum.toStringParentMtd != null) {
      fs6.pushTable(newEnum.toStringParentMtd);
      var fun = (YaaFun) fs6.getSymbol(newEnum.toStringParentMtd.placeOfUse());
      fun.startCode();
      fun.initParam(List.of());
      mw().visitAnnotation("Ljava/lang/Override;", false);
      newEnum.toStringParentMtd.stmt.visit(fs6);
      fun.closeCode();
      fs6.popTable();
    }

    for (var block : newEnum.implementations) {
      fs6.pushTable(block);
      for (var mtd$pack : block.methods.values()) {
        for (var mtd : mtd$pack) {
          fs6.pushTable(mtd);
          var fun = (YaaFun) fs6.getSymbol(mtd.placeOfUse());
          fun.startCode(clz.decInfoMap.get(mtd.placeOfUse()));
          fun.initParam(mtd.parameters);
          mtd.stmt.visit(fs6);
          fun.closeCode();
          fs6.popTable();
        }
      }
      fs6.popTable();
    }

    for (var mtd : newEnum.methods) {
      fs6.pushTable(mtd);
      var fun = (YaaFun) fs6.getSymbol(mtd.placeOfUse());
      fun.startCode();
      mw().visitCode();
      fun.initParam(mtd.parameters);
      mtd.stmt.visit(fs6);
      fun.closeCode();
      fs6.popTable();
    }

    for (var inner$clz : newEnum.classes) {
      inner$clz.visit(fs6);
    }

    for (var inner$enum : newEnum.enums) {
      inner$enum.visit(fs6);
    }

    if (newEnum.runBlocks.size() > 0) {
      F6.mtdWriters.push(fs6.cw.peek().visitMethod(
          ACC_STATIC, "<clinit>", "()V", null, null
      ));
      fs6.push$variables();
      F6.mw().visitCode();
      fs6.variables.peek().index = -1;
      for (var run_block : newEnum.runBlocks) {
        fs6.pushTable(run_block);
        for (var stmt : run_block.stmts) {
          stmt.visit(fs6);
        }
        fs6.popTable();
      }
      F6.mw().visitInsn(RETURN);
      fs6.pop$variables();
      F6.mw().visitMaxs(0, 0);
      F6.mw().visitEnd();
      F6.mtdWriters.pop();
    }

    fs6.pop$fields();
    fs6.cw.peek().visitEnd();
    F6Utils.saveClass4Writing(clz.codeName);
    fs6.cw.pop();
    GlobalData.topClz.pop();
    fs6.popTable();
  }

  private static String enumSignature(YaaClz clz) {
    var sb = new StringBuilder();
    sb.append("Ljava/lang/Enum<");
    sb.append(clz.descriptor());
    sb.append(">;");
    for (var trait : clz.traits.values()) {
      sb.append(trait.descriptor());
    }
    return sb.toString();
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

  public static void enumInit(Init init, YaaClz clz, YaaInit co, NewEnum nc) {
    fs6.pushTable(init);
    F6.variableMeta.push(new ArrayList<>());
    fs6.push$variables();

    F6.mtdWriters.push(fs6.cw.peek().visitMethod(
        ACC_PRIVATE, "<init>",
        co.enumDescriptor(),
        co.descriptor(), null
    ));

    F6.mw().visitCode();

    F6.mw().visitVarInsn(ALOAD, 0);
    F6.mw().visitVarInsn(ALOAD, 1);
    F6.mw().visitVarInsn(ILOAD, 2);
    F6.mw().visitMethodInsn(
        INVOKESPECIAL,
        "java/lang/Enum", "<init>",
        "(Ljava/lang/String;I)V", false
    );

    fs6.variables.peek().index = 2;

    handleParameters(init.parameters);

    init.stmt.visit(fs6);

    initializeDefFields(nc.vDefinitions, clz);

    F6.mw().visitInsn(RETURN);
    F6.mw().visitMaxs(0, 0);
    F6.mw().visitEnd();
    F6.mtdWriters.pop();
    fs6.pop$variables();
    fs6.popTable();
    F6.variableMeta.pop();
  }

  private static void initializeDefFields(List<VDefinition> def$fields, YaaClz clz) {
    for (var v$def : def$fields) {
      F6.mw().visitVarInsn(ALOAD, 0);
      var f$name = v$def.name.content;
      var field = (YaaField) fs6.getSymbol(f$name);
      v$def.value.visit(fs6);
      F6.mw().visitFieldInsn(PUTFIELD, clz.codeName, f$name, field.descriptor());
    }
  }

  public static void enumInit(YaaClz clz, NewEnum nc) {
    fs6.push$variables();

    F6.mtdWriters.push(fs6.cw.peek().visitMethod(
        ACC_PRIVATE, "<init>",
        "(Ljava/lang/String;I)V",
        "()V", null
    ));

    F6.mw().visitCode();

    F6.mw().visitVarInsn(ALOAD, 0);
    F6.mw().visitVarInsn(ALOAD, 1);
    F6.mw().visitVarInsn(ILOAD, 2);
    F6.mw().visitMethodInsn(
        INVOKESPECIAL,
        "java/lang/Enum", "<init>",
        "(Ljava/lang/String;I)V", false
    );

    initializeDefFields(nc.vDefinitions, clz);

    F6.mw().visitInsn(RETURN);
    F6.mw().visitMaxs(0, 0);
    F6.mw().visitEnd();
    F6.mtdWriters.pop();
    fs6.pop$variables();
  }

  private static void handleParameters(List<Parameter> parameters) {
    for (var param : parameters) {
      var variables = fs6.variables.peek();
      var param$name = param.name.content;
      var param$field = (YaaField) GlobalData.fs.getSymbol(param$name);
      var data = param$field.data;
      switch (data.name) {
        case GlobalData.long$name, GlobalData.double$name -> {
          variables.putWideVar(param$name);
        }
        default -> variables.putVar(param$name);
      }
      var label = new Label();
      F6.mw().visitLabel(label);
      F6.mw().visitLineNumber(param.start.line, label);
      var index = variables.index;
      F6.variableMeta.peek().add(new VariableData(
          param$name, label, data.descriptor(),
          data.clzUseSignature(), index, new ArrayList<>(0), List.of()
      ));
    }
  }
}

