package yaa.pojos.primitives;

import yaa.pojos.*;

import java.util.LinkedList;

import static yaa.pojos.BoundState.clz_bound;
import static yaa.pojos.GlobalData.*;

public class ArrayMold {
  public static YaaClz newArray() {
    //There are no index and index assign methods here
    //This is to make array operations feel like those for list
    var clz = new YaaClz(array$name);
    var inputted = new YaaClz("T");
    inputted.boundState = clz_bound;
    inputted.variance = YaaClzVariance.covariant;
    inputted.parent = object$clz;
    inputted.cbIndex = 0;
    clz.inputted.add(inputted);

    var setMtds = new LinkedList<YaaFun>();
    var setMtd = new YaaFun("set", array$name);
    setMtd.hasClzTypeParam = true;
    setMtd.parameters.add(new YaaClz(int$name));
    var settableParam = new YaaClz("T");
    settableParam.boundState = clz_bound;
    settableParam.parent = object$clz;
    settableParam.cbIndex = 0;
    setMtd.parameters.add(settableParam);
    setMtds.add(setMtd);
    var setMtdPack = new MtdPack(setMtds, "set");

    var getMtds = new LinkedList<YaaFun>();
    var getMtd = new YaaFun("get", array$name);
    getMtd.hasClzTypeParam = true;
    var gotten = new YaaClz("T");
    gotten.parent = object$clz;
    gotten.boundState = clz_bound;
    gotten.cbIndex = 0;
    getMtd.type = gotten;
    getMtd.parameters.add(new YaaClz(int$name));
    getMtds.add(getMtd);
    var getMtdPack = new MtdPack(getMtds, "get");

    var array$value = new YaaClz(array$name);
    var return$inputted = new YaaClz("T");
    return$inputted.parent = object$clz;
    return$inputted.boundState = clz_bound;
    return$inputted.cbIndex = 0;
    array$value.inputted.add(return$inputted);

    var sizeMtds = new LinkedList<YaaFun>();
    var sizeMtd = new YaaFun("size", array$name);
    sizeMtd.type = new YaaClz(int$name);
    sizeMtds.add(sizeMtd);
    var sizeMtdPack = new MtdPack(sizeMtds, "size");

    var equalMtds = new LinkedList<YaaFun>();

    var int_array_fun = new YaaFun("equals", array$name);
    int_array_fun.type = boole$clz;
    var int_array_param = new YaaClz(array$name);
    int_array_param.inputted.add(int$clz);
    int_array_fun.parameters.add(int_array_param);

    var long_array_fun = new YaaFun("equals", array$name);
    long_array_fun.type = boole$clz;
    var long_array_param = new YaaClz(array$name);
    long_array_param.inputted.add(long$clz);
    long_array_fun.parameters.add(long_array_param);

    var float_array_fun = new YaaFun("equals", array$name);
    float_array_fun.type = boole$clz;
    var float_array_param = new YaaClz(array$name);
    float_array_param.inputted.add(float$clz);
    float_array_fun.parameters.add(float_array_param);

    var double_array_fun = new YaaFun("equals", array$name);
    double_array_fun.type = boole$clz;
    var double_array_param = new YaaClz(array$name);
    double_array_param.inputted.add(double$clz);
    double_array_fun.parameters.add(double_array_param);

    var short_array_fun = new YaaFun("equals", array$name);
    short_array_fun.type = boole$clz;
    var short_array_param = new YaaClz(array$name);
    short_array_param.inputted.add(short$clz);
    short_array_fun.parameters.add(short_array_param);

    var byte_array_fun = new YaaFun("equals", array$name);
    byte_array_fun.type = boole$clz;
    var byte_array_param = new YaaClz(array$name);
    byte_array_param.inputted.add(byte$clz);
    byte_array_fun.parameters.add(byte_array_param);

    var char_array_fun = new YaaFun("equals", array$name);
    char_array_fun.type = boole$clz;
    var char_array_param = new YaaClz(array$name);
    char_array_param.inputted.add(char$clz);
    char_array_fun.parameters.add(char_array_param);

    var boole_array_fun = new YaaFun("equals", array$name);
    boole_array_fun.type = boole$clz;
    var boole_array_param = new YaaClz(array$name);
    boole_array_param.inputted.add(boole$clz);
    boole_array_fun.parameters.add(boole_array_param);

    var object_array_fun = new YaaFun("equals", array$name);
    object_array_fun.type = boole$clz;
    var object_array_param = new YaaClz(array$name);
    object_array_param.inputted.add(object$clz);
    object_array_fun.parameters.add(object_array_param);

    equalMtds.add(int_array_fun);
    equalMtds.add(long_array_fun);
    equalMtds.add(float_array_fun);
    equalMtds.add(double_array_fun);
    equalMtds.add(short_array_fun);
    equalMtds.add(byte_array_fun);
    equalMtds.add(char_array_fun);
    equalMtds.add(boole_array_fun);
    equalMtds.add(object_array_fun);

    var equalMtdPack = new MtdPack(equalMtds, "equals");

    clz.instanceMethods.put("equals", equalMtdPack);
    clz.instanceMethods.put("get", getMtdPack);
    clz.instanceMethods.put("set", setMtdPack);
    clz.instanceMethods.put("size", sizeMtdPack);

    var int$init = new YaaInit();
    int$init.parameters.add(int$clz);

    clz.inits.add(new YaaInit());
    clz.inits.add(int$init);

    return clz;
  }
}