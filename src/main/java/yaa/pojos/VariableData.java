package yaa.pojos;

import org.objectweb.asm.Label;
import yaa.ast.ObjectType;
import yaa.ast.YaaMetaCall;

import java.util.List;

public class VariableData {
  public String name;
  public String typeSignature;
  public Label label;
  public String descriptor;
  public int index;
  public List<YaaMetaCall> metaCalls;
  public List<ObjectType> type_args; //List[String@Meta1], the String is a type arg and its meta is desired

  public VariableData(String name, Label lb, String dt,
                      String ts, int index, List<YaaMetaCall> anno_data, List<ObjectType> type_args) {
    this.name = name;
    this.type_args = type_args;
    this.typeSignature = ts;
    this.label = lb;
    this.descriptor = dt;
    this.index = index;
    this.metaCalls = anno_data;
  }
}
