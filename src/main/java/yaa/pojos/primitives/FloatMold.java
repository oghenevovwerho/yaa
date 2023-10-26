package yaa.pojos.primitives;

import yaa.pojos.MtdPack;
import yaa.pojos.YaaClz;
import yaa.pojos.YaaFun;
import yaa.pojos.jMold.JMold;

import java.util.LinkedList;

import static yaa.pojos.GlobalData.*;
import static yaa.pojos.GlobalData.boole$name;

public class FloatMold {
  public static YaaClz newFloat() {
    var clz = new YaaClz(float$name);
    var right$mtds = plusMtds();
    clz.instanceMethods.put(right$mtds.name, right$mtds);
    var modulo$mtds = moduloMtds();
    clz.instanceMethods.put(modulo$mtds.name, modulo$mtds);
    var minus$mtds = minusMtds();
    clz.instanceMethods.put(minus$mtds.name, minus$mtds);
    var less$mtd = lesserMtds();
    clz.instanceMethods.put(less$mtd.name, less$mtd);
    var great$mtd = greaterMtds();
    clz.instanceMethods.put(great$mtd.name, great$mtd);
    var less$equal$mtd = lessEqualMtds();
    clz.instanceMethods.put(less$equal$mtd.name, less$equal$mtd);
    var great$equal$method = greatEqualMtds();
    clz.instanceMethods.put(great$equal$method.name, great$equal$method);
    var times$mtds = timesMtds();
    clz.instanceMethods.put(times$mtds.name, times$mtds);
    var root$mtds = rootMtds();
    clz.instanceMethods.put(root$mtds.name, root$mtds);
    var power$mtds = powerMtds();
    clz.instanceMethods.put(power$mtds.name, power$mtds);
    var divide$mtds = divideMtds();
    clz.instanceMethods.put(divide$mtds.name, divide$mtds);
    var unary$mtd$list = unaryPlus();
    clz.instanceMethods.put(unary$mtd$list.name, unary$mtd$list);
    var unary$minus$mtds = unaryMinus();
    clz.instanceMethods.put(unary$minus$mtds.name, unary$minus$mtds);
    var to$methods = toMethod();
    clz.instanceMethods.put(to$methods.name, to$methods);
    var toStringMtd = MoldUtils.toStringMtds(float$name);
    clz.instanceMethods.put("toString", toStringMtd);
    var hashCodeMtd = MoldUtils.hashCodeMtds(float$name);
    clz.instanceMethods.put("hashCode", hashCodeMtd);
    var getClassMtd = MoldUtils.getClassMtds(float$name);
    clz.instanceMethods.put("getClass", getClassMtd);

    var box$fun = new YaaFun("box");
    box$fun.type = new YaaClz("java.lang.Float");
    var box$mtd = new LinkedList<YaaFun>();
    box$mtd.add(box$fun);
    clz.instanceMethods.put("box", new MtdPack(box$mtd));
    return clz;
  }

  private static MtdPack toMethod() {
    var mtd$list = new LinkedList<YaaFun>();
    var long$stream = new JMold().newClz("java.util.stream.LongStream");

    var right$int = new$fun("to");
    right$int.parameters.add(new YaaClz(int$name));
    right$int.type = long$stream;
    mtd$list.add(right$int);

    var right$long = new$fun("to");
    right$long.parameters.add(new YaaClz(long$name));
    right$long.type = long$stream;
    mtd$list.add(right$long);
    return new MtdPack(mtd$list, "to");
  }

  private static MtdPack unaryPlus() {
    var mtd$list = new$fun(unary_plus_op_name);
    mtd$list.type = new YaaClz(float$name);
    var unary$mtd$list = new LinkedList<YaaFun>();
    unary$mtd$list.add(mtd$list);
    return new MtdPack(unary$mtd$list, unary_plus_op_name);
  }

  private static MtdPack unaryMinus() {
    var mtd$list = new$fun(unary_minus_op_name);
    mtd$list.type = new YaaClz(float$name);
    var unary$mtd$list = new LinkedList<YaaFun>();
    unary$mtd$list.add(mtd$list);
    return new MtdPack(unary$mtd$list, unary_minus_op_name);
  }

