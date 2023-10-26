package yaa.pojos;

import java.util.Collection;
import java.util.List;

public class FunCallInfo {
  public Collection<YaaField> closed;
  public List<YaaInfo> parameters;
  public YaaInfo declared_type;

  public FunCallInfo() {
  }

  public String descriptor(YaaClz varArg) {
    var sb = new StringBuilder();
    sb.append("(");
    for (var field : closed) {
      sb.append(field.descriptor());
    }
    for (var parameter : parameters) {
      if (parameter.isIBounded()) {
        parameter = ((YaaClz) parameter).parent;
      }
      sb.append(parameter.descriptor());
    }
    if (varArg != null) {
      sb.append(varArg.descriptor());
    }
    sb.append(")");
    if (declared_type.isIBounded()) {
      sb.append(((YaaClz) declared_type).parent.descriptor());
    } else {
      sb.append(declared_type.descriptor());
    }
    return sb.toString();
  }

  public FunCallInfo(Collection<YaaField> closed, List<YaaInfo> declared) {
    this.parameters = declared;
    this.closed = closed;
  }
}