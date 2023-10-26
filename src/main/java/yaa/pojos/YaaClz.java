package yaa.pojos;

import yaa.ast.ObjectType;
import yaa.pojos.jMold.JMold;
import yaa.semantic.handlers.VCallOp;
import yaa.semantic.passes.fs5.F5;
import yaa.semantic.passes.fs6.F6Utils;

import java.util.*;

import static yaa.pojos.GlobalData.*;
import static yaa.pojos.TypeCategory.class_c;
import static yaa.pojos.TypeCategory.trait_c;
import static yaa.pojos.primitives.ArrayMold.newArray;

public class YaaClz extends YaaInfo {
  public boolean isFinal;
  public boolean isJvm;
  public boolean isAbstract;
  public TypeCategory category = class_c;
  public YaaFun functionalMtd;

  public List<YaaClz> inputted = new ArrayList<>(1);
  public Map<String, MtdPack> instanceMethods = new HashMap<>(1);
  public Map<String, MtdPack> staticMethods = new HashMap<>(1);

  public Map<String, List<Integer>> abstractIndices = new HashMap<>(1);

  public Map<String, YaaMeta> metas = new HashMap<>(1);
  public YaaClz parent;
  public Map<String, YaaField> static$fields = new HashMap<>(1);
  public Map<String, YaaClz> internal$classes = new HashMap<>(1);
  public Map<String, YaaField> instance$fields = new HashMap<>(1);
  public List<YaaInit> inits = new ArrayList<>(1);
  public Map<String, YaaClz> traits = new HashMap<>(1);
  public List<YaaClz> clz$traits = new ArrayList<>(1);
  public YaaClzVariance variance = YaaClzVariance.invariant;
  public YaaClz outerClz;
  public Map<String, FunDecInfo> decInfoMap = new HashMap<>(1);
  public Map<String, Integer> enumIndices = new TreeMap<>();
  public Map<String, YaaClz> staticInnerClasses = new HashMap<>(1);
  public int endLine;

  public static YaaClz makePrimitive(String name) {
    var primitive_clz = new YaaClz(name);
    primitive_clz.inputted = new ArrayList<>(1);
    primitive_clz.instanceMethods = new HashMap<>(1);
    primitive_clz.staticMethods = new HashMap<>(1);

    primitive_clz.abstractIndices = null;
    primitive_clz.static$fields = null;
    primitive_clz.internal$classes = null;
    primitive_clz.instance$fields = null;
    primitive_clz.inits = null;
    primitive_clz.traits = null;
    primitive_clz.clz$traits = null;
    primitive_clz.decInfoMap = null;
    primitive_clz.enumIndices = null;
    return primitive_clz;
  }

  public String initCandidates(List<YaaClz> type_arguments) {
    var bd = new StringBuilder();
    if (inits.size() == 0) {
      bd.append("The empty initializer () is the only valid candidate\n");
      return bd.toString();
    }
    if (inits.size() == 1) {
      bd.append("The initializer below is the only valid candidate\n");
    } else {
      bd.append("The constructors below are the only valid candidates\n");
    }
    for (var init : VCallOp.getChangedInits(this, type_arguments)) {
      bd.append("   ").append(init).append("\n");
    }
    return bd.toString();
  }

  @Override
  protected int inputtedSize() {
    return inputted.size();
  }

  public YaaClz() {
  }

  public void checkTypeArgumentSize(int size, String address) {
    if (size != inputted.size()) {
      var pc = inputted.size();
      var word = pc == 1 ? " type argument" : " type arguments";
      throw new YaaError(
          address, this.toString(),
          "The type above expected " + pc + word + " but got " + size
      );
    }
  }

