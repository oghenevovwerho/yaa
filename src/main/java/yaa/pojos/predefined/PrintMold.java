package yaa.pojos.predefined;

import yaa.pojos.MtdPack;
import yaa.pojos.YaaClz;
import yaa.pojos.YaaFun;
import yaa.pojos.primitives.ArrayMold;

import java.util.LinkedList;

import static yaa.pojos.GlobalData.*;

public class PrintMold {
  public static MtdPack printMtd(String name) {
    var mtd$list = new LinkedList<YaaFun>();

    var right$string = new YaaFun(name, "java/io/PrintStream");
    right$string.parameters.add(new YaaClz("java.lang.String"));
    mtd$list.add(right$string);

    var right$int = new YaaFun(name, "java/io/PrintStream");
    right$int.parameters.add(new YaaClz(int$name));
    mtd$list.add(right$int);

    var right$long = new YaaFun(name, "java/io/PrintStream");
    right$long.parameters.add(new YaaClz(long$name));
    mtd$list.add(right$long);

    var right$float = new YaaFun(name, "java/io/PrintStream");
    right$float.parameters.add(new YaaClz(float$name));
    mtd$list.add(right$float);

    var right$Double = new YaaFun(name, "java/io/PrintStream");
    right$Double.parameters.add(new YaaClz(double$name));
    mtd$list.add(right$Double);

    var right$boole = new YaaFun(name, "java/io/PrintStream");
    right$boole.parameters.add(new YaaClz(boole$name));
    mtd$list.add(right$boole);

    var right$char = new YaaFun(name, "java/io/PrintStream");
    right$char.parameters.add(new YaaClz(char$name));
    mtd$list.add(right$char);

    //for no parameter, i.e println()
    mtd$list.add(new YaaFun(name, "java/io/PrintStream"));

    var right$object = new YaaFun(name, "java/io/PrintStream");
    right$object.parameters.add(new YaaClz(object$name));
    mtd$list.add(right$object);

    var type$arguments = new LinkedList<YaaClz>();
    type$arguments.add(new YaaClz(char$name));
    var array$type = ArrayMold.newArray().changeCBounds(type$arguments);

    var right$char$array = new YaaFun(name, "java/io/PrintStream");
    right$char$array.parameters.add(array$type);
    mtd$list.add(right$char$array);

    var print_pack = new MtdPack(mtd$list, name);
    //print_pack.isPredefined = true;
    return print_pack;
  }
}