  private static MtdPack rootMtds() {
    var mtd$list = new LinkedList<YaaFun>();
    var right$int = new$fun(root_op_name);
    right$int.parameters.add(new YaaClz(int$name));
    right$int.type = new YaaClz(float$name);
    mtd$list.add(right$int);

    var right$long = new$fun(root_op_name);
    right$long.parameters.add(new YaaClz(long$name));
    right$long.type = new YaaClz(float$name);
    mtd$list.add(right$long);

    var right$float = new$fun(root_op_name);
    right$float.parameters.add(new YaaClz(float$name));
    right$float.type = new YaaClz(float$name);
    mtd$list.add(right$float);

    var right$Double = new$fun(root_op_name);
    right$Double.parameters.add(new YaaClz(double$name));
    right$Double.type = new YaaClz(double$name);
    mtd$list.add(right$Double);

    var right$Short = new$fun(root_op_name);
    right$Short.parameters.add(new YaaClz(short$name));
    right$Short.type = new YaaClz(float$name);
    mtd$list.add(right$Short);

    var right$Byte = new$fun(root_op_name);
    right$Byte.parameters.add(new YaaClz(byte$name));
    right$Byte.type = new YaaClz(float$name);
    mtd$list.add(right$Byte);

    return new MtdPack(mtd$list, root_op_name);
  }

  private static MtdPack powerMtds() {
    var mtd$list = new LinkedList<YaaFun>();

    var right$int = new$fun(power_op_name);
    right$int.parameters.add(new YaaClz(int$name));
    right$int.type = new YaaClz(float$name);
    mtd$list.add(right$int);

    var right$long = new$fun(power_op_name);
    right$long.parameters.add(new YaaClz(long$name));
    right$long.type = new YaaClz(float$name);
    mtd$list.add(right$long);

    var right$float = new$fun(power_op_name);
    right$float.parameters.add(new YaaClz(float$name));
    right$float.type = new YaaClz(float$name);
    mtd$list.add(right$float);

    var right$Double = new$fun(power_op_name);
    right$Double.parameters.add(new YaaClz(double$name));
    right$Double.type = new YaaClz(double$name);
    mtd$list.add(right$Double);

    var right$Short = new$fun(power_op_name);
    right$Short.parameters.add(new YaaClz(short$name));
    right$Short.type = new YaaClz(float$name);
    mtd$list.add(right$Short);

    var right$Byte = new$fun(power_op_name);
    right$Byte.parameters.add(new YaaClz(byte$name));
    right$Byte.type = new YaaClz(float$name);
    mtd$list.add(right$Byte);

    return new MtdPack(mtd$list, power_op_name);
  }

  private static MtdPack divideMtds() {
    var mtd$list = new LinkedList<YaaFun>();

    var right$int = new$fun(divide_op_name);
    right$int.parameters.add(new YaaClz(int$name));
    right$int.type = new YaaClz(float$name);
    mtd$list.add(right$int);

    var right$long = new$fun(divide_op_name);
    right$long.parameters.add(new YaaClz(long$name));
    right$long.type = new YaaClz(float$name);
    mtd$list.add(right$long);

    var right$float = new$fun(divide_op_name);
    right$float.parameters.add(new YaaClz(float$name));
    right$float.type = new YaaClz(float$name);
    mtd$list.add(right$float);

    var right$Double = new$fun(divide_op_name);
    right$Double.parameters.add(new YaaClz(double$name));
    right$Double.type = new YaaClz(double$name);
    mtd$list.add(right$Double);

    var right$Short = new$fun(divide_op_name);
    right$Short.parameters.add(new YaaClz(short$name));
    right$Short.type = new YaaClz(float$name);
    mtd$list.add(right$Short);

    var right$Byte = new$fun(divide_op_name);
    right$Byte.parameters.add(new YaaClz(byte$name));
    right$Byte.type = new YaaClz(float$name);
    mtd$list.add(right$Byte);

    return new MtdPack(mtd$list, divide_op_name);
  }