  public void checkTypeArguments(
      List<YaaClz> typeArguments, List<String> addresses) {
    for (int i = 0; i < inputted.size(); i++) {
      var inputtedClz = inputted.get(i);
      var argumentClz = typeArguments.get(i);
      if (!inputtedClz.parent.isParentOf(argumentClz)) {
        if (name.equals(array$name) && argumentClz.isPrimitive()
            && inputtedClz.parent.name.equals("java.lang.Object")) {
          return;
        }
        throw new YaaError(
            addresses.get(i), argumentClz.toString(),
            "The type above does not extend " + inputtedClz.parent
        );
      }
      for (var trait : inputtedClz.parent.traits.values()) {
        if (argumentClz.hasTrait(trait) == null) {
          throw new YaaError(
              addresses.get(i), argumentClz.toString(),
              "The type above does not implement " + trait
          );
        }
      }
    }
  }

  @Override
  public String descriptor() {
    switch (name) {
      case boole$name -> {
        return "Z";
      }
      case int$name -> {
        return "I";
      }
      case double$name -> {
        return "D";
      }
      case float$name -> {
        return "F";
      }
      case short$name -> {
        return "S";
      }
      case char$name -> {
        return "C";
      }
      case byte$name -> {
        return "B";
      }
      case long$name -> {
        return "J";
      }
      case void$name -> {
        return "V";
      }
      case array$name -> {
        return "[" + inputted.get(0).descriptor();
      }
    }
    if (cbIndex > -1) {
      return parent.descriptor();
    }
    return "L" + codeName + ";";
  }

  public YaaClz(String name) {
    this.name = name;
    this.codeName = name.replace(".", "/");
  }

  public YaaField getStaticField(String fieldName) {
    var field = static$fields.get(fieldName);
    if (field != null) {
      return field;
    }
    if (parent != null) {
      return parent.getStaticField(fieldName);
    }
    var newClz = new JMold().newClz(name);
    if (newClz == null) {
      return null;
    }
    return getJvmStaticField(newClz, fieldName);
  }

  private YaaField getJvmStaticField(YaaClz newClz, String fieldName) {
    var field = newClz.static$fields.get(fieldName);
    if (field != null) {
      return field;
    }
    if (newClz.parent != null) {
      return getJvmStaticField(newClz.parent, fieldName);
    }
    return null;
  }

  public YaaField getInstantField(String fieldName) {
    var field = instance$fields.get(fieldName);
    if (field != null) {
      return field;
    }
    if (parent != null) {
      return parent.getInstantField(fieldName);
    }
    var jvmClz = new JMold().newClz(name);
    if (jvmClz != null) {
      return getJvmField(jvmClz, fieldName);
    }
    return null;
  }

  private YaaField getJvmField(YaaClz jvmClz, String fieldName) {
    var field = jvmClz.instance$fields.get(fieldName);
    if (field != null) {
      return field;
    }
    if (jvmClz.parent != null) {
      return jvmClz.parent.getJvmField(jvmClz.parent, fieldName);
    }
    return null;
  }

  public Parent$Mtd getShadowMethod(String mtdName) {
    if (mtdName.equals("get")) {
      if (parent != null) {
        if (parent.name.equals("java.util.Optional")) {
          var pack = new MtdPack(new ArrayList<>(1));
          pack.name = "get";
          return new Parent$Mtd(parent, pack);
        }
        var parent$result = parent.instanceMethods.get(mtdName);
        if (parent$result != null) {
          return new Parent$Mtd(parent, parent$result);
        }
      }
    }
    if (parent != null) {
      var parent$result = parent.instanceMethods.get(mtdName);
      if (parent$result != null) {
        return new Parent$Mtd(parent, parent$result);
      }
    }
    var objectMtd = object$clz.instanceMethods.get(mtdName);
    if (objectMtd != null) {
      return new Parent$Mtd(new YaaClz(object$name), objectMtd);
    }
    for (var trait : traits.values()) {
      var traitMtd = trait.instanceMethods.get(mtdName);
      if (traitMtd != null) {
        return new Parent$Mtd(trait, traitMtd);
      }
    }
    return null;
  }

