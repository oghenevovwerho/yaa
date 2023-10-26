package yaa.pojos.jMold;

import yaa.pojos.*;
import yaa.pojos.primitives.ArrayMold;

import java.lang.annotation.*;
import java.lang.reflect.*;
import java.util.*;

import static java.lang.Class.forName;
import static java.lang.annotation.ElementType.*;
import static yaa.pojos.BoundState.clz_bound;
import static yaa.pojos.BoundState.mtd_bound;
import static yaa.pojos.GlobalData.*;
import static yaa.pojos.YaaClzVariance.*;
import static yaa.pojos.jMold.JMoldUtils.*;

public class JMold {
  public static final
  Map<String, YaaClz> definedClasses = new HashMap<>();

  private Map<String, YaaClz> clzInputIndices;
  private Map<String, YaaClz> mtdInputIndices;
  private YaaClz topClz;

  public YaaClz newClz(String objectName) {
    var definedClz = definedClasses.get(objectName);
    if (definedClz != null) {
      return definedClz;
    }
    var clz = getJvmClz(objectName);
    if (clz == null) {
      return null;
    }
    if (clz.isAnnotation()) {
      return null;//annotations are dealt with specially;
    }
    return clz$from$jvm(clz);
  }

  public static ClassLoader yaa_compiler_loader;

