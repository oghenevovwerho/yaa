package yaa.pojos;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class YaaInit extends YaaInfo {
  public List<YaaInfo> parameters = new ArrayList<>();
  public List<YaaClz> inputted = new ArrayList<>(1);
  public List<YaaInfo> raw_parameters;
  public InitCallInfo callInfo;

  public YaaInit() {
  }

  public String descriptor(Collection<YaaField> closed) {
    String return$type = "V";
    var sb = new StringBuilder();
    sb.append("(");
    for (var field : closed) {
      sb.append(field.descriptor());
    }
    for (YaaInfo parameter : parameters) {
      sb.append(parameter.descriptor());
    }
    return sb.append(")").append(return$type).toString();
  }

  @Override
  public String descriptor() {
    String return$type = "V";
    var sb = new StringBuilder();
    sb.append("(");
    for (YaaInfo parameter : parameters) {
      sb.append(parameter.descriptor());
    }
    return sb.append(")").append(return$type).toString();
  }

  public String enumDescriptor() {
    String return$type = "V";
    var sb = new StringBuilder();
    sb.append("(");
    sb.append("Ljava/lang/String;I");
    for (YaaInfo parameter : parameters) {
      sb.append(parameter.descriptor());
    }
    return sb.append(")").append(return$type).toString();
  }

  public String descriptor(String outerDescriptor) {
    String return$type = "V";
    var sb = new StringBuilder();
    sb.append("(").append(outerDescriptor);
    for (YaaInfo parameter : parameters) {
      sb.append(parameter.descriptor());
    }
    return sb.append(")").append(return$type).toString();
  }

  @Override
  public YaaInit acceptsInit(List<YaaInfo> arguments) {
    if (parameters.size() == arguments.size()) {
      var declared_parameters = new ArrayList<YaaInfo>();
      for (int i = 0; i < arguments.size(); i++) {
        var argument = arguments.get(i);
        var parameter = parameters.get(i);
        if (parameter.isIBounded() && !argument.isIBounded()) {
          return null;
        }
        if (!parameter.accepts(argument)) {
          if (parameter instanceof YaaClz clz) {
            if (argument instanceof YaaClz arg$clz) {
              if (!clz.isParentOf(arg$clz)) {
                if (arg$clz.hasTrait(clz) == null) {
                  return null;
                }
              }
            } else {
              return null;
            }
          }
        }
        if (argument.typeParam != null && !argument.isPrimitive()) {
          declared_parameters.add(argument.typeParam.parent);
        } else if (parameter.typeParam != null) {
          declared_parameters.add(parameter.typeParam.parent);
        } else {
          declared_parameters.add(parameter);
        }
      }
      if (raw_parameters != null) {
        declared_parameters = new ArrayList<>(parameters.size());
        declared_parameters.addAll(raw_parameters);
      }
      var yaaInit = (YaaInit) this.cloneInfo();
      yaaInit.callInfo = new InitCallInfo(declared_parameters);
      return yaaInit;
    }
    return null;
  }

  @Override
  public String toString() {
    return parameterString() + "{...}";
  }

  private String parameterString() {
    if (parameters.size() == 0) {
      return "()";
    }
    if (parameters.size() == 1) {
      return "(" + parameters.get(0) + ")";
    }
    var sb = new StringBuilder();
    sb.append(parameters.get(0));
    for (int i = 1; i < parameters.size(); i++) {
      sb.append(", ").append(parameters.get(i));
    }
    return "(" + sb + ")";
  }

  public YaaClz changeCParams(List<YaaClz> type_arguments) {
    return null;
  }
}