  private MtdPack getJvmMtd(YaaClz newClz, String mtdName) {
    var mtd = newClz.instanceMethods.get(mtdName);
    if (mtd != null) {
      return mtd;
    }
    if (newClz.parent != null) {
      var parent$result = newClz.parent.getJvmMtd(newClz.parent, mtdName);
      if (parent$result != null) {
        return parent$result;
      }
    }
    for (var trait : newClz.traits.values()) {
      var traitMtd = trait.instanceMethods.get(mtdName);
      if (traitMtd != null) {
        return traitMtd;
      }
    }
    return null;
  }

  public MtdPack getMethod(String mtdName) {
    var method = instanceMethods.get(mtdName);
    if (method != null) {
      return method;
    }
    if (isPrimitive()) {
      switch (name) {
        case int$name -> {
          return int$clz.instanceMethods.get(mtdName);
        }
        case float$name -> {
          return float$clz.instanceMethods.get(mtdName);
        }
        case double$name -> {
          return double$clz.instanceMethods.get(mtdName);
        }
        case long$name -> {
          return long$clz.instanceMethods.get(mtdName);
        }
        case short$name -> {
          return short$clz.instanceMethods.get(mtdName);
        }
        case byte$name -> {
          return byte$clz.instanceMethods.get(mtdName);
        }
        case char$name -> {
          return char$clz.instanceMethods.get(mtdName);
        }
        case boole$name -> {
          return boole$clz.instanceMethods.get(mtdName);
        }
      }
    }
    var objectMtd = object$clz.instanceMethods.get(mtdName);
    if (objectMtd != null) {
      return objectMtd;
    }
    return getDeeperMtd(mtdName);
  }

  private MtdPack getDeeperMtd(String mtdName) {
    if (parent != null) {
      var parent$result = parent.instanceMethods.get(mtdName);
      if (parent$result != null) {
        return parent$result;
      }
    }
    for (var trait : traits.values()) {
      var traitMtd = trait.getMethod(mtdName);
      if (traitMtd != null) {
        return traitMtd;
      }
    }
    if (array$name.equals(name)) {
      //this case is useful for constructs like arrayOfNames + "otherName"
      var arguments = new ArrayList<YaaClz>(1);
      arguments.add(inputted.get(0));
      return newArray()
          .changeCBounds(arguments).instanceMethods.get(mtdName);
    }
    var jvm_clz = new JMold().newClz(name);
    if (jvm_clz != null) {
      return getJvmMtd(jvm_clz, mtdName);
    }
    return null;
  }

  public MtdPack getStaticMethod(String mtdName) {
    var method = staticMethods.get(mtdName);
    if (method != null) {
      return method;
    }
    if (mtdName.equals("getClass")) {
      if (cachedGetClass != null) {
        return cachedGetClass;
      }
      var mtds = new ArrayList<YaaFun>(1);
      var fun = new YaaFun("getClass");
      fun.type = new JMold().newClz("java.lang.Class");
      fun.owner = "java/lang/Object";
      fun.itIsStatic = true;
      mtds.add(fun);
      cachedGetClass = new MtdPack(mtds, "getClass");
      return cachedGetClass;
    }
    return getDeeperStaticMtd(mtdName);
  }

  private MtdPack cachedGetClass = null;

  private MtdPack getDeeperStaticMtd(String mtdName) {
    if (parent != null) {
      var parent$result = parent.staticMethods.get(mtdName);
      if (parent$result != null) {
        return parent$result;
      }
    }
    for (var trait : traits.values()) {
      var traitMtd = trait.getStaticMethod(mtdName);
      if (traitMtd != null) {
        return traitMtd;
      }
    }
    var clz = new JMold().newClz(name);
    if (clz == null) {
      return null;
    }
    return getJvmStaticMtd(clz, mtdName);
  }

  private MtdPack getJvmStaticMtd(YaaClz newClz, String mtdName) {
    var mtd = newClz.staticMethods.get(mtdName);
    if (mtd != null) {
      return mtd;
    }
    if (newClz.parent != null) {
      var parent$result =
          newClz.parent.getJvmStaticMtd(newClz.parent, mtdName);
      if (parent$result != null) {
        return parent$result;
      }
    }
    for (var trait : newClz.traits.values()) {
      var traitMtd = trait.staticMethods.get(mtdName);
      if (traitMtd != null) {
        return traitMtd;
      }
    }
    return null;
  }