  private YaaClz clz$from$jvm(Class<?> jlc) {
    var definedClz = definedClasses.get(jlc.getCanonicalName());
    if (definedClz != null) {
      topClz = definedClz;
    } else {
      topClz = new YaaClz(jlc.getCanonicalName());
      topClz.isJvm = true;
      clzInputIndices = new HashMap<>(1);
      if (jlc.isEnum()) {
        topClz.category = TypeCategory.enum_c;
      }
      topClz.codeName = jlc.getTypeName().replace(".", "/");
      topClz.isFinal = Modifier.isFinal(jlc.getModifiers());
      if (jlc.isInterface()) {
        topClz.category = TypeCategory.trait_c;
      }
      topClz.isAbstract = Modifier.isAbstract(jlc.getModifiers());
      topClz.inputted = defineInputsOnClz(jlc);

      if (Modifier.isPublic(jlc.getModifiers())) {
        topClz.privacy = 0;
      } else if (Modifier.isProtected(jlc.getModifiers())) {
        topClz.privacy = 1;
      } else {
        topClz.privacy = 2;
      }
      var staticMtds = new HashMap<String, MtdPack>();
      var instantMtds = new HashMap<String, MtdPack>();
      var abstractIndices = new HashMap<String, List<Integer>>();
      var inits = new ArrayList<YaaInit>();

      for (var init : jlc.getDeclaredConstructors()) {
        if (Modifier.isPrivate(init.getModifiers())) {
          continue;
        }
        inits.add(newInit(init));
      }

      for (var mtd : jlc.getDeclaredMethods()) {
        if (Modifier.isPrivate(mtd.getModifiers())) {
          continue;
        }
        if (mtd.isBridge()) {
          continue;
        }
        var yaaMtd = newMtd(mtd);
        if (topClz.category == TypeCategory.trait_c) {
          yaaMtd.itIsTraitMtd = true;
        }
        var mtdName = yaaMtd.name;
        if (Modifier.isStatic(mtd.getModifiers())) {
          var s$method$pack = staticMtds.get(mtdName);
          if (s$method$pack != null) {
            s$method$pack.methods.add(yaaMtd);
            if (mtd.isVarArgs()) {
              var mutatedFun = (YaaFun) yaaMtd.cloneInfo();
              mutatedFun.theRemovedVarArgClz = (YaaClz) mutatedFun.parameters.get(yaaMtd.parameters.size() - 1);
              mutatedFun.parameters.remove(mutatedFun.parameters.size() - 1);
              s$method$pack.methods.add(mutatedFun);
            }
          } else {
            var static$mtds = new ArrayList<YaaFun>();
            static$mtds.add(yaaMtd);
            var new$pack = new MtdPack(static$mtds, mtdName);
            staticMtds.put(mtdName, new$pack);
            if (mtd.isVarArgs()) {
              var mutatedFun = (YaaFun) yaaMtd.cloneInfo();
              mutatedFun.theRemovedVarArgClz = (YaaClz) mutatedFun.parameters.get(yaaMtd.parameters.size() - 1);
              mutatedFun.parameters.remove(mutatedFun.parameters.size() - 1);
              static$mtds.add(mutatedFun);
            }
          }
        } else {
          var i$mtd$pack = instantMtds.get(mtdName);
          if (i$mtd$pack != null) {
            if (itIsMtdSig(topClz, mtd)) {
              var index = i$mtd$pack.methods.size();
              abstractIndices.get(mtdName).add(index);
            }
            i$mtd$pack.methods.add(yaaMtd);
            if (mtd.isVarArgs()) {
              var mutatedFun = (YaaFun) yaaMtd.cloneInfo();
              mutatedFun.theRemovedVarArgClz = (YaaClz) mutatedFun.parameters.get(yaaMtd.parameters.size() - 1);
              mutatedFun.parameters.remove(mutatedFun.parameters.size() - 1);
              i$mtd$pack.methods.add(mutatedFun);
            }
          } else {
            var newPack = new MtdPack(new ArrayList<>());
            var newIndices = new ArrayList<Integer>();
            if (itIsMtdSig(topClz, mtd)) {
              newIndices.add(newPack.methods.size());
            }
            abstractIndices.put(mtdName, newIndices);
            newPack.methods.add(yaaMtd);
            newPack.name = mtdName;
            instantMtds.put(mtdName, newPack);
            if (mtd.isVarArgs()) {
              var mutatedFun = (YaaFun) yaaMtd.cloneInfo();
              mutatedFun.theRemovedVarArgClz = (YaaClz) mutatedFun.parameters.get(yaaMtd.parameters.size() - 1);
              mutatedFun.parameters.remove(mutatedFun.parameters.size() - 1);
              newPack.methods.add(mutatedFun);
            }
          }
        }
      }
      var enumIndex = 0;
      for (var fd : jlc.getFields()) {
        var field$name = fd.getName();
        var field = new YaaField(field$name, Modifier.isFinal(fd.getModifiers()));
        field.owner = topClz.codeName;
        var type_name = fd.getAnnotatedType().getType().getTypeName();
        field.data = yaaType(type_name, fd.getType());
        field.itIsStatic = Modifier.isStatic(fd.getModifiers());
        if (Modifier.isPublic(fd.getModifiers())) {
          topClz.privacy = 0;
        } else if (Modifier.isProtected(fd.getModifiers())) {
          topClz.privacy = 1;
        } else {
          topClz.privacy = 2;
        }
        if (fd.isEnumConstant()) {
          var enum_field = new YaaField(field$name, true);
          enum_field.itIsWhat = FieldIsWhat.top$field;
          topClz.enumIndices.put(field$name, enumIndex++);
          enum_field.isEnumField = true;
          enum_field.data = topClz;
          topClz.instance$fields.put(field$name, enum_field);
        } else if (Modifier.isStatic(fd.getModifiers())) {
          topClz.static$fields.put(field$name, field);
        } else {
          topClz.instance$fields.put(field$name, field);
        }
      }
      topClz.staticMethods = staticMtds;
      topClz.instanceMethods = instantMtds;
      topClz.abstractIndices = abstractIndices;
      topClz.inits = inits;
      definedClasses.put(jlc.getCanonicalName(), topClz);
    }
    parents.push(topClz);
    var superClass = jlc.getSuperclass();
    if (superClass != null) {
      parents.peek().parent = clz$from$jvm(superClass);
    }
    for (var trait : jlc.getInterfaces()) {
      var yaa$trait = clz$from$jvm(trait);
      var parent$traits = trait.getInterfaces();
      if (parent$traits.length > 0) {
        traits.push(yaa$trait);
        traits.peek().parent = clz$from$jvm(parent$traits[0]);
        var final$trait = traits.pop();
        parents.peek().traits.put(final$trait.name, final$trait);
      } else {
        parents.peek().traits.put(yaa$trait.name, yaa$trait);
      }
    }
    return parents.pop();
  }

  private static final Set<String> objectMtds = new HashSet<>(8);

  static {
    objectMtds.add("toString");
    objectMtds.add("wait");
    objectMtds.add("notify");
    objectMtds.add("notifyAll");
    objectMtds.add("getClass");
    objectMtds.add("finalize");
    objectMtds.add("hashCode");
    objectMtds.add("equals");
  }

