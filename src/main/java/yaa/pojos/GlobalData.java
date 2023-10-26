package yaa.pojos;

import yaa.ast.Stmt;
import yaa.pojos.jMold.JMold;
import yaa.semantic.passes.fs1.F1;
import yaa.semantic.passes.fs2.F2;
import yaa.semantic.passes.fs3.F3;
import yaa.semantic.passes.fs4.F4;
import yaa.semantic.passes.fs5.F5;
import yaa.semantic.passes.fs6.F6;
import yaa.semantic.passes.fs6.results.YaaResult;

import java.io.File;
import java.util.*;

public class GlobalData {
  private static final
  Map<String, MtdPack> functions = new HashMap<>();
  public static LogData logData = new LogData();

  public static void addInfoLog(String message) {
    logData.messages.add(new LogMessage(LogCategory.info, message));
  }

  public static void addWarnLog(String message) {
    logData.messages.add(new LogMessage(LogCategory.warn, message));
  }

  public static void addErrorLog(String message) {
    logData.messages.add(new LogMessage(LogCategory.error, message));
  }

  private static final
  Map<String, YaaField> fields = new HashMap<>();
  private static final
  Map<String, YaaClz> classes = new HashMap<>();
  public static YaaFun mainFunction;
  public static List<LogMessage> logMessages = new ArrayList<>(10);
  static Map<String, YaaClz> imp_classes = new HashMap<>();
  private static final
  Map<String, YaaMeta> metas = new HashMap<>();
  public static final
  YaaInfo nothing = null;
  public static
  Map<String, Map<Stmt, YaaTable>> tables4File;
  public static final
  Map<String, Map<Stmt, YaaResult>> results = new HashMap<>();
  public static final Map<String, YaaClz> cachedClass = new HashMap<>(10);

  public static Set<String> langPkgClassNames = new HashSet<>(100);
  public static Set<String> systemStaticMtdNames = new HashSet<>(30);
  public static Set<String> systemStaticFdNames = new HashSet<>(5);
  public static final String configMetaClzName = "yaa.lang.ConfigMeta";
  public static final String configMetaRefName = "cf";

  static {
    systemStaticFdNames.add("in");
    systemStaticFdNames.add("out");
    systemStaticFdNames.add("err");
  }

  static {
    systemStaticMtdNames.add("arrayCopy");
    systemStaticMtdNames.add("clearProperty");
    systemStaticMtdNames.add("console");
    systemStaticMtdNames.add("currentTimeMillis");
    systemStaticMtdNames.add("exit");
    systemStaticMtdNames.add("gc");
    systemStaticMtdNames.add("getenv");
    systemStaticMtdNames.add("getLogger");
    systemStaticMtdNames.add("getProperties");
    systemStaticMtdNames.add("getProperty");
    systemStaticMtdNames.add("getSecurityManager");
    systemStaticMtdNames.add("identityHashCode");
    systemStaticMtdNames.add("inheritedChannel");
    systemStaticMtdNames.add("lineSeparator");
    systemStaticMtdNames.add("load");
    systemStaticMtdNames.add("loadLibrary");
    systemStaticMtdNames.add("mapLibraryName");
    systemStaticMtdNames.add("nanoTime");
    systemStaticMtdNames.add("runFinalization");
    systemStaticMtdNames.add("setErr");
    systemStaticMtdNames.add("setIn");
    systemStaticMtdNames.add("setOut");
    systemStaticMtdNames.add("setProperties");
    systemStaticMtdNames.add("setProperty");
    systemStaticMtdNames.add("setSecurityManager");
  }

