package yaa.semantic.passes.fs6;

import org.objectweb.asm.ClassWriter;
import yaa.ast.*;
import yaa.pojos.YaaMeta;

import static org.objectweb.asm.Opcodes.*;
import static yaa.pojos.GlobalData.fs6;
import static yaa.semantic.passes.fs6.F6Utils.*;
import static yaa.semantic.passes.fs6.F6Utils.saveClass4Writing;

public class F6NewMeta {
  public static void newMeta(NewMeta newMeta) {
    fs6.pushTable(newMeta);
    var fs6Meta = (YaaMeta) fs6.getSymbol(newMeta.placeOfUse());
    ClassWriter cw = new ClassWriter(0);
    fs6.cw.push(cw);

    cw.visit(
        V18, ACC_PUBLIC | ACC_ANNOTATION | ACC_ABSTRACT | ACC_INTERFACE,
        fs6Meta.codeName,
        null,
        "java/lang/Object",
        new String[]{"java/lang/annotation/Annotation"}
    );

    fs6.cw.peek().visitSource(fs6Meta.codeName + ".yaa", null);

    for (var field : newMeta.vDefinitions) {
      var name = field.name.content;
      var mv = cw.visitMethod(
          ACC_PUBLIC | ACC_ABSTRACT,
          name,
          "()" + fs6.getSymbol(name).descriptor(),
          null, null
      );
      var av = mv.visitAnnotationDefault();
      av.visit(null, plainMetaValue4Java(field.value));
      av.visitEnd();
      mv.visitEnd();
    }

    for (var field : newMeta.vDeclarations) {
      var name = field.name.content;
      var mv = cw.visitMethod(
          ACC_PUBLIC | ACC_ABSTRACT,
          name,
          "()" + fs6.getSymbol(name).descriptor(),
          null, null
      );
      mv.visitEnd();
    }

    fs6.cw.peek().visitEnd();
    fs6.popTable();
    saveClass4Writing(fs6Meta.codeName);
    fs6.cw.pop();
  }

  public static Object plainMetaValue4Java(Stmt value) {
    if (value instanceof AstString string) {
      var builder = new StringBuilder();
      for (var item : string.content) {
        builder.append(item);
      }
      return builder.toString();
    }
    if (value instanceof Decimal decimal) {
      return Integer.parseInt(decimal.token.content);
    }
    if (value instanceof Pointed pointed) {
      return Double.parseDouble(pointed.token.content);
    }
    if (value instanceof Floated floated) {
      return Float.parseFloat(floated.token.content);
    }
    if (value instanceof Longed longed) {
      return Long.parseLong(longed.token.content);
    }
    if (value instanceof Shorted shorted) {
      return Short.parseShort(removeLastChar(shorted.token.content));
    }
    if (value instanceof Byted byted) {
      return Byte.parseByte(removeLastChar(byted.token.content));
    }
    if (value instanceof True) {
      return true;
    }
    if (value instanceof False) {
      return false;
    }
    return 20;
  }
}