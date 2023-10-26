package yaa.pojos;

import static yaa.pojos.GlobalData.*;

public class NameUtils {
  public static String dottedStoreName(String simpleName) {
    var path = fs1.path.substring(0, fs1.path.length() - 4); //remove the .yaa
    return path.replace(sp, ".") + "." + simpleName;
  }

  public static String clzCodeName(String simpleName) {
    StateUtils.clz$count = 1;
    var pkg = fs.path.substring(0, fs.path.lastIndexOf(".")).replace(GlobalData.sp, "/");
    StringBuilder name = new StringBuilder(pkg + "/" + simpleName);
    var clzNames = GlobalData.usedClzNames.get(fs1.path);
    while (clzNames.contains(name.toString())) {
      name.append(StateUtils.clz$count++);
    }
    return name.toString();
  }

  public static String top$elements$clz$name() {
    StateUtils.clz$count = 1;
    var path = fs.path;
    var lastDotIndex = path.lastIndexOf(".");
    var pkg = path.substring(0, lastDotIndex).replace(sp, "/");
    var simple$name = path.substring(path.lastIndexOf(sp) + 1, lastDotIndex);
    StringBuilder name = new StringBuilder();
    name.append(pkg).append("/").append(simple$name.replace(sp, "/"));
    var clzNames = usedClzNames.get(fs2.path);
    while (clzNames.contains(name.toString())) {
      name.append(StateUtils.clz$count++);
    }
    return name.toString();
  }

  public static String generateName4lambda() {
    return "lambda" + System.nanoTime();
  }
}
