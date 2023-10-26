package yaa.pojos;

import static yaa.semantic.passes.fs6.F6.mw;
import static org.objectweb.asm.Opcodes.*;

public class YaaField extends YaaInfo {
  public FieldIsWhat itIsWhat;
  public String field$name;
  public YaaInfo data;
  public boolean isPredefined;
  public boolean itIsFinal = false;
  public boolean itIsStatic;
  public String owner;
  public boolean is4loop;
  //used to index automatically cast variables
  public String cast$name;
  public boolean isEnumField;
  public String path;
  public boolean isRecordField;

  public YaaField() {
  }

  @Override
  public boolean isUnboundedAndNotPrimitive() {
    return data.isUnboundedAndNotPrimitive();
  }

  @Override
  public String clzUseSignature() {
    return data.clzUseSignature();
  }

  @Override
  protected int inputtedSize() {
    return data.inputtedSize();
  }

  public YaaField(String field$name, boolean itIsFinal) {
    this.itIsFinal = itIsFinal;
    this.field$name = field$name;
  }

  public YaaField(String field$name) {
    this.field$name = field$name;
  }

  @Override
  public String toString() {
    return field$name + " : " + data;
  }

  @Override
  public boolean accepts(YaaInfo other) {
    return data.accepts(other);
  }

  @Override
  public String descriptor() {
    if (typeParam != null) {
      return typeParam.descriptor();
    }
    return data.descriptor();
  }

  @Override
  public boolean isBoxed() {
    return data.isBoxed();
  }

  @Override
  public boolean isPrimitive() {
    return data.isPrimitive();
  }

  public void generateReferenceCode() {
    if (cast$name != null) {
      var mtdIndex = GlobalData.fs6.variables.peek().indexOf(cast$name);
      switch (data.name) {
        case GlobalData.double$name -> mw().visitVarInsn(DLOAD, mtdIndex);
        case GlobalData.long$name -> mw().visitVarInsn(LLOAD, mtdIndex);
        case GlobalData.float$name -> mw().visitVarInsn(FLOAD, mtdIndex);
        default -> {
          if (isPrimitive()) {
            mw().visitVarInsn(ILOAD, mtdIndex);
          } else {
            mw().visitVarInsn(ALOAD, mtdIndex);
          }
        }
      }
      return;
    }
    if (itIsWhat == FieldIsWhat.top$field) {
      mw().visitFieldInsn(
          GETSTATIC,
          owner,
          field$name,
          descriptor()
      );
      return;
    }
    if (itIsStatic) {
      mw().visitFieldInsn(
          GETSTATIC,
          owner,
          field$name,
          descriptor()
      );
      return;
    }
    if (isRecordField) {
      var top$clz = GlobalData.topClz.peek();
      mw().visitVarInsn(ALOAD, 0);
      mw().visitFieldInsn(
          GETFIELD,
          top$clz.codeName,
          field$name,
          descriptor()
      );
      return;
    }
    if (itIsWhat == FieldIsWhat.clz$field) {
      mw().visitVarInsn(ALOAD, 0);
      mw().visitFieldInsn(GETFIELD, owner, field$name, descriptor());
      return;
    }
    if (itIsWhat == FieldIsWhat.mtd$field) {
      var mtdIndex = GlobalData.fs6.variables.peek().indexOf(field$name);
      switch (data.name) {
        case GlobalData.double$name -> mw().visitVarInsn(DLOAD, mtdIndex);
        case GlobalData.long$name -> mw().visitVarInsn(LLOAD, mtdIndex);
        case GlobalData.float$name -> mw().visitVarInsn(FLOAD, mtdIndex);
        default -> {
          if (isPrimitive()) {
            mw().visitVarInsn(ILOAD, mtdIndex);
          } else {
            mw().visitVarInsn(ALOAD, mtdIndex);
          }
        }
      }
    }
  }
}
