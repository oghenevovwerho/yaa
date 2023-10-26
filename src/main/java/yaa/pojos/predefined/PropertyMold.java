package yaa.pojos.predefined;

import yaa.pojos.MtdPack;
import yaa.pojos.YaaClz;
import yaa.pojos.YaaFun;

import java.util.LinkedList;

public class PropertyMold {
  public static MtdPack property() {
    var mtd$list = new LinkedList<YaaFun>();

    var right$string = new YaaFun("getProperty", "java/lang/System");
    right$string.parameters.add(new YaaClz("java.lang.String"));
    right$string.type = new YaaClz("java.lang.String");
    mtd$list.add(right$string);

    var print_pack = new MtdPack(mtd$list, "getProperty");
   // print_pack.isPredefined = true;
    return print_pack;
  }
}
