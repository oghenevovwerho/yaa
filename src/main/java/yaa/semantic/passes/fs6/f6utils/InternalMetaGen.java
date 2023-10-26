package yaa.semantic.passes.fs6.f6utils;

import yaa.ast.ObjectType;
import yaa.pojos.MetaPosition;
import yaa.pojos.ToExecute;
import yaa.pojos.YaaMeta;

import java.util.List;

import static yaa.pojos.GlobalData.fs6;

public class InternalMetaGen {
  private static MetaPosition parent;

  private static String getFromString(MetaPosition metaPosition) {
    var sb = new StringBuilder();
    sb.append(metaPosition.index).append(";");
    while (metaPosition.parent != null) {
      sb.append(metaPosition.parent.index).append(";");
      metaPosition = metaPosition.parent;
    }
    return sb.toString();
  }

  public static void nestedTypeArgs(List<ObjectType> types, ToExecute toExecute) {
    var type_arg_index = 0;
    for (var type_arg : types) {
      MetaPosition metaPosition = new MetaPosition(type_arg_index, parent);
      for (var metaCall : type_arg.metaCalls) {
        var from_string = getFromString(metaPosition);
        var meta_type = (YaaMeta) fs6.getSymbol(metaCall.name.content);
        toExecute.execute(from_string, meta_type, type_arg, metaCall);
      }
      if (type_arg.arguments.size() > 0) {
        parent = metaPosition;
        nestedTypeArgs(type_arg.arguments, toExecute);
      }
      type_arg_index++;
    }
    parent = null;
  }
}