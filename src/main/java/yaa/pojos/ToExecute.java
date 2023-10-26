package yaa.pojos;

import yaa.ast.ObjectType;
import yaa.ast.YaaMetaCall;

@FunctionalInterface
public interface ToExecute {
  public void execute(String fromString, YaaMeta meta, ObjectType typeArg, YaaMetaCall metaCall);
}
