package yaa.semantic.passes.fs6;

import yaa.ast.Continue;
import yaa.pojos.YaaInfo;
import yaa.pojos.GlobalData;

import static yaa.semantic.passes.fs6.F6.mw;
import static org.objectweb.asm.Opcodes.GOTO;

public class F6Continue {
  public static YaaInfo handleContinue(Continue aContinue) {
    var name = aContinue.name;
    if (name == null) {
      mw().visitJumpInsn(GOTO, GlobalData.fs6.continue$locations.peek().label);
    } else {
      var name$content = name.content;
      for (var it = GlobalData.fs6.continue$locations.elements().asIterator(); it.hasNext(); ) {
        var ni = it.next();
        if (ni.name.equals(name$content)) {
          mw().visitJumpInsn(GOTO, ni.label);
        }
      }
    }
    return GlobalData.void$clz;
  }
}