  static {
    langPkgClassNames.add("ArithmeticException");
    langPkgClassNames.add("ArrayIndexOutOfBoundsException");
    langPkgClassNames.add("ArrayStoreException");
    langPkgClassNames.add("ClassCastException");
    langPkgClassNames.add("ClassNotFoundException");
    langPkgClassNames.add("CloneNotSupportedException");
    langPkgClassNames.add("EnumConstantNotPresentException");
    langPkgClassNames.add("Exception");
    langPkgClassNames.add("IllegalAccessException");
    langPkgClassNames.add("IllegalArgumentException");
    langPkgClassNames.add("IllegalMonitorStateException");
    langPkgClassNames.add("IllegalStateException");
    langPkgClassNames.add("IllegalThreadStateException");
    langPkgClassNames.add("IndexOutOfBoundsException");
    langPkgClassNames.add("InstantiationException");
    langPkgClassNames.add("InterruptedException");
    langPkgClassNames.add("NegativeArraySizeException");
    langPkgClassNames.add("NoSuchFieldException");
    langPkgClassNames.add("NoSuchMethodException");
    langPkgClassNames.add("NullPointerException");
    langPkgClassNames.add("NumberFormatException");
    langPkgClassNames.add("ReflectiveOperationException");
    langPkgClassNames.add("RuntimeException");
    langPkgClassNames.add("SecurityException");
    langPkgClassNames.add("StringIndexOutOfBoundsException");
    langPkgClassNames.add("TypeNotPresentException");
    langPkgClassNames.add("UnsupportedOperationException");

    langPkgClassNames.add("AbstractMethodError");
    langPkgClassNames.add("AssertionError");
    langPkgClassNames.add("BootstrapMethodError");
    langPkgClassNames.add("ClassCircularityError");
    langPkgClassNames.add("ClassFormatError");
    langPkgClassNames.add("Error");
    langPkgClassNames.add("ExceptionInInitializerError");
    langPkgClassNames.add("IllegalAccessError");
    langPkgClassNames.add("IncompatibleClassChangeError");
    langPkgClassNames.add("InstantiationError");
    langPkgClassNames.add("InternalError");
    langPkgClassNames.add("LinkageError");
    langPkgClassNames.add("NoClassDefFoundError");
    langPkgClassNames.add("NoSuchFieldError");
    langPkgClassNames.add("NoSuchMethodError");
    langPkgClassNames.add("OutOfMemoryError");
    langPkgClassNames.add("StackOverflowError");
    langPkgClassNames.add("ThreadDeath");
    langPkgClassNames.add("UnknownError");
    langPkgClassNames.add("UnsatisfiedLinkError");
    langPkgClassNames.add("UnsupportedClassVersionError");
    langPkgClassNames.add("VerifyError");
    langPkgClassNames.add("VirtualMachineError");

    langPkgClassNames.add("Deprecated");
    langPkgClassNames.add("Override");
    langPkgClassNames.add("SafeVarargs");
    langPkgClassNames.add("SuppressWarnings");

    langPkgClassNames.add("Boolean");
    langPkgClassNames.add("Byte");
    langPkgClassNames.add("Character");
    langPkgClassNames.add("Class");
    langPkgClassNames.add("ClassLoader");
    langPkgClassNames.add("ClassValue");
    langPkgClassNames.add("Compiler");
    langPkgClassNames.add("Double");
    langPkgClassNames.add("Enum");
    langPkgClassNames.add("Float");
    langPkgClassNames.add("InheritableThreadLocal");
    langPkgClassNames.add("Integer");
    langPkgClassNames.add("Long");
    langPkgClassNames.add("Math");
    langPkgClassNames.add("Number");
    langPkgClassNames.add("Object");
    langPkgClassNames.add("Package");
    langPkgClassNames.add("Process");
    langPkgClassNames.add("ProcessBuilder");
    langPkgClassNames.add("Runtime");
    langPkgClassNames.add("RuntimePermission");
    langPkgClassNames.add("SecurityManager");
    langPkgClassNames.add("Short");
    langPkgClassNames.add("StackTraceElement");
    langPkgClassNames.add("StrictMath");
    langPkgClassNames.add("String");
    langPkgClassNames.add("StringBuffer");
    langPkgClassNames.add("StringBuilder");
    langPkgClassNames.add("System");
    langPkgClassNames.add("Thread");
    langPkgClassNames.add("ThreadGroup");
    langPkgClassNames.add("ThreadLocal");
    langPkgClassNames.add("Throwable");
    langPkgClassNames.add("Void");

    langPkgClassNames.add("Appendable");
    langPkgClassNames.add("AutoCloseable");
    langPkgClassNames.add("CharSequence");
    langPkgClassNames.add("Cloneable");
    langPkgClassNames.add("Comparable");
    langPkgClassNames.add("Iterable");
    langPkgClassNames.add("Readable");
    langPkgClassNames.add("Runnable");
  }

  public static String sp = File.separator;

  public static final Set<String> no_override_object_mtds = new HashSet<>(6);

  public static final Set<String> no_call_object_mtds = new HashSet<>(6);

  public static final Map<String, Set<String>> usedClzNames = new HashMap<>();
  public static final
  Map<String, Map<String, String>> usedFnNames = new HashMap<>();

  public static FileState fs;
  public static F1 fs1;
  public static F2 fs2;
  public static F3 fs3;
  public static F4 fs4;
  public static F5 fs5;
  public static F6 fs6;

  public static List<ClassFileData> compiledClasses;

  public static final String plus_op_name = "$_plus";
  public static final String minus_op_name = "$_minus";
  public static final String bit_and_op_name = "$_bit_and";
  public static final String bit_or_op_name = "$_bit_or";
  public static final String bit_negate_op_name = "$_bit_negate";
  public static final String divide_op_name = "$_divide";
  public static final String greater_equal_op_name = "$_greater_equal";
  public static final String greater_op_name = "$_greater";
  public static final String lesser_equal_op_name = "$_lesser_equal";
  public static final String lesser_op_name = "$_lesser";
  public static final String modulo_op_name = "$_modulo";
  public static final String or_op_name = "$_or";
  public static final String and_op_name = "$_and";
  public static final String power_op_name = "$_power";
  public static final String root_op_name = "$_root";
  public static final String right_shift_op_name = "$_right_shift";
  public static final String left_shift_op_name = "$_left_shift";
  public static final String ur_shift_op_name = "$_ur_shift";
  public static final String xor_op_name = "$_xor";
  public static final String times_op_name = "$_times";
  public static final String unary_not_op_name = "$_unary_not";
  public static final String unary_plus_op_name = "$_unary_plus";
  public static final String unary_minus_op_name = "$_unary_minus";

  public static final String void$name = "void";
  public static final String char$name = "UI16";
  public static final String long$name = "I64";
  public static final String int$name = "I32";
  public static final String float$name = "F32";
  public static final String boole$name = "I1";
  public static final String double$name = "F64";
  public static final String array$name = "Array";
  public static final String byte$name = "I8";
  public static final String short$name = "I16";

  public static final String null$name = "nil";
  public static final String object$name = "java.lang.Object";

  public static final String void$boxed = "java.lang.Void";
  public static final String char$boxed = "java.lang.Character";
  public static final String long$boxed = "java.lang.Long";
  public static final String int$boxed = "java.lang.Integer";
  public static final String float$boxed = "java.lang.Float";
  public static final String boole$boxed = "java.lang.Boolean";
  public static final String double$boxed = "java.lang.Double";
  public static final String byte$boxed = "java.lang.Byte";
  public static final String short$boxed = "java.lang.Short";

  public static YaaClz void$clz = YaaClz.makePrimitive(void$name);
  public static YaaClz null$clz = new YaaClz(null$name);
  public static YaaClz object$clz;
  public static YaaClz int$clz;
  public static YaaClz byte$clz;
  public static YaaClz float$clz;
  public static YaaClz char$clz;
  public static YaaClz short$clz;
  public static YaaClz long$clz;
  public static YaaClz boole$clz;
  public static YaaClz double$clz;

  static {
    no_override_object_mtds.add("finalize");
    no_override_object_mtds.add("wait");
    no_override_object_mtds.add("notify");
    no_override_object_mtds.add("notifyAll");
    no_override_object_mtds.add("getClass");
    no_override_object_mtds.add("clone");

    no_call_object_mtds.add("finalize");
    no_call_object_mtds.add("wait");
    no_call_object_mtds.add("notify");
    no_call_object_mtds.add("notifyAll");
    no_call_object_mtds.add("clone");

    null$clz.inits.add(new YaaInit());
  }

  public static final Stack<YaaClz> topClz = new Stack<>();
  public static final Map<String, String> topClzCodeName = new HashMap<>();

  public static void defineFunGlobally(String name, MtdPack mtdPack) {
    functions.put(name, mtdPack);
  }

  public static MtdPack getGlobalFunction(String name) {
    return functions.get(name);
  }

  public static void defineFieldGlobally(String name, YaaField field) {
    fields.put(name, field);
  }

  public static void defineClassGlobally(String name, YaaClz yaaClz) {
    classes.put(name, yaaClz);
  }

  public static void defineImportableClass(String name, YaaClz yaaClz) {
    imp_classes.put(name, yaaClz);
  }

  public static void defineMetaGlobally(String name, YaaMeta meta) {
    metas.put(name, meta);
  }

  public static YaaClz getTouchedClass(String name) {
    var defined_class = classes.get(name);
    if (defined_class != null) {
      return defined_class;
    }
    return new JMold().newClz(name);
  }

  public static YaaField getGlobalField(String name) {
    return fields.get(name);
  }

  public static YaaInfo getGlobalSymbol(String name) {
    var clz = classes.get(name);
    if (clz != null) {
      return clz;
    }
    var mtd = getGlobalFunction(name);
    if (mtd != null) {
      return mtd;
    }
    return getGlobalField(name);
  }
}