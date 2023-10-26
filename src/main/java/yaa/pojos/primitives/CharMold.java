package yaa.pojos.primitives;

import yaa.pojos.MtdPack;
import yaa.pojos.YaaClz;
import yaa.pojos.YaaFun;

import java.util.LinkedList;

import static yaa.pojos.GlobalData.*;

public class CharMold {
  public static YaaClz newChar() {
    var clz = new YaaClz(char$name);

    var toStringMtd = MoldUtils.toStringMtds(char$name);
    clz.instanceMethods.put("toString", toStringMtd);
    var hashCodeMtd = MoldUtils.hashCodeMtds(char$name);
    clz.instanceMethods.put("hashCode", hashCodeMtd);
    var getClassMtd = MoldUtils.getClassMtds(char$name);
    clz.instanceMethods.put("getClass", getClassMtd);

    var great$equal$method = greatEqualMtds();
    clz.instanceMethods.put(great$equal$method.name, great$equal$method);
    var less$equal$method = lessEqualMtds();
    clz.instanceMethods.put(less$equal$method.name, less$equal$method);

    clz.instanceMethods.put(plus_op_name, plusMtds());

    var box$fun = new YaaFun("box");
    box$fun.type = new YaaClz("java.lang.Character");
    var box$mtd = new LinkedList<YaaFun>();
    box$mtd.add(box$fun);
    clz.instanceMethods.put("box", new MtdPack(box$mtd));

    return clz;
  }

  private static MtdPack lessEqualMtds() {
    var mtd$list = new LinkedList<YaaFun>();

    var right$char = new$fun(lesser_equal_op_name);
    right$char.parameters.add(new YaaClz(char$name));
    right$char.type = new YaaClz(boole$name);
    mtd$list.add(right$char);

    var right$int = new$fun(lesser_equal_op_name);
    right$int.parameters.add(new YaaClz(int$name));
    right$int.type = new YaaClz(boole$name);
    mtd$list.add(right$int);

    var right$long = new$fun(lesser_equal_op_name);
    right$long.parameters.add(new YaaClz(long$name));
    right$long.type = new YaaClz(boole$name);
    mtd$list.add(right$long);

    var right$float = new$fun(lesser_equal_op_name);
    right$float.parameters.add(new YaaClz(float$name));
    right$float.type = new YaaClz(boole$name);
    mtd$list.add(right$float);

    var right$Double = new$fun(lesser_equal_op_name);
    right$Double.parameters.add(new YaaClz(double$name));
    right$Double.type = new YaaClz(boole$name);
    mtd$list.add(right$Double);

    var right$Short = new$fun(lesser_equal_op_name);
    right$Short.parameters.add(new YaaClz(short$name));
    right$Short.type = new YaaClz(boole$name);
    mtd$list.add(right$Short);

    var right$Byte = new$fun(lesser_equal_op_name);
    right$Byte.parameters.add(new YaaClz(byte$name));
    right$Byte.type = new YaaClz(boole$name);
    mtd$list.add(right$Byte);

    return new MtdPack(mtd$list, lesser_equal_op_name);
  }

  private static MtdPack greatEqualMtds() {
    var mtd$list = new LinkedList<YaaFun>();

    var right$char = new$fun(greater_equal_op_name);
    right$char.parameters.add(new YaaClz(char$name));
    right$char.type = new YaaClz(boole$name);
    mtd$list.add(right$char);

    var right$int = new$fun(greater_equal_op_name);
    right$int.parameters.add(new YaaClz(int$name));
    right$int.type = new YaaClz(boole$name);
    mtd$list.add(right$int);

    var right$long = new$fun(greater_equal_op_name);
    right$long.parameters.add(new YaaClz(long$name));
    right$long.type = new YaaClz(boole$name);
    mtd$list.add(right$long);

    var right$float = new$fun(greater_equal_op_name);
    right$float.parameters.add(new YaaClz(float$name));
    right$float.type = new YaaClz(boole$name);
    mtd$list.add(right$float);

    var right$Double = new$fun(greater_equal_op_name);
    right$Double.parameters.add(new YaaClz(double$name));
    right$Double.type = new YaaClz(boole$name);
    mtd$list.add(right$Double);

    var right$Short = new$fun(greater_equal_op_name);
    right$Short.parameters.add(new YaaClz(short$name));
    right$Short.type = new YaaClz(boole$name);
    mtd$list.add(right$Short);

    var right$Byte = new$fun(greater_equal_op_name);
    right$Byte.parameters.add(new YaaClz(byte$name));
    right$Byte.type = new YaaClz(boole$name);
    mtd$list.add(right$Byte);

    return new MtdPack(mtd$list, greater_equal_op_name);
  }

  private static MtdPack plusMtds() {
    var mtd$list = new LinkedList<YaaFun>();

    var right$string = new$funWithString();
    right$string.parameters.add(new YaaClz("java.lang.String"));
    mtd$list.add(right$string);

    var right$int = new$funWithString();
    right$int.parameters.add(new YaaClz(int$name));
    mtd$list.add(right$int);

    var right$long = new$funWithString();
    right$long.parameters.add(new YaaClz(int$name));
    mtd$list.add(right$long);

    var right$float = new$funWithString();
    right$float.parameters.add(new YaaClz(float$name));
    mtd$list.add(right$float);

    var right$Double = new$funWithString();
    right$Double.parameters.add(new YaaClz(double$name));
    mtd$list.add(right$Double);

    var right$Short = new$funWithString();
    right$Short.parameters.add(new YaaClz(short$name));
    mtd$list.add(right$Short);

    var right$Byte = new$funWithString();
    right$Byte.parameters.add(new YaaClz(byte$name));
    mtd$list.add(right$Byte);

    var right$boole = new$funWithString();
    right$boole.parameters.add(new YaaClz(boole$name));
    mtd$list.add(right$boole);

    var right$char = new$funWithString();
    right$char.parameters.add(new YaaClz(char$name));
    mtd$list.add(right$char);

    return new MtdPack(mtd$list, plus_op_name);
  }

  private static YaaFun new$funWithString() {
    var fun = new YaaFun(plus_op_name, char$name);
    fun.type = new YaaClz("java.lang.String");
    return fun;
  }

  private static YaaFun new$fun(String mtd$name) {
    return new YaaFun(mtd$name, char$name);
  }
}