  private static MtdPack timesMtds() {
    var mtd$list = new LinkedList<YaaFun>();

    var right$int = new$fun(times_op_name);
    right$int.parameters.add(new YaaClz(int$name));
    right$int.type = new YaaClz(float$name);
    mtd$list.add(right$int);

    var right$long = new$fun(times_op_name);
    right$long.parameters.add(new YaaClz(long$name));
    right$long.type = new YaaClz(float$name);
    mtd$list.add(right$long);

    var right$float = new$fun(times_op_name);
    right$float.parameters.add(new YaaClz(float$name));
    right$float.type = new YaaClz(float$name);
    mtd$list.add(right$float);

    var right$Double = new$fun(times_op_name);
    right$Double.parameters.add(new YaaClz(double$name));
    right$Double.type = new YaaClz(double$name);
    mtd$list.add(right$Double);

    var right$Short = new$fun(times_op_name);
    right$Short.parameters.add(new YaaClz(short$name));
    right$Short.type = new YaaClz(float$name);
    mtd$list.add(right$Short);

    var right$Byte = new$fun(times_op_name);
    right$Byte.parameters.add(new YaaClz(byte$name));
    right$Byte.type = new YaaClz(float$name);
    mtd$list.add(right$Byte);

    return new MtdPack(mtd$list, times_op_name);
  }

  private static MtdPack plusMtds() {
    var mtd$list = new LinkedList<YaaFun>();

    var right$int = new$fun(plus_op_name);
    right$int.parameters.add(new YaaClz(int$name));
    right$int.type = new YaaClz(float$name);
    mtd$list.add(right$int);

    var right$long = new$fun(plus_op_name);
    right$long.parameters.add(new YaaClz(long$name));
    right$long.type = new YaaClz(float$name);
    mtd$list.add(right$long);

    var right$float = new$fun(plus_op_name);
    right$float.parameters.add(new YaaClz(float$name));
    right$float.type = new YaaClz(float$name);
    mtd$list.add(right$float);

    var right$Double = new$fun(plus_op_name);
    right$Double.parameters.add(new YaaClz(double$name));
    right$Double.type = new YaaClz(double$name);
    mtd$list.add(right$Double);

    var right$Short = new$fun(plus_op_name);
    right$Short.parameters.add(new YaaClz(short$name));
    right$Short.type = new YaaClz(float$name);
    mtd$list.add(right$Short);

    var right$Byte = new$fun(plus_op_name);
    right$Byte.parameters.add(new YaaClz(byte$name));
    right$Byte.type = new YaaClz(float$name);
    mtd$list.add(right$Byte);

    return new MtdPack(mtd$list, plus_op_name);
  }

  private static MtdPack moduloMtds() {
    var mtd$list = new LinkedList<YaaFun>();

    var right$int = new$fun(modulo_op_name);
    right$int.parameters.add(new YaaClz(int$name));
    right$int.type = new YaaClz(float$name);
    mtd$list.add(right$int);

    var right$long = new$fun(modulo_op_name);
    right$long.parameters.add(new YaaClz(long$name));
    right$long.type = new YaaClz(float$name);
    mtd$list.add(right$long);

    var right$float = new$fun(modulo_op_name);
    right$float.parameters.add(new YaaClz(float$name));
    right$float.type = new YaaClz(float$name);
    mtd$list.add(right$float);

    var right$Double = new$fun(modulo_op_name);
    right$Double.parameters.add(new YaaClz(double$name));
    right$Double.type = new YaaClz(double$name);
    mtd$list.add(right$Double);

    var right$Short = new$fun(modulo_op_name);
    right$Short.parameters.add(new YaaClz(short$name));
    right$Short.type = new YaaClz(float$name);
    mtd$list.add(right$Short);

    var right$Byte = new$fun(modulo_op_name);
    right$Byte.parameters.add(new YaaClz(byte$name));
    right$Byte.type = new YaaClz(float$name);
    mtd$list.add(right$Byte);

    return new MtdPack(mtd$list, modulo_op_name);
  }