  public boolean isParentOf(YaaInfo otherObject) {
    if (otherObject.isPrimitive() || isPrimitive()) {
      return false;
    }
    if (name.equals(object$name)) {
      return true;
    }
    if (cbIndex > -1) {
      return parent.isParentOf(otherObject);
    }
    if (otherObject.cbIndex > -1) {
      return isParentOf(((YaaClz) otherObject).parent);
    }
    if (otherObject instanceof YaaClz clz) {
      return clz.isChildOf(this);
    }
    return false;
  }

  public boolean isChildOf(YaaInfo otherObject) {
    if (isPrimitive() || otherObject.isPrimitive()) {
      return false;
    }
    if (name.equals(null$name) &&
        otherObject.name.equals("java.util.Optional")) {
      return false;
    }
    if (otherObject instanceof YaaClz clz) {
      if (clz.name.equals(object$name)) {
        return true;
      }
      if (clz.isSame$Obj(this)) {
        return true;
      }
      if (parent != null) {
        return parent.isChildOf(clz);
      }
      return false;
    }
    return false;
  }

  public YaaClz hasTrait(YaaClz trait) {
    if (isJvm) {
      var jvm_trait = new JMold().newClz(name);
      return jvm_trait.reallyHasTrait(jvm_trait.traits, trait);
    }
    return reallyHasTrait(traits, trait);
  }

  private YaaClz reallyHasTrait(Map<String, YaaClz> traits, YaaClz trait) {
    if (isSame$Obj(trait)) {
      return this;
    }
    var definedTrait = traits.get(trait.name);
    if (definedTrait != null && definedTrait.accepts(trait)) {
      return trait;
    }
    for (var singleTrait : traits.values()) {
      if (singleTrait.parent != null) {
        if (singleTrait.parent.hasTrait(trait) != null) {
          return trait;
        }
      }
    }
    if (parent != null && parent.hasTrait(trait) != null) {
      return trait;
    }
    return null;
  }

  @Override
  public boolean isBoxed() {
    switch (name) {
      case int$boxed -> {
        return true;
      }
      case float$boxed -> {
        return true;
      }
      case long$boxed -> {
        return true;
      }
      case short$boxed -> {
        return true;
      }
      case boole$boxed -> {
        return true;
      }
      case byte$boxed -> {
        return true;
      }
      case char$boxed -> {
        return true;
      }
      case double$boxed -> {
        return true;
      }
      case void$boxed -> {
        return true;
      }
    }
    return false;
  }

  @Override
  public boolean accepts(YaaInfo other) {
    if (primitiveAccepts(name, other.name)) {
      return true;
    }
    if (other instanceof YaaFun fun) {
      if (checkAgainstFun(fun)) {
        return true;
      }
    }
    if (other instanceof YaaClz otherClz) {
      if (otherClz.name.equals(null$name)) {
        return !isPrimitive();
      }
      if (!name.equals(other.name)) {
        return false;
      }
      if (category == trait_c) {
        return otherClz.hasTrait(this) != null;
      }
      if (inputted.size() != otherClz.inputted.size()) {
        return false;
      }
      if (name.equals("java.lang.Class")) {
        //so that java.lang.Class<T> and java.lang.Class<?> can pass
        return true;
      }
      for (int i = 0; i < inputted.size(); i++) {
        var currentParam = inputted.get(i);
        var lObject = inputted.get(i);
        var rObject = otherClz.inputted.get(i);
        if (currentParam.variance == YaaClzVariance.covariant) {
          if (!lObject.isParentOf(rObject)) {
            return false;
          }
        } else if (currentParam.variance == YaaClzVariance.contravariant) {
          if (!lObject.isChildOf(rObject)) {
            return false;
          }
        } else {
          if (!lObject.isSame$Obj(rObject)) {
            if (rObject.hasTrait(lObject) == null) {
              return false;
            }
          }
        }
      }
      return true;
    }
    return false;
  }