  public YaaMeta newMeta(String metaName) {
    clzInputIndices = new HashMap<>(1);
    var meta = new YaaMeta();
    meta.name = metaName;
    meta.codeName = metaName.replace(".", "/");
    try {
      var ano = Class.forName(metaName, false, yaa_compiler_loader);
      for (var mtd : ano.getDeclaredMethods()) {
        if (!objectMtds.contains(mtd.getName())) {
          var field = new YaaField(mtd.getName());
          field.data = yaaType(
              mtd.getReturnType().getTypeName(), mtd.getReturnType()
          );
          //getDefaultValue works with annotations
          if (mtd.getDefaultValue() == null) {
            meta.requiredFields.put(mtd.getName(), field);
          } else {
            meta.defaultFields.put(mtd.getName(), field);
          }
        }
      }
      if (ano.isAnnotationPresent(Target.class)) {
        for (var ann_ann : ano.getAnnotations()) {
          var allowed_name = "java.lang.annotation.Target";
          if (ann_ann.annotationType().getTypeName().equals(allowed_name)) {
            meta.allowedPlaces = allowedIn(ann_ann.toString());
          }
        }
      }
      if (ano.isAnnotationPresent(Retention.class)) {
        for (var ann_ann : ano.getAnnotations()) {
          var allowed_name = "java.lang.annotation.Retention";
          if (ann_ann.annotationType().getTypeName().equals(allowed_name)) {
            meta.retention = retention(ann_ann.toString());
          }
        }
      }
      if (ano.isAnnotationPresent(Repeatable.class)) {
        for (var ann_ann : ano.getAnnotations()) {
          var allowed_name = "java.lang.annotation.Repeatable";
          if (ann_ann.annotationType().getTypeName().equals(allowed_name)) {
            meta.isRepeatable = true;
          }
        }
      }
    } catch (ClassNotFoundException e) {
      return null;
    }
    return meta;
  }

  private RetentionPolicy retention(String ret_ano_string) {
    var retain_till = ret_ano_string
        .substring(ret_ano_string.indexOf("(") + 1, ret_ano_string.indexOf(")"));
    return switch (retain_till) {
      case "RUNTIME" -> RetentionPolicy.RUNTIME;
      case "SOURCE" -> RetentionPolicy.SOURCE;
      default -> RetentionPolicy.CLASS;
    };
  }

  public static Set<ElementType> allowedIn(String target) {
    var cleaned_target = target
        .substring(target.indexOf("{") + 1, target.indexOf("}"));
    if (cleaned_target.length() == 0) {
      var allowedUsage = new HashSet<ElementType>(2);
      allowedUsage.add(TYPE);
      allowedUsage.add(FIELD);
      allowedUsage.add(ANNOTATION_TYPE);
      allowedUsage.add(CONSTRUCTOR);
      allowedUsage.add(LOCAL_VARIABLE);
      allowedUsage.add(TYPE_USE);
      allowedUsage.add(PARAMETER);
      allowedUsage.add(TYPE_PARAMETER);
      allowedUsage.add(METHOD);
      allowedUsage.add(MODULE);
      allowedUsage.add(PACKAGE);
      allowedUsage.add(RECORD_COMPONENT);
      return allowedUsage;
    }
    //so that the last element can be retrieved by the tokenizer.
    var tokenizer = new StringTokenizer(cleaned_target, ",");
    var items = new ArrayList<String>(2);
    while (true) {
      try {
        items.add(tokenizer.nextToken());
      } catch (NoSuchElementException e) {
        var allowedUsage = new HashSet<ElementType>(2);
        for (var allowed : items) {
          switch (allowed.strip()) {
            case "TYPE" -> allowedUsage.add(TYPE);
            case "FIELD" -> allowedUsage.add(FIELD);
            case "ANNOTATION_TYPE" -> allowedUsage.add(ANNOTATION_TYPE);
            case "CONSTRUCTOR" -> allowedUsage.add(CONSTRUCTOR);
            case "LOCAL_VARIABLE" -> allowedUsage.add(LOCAL_VARIABLE);
            case "TYPE_USE" -> allowedUsage.add(TYPE_USE);
            case "PARAMETER" -> allowedUsage.add(PARAMETER);
            case "TYPE_PARAMETER" -> allowedUsage.add(TYPE_PARAMETER);
            case "METHOD" -> allowedUsage.add(METHOD);
            case "MODULE" -> allowedUsage.add(MODULE);
            case "PACKAGE" -> allowedUsage.add(PACKAGE);
            case "RECORD_COMPONENT" -> allowedUsage.add(RECORD_COMPONENT);
          }
        }
        return allowedUsage;
      }
    }
  }

