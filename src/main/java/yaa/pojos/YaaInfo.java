package yaa.pojos;

import com.esotericsoftware.kryo.Kryo;
import yaa.ast.*;
import yaa.parser.TkKind;
import yaa.parser.YaaToken;

import java.io.Serializable;
import java.lang.annotation.ElementType;
import java.lang.annotation.RetentionPolicy;
import java.util.*;

import static yaa.pojos.BoundState.unbounded;

public class YaaInfo implements Serializable {
  public String codeName;
  public int privacy = 0;
  public YaaClz typeParam;
  public int column;
  public int startLine;
  public String name;
  public int cbIndex = -1;
  public int mbIndex = -1;
  public BoundState boundState = unbounded;

  public String placeOfUSe() {
    return "[" + startLine + ": " + column + "]";
  }

  @Override
  public String toString() {
    return name;
  }

  public boolean isUnboundedAndNotPrimitive() {
    return boundState != unbounded && !isPrimitive();
  }

  public boolean isIBounded() {
    return cbIndex > -1 || mbIndex > -1;
  }

  public String descriptor() {
    return null;
  }

  public boolean isBoxed() {
    return false;
  }

  public boolean isPrimitive() {
    return false;
  }

  public boolean accepts(YaaInfo other) {
    return false;
  }

  public yaa.pojos.YaaFun acceptsMtd(List<YaaInfo> otherMtd) {
    return null;
  }

  public yaa.pojos.YaaInit acceptsInit(List<YaaInfo> otherInit) {
    return null;
  }

  // Note that Fury instances should be reused between
  // multiple serializations of different objects
  public static final Kryo kryo = new Kryo();

  static {
    // Registering types can reduce class name serialization overhead, but not mandatory.
    // If secure mode is enabled, all custom types must be registered.
    kryo.register(LinkedList.class);
    kryo.register(YaaClz.class);
    kryo.register(yaa.pojos.YaaClzVariance.class);
    kryo.register(yaa.pojos.MtdPack.class);
    kryo.register(yaa.pojos.MtdIsWhat.class);
    kryo.register(yaa.pojos.YaaFun.class);
    kryo.register(ArrayList.class);
    kryo.register(TreeMap.class);
    kryo.register(Map.class);
    kryo.register(Collection.class);
    kryo.register(List.class);
    kryo.register(yaa.pojos.YaaInit.class);
    kryo.register(HashMap.class);
    kryo.register(BoundState.class);
    kryo.register(TreeMap.class);
    kryo.register(yaa.pojos.YaaField.class);
    kryo.register(yaa.pojos.FieldIsWhat.class);
    kryo.register(yaa.pojos.Privacy.class);
    kryo.register(yaa.pojos.FunCallInfo.class);
    kryo.register(yaa.pojos.TypeCategory.class);
    kryo.register(NewFun.class);
    kryo.register(YaaInfo.class);
    kryo.register(YaaToken.class);
    kryo.register(TkKind.class);
    kryo.register(ObjectType.class);
    kryo.register(Parameter.class);
    kryo.register(TypeParam.class);
    kryo.register(Stmts.class);
    kryo.register(MetaArg.class);
    kryo.register(YaaMetaCall.class);
    kryo.register(VMtd.class);
    kryo.register(EMtd.class);
    kryo.register(AstString.class);
    kryo.register(FunDecInfo.class);
    kryo.register(HashSet.class);
    kryo.register(YaaMeta.class);
    kryo.register(RetentionPolicy.class);
    kryo.register(ElementType.class);
  }

  public YaaInfo cloneInfo() {
    return kryo.copy(this);
  }

  public String clzUseSignature() {
    return null;
  }

  protected int inputtedSize() {
    return 0;
  }
}