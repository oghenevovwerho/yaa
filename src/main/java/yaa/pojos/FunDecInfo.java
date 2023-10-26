package yaa.pojos;

import java.util.List;

public class FunDecInfo {
  public List<YaaInfo> declared_parameters;
  public YaaInfo declared_type;

  public String descriptor() {
    var sb = new StringBuilder();
    sb.append("(");
    for (var parameter : declared_parameters) {
      if (parameter instanceof YaaClz clz) {
        if (clz.cbIndex > -1 || clz.mbIndex > -1) {
          parameter = ((YaaClz) parameter).parent;
        }
      }
      sb.append(parameter.descriptor());
    }
    sb.append(")").append(declared_type.descriptor());
    return sb.toString();
  }
}
