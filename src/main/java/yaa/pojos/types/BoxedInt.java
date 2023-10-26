package yaa.pojos.types;

import yaa.pojos.GlobalData;
import yaa.pojos.MtdPack;
import yaa.pojos.YaaFun;
import yaa.pojos.jMold.JMold;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static yaa.pojos.GlobalData.*;

public final class BoxedInt extends YaaType {
  public String name = "java.lang.Integer";

  public static final Map<String, MtdPack> instanceMethods = new HashMap<>(15);
  public final Map<String, MtdPack> staticMethods = new HashMap<>(33);
  private static final BoxedInt singletonBoxedInt = new BoxedInt();

  static {
    var intValueMtd = new YaaFun("intValue", List.of());
    intValueMtd.type = int$clz;
    intValueMtd.owner = "java/lang/Integer";
    instanceMethods.put("intValue", new MtdPack(List.of(intValueMtd), "intValue"));

    var byteValueMtd = new YaaFun("byteValue", List.of());
    byteValueMtd.type = byte$clz;
    byteValueMtd.owner = "java/lang/Integer";
    instanceMethods.put("byteValue", new MtdPack(List.of(byteValueMtd), "byteValue"));

    var doubleValueMtd = new YaaFun("doubleValue", List.of());
    doubleValueMtd.type = double$clz;
    doubleValueMtd.owner = "java/lang/Integer";
    instanceMethods.put("doubleValue", new MtdPack(List.of(doubleValueMtd), "doubleValue"));

    var floatValueMtd = new YaaFun("floatValue", List.of());
    floatValueMtd.type = float$clz;
    floatValueMtd.owner = "java/lang/Integer";
    instanceMethods.put("floatValue", new MtdPack(List.of(floatValueMtd), "floatValue"));

    var longValueMtd = new YaaFun("longValue", List.of());
    longValueMtd.type = long$clz;
    longValueMtd.owner = "java/lang/Integer";
    instanceMethods.put("longValue", new MtdPack(List.of(longValueMtd), "longValue"));

    var shortValueMtd = new YaaFun("shortValue", List.of());
    shortValueMtd.type = short$clz;
    shortValueMtd.owner = "java/lang/Integer";
    instanceMethods.put("shortValue", new MtdPack(List.of(shortValueMtd), "shortValue"));

    var getClassMtd = new YaaFun("getClass", List.of());
    getClassMtd.type = new JMold().newClz("java.lang.Class");
    getClassMtd.owner = "java/lang/Integer";
    instanceMethods.put("getClass", new MtdPack(List.of(getClassMtd), "getClass"));

    var hashCodeMtd = new YaaFun("hashCode", List.of());
    hashCodeMtd.type = int$clz;
    hashCodeMtd.owner = "java/lang/Integer";
    instanceMethods.put("hashCode", new MtdPack(List.of(hashCodeMtd), "hashCode"));

    var describeConstableMtd = new YaaFun("describeConstable", List.of());
    //var inputted = List.of();
    //describeConstableMtd.type = new JMold().newClz("java.util.Optional").changeCBounds(inputted);
    describeConstableMtd.owner = "java/lang/Integer";
    instanceMethods.put("describeConstable", new MtdPack(List.of(describeConstableMtd), "describeConstable"));

    var toStringMtd = new YaaFun("toString", List.of());
    toStringMtd.type = new JMold().newClz("java.lang.String");
    toStringMtd.owner = "java/lang/Integer";
    instanceMethods.put("toString", new MtdPack(List.of(toStringMtd), "toString"));
  }

  public BoxedInt() {
  }

  public BoxedInt getSingletonBoxedInt() {
    return singletonBoxedInt;
  }

  public String descriptor() {
    return "Ljava/lang/Integer;";
  }

  @Override
  public String toString() {
    return "java.lang.Integer";
  }
}