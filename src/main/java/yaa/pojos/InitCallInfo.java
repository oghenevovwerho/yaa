package yaa.pojos;

import java.util.List;

public class InitCallInfo {
  public List<YaaInfo> parameters;

  public InitCallInfo(List<YaaInfo> declared_parameters) {
    this.parameters = declared_parameters;
  }

  public String descriptor() {
    var sb = new StringBuilder();
    sb.append("(");
    for (var parameter : parameters) {
      if (parameter.isIBounded()) {
        parameter = ((YaaClz) parameter).parent;
      }
      sb.append(parameter.descriptor());
    }
    sb.append(")").append("V");
    return sb.toString();
  }
}
