package yaa.semantic.passes.fs6;

import yaa.pojos.YaaFun;
import yaa.pojos.YaaInfo;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Type;

import static yaa.semantic.passes.fs6.F6.mw;
import static org.objectweb.asm.Opcodes.*;

public class F6NoName {
  public static YaaInfo produceInvokeCode(YaaFun new$fn, String top_name) {
    var sb = new StringBuilder();
    sb.append("(");
    for (var closed$field : new$fn.closures.values()) {
      sb.append(closed$field.descriptor());
      F6Name.id(closed$field.field$name);
    }
    sb.append(")");

    var bootstrap_method_handle = new Handle(
      H_INVOKESTATIC,
      "java/lang/invoke/LambdaMetafactory",
      "metafactory",
      "(" +
        "Ljava/lang/invoke/MethodHandles$Lookup;" +
        "Ljava/lang/String;" +
        "Ljava/lang/invoke/MethodType;" +
        "Ljava/lang/invoke/MethodType;" +
        "Ljava/lang/invoke/MethodHandle;" +
        "Ljava/lang/invoke/MethodType;" +
        ")Ljava/lang/invoke/CallSite;",
      false
    );

    mw().visitInvokeDynamicInsn(
      new$fn.iMtdName,
      sb + new$fn.iClzDescriptor,
      bootstrap_method_handle,
      Type.getType(new$fn.iMtdDescriptor),
      new Handle(
        H_INVOKESTATIC,
        top_name,
        new$fn.name,
        new$fn.descriptor(),
        false
      ),
      Type.getType(new$fn.lambdaDescriptor())
    );
    return new$fn;
  }
}