  private final Stack<YaaClz> traits = new Stack<>();
  private final Stack<YaaClz> parents = new Stack<>();

  private List<YaaClz> defineInputsOnMtd(Method jvmMtd) {
    var typeParams = jvmMtd.getTypeParameters();
    var param_size = typeParams.length;
    mtdInputIndices = new HashMap<>(param_size);
    var inputted = new ArrayList<YaaClz>(param_size);
    for (int i = 0; i < param_size; i++) {
      var parameter = typeParams[i];
      var paramName = parameter.getName();
      var inputClz = new YaaClz(paramName);
      inputClz.boundState = mtd_bound;
      inputClz.mbIndex = i;
      var paramBounds = parameter.getBounds();
      if (paramBounds.length > 0) {
        var parentBound = paramBounds[0];
        var parent = yaaType((parentBound.getTypeName()), parentBound);
        parent.boundState = mtd_bound;
        inputClz.parent = parent;
        for (int j = 1; j < paramBounds.length; j++) {
          var traitBound = paramBounds[j];
          var inputClzTrait = yaaType((traitBound.getTypeName()), traitBound);
          inputClz.traits.put(inputClzTrait.name, inputClzTrait);
        }
      } else {
        var parent = new YaaClz(object$name);
        parent.isJvm = true;
        parent.boundState = mtd_bound;
        inputClz.parent = parent;
      }
      mtdInputIndices.put(paramName, inputClz);
      inputted.add(inputClz);
    }
    return inputted;
  }

  private List<YaaClz> defineInputsOnClz(Class<?> jvmClz) {
    var typeParams = jvmClz.getTypeParameters();
    var inputted = new ArrayList<YaaClz>(typeParams.length);
    for (int i = 0; i < typeParams.length; i++) {
      var parameter = typeParams[i];
      var paramName = parameter.getName();
      var inputClz = new YaaClz(paramName);
      inputClz.boundState = clz_bound;
      inputClz.cbIndex = i;
      var paramBounds = parameter.getBounds();
      if (paramBounds.length > 0) {
        var parentBound = paramBounds[0];
        var parent = yaaType((parentBound.getTypeName()), parentBound);
        parent.boundState = clz_bound;
        inputClz.parent = parent;
        for (int j = 1; j < paramBounds.length; j++) {
          var traitBound = paramBounds[j];
          var inputClzTrait = yaaType((traitBound.getTypeName()), traitBound);
          inputClz.traits.put(inputClzTrait.name, inputClzTrait);
        }
      } else {
        var parent = new YaaClz(object$name);
        parent.isJvm = true;
        parent.boundState = clz_bound;
        inputClz.parent = parent;
      }
      clzInputIndices.put(paramName, inputClz);
      inputted.add(inputClz);
    }
    return inputted;
  }

  private YaaInit newInit(Constructor<?> jvmInit) {
    var yaaInit = new YaaInit();
    for (var param : jvmInit.getParameters()) {
      var param$type$name = (param.getAnnotatedType().getType().getTypeName());
      var yaa_type = yaaType(param$type$name, param.getAnnotatedType().getType());
      yaaInit.parameters.add(yaa_type);
    }
    if (Modifier.isPublic(jvmInit.getModifiers())) {
      yaaInit.privacy = 0;
    } else if (Modifier.isProtected(jvmInit.getModifiers())) {
      yaaInit.privacy = 1;
    } else {
      yaaInit.privacy = 2;
    }
    return yaaInit;
  }

  private YaaFun newMtd(Method jvmMtd) {
    var inputted = defineInputsOnMtd(jvmMtd);
    //the inputted has to come first to aid in type resolution
    var yaaMtd = new YaaFun(jvmMtd.getName());
    if (jvmMtd.getDeclaringClass().isAnnotationPresent(FunctionalInterface.class) && !jvmMtd.isDefault()) {
      topClz.functionalMtd = yaaMtd;
    }
    var mtd$type$name = (jvmMtd.getAnnotatedReturnType().getType().getTypeName());
    var type = yaaType(mtd$type$name, jvmMtd.getAnnotatedReturnType().getType());
    if (type.boundState == clz_bound || type.name.equals(array$name)) {
      yaaMtd.hasClzTypeParam = true;
    }
    yaaMtd.type = type;
    for (var param : jvmMtd.getParameters()) {
      var dollar_name = (param.getAnnotatedType().getType().getTypeName());
      var paramType = yaaType(dollar_name, param.getAnnotatedType().getType());
      if (paramType.boundState == clz_bound || paramType.name.equals(array$name)) {
        yaaMtd.hasClzTypeParam = true;
      }
      yaaMtd.parameters.add(paramType);
    }
    if (Modifier.isPublic(jvmMtd.getModifiers())) {
      yaaMtd.privacy = 0;
    } else if (Modifier.isProtected(jvmMtd.getModifiers())) {
      yaaMtd.privacy = 1;
    } else {
      yaaMtd.privacy = 2;
    }
    if (Modifier.isAbstract(jvmMtd.getModifiers())) {
      yaaMtd.mtdIsWhat = MtdIsWhat.abstractMtd;
    }
    yaaMtd.itIsStatic = Modifier.isStatic(jvmMtd.getModifiers());
    yaaMtd.inputted = inputted;
    yaaMtd.owner = topClz.codeName;

    return yaaMtd;
  }

