package yaa.ast;

import yaa.parser.YaaToken;
import yaa.pojos.YaaClzVariance;

import java.util.List;

public class TypeParam {
  public YaaToken paramName;
  public ObjectType type;
  public YaaToken variance$token;
  public List<YaaMetaCall> metaCalls;
  public YaaClzVariance variance = YaaClzVariance.invariant;

  public TypeParam(){}

  public TypeParam(YaaToken paramName, ObjectType type) {
    this.paramName = paramName;
    this.type = type;
  }

  @Override
  public String toString() {
    String sb = paramName.content;
    switch (variance) {
      case covariant -> sb = sb + " out";
      case contravariant -> sb = sb + " in";
    }
    return sb;
  }
}
