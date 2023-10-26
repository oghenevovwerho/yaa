package yaa.pojos;

import java.util.HashMap;
import java.util.Map;

import static yaa.semantic.passes.fs6.F6.mw;
import static org.objectweb.asm.Opcodes.*;

public class MtdVariables {
  public final Map<String, Integer> variables = new HashMap<>();
  public int index = 0;

  public Integer putVar(String fieldName) {
    index++;
    variables.put(fieldName, index);
    return index;
  }

  public Integer indexOf(String fieldName) {
    return variables.get(fieldName);
  }

  public Integer putWideVar(String fieldName) {
    variables.put(fieldName, index + 1);
    index = index + 2;
    return index - 1;
  }

  public void load(String info$name, String field$name) {
    switch (info$name) {
      case GlobalData.long$name -> {
        mw().visitVarInsn(LLOAD, indexOf(field$name));
      }
      case GlobalData.double$name -> {
        mw().visitVarInsn(DLOAD, indexOf(field$name));
      }
      case GlobalData.float$name -> {
        mw().visitVarInsn(FLOAD, indexOf(field$name));
      }
      case GlobalData.int$name, GlobalData.boole$name,
          GlobalData.byte$name, GlobalData.char$name, GlobalData.short$name -> {
        mw().visitVarInsn(ILOAD, indexOf(field$name));
      }
      default -> {
        mw().visitVarInsn(ALOAD, indexOf(field$name));
      }
    }
  }

  public void store(YaaInfo info, String variable$name) {
    switch (info.name) {
      case GlobalData.double$name -> {
        var newFieldIndex = GlobalData.fs6.variables.peek().putWideVar(variable$name);
        mw().visitVarInsn(DSTORE, newFieldIndex);
      }
      case GlobalData.float$name -> {
        var newFieldIndex = GlobalData.fs6.variables.peek().putVar(variable$name);
        mw().visitVarInsn(FSTORE, newFieldIndex);
      }
      case GlobalData.long$name -> {
        var newFieldIndex = GlobalData.fs6.variables.peek().putWideVar(variable$name);
        mw().visitVarInsn(LSTORE, newFieldIndex);
      }
      default -> {
        var newFieldIndex = GlobalData.fs6.variables.peek().putVar(variable$name);
        if (info.isPrimitive()) {
          mw().visitVarInsn(ISTORE, newFieldIndex);
        } else {
          mw().visitVarInsn(ASTORE, newFieldIndex);
        }
      }
    }
  }
}