  private boolean checkAgainstFun(YaaFun fun) {
    var actual_type = getTouchedClass(name);
    //var changed_type = actual_type.changeClzBounds(inputted);
    if (actual_type.functionalMtd != null) {
      var rawMtd = actual_type.functionalMtd;
      if (fun.parameterNames.size() != rawMtd.parameters.size()) {
        return false;
      }
      fun.iClzDescriptor = descriptor();
      fun.iMtdName = rawMtd.name;
      StringBuilder bd = new StringBuilder();
      bd.append("(");
      for (var param : rawMtd.parameters) {
        bd.append(param.descriptor());
      }
      bd.append(")");
      bd.append(rawMtd.type.descriptor());
      fun.iMtdDescriptor = bd.toString();
      var func = (YaaFun) fs.getSymbol(F5.topAnonymous.peek().placeOfUse());
      func.parameters = new ArrayList<>(3);
      for (int i = 0; i < func.parameterNames.size(); i++) {
        var type = rawMtd.parameters.get(i);
        func.parameters.add(type);
      }
      func.type = rawMtd.type;
      fs5.doNoNane();
      return true;
    } else if (actual_type.category == trait_c) {
      if (actual_type.instanceMethods.size() == 1) {
        var mtd_values = actual_type.instanceMethods.values();
        for (var mtd_pack : mtd_values) {
          if (mtd_pack.methods.size() > 1) {
            continue;
          }
          var rawMtd = mtd_pack.methods.get(0);
          if (fun.parameterNames.size() != rawMtd.parameters.size()) {
            return false;
          }
          fun.iClzDescriptor = descriptor();
          fun.iMtdName = rawMtd.name;
          StringBuilder bd = new StringBuilder();
          bd.append("(");
          for (var param : rawMtd.parameters) {
            bd.append(param.descriptor());
          }
          bd.append(")");
          bd.append(rawMtd.type.descriptor());
          fun.iMtdDescriptor = bd.toString();
          var func = (YaaFun) fs.getSymbol(F5.topAnonymous.peek().placeOfUse());
          func.parameters = new ArrayList<>(3);
          for (int i = 0; i < func.parameterNames.size(); i++) {
            var type = rawMtd.parameters.get(i);
            func.parameters.add(type);
          }
          func.type = rawMtd.type;
          fs5.doNoNane();
          return true;
        }
      }
    }
    return false;
  }

  public String getSimpleName() {
    return name.substring(name.lastIndexOf('.') + 1);
  }

  private boolean primitiveAccepts(String my$name, String other$name) {
    if (other$name.equals(void$name)) {
      return false;
    }
    switch (my$name) {
      case double$name -> {
        if (F6Utils.itIsPrimitive(other$name)) {
          if (!other$name.equals(boole$name)) {
            return true;
          }
        }
      }
      case float$name -> {
        var widest = F6Utils.widest(float$name, other$name)[0];
        if (F6Utils.itIsPrimitive(other$name)) {
          if (!other$name.equals(boole$name)) {
            if (widest.equals(float$name)) {
              return true;
            }
          }
        }
      }
      case long$name -> {
        var widest = F6Utils.widest(long$name, other$name)[0];
        if (F6Utils.itIsPrimitive(other$name)) {
          if (!other$name.equals(boole$name)) {
            if (widest.equals(long$name)) {
              return true;
            }
          }
        }
      }
      case int$name -> {
        var widest = F6Utils.widest(int$name, other$name)[0];
        if (F6Utils.itIsPrimitive(other$name)) {
          if (!other$name.equals(boole$name)) {
            if (widest.equals(int$name)) {
              return true;
            }
          }
        }
      }
      case char$name -> {
        var widest = F6Utils.widest(char$name, other$name)[0];
        if (F6Utils.itIsPrimitive(other$name)) {
          if (!other$name.equals(boole$name)) {
            if (widest.equals(char$name)) {
              return true;
            }
          }
        }
      }
      case short$name -> {
        if (F6Utils.itIsPrimitive(other$name)) {
          if (!other$name.equals(boole$name)) {
            if (other$name.equals(byte$name)) {
              return true;
            }
          }
        }
      }
    }
    return false;
  }