  private YaaClz yaaType(String typeName, Type type) {
    switch (typeName) {
      case "void" -> {
        return GlobalData.void$clz;
      }
      case "int" -> {
        return YaaClz.makePrimitive(int$name);
      }
      case "boolean" -> {
        return YaaClz.makePrimitive(boole$name);
      }
      case "long" -> {
        return YaaClz.makePrimitive(long$name);
      }
      case "double" -> {
        return YaaClz.makePrimitive(double$name);
      }
      case "float" -> {
        return YaaClz.makePrimitive(float$name);
      }
      case "char" -> {
        return YaaClz.makePrimitive(char$name);
      }
      case "short" -> {
        return YaaClz.makePrimitive(short$name);
      }
      case "byte" -> {
        return YaaClz.makePrimitive(byte$name);
      }
    }
    return refType(typeName, type);
  }

  private YaaClz refType(String typeName, Type type) {
    if (mtdInputIndices != null) {
      var mtd_param_clz = mtdInputIndices.get(typeName);
      if (mtd_param_clz != null) {
        return mtd_param_clz;
      }
    }

    var clz_param_clz = clzInputIndices.get(typeName);
    if (clz_param_clz != null) {
      return clz_param_clz;
    }

    if (type instanceof Class<?> clz) {
      var comp$type = clz.getComponentType();
      if (comp$type != null) {
        //This array is for proper type component types e.g. Int, String
        var inputted = new ArrayList<YaaClz>(1);
        inputted.add(yaaType(comp$type.getCanonicalName(), comp$type));
        return ArrayMold.newArray().changeCBounds(inputted);
      }
    }

    if (type instanceof GenericArrayType array$argument) {
      //This array is for generic component types e.g. E, T
      var inputted = new ArrayList<YaaClz>(1);
      var array$type = ArrayMold.newArray();
      var arName = (array$argument.getGenericComponentType().getTypeName());
      inputted.add(refType(arName, array$argument.getGenericComponentType()));
      array$type.inputted = inputted;
      return array$type;
    }

    if (type instanceof ParameterizedType pt) {
      var type_arguments = pt.getActualTypeArguments();
      var clz_arguments = new ArrayList<YaaClz>(type_arguments.length);
      var parameterisedClz = new YaaClz((pt.getRawType().getTypeName()));
      setPropertiesOnClz(parameterisedClz, (Class<?>) pt.getRawType());
      for (var type_argument : type_arguments) {
        if (type_argument instanceof WildcardType wc) {
          if (type_argument.getTypeName().equals("?")) {
            var to$input = new YaaClz("?");
            to$input.variance = covariant;
            clz_arguments.add(to$input);
          } else if (type_argument.getTypeName().equals("*")) {
            var to$input = new YaaClz("*");
            to$input.variance = contravariant;
            clz_arguments.add(to$input);
          } else {
            if (type_argument.getTypeName().contains("? super ")) {
              var super$name = type_argument.getTypeName().substring(8);
              var upper$class = refType(super$name, wc.getLowerBounds()[0]);
              upper$class.variance = contravariant;
              clz_arguments.add(upper$class);
            } else if (type_argument.getTypeName().contains("? extends ")) {
              var super$name = type_argument.getTypeName().substring(10);
              var upper$class = refType(super$name, wc.getUpperBounds()[0]);
              upper$class.variance = covariant;
              clz_arguments.add(upper$class);
            }
          }
        } else {
          var toInput = refType((type_argument.getTypeName()), type_argument);
          toInput.variance = invariant;
          clz_arguments.add(toInput);
        }
      }
      parameterisedClz.inputted = clz_arguments;
      parameterisedClz.boundState = clz_bound;
      return parameterisedClz;
    }
    var new_type = new YaaClz(typeName);
    if (type instanceof Class<?> raw_clz) {
      setPropertiesOnClz(new_type, raw_clz);
    }
    return new_type;
  }

