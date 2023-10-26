package yaa.pojos;

import io.fury.Fury;
import io.fury.config.Language;
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
  public static final Fury fury = Fury.builder().withLanguage(Language.JAVA)
      // Allow to deserialize objects unknown types,
      // more flexible but less secure.
      // .withSecureMode(false)
      .build();

  static {
    // Registering types can reduce class name serialization overhead, but not mandatory.
    // If secure mode is enabled, all custom types must be registered.
    fury.register(LinkedList.class);
    fury.register(YaaClz.class);
    fury.register(yaa.pojos.YaaClzVariance.class);
    fury.register(yaa.pojos.MtdPack.class);
    fury.register(yaa.pojos.MtdIsWhat.class);
    fury.register(yaa.pojos.YaaFun.class);
    fury.register(ArrayList.class);
    fury.register(TreeMap.class);
    fury.register(Map.class);
    fury.register(Collection.class);
    fury.register(List.class);
    fury.register(yaa.pojos.YaaInit.class);
    fury.register(HashMap.class);
    fury.register(BoundState.class);
    fury.register(TreeMap.class);
    fury.register(yaa.pojos.YaaField.class);
    fury.register(yaa.pojos.FieldIsWhat.class);
    fury.register(yaa.pojos.Privacy.class);
    fury.register(yaa.pojos.FunCallInfo.class);
    fury.register(yaa.pojos.TypeCategory.class);
    fury.register(NewFun.class);
    fury.register(YaaToken.class);
    fury.register(TkKind.class);
    fury.register(ObjectType.class);
    fury.register(Parameter.class);
    fury.register(TypeParam.class);
    fury.register(Stmts.class);
    fury.register(MetaArg.class);
    fury.register(YaaMetaCall.class);
    fury.register(VMtd.class);
    fury.register(EMtd.class);
    fury.register(AstString.class);
    fury.register(FunDecInfo.class);
    fury.register(HashSet.class);
    fury.register(YaaMeta.class);
    fury.register(RetentionPolicy.class);
    fury.register(ElementType.class);
  }

  public YaaInfo cloneInfo() {
    return (YaaInfo) fury.deserialize(fury.serialize(this));
  }

  public String clzUseSignature() {
    return null;
  }

  protected int inputtedSize() {
    return 0;
  }
}