  @Override
  public boolean isPrimitive() {
    switch (name) {
      case int$name -> {
        return true;
      }
      case float$name -> {
        return true;
      }
      case long$name -> {
        return true;
      }
      case short$name -> {
        return true;
      }
      case boole$name -> {
        return true;
      }
      case byte$name -> {
        return true;
      }
      case char$name -> {
        return true;
      }
      case double$name -> {
        return true;
      }
      case void$name -> {
        return true;
      }
    }
    return false;
  }

  public boolean isSame$Obj(YaaInfo other) {
    if (other instanceof YaaClz otherClz) {
      if (otherClz.name.equals(null$name)) {
        return !name.equals("java.util.Optional");
      }
      if (!name.equals(other.name)) {
        return false;
      }
      if (inputted.size() != otherClz.inputted.size()) {
        return false;
      }
      for (int i = 0; i < inputted.size(); i++) {
        var lObject = inputted.get(i);
        var rObject = otherClz.inputted.get(i);
        if (lObject.variance == YaaClzVariance.covariant) {
          if (!lObject.isParentOf(rObject)
              && rObject.hasTrait(lObject) == null) {
            return false;
          }
        } else if (lObject.variance == YaaClzVariance.contravariant) {
          if (!lObject.isChildOf(rObject)
              && lObject.hasTrait(rObject) == null) {
            return false;
          }
        } else {
          if (!lObject.isSame$Obj(rObject)) {
            return false;
          }
        }
      }
      return true;
    }
    return false;
  }

  @Override
  public String toString() {
    return name + stringOfArguments();
  }

  private String stringOfArguments() {
    if (inputted.size() == 0) {
      return "";
    }
    if (inputted.size() == 1) {
      return "<" + inputted.get(0) + ">";
    }

    var sb = new StringBuilder();
    sb.append(inputted.get(0));
    for (int i = 1; i < inputted.size(); i++) {
      sb.append(", ").append(inputted.get(i));
    }
    return "<" + sb + ">";
  }

  @Override
  public String clzUseSignature() {
    return YaaClzUtils.typeUseSignature(this);
  }

  public YaaClz changeMBounds(List<YaaClz> arguments) {
    var new_clz = (YaaClz) cloneInfo();
    for (int i = 0; i < new_clz.inputted.size(); i++) {
      var type_param = new_clz.inputted.get(i);
      if (type_param.inputted.size() > 0) {
        new_clz.inputted.set(i, type_param.changeMBounds(arguments));
        continue;
      }
      var mb_index = type_param.mbIndex;
      if (mb_index > -1) {
        var newIn = (YaaClz) arguments.get(mb_index).cloneInfo();
        if (newIn.mbIndex > -1) {
          newIn = newIn.parent;
        }
        newIn.typeParam = type_param;
        newIn.boundState = BoundState.mtd_bound;
        newIn.variance = type_param.variance;
        new_clz.inputted.set(i, newIn);
      }
    }
    return new_clz;
  }

