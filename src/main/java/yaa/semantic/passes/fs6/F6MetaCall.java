package yaa.semantic.passes.fs6;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.FieldVisitor;
import yaa.ast.Name;
import yaa.ast.VCall;
import yaa.ast.VGet;
import yaa.ast.YaaMetaCall;
import yaa.pojos.TypeCategory;
import yaa.pojos.YaaClz;
import yaa.pojos.YaaInfo;
import yaa.pojos.YaaMeta;

import static yaa.pojos.GlobalData.*;
import static yaa.pojos.GlobalData.byte$name;
import static yaa.semantic.passes.fs6.F6NewMeta.plainMetaValue4Java;

public class F6MetaCall {
  public static void visitArguments(YaaMetaCall metaCall, YaaMeta meta, AnnotationVisitor av) {
    for (var arg : metaCall.arguments.entrySet()) {
      var meta_field = meta.requiredFields.get(arg.getKey().content);
      if (meta_field == null) {
        //if not found in required fields, it means the user wants to override.
        meta_field = meta.defaultFields.get(arg.getKey().content);
      }
      if (meta_field.data.name.equals(array$name)) {
        var array_input = ((YaaClz) meta_field.data).inputted.get(0);
        if (array_input.category == TypeCategory.enum_c) {
          var array_an_visitor = av.visitArray(arg.getKey().content);
          var ast_array = (VCall) arg.getValue(); //arrays are implemented as VCall
          var enum_descriptor = array_input.descriptor();
          for (var enum_in_array : ast_array.arguments) {
            if (enum_in_array instanceof Name name_ast) {
              array_an_visitor.visitEnum(null, enum_descriptor, name_ast.token.content);
            }
            if (enum_in_array instanceof VGet vGet) {
              array_an_visitor.visitEnum(null, enum_descriptor, vGet.n2.content);
            }
          }
          array_an_visitor.visitEnd();
        } else if (array_input.name.equals("java.lang.String")) {
          var array_an_visitor = av.visitArray(arg.getKey().content);
          var ast_array = (VCall) arg.getValue(); //arrays are implemented as VCall
          for (var string_in_array : ast_array.arguments) {
            array_an_visitor.visit(null, plainMetaValue4Java(string_in_array));
          }
          array_an_visitor.visitEnd();
        } else {
          //for the primitives now
          switch (array_input.name) {
            case double$name -> {
              var ast_array = (VCall) arg.getValue();
              var array_content = new double[ast_array.arguments.size()];
              int index = 0;
              for (var double_in_array : ast_array.arguments) {
                array_content[index++] = (double) plainMetaValue4Java(double_in_array);
              }
              av.visit(arg.getKey().content, array_content);
            }
            case int$name -> {
              var ast_array = (VCall) arg.getValue();
              var array_content = new int[ast_array.arguments.size()];
              int index = 0;
              for (var int_in_array : ast_array.arguments) {
                array_content[index++] = (int) plainMetaValue4Java(int_in_array);
              }
              av.visit(arg.getKey().content, array_content);
            }
            case long$name -> {
              var ast_array = (VCall) arg.getValue();
              var array_content = new long[ast_array.arguments.size()];
              int index = 0;
              for (var long_in_array : ast_array.arguments) {
                array_content[index++] = (long) plainMetaValue4Java(long_in_array);
              }
              av.visit(arg.getKey().content, array_content);
            }
            case short$name -> {
              var ast_array = (VCall) arg.getValue();
              var array_content = new short[ast_array.arguments.size()];
              int index = 0;
              for (var short_in_array : ast_array.arguments) {
                array_content[index++] = (short) plainMetaValue4Java(short_in_array);
              }
              av.visit(arg.getKey().content, array_content);
            }
            case float$name -> {
              var ast_array = (VCall) arg.getValue();
              var array_content = new float[ast_array.arguments.size()];
              int index = 0;
              for (var float_in_array : ast_array.arguments) {
                array_content[index++] = (float) plainMetaValue4Java(float_in_array);
              }
              av.visit(arg.getKey().content, array_content);
            }
            case char$name -> {
              var ast_array = (VCall) arg.getValue();
              var array_content = new char[ast_array.arguments.size()];
              int index = 0;
              for (var char_in_array : ast_array.arguments) {
                array_content[index++] = (char) plainMetaValue4Java(char_in_array);
              }
              av.visit(arg.getKey().content, array_content);
            }
            case boole$name -> {
              var ast_array = (VCall) arg.getValue();
              var array_content = new boolean[ast_array.arguments.size()];
              int index = 0;
              for (var boole_in_array : ast_array.arguments) {
                array_content[index++] = (boolean) plainMetaValue4Java(boole_in_array);
              }
              av.visit(arg.getKey().content, array_content);
            }
            case byte$name -> {
              var ast_array = (VCall) arg.getValue();
              var array_content = new byte[ast_array.arguments.size()];
              int index = 0;
              for (var byte_in_array : ast_array.arguments) {
                array_content[index++] = (byte) plainMetaValue4Java(byte_in_array);
              }
              av.visit(arg.getKey().content, array_content);
            }
          }
        }
      } else if (meta_field.data instanceof YaaClz clz && clz.category == TypeCategory.enum_c) {
        if (arg.getValue() instanceof Name name_ast) {
          av.visitEnum(arg.getKey().content, clz.descriptor(), name_ast.token.content);
        }
        if (arg.getValue() instanceof VGet vGet) {
          av.visitEnum(arg.getKey().content, clz.descriptor(), vGet.n2.content);
        }
      } else {
        av.visit(arg.getKey().content, plainMetaValue4Java(arg.getValue()));
      }
    }
    av.visitEnd();
  }
}