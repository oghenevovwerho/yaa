package yaa.pojos.jMold;

import yaa.pojos.TypeCategory;
import yaa.pojos.YaaClz;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Class.forName;
import static java.lang.ClassLoader.getSystemClassLoader;

public class JMoldUtils {
  protected static List<String> tokenize$clz$name(String name) {
    var char_array = name.toCharArray();
    var tokens = new ArrayList<String>();
    var temp = new StringBuilder();
    for (char c : char_array) {
      if (c == '.') {
        tokens.add(temp.toString());
        temp = new StringBuilder();
      } else {
        temp.append(c);
      }
    }
    tokens.add(temp.toString());
    return tokens;
  }

  protected static boolean itIsMtdSig(YaaClz topClz, Method mtd) {
    if (Modifier.isAbstract((mtd.getModifiers()))) {
      return true;
    }
    return topClz.category == TypeCategory.trait_c && !mtd.isDefault();
  }

  protected static Class<?> getJvmClz(String jvmClzName) {
    Class<?> final$clz;
    try {
      final$clz = forName(jvmClzName, false, JMold.yaa_compiler_loader);
    } catch (ClassNotFoundException e) {
      return null;
    }
    return final$clz;
  }

  protected static Object member$in$clz(Class<?> ci, String mbName) {
    for (var clz : ci.getClasses()) {
      if (mbName.equals(clz.getSimpleName())) {
        return clz;
      }
    }
    for (var mtd : ci.getMethods()) {
      if (Modifier.isStatic(mtd.getModifiers())) {
        if (Modifier.isPublic(mtd.getModifiers()) && mtd.getName().equals(mbName)) {
          return mtd;
        }
      }
    }
    for (var field : ci.getFields()) {
      if (Modifier.isStatic(field.getModifiers())) {
        if (Modifier.isPublic(field.getModifiers()) && field.getName().equals(mbName)) {
          return field;
        }
      }
    }
    return null;
  }
}
