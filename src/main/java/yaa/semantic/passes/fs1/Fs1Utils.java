package yaa.semantic.passes.fs1;

public class Fs1Utils {
//  protected static void isItDefined(String common_name, String address) {
//    var definedInfo = GlobalData.fs1.getSymbol(common_name);
//    if (definedInfo != null) {
//      if (new JMold().newClz("java.lang." + common_name) != null) {
//        return;
//      }
//      if (definedInfo.isPredefined) {
//        return;
//      }
//      throw new YaaError(
//        address, "The name \"" + common_name
//        + "\" is already used at " + definedInfo.lineInfo
//      );
//    }
//  }

  public static String stringOfCollection(Object[] items) {
    if (items.length == 0) {
      return "";
    }
    if (items.length == 1) {
      return items[0].toString();
    }
    var sb = new StringBuilder();
    sb.append(items[0].toString());
    for (int i = 1; i < items.length; i++) {
      sb.append("\n   ").append(items[i]);
    }
    return sb.toString();
  }
}