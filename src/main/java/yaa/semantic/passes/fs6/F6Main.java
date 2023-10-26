package yaa.semantic.passes.fs6;

import org.objectweb.asm.ClassWriter;
import yaa.Yaa;
import yaa.ast.MainFunction;
import yaa.pojos.GlobalData;
import yaa.pojos.YaaInfo;

import java.util.ArrayList;

import static org.objectweb.asm.Opcodes.*;
import static yaa.pojos.GlobalData.fs6;
import static yaa.pojos.GlobalData.nothing;
import static yaa.semantic.passes.fs6.F6.mw;

public class F6Main {
  public static YaaInfo main(MainFunction main, ClassWriter tcw) {
    fs6.cw.peek().visitField(
        ACC_PUBLIC + ACC_STATIC,
        "cmd",
        "[Ljava/lang/String;", null, null
    ).visitEnd();
    var main$fn = GlobalData.mainFunction;
    F6.f6TopMtd.push(main$fn);
    fs6.push$variables();
    F6.variableMeta.push(new ArrayList<>());
    F6.mtdWriters.push(tcw.visitMethod(
        ACC_STATIC + ACC_PUBLIC, main$fn.name,
        "([Ljava/lang/String;)V",
        null, new String[]{}
    ));
    mw().visitCode();
    mw().visitVarInsn(ALOAD, 0); //the cmd arguments
    mw().visitFieldInsn(
        PUTSTATIC, Yaa.main$clz$name, "cmd", "[Ljava/lang/String;"
    );
    main.stmt.visit(fs6);
    main$fn.closeCode();
    return nothing;
  }
}
