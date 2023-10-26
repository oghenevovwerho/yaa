package yaa.semantic.passes.fs6;

import yaa.ast.Break;
import yaa.pojos.YaaInfo;
import yaa.pojos.GlobalData;

import static org.objectweb.asm.Opcodes.GOTO;

public class F6Break {
  public static YaaInfo handleBreak(Break aBreak) {
    var name = aBreak.name;
    if (name == null) {
      F6.mw().visitJumpInsn(GOTO, GlobalData.fs6.break$locations.peek().label);
    } else {
      for (var it = GlobalData.fs6.break$locations.elements().asIterator(); it.hasNext(); ) {
        var ni = it.next();
        if (ni.name.equals(name.content)) {
          F6.mw().visitJumpInsn(GOTO, ni.label);
        }
      }
    }
    return GlobalData.void$clz;
  }
}