  private static MtdPack minusMtds() {
    var mtd$list = new LinkedList<YaaFun>();

    var right$int = new$fun(minus_op_name);
    right$int.parameters.add(new YaaClz(int$name));
    right$int.type = new YaaClz(float$name);
    mtd$list.add(right$int);

    var right$long = new$fun(minus_op_name);
    right$long.parameters.add(new YaaClz(long$name));
    right$long.type = new YaaClz(float$name);
    mtd$list.add(right$long);

    var right$float = new$fun(minus_op_name);
    right$float.parameters.add(new YaaClz(float$name));
    right$float.type = new YaaClz(float$name);
    mtd$list.add(right$float);

    var right$Double = new$fun(minus_op_name);
    right$Double.parameters.add(new YaaClz(double$name));
    right$Double.type = new YaaClz(double$name);
    mtd$list.add(right$Double);

    var right$Short = new$fun(minus_op_name);
    right$Short.parameters.add(new YaaClz(short$name));
    right$Short.type = new YaaClz(float$name);
    mtd$list.add(right$Short);

    var right$Byte = new$fun(minus_op_name);
    right$Byte.parameters.add(new YaaClz(byte$name));
    right$Byte.type = new YaaClz(float$name);
    mtd$list.add(right$Byte);

    return new MtdPack(mtd$list, minus_op_name);
  }

  private static MtdPack lesserMtds() {
    var mtd$list = new LinkedList<YaaFun>();

    var right$int = new$fun(lesser_op_name);
    right$int.parameters.add(new YaaClz(int$name));
    right$int.type = new YaaClz(boole$name);
    mtd$list.add(right$int);

    var right$long = new$fun(lesser_op_name);
    right$long.parameters.add(new YaaClz(long$name));
    right$long.type = new YaaClz(boole$name);
    mtd$list.add(right$long);

    var right$float = new$fun(lesser_op_name);
    right$float.parameters.add(new YaaClz(float$name));
    right$float.type = new YaaClz(boole$name);
    mtd$list.add(right$float);

    var right$Double = new$fun(lesser_op_name);
    right$Double.parameters.add(new YaaClz(double$name));
    right$Double.type = new YaaClz(boole$name);
    mtd$list.add(right$Double);

    var right$Short = new$fun(lesser_op_name);
    right$Short.parameters.add(new YaaClz(short$name));
    right$Short.type = new YaaClz(boole$name);
    mtd$list.add(right$Short);

    var right$Byte = new$fun(lesser_op_name);
    right$Byte.parameters.add(new YaaClz(byte$name));
    right$Byte.type = new YaaClz(boole$name);
    mtd$list.add(right$Byte);

    return new MtdPack(mtd$list, lesser_op_name);
  }

  private static MtdPack greaterMtds() {
    var mtd$list = new LinkedList<YaaFun>();

    var right$int = new$fun(greater_op_name);
    right$int.parameters.add(new YaaClz(int$name));
    right$int.type = new YaaClz(boole$name);
    mtd$list.add(right$int);

    var right$long = new$fun(greater_op_name);
    right$long.parameters.add(new YaaClz(long$name));
    right$long.type = new YaaClz(boole$name);
    mtd$list.add(right$long);

    var right$float = new$fun(greater_op_name);
    right$float.parameters.add(new YaaClz(float$name));
    right$float.type = new YaaClz(boole$name);
    mtd$list.add(right$float);

    var right$Double = new$fun(greater_op_name);
    right$Double.parameters.add(new YaaClz(double$name));
    right$Double.type = new YaaClz(boole$name);
    mtd$list.add(right$Double);

    var right$Short = new$fun(greater_op_name);
    right$Short.parameters.add(new YaaClz(short$name));
    right$Short.type = new YaaClz(boole$name);
    mtd$list.add(right$Short);

    var right$Byte = new$fun(greater_op_name);
    right$Byte.parameters.add(new YaaClz(byte$name));
    right$Byte.type = new YaaClz(boole$name);
    mtd$list.add(right$Byte);

    return new MtdPack(mtd$list, greater_op_name);
  }

  private static MtdPack lessEqualMtds() {
    var mtd$list = new LinkedList<YaaFun>();

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

  private static YaaFun new$fun(String mtd$name) {
    return new YaaFun(mtd$name, float$name);
  }
}