  private void setPropertiesOnClz(YaaClz newClz, Class<?> raw_clz) {
    newClz.isJvm = true;
    if (raw_clz.isEnum()) {
      newClz.category = TypeCategory.enum_c;
    }
    newClz.isFinal = Modifier.isFinal(raw_clz.getModifiers());
    if (raw_clz.isInterface()) {
      newClz.category = TypeCategory.trait_c;
    }
    newClz.isAbstract = Modifier.isAbstract(raw_clz.getModifiers());
  }

  public YaaInfo cacheJvmImp(String objectName) {
    Class<?> primeObj = null;

    var impTokens = tokenize$clz$name(objectName);
    var gotten = new StringBuilder();
    var tokenIndex = 0;
    gotten.append(impTokens.get(tokenIndex++));
    for (; tokenIndex < impTokens.size(); tokenIndex++) {
      gotten.append(".").append(impTokens.get(tokenIndex));
      try {
        primeObj = forName(gotten.toString(), false, yaa_compiler_loader);
        break;//if ClassNotFoundException is not thrown, continue checking;
      } catch (ClassNotFoundException ignored) {
      }
    }

    if (primeObj == null || primeObj.isAnnotation()) {
      return null;
    }

    return newClz(gotten.toString());
  }

  public YaaInfo impObj(String objectName, String lineInfo) {
    if (objectName.equals("java.lang.constant.ConstantDesc")) {
      return new YaaClz(objectName);
    }

    Class<?> primeObj = null;

    var imp_tokens = tokenize$clz$name(objectName);
    var gotten = new StringBuilder();
    var tokenIndex = 0;
    gotten.append(imp_tokens.get(tokenIndex++));
    for (; tokenIndex < imp_tokens.size(); tokenIndex++) {
      gotten.append(".").append(imp_tokens.get(tokenIndex));
      try {
        primeObj = forName(gotten.toString(), false, yaa_compiler_loader);
        break;
      } catch (ClassNotFoundException ignored) {
      }
    }

    assert primeObj != null;
    topClz = newClz(gotten.toString());

    if (tokenIndex == imp_tokens.size() - 1) {
      return topClz;
    }

    var after$gotten = new StringBuilder(gotten.toString());
    for (int index = tokenIndex + 1; index < imp_tokens.size(); index++) {
      after$gotten.append(".").append(imp_tokens.get(index));
      var gottenMb = member$in$clz(primeObj, imp_tokens.get(index));
      if (gottenMb == null) {
        throw new YaaError(
            lineInfo, primeObj.getCanonicalName()
            + " does not define " + imp_tokens.get(index)
        );
      }
      if (gottenMb instanceof Class<?> ci) {
        topClz = newClz(ci.getTypeName());
        if (index != imp_tokens.size() - 1) {
          if (ci.isEnum()) {
            var field = topClz.getInstantField(imp_tokens.get(index + 1));
            if (field == null) {
              throw new YaaError(
                  lineInfo, after$gotten.toString(),
                  "The imported enum above does not define the member \""
                      + imp_tokens.get(index + 1) + "\""
              );
            }
            return field;
          }
          primeObj = ci;
        } else {
          return topClz;
        }
      } else if (gottenMb instanceof Method mtd) {
        if (index != imp_tokens.size() - 1) {
          throw new YaaError(
              lineInfo, after$gotten.toString(),
              "The imported content above resolves to a function",
              "Being a function it cannot define the inner member \""
                  + imp_tokens.get(index + 1) + "\""
          );
        }
        return topClz.getStaticMethod(mtd.getName());
      } else if (gottenMb instanceof Field fd) {
        if (index != imp_tokens.size() - 1) {
          throw new YaaError(
              lineInfo, after$gotten.toString(),
              "The imported content above resolves to a field",
              "Being a field it cannot define the inner member \""
                  + imp_tokens.get(index + 1) + "\""
          );
        }
        if (topClz.category == TypeCategory.enum_c) {
          return topClz.instance$fields.get(fd.getName());
        }
        return topClz.getStaticField(fd.getName());
      }
    }
    return null;
  }
}
