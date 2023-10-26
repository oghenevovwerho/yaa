package yaa.pojos;

import yaa.ast.Anonymous;
import yaa.ast.Init;
import yaa.ast.NewFun;
import yaa.pojos.jMold.JMold;
import yaa.pojos.primitives.ArrayMold;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static yaa.pojos.GlobalData.*;

public class YaaTable {
  private final Map<String, YaaInfo> symbols = new HashMap<>();
  public YaaTable parent;
  private static final Map<String, YaaField> cachedFields = new HashMap<>(2);
  private static final YaaMeta mcMeta;
  private static final MtdPack intStreamRangeMtd;
  private static final Map<String, MtdPack> cachedMtds = new HashMap<>(10);

  static {
    mcMeta = MetaConfig.config();
    var intStream = new JMold().newClz("java.util.stream.IntStream");
    intStreamRangeMtd = intStream.getStaticMethod("range");
  }

  public YaaTable() {
  }

  public void putSymbol(String symbolName, YaaInfo symbol) {
    symbols.put(symbolName, symbol);
  }

  public YaaInfo getSymbol(String symbolName) {
    var symbol = symbols.get(symbolName);
    if (symbol != null) {
      return symbol;
    }
    if (parent != null) {
      return parent.getSymbol(symbolName);
    }
    if (symbolName.equals(configMetaRefName)) {
      return mcMeta;
    }
    var predefinedClz = switch (symbolName) {
      case null$name -> null$clz;
      case int$name -> int$clz;
      case boole$name -> boole$clz;
      case long$name -> long$clz;
      case short$name -> short$clz;
      case byte$name -> byte$clz;
      case float$name -> float$clz;
      case double$name -> double$clz;
      case char$name -> char$clz;
      case array$name -> ArrayMold.newArray();
      default -> {
        if (langPkgClassNames.contains(symbolName)) {
          var cached_class = cachedClass.get("java.lang." + symbolName);
          if (cached_class != null) {
            yield cached_class;
          }
          var clz = new JMold().newClz("java.lang." + symbolName);
          cachedClass.put("java.lang." + symbolName, clz);
          yield clz;
        }
        yield null;
      }
    };
    if (predefinedClz != null) {
      return predefinedClz;
    }
    if (systemStaticMtdNames.contains(symbolName)) {
      var mtdPack = cachedMtds.get(symbolName);
      if (mtdPack != null) {
        return mtdPack;
      }
      var systemClass = new JMold().newClz("java.lang.System");
      var gotten_mtd = systemClass.getStaticMethod(symbolName);
      cachedMtds.put(symbolName, gotten_mtd);
      return gotten_mtd;
    }
    if (symbolName.equals("range")) {
      return intStreamRangeMtd;
    }
    if (symbolName.equals("cmd")) {
      var cmd_field = new YaaField("cmd", true);
      cmd_field.itIsStatic = true;
      cmd_field.isPredefined = true;
      var array = ArrayMold.newArray();
      var arguments = new ArrayList<YaaClz>(1);
      arguments.add(new YaaClz("java.lang.String"));
      cmd_field.data = array.changeCBounds(arguments);
      cachedFields.put("cmd", cmd_field);
      return cmd_field;
    }
    if (systemStaticFdNames.contains(symbolName)) {
      var cached_field = cachedFields.get(symbolName);
      if (cached_field != null) {
        return cached_field;
      }
      var systemClass = new JMold().newClz("java.lang.System");
      var gotten_field = systemClass.getStaticField(symbolName);
      cachedFields.put(symbolName, gotten_field);
      return gotten_field;
    }
    return null;
  }

  public YaaInfo getSymbolInSameScope(String symbolName) {
    return symbols.get(symbolName);
  }

  //this is only used in the first pass, the usual getSymbol is not used because
  //the preludes such as "System.in" should not be included in the found symbols
  public YaaInfo getAlreadyDefinedSymbolInPass1(String symbolName) {
    var symbol = symbols.get(symbolName);
    if (symbol != null) {
      return symbol;
    }
    if (parent != null) {
      return parent.getAlreadyDefinedSymbolInPass1(symbolName);
    }
    return null;
  }
}