  public YaaClz changeCBounds(List<YaaClz> arguments) {
    var new_clz = (YaaClz) cloneInfo();
    for (int i = 0; i < new_clz.inputted.size(); i++) {
      var type_param = new_clz.inputted.get(i);
      if (type_param.inputted.size() > 0) {
        new_clz.inputted.set(i, type_param.changeCBounds(arguments));
        continue;
      }
      var cb_index = type_param.cbIndex;
      if (cb_index > -1) {
        var newIn = (YaaClz) arguments.get(cb_index).cloneInfo();
        if (newIn.cbIndex > -1) {
          newIn = newIn.parent;
        }
        newIn.boundState = BoundState.clz_bound;
        //System.out.println(this + "  " + type_param + "  " + type_param.variance);
        //newIn.variance = YaaInfo.kryo.copy(type_param.variance);
        newIn.typeParam = (YaaClz) type_param.cloneInfo();
        new_clz.inputted.set(i, newIn);
      }
    }

    if (new_clz.instance$fields != null) {
      changeInstanceFields(new_clz, arguments);
    }

    if (new_clz.static$fields != null) {
      changeStaticFields(new_clz, arguments);
    }
    return new_clz;
  }

  private void changeInstanceFields(YaaClz new_clz, List<YaaClz> arguments) {
    for (var variable : new_clz.instance$fields.values()) {
      if (variable.data instanceof YaaClz dataClz) {
        if (dataClz.inputted.size() > 0) {
          variable.data = dataClz.changeCBounds(arguments);
          continue;
        }
        if (dataClz.cbIndex > -1) {
          var new_data = (YaaClz) arguments.get(dataClz.cbIndex).cloneInfo();
          if (new_data.cbIndex > -1) {
            new_data = new_data.parent;
          }
          var instant_field = new_clz.instance$fields.get(variable.field$name);
          instant_field.typeParam = dataClz;
          instant_field.data = new_data;
        }
      }
    }
  }

  private void changeStaticFields(YaaClz new_clz, List<YaaClz> arguments) {
    for (var variable : new_clz.static$fields.values()) {
      if (variable.data instanceof YaaClz dataClz) {
        if (dataClz.inputted.size() > 0) {
          variable.data = dataClz.changeCBounds(arguments);
          continue;
        }
        if (dataClz.cbIndex > -1) {
          var new_data = (YaaClz) arguments.get(dataClz.cbIndex).cloneInfo();
          if (new_data.cbIndex > -1) {
            new_data = new_data.parent;
          }
          var instant_field = new_clz.static$fields.get(variable.field$name);
          instant_field.typeParam = dataClz;
          instant_field.data = new_data;
        }
      }
    }
  }

  public static YaaClz f2Clz(ObjectType type) {
    var name = type.typeName.content;
    var clz = fs.getSymbol(name);
    if (clz instanceof YaaClz gottenClz) {
      return gottenClz;
    }
    throw new YaaError(
        type.placeOfUse(), "'" + name + "' must be a valid type"
    );
  }

  public static YaaClz f3Clz(ObjectType type) {
    var name = type.typeName.content;
    var gottenClz = fs.getSymbol(name);
    if (gottenClz instanceof YaaClz clz) {
      clz.checkTypeArgumentSize(type.arguments.size(), type.placeOfUse());
      if (clz.inputted.size() == 0) {
        return clz;
      }
      var arguments = new ArrayList<YaaClz>();
      for (var argument : type.arguments) {
        arguments.add((YaaClz) argument.visit(fs));
      }
      return clz.changeCBounds(arguments);
    }
    throw new YaaError(
        type.placeOfUse(), "'" + name + "' must be a valid type"
    );
  }

  public static YaaClz fsClz(ObjectType type) {
    var name = type.typeName.content;
    var gottenClz = fs.getSymbol(name);
    if (gottenClz instanceof YaaClz clz) {
      clz.checkTypeArgumentSize(type.arguments.size(), type.placeOfUse());
      if (clz.inputted.size() == 0) {
        return clz;
      }

      var arguments = new ArrayList<YaaClz>();
      for (var argument : type.arguments) {
        arguments.add((YaaClz) argument.visit(fs));
      }
      var addresses = new ArrayList<String>();
      for (var objectType : type.arguments) {
        addresses.add(objectType.placeOfUse());
      }
      clz.checkTypeArguments(arguments, addresses);
      return clz.changeCBounds(arguments);
    }
    throw new YaaError(
        type.placeOfUse(), "'" + name + "' must be a valid type"
    );
  }
}