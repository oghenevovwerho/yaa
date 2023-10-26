package yaa.pojos.types;

import yaa.pojos.MtdPack;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class JvmString {
  public String name = "java.lang.String";

  public static final Map<String, MtdPack> instanceMethods = new HashMap<>(60);
  public final Map<String, MtdPack> staticMethods = new HashMap<>(33);
  private static final BoxedInt singletonBoxedInt = new BoxedInt();

  static {  }
}
