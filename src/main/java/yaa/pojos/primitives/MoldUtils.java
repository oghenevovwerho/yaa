package yaa.pojos.primitives;

import yaa.pojos.MtdPack;
import yaa.pojos.YaaClz;
import yaa.pojos.YaaFun;

import java.util.ArrayList;

import static yaa.pojos.GlobalData.*;

public class MoldUtils {
  protected static MtdPack equalMtds(String clzName) {
    var mtd$list = new ArrayList<YaaFun>();

    var right$int = new YaaFun("equals", clzName);
    right$int.parameters.add(new YaaClz(int$name));
    right$int.type = new YaaClz(boole$name);
    mtd$list.add(right$int);

    var right$long = new YaaFun("equals", clzName);
    right$long.parameters.add(new YaaClz(long$name));
    right$long.type = new YaaClz(boole$name);
    mtd$list.add(right$long);

    var right$float = new YaaFun("equals", clzName);
    right$float.parameters.add(new YaaClz(float$name));
    right$float.type = new YaaClz(boole$name);
    mtd$list.add(right$float);

    var right$Double = new YaaFun("equals", clzName);
    right$Double.parameters.add(new YaaClz(double$name));
    right$Double.type = new YaaClz(boole$name);
    mtd$list.add(right$Double);

    var right$Short = new YaaFun("equals", clzName);
    right$Short.parameters.add(new YaaClz(short$name));
    right$Short.type = new YaaClz(boole$name);
    mtd$list.add(right$Short);

    var right$Byte = new YaaFun("equals", clzName);
    right$Byte.parameters.add(new YaaClz(byte$name));
    right$Byte.type = new YaaClz(boole$name);
    mtd$list.add(right$Byte);

    var right$object = new YaaFun("equals", clzName);
    right$object.parameters.add(new YaaClz(object$name));
    right$object.type = new YaaClz(boole$name);
    mtd$list.add(right$object);

    return new MtdPack(mtd$list, "equals");
  }

  protected static MtdPack toStringMtds(String clzName) {
    var mtd$list = new ArrayList<YaaFun>();

    var right$int = new YaaFun("toString", clzName);
    right$int.type = new YaaClz("java.lang.String");
    mtd$list.add(right$int);

    return new MtdPack(mtd$list, "toString");
  }

  protected static MtdPack hashCodeMtds(String clzName) {
    var mtd$list = new ArrayList<YaaFun>();

    var right$int = new YaaFun("hashCode", clzName);
    right$int.type = new YaaClz(int$name);
    mtd$list.add(right$int);

    return new MtdPack(mtd$list, "hashCode");
  }

  protected static MtdPack getClassMtds(String clzName) {
    var mtd$list = new ArrayList<YaaFun>();

    var right$int = new YaaFun("getClass", clzName);
    right$int.type = new YaaClz("java.lang.Class");
    mtd$list.add(right$int);

    return new MtdPack(mtd$list, "getClass");
  }
}
