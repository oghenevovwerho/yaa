package yaa.semantic.passes.fs6;

import yaa.ast.*;
import yaa.pojos.YaaClz;
import yaa.pojos.YaaInfo;
import yaa.pojos.jMold.JMold;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Integer.parseInt;
import static org.objectweb.asm.Opcodes.*;
import static yaa.pojos.GlobalData.*;
import static yaa.semantic.passes.fs6.F6.mw;

public class F6String {
  public static YaaClz f6String(AstString string) {
    if (string.itIsInterpolated) {
      return interpolated(string);
    }

    var mainBuilder = new StringBuilder();
    var itemsInStringAst = string.content;
    var contentSize = itemsInStringAst.size();
    for (int i = 0; i < contentSize; i++) {
      var itemInStringAst = itemsInStringAst.get(i);
      if (isSpecialSymbol(itemInStringAst)) {
        mainBuilder.append(specialSymbolResolvedValue(itemInStringAst));
      } else {
        var nonSpecialSymbolBuilder = new StringBuilder();
        while (!isSpecialSymbol(itemInStringAst)) {
          nonSpecialSymbolBuilder.append(itemInStringAst);
          if (i < contentSize - 1) {
            itemInStringAst = itemsInStringAst.get(++i);
          } else {
            break;
          }
        }
        mainBuilder.append(nonSpecialSymbolBuilder);
        if (isSpecialSymbol(itemsInStringAst.get(i))) {
          mainBuilder.append(specialSymbolResolvedValue(itemInStringAst));
        }
      }
    }
    mw().visitLdcInsn(mainBuilder.toString());
    return new JMold().newClz("java.lang.String");
  }

  private static String specialSymbolResolvedValue(Object specialSymbol) {
    if (specialSymbol instanceof NewLine) {
      return "\n";
    } else if (specialSymbol instanceof SQuote) {
      return "'";
    } else if (specialSymbol instanceof BTick) {
      return "`";
    } else if (specialSymbol instanceof BSlash) {
      return "\\";
    } else if (specialSymbol instanceof LCurly) {
      return "{";
    } else if (specialSymbol instanceof UniKode uniKode) {
      return Character.toString(parseInt(uniKode.token.content));
    }
    return "#";
  }

  private static boolean isSpecialSymbol(Object itemInStringAst) {
    if (itemInStringAst instanceof HashSign) {
      return true;
    } else if (itemInStringAst instanceof NewLine) {
      return true;
    } else if (itemInStringAst instanceof SQuote) {
      return true;
    } else if (itemInStringAst instanceof BTick) {
      return true;
    } else if (itemInStringAst instanceof BSlash) {
      return true;
    } else if (itemInStringAst instanceof LCurly) {
      return true;
    } else return itemInStringAst instanceof UniKode;
  }

  private static YaaClz interpolated(AstString string) {
    var formattedContent = new ArrayList<>();
    var itemsInStringAst = string.content;
    var contentSize = itemsInStringAst.size();
    for (int i = 0; i < contentSize; i++) {
      var itemInStringAst = itemsInStringAst.get(i);
      if (itemInStringAst instanceof Stmt) {
        formattedContent.add(itemInStringAst);
      } else {
        var nonStmtBuilder = new StringBuilder();
        if (isSpecialSymbol(itemInStringAst)) {
          nonStmtBuilder.append(specialSymbolResolvedValue(itemInStringAst));
        } else {
          var nonSpecialSymbolBuilder = new StringBuilder();
          while (!isSpecialSymbol(itemInStringAst) && !(itemInStringAst instanceof Stmt)) {
            nonSpecialSymbolBuilder.append(itemInStringAst);
            if (i < contentSize - 1) {
              itemInStringAst = itemsInStringAst.get(++i);
            } else {
              break;
            }
          }
          nonStmtBuilder.append(nonSpecialSymbolBuilder);
          if (isSpecialSymbol(itemsInStringAst.get(i))) {
            nonStmtBuilder.append(specialSymbolResolvedValue(itemInStringAst));
          }
          formattedContent.add(nonStmtBuilder.toString());
          if (itemInStringAst instanceof Stmt) {
            formattedContent.add(itemInStringAst);
          }
        }
      }
    }
    handleStringConcat(formattedContent);
    return new JMold().newClz("java.lang.String");
  }

  private static void handleStringConcat(List<Object> content) {
    mw().visitTypeInsn(NEW, "java/lang/StringBuilder");
    mw().visitInsn(DUP);

    mw().visitMethodInsn(
        INVOKESPECIAL,
        "java/lang/StringBuilder",
        "<init>",
        "()V", false
    );

    for (var item : content) {
      if (item instanceof Stmt stmt) {
        var item$value = stmt.visit(fs6);
        mw().visitMethodInsn(
            INVOKEVIRTUAL,
            "java/lang/StringBuilder",
            "append",
            "(" + expInStringDescriptor(item$value) + ")Ljava/lang/StringBuilder;",
            false
        );
      } else {
        mw().visitLdcInsn(item.toString());
        mw().visitMethodInsn(
            INVOKEVIRTUAL, "java/lang/StringBuilder", "append",
            "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false
        );
      }
    }

    mw().visitMethodInsn(
        INVOKEVIRTUAL,
        "java/lang/StringBuilder",
        "toString",
        "()Ljava/lang/String;",
        false
    );
  }

  private static String expInStringDescriptor(YaaInfo expInString) {
    if (expInString.name.equals(array$name)) {
      if (expInString instanceof YaaClz array_clz) {
        if (array_clz.inputted.get(0).name.equals(char$name)) {
          return expInString.descriptor();
        }
      }
    } else if (expInString.name.equals("java.lang.StringBuffer")) {
      return expInString.descriptor();
    } else if (expInString.name.equals("java.lang.CharSequence")) {
      return expInString.descriptor();
    } else if (expInString.name.equals(int$name)) {
      return expInString.descriptor();
    } else if (expInString.name.equals(long$name)) {
      return expInString.descriptor();
    } else if (expInString.name.equals(float$name)) {
      return expInString.descriptor();
    } else if (expInString.name.equals(double$name)) {
      return expInString.descriptor();
    } else if (expInString.name.equals(char$name)) {
      return expInString.descriptor();
    } else if (expInString.name.equals(boole$name)) {
      return expInString.descriptor();
    } else if (expInString.isPrimitive()) {
      return "I";//the descriptor of int represents short and byte
    }
    return "Ljava/lang/Object;";
  }
}
