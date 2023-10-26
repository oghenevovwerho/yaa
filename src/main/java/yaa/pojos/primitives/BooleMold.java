package yaa.pojos.primitives;

import yaa.pojos.MtdPack;
import yaa.pojos.YaaClz;
import yaa.pojos.YaaFun;

import java.util.ArrayList;

import static yaa.pojos.GlobalData.*;

public class BooleMold {
  public static YaaClz newBoole() {
    var clz = new YaaClz(boole$name);

    var or$fun = new YaaFun(or_op_name, boole$name);
    or$fun.parameters.add(new YaaClz(boole$name));
    or$fun.type = new YaaClz(boole$name);
    var or$mtd = new ArrayList<YaaFun>();
    or$mtd.add(or$fun);
    clz.instanceMethods.put(or_op_name, new MtdPack(or$mtd));

    var not$fun = new YaaFun(unary_not_op_name, boole$name);
    not$fun.type = new YaaClz(boole$name);
    var not$mtd = new ArrayList<YaaFun>();
    not$mtd.add(not$fun);
    clz.instanceMethods.put(unary_not_op_name, new MtdPack(not$mtd));

    var and$fun = new YaaFun(and_op_name, boole$name);
    and$fun.parameters.add(new YaaClz(boole$name));
    and$fun.type = new YaaClz(boole$name);
    var and$mtd = new ArrayList<YaaFun>();
    and$mtd.add(and$fun);
    clz.instanceMethods.put(and_op_name, new MtdPack(and$mtd));

    var box$fun = new YaaFun("box");
    box$fun.type = new YaaClz("java.lang.Boolean");
    var box$mtd = new ArrayList<YaaFun>();
    box$mtd.add(box$fun);
    clz.instanceMethods.put("box", new MtdPack(box$mtd));

    var toStringMtd = MoldUtils.toStringMtds(boole$name);
    clz.instanceMethods.put("toString", toStringMtd);
    var hashCodeMtd = MoldUtils.hashCodeMtds(boole$name);
    clz.instanceMethods.put("hashCode", hashCodeMtd);
    var getClassMtd = MoldUtils.getClassMtds(boole$name);
    clz.instanceMethods.put("getClass", getClassMtd);

    return clz;
  }
}
