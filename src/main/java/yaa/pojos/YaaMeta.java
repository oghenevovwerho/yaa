package yaa.pojos;

import yaa.parser.YaaToken;

import java.lang.annotation.ElementType;
import java.lang.annotation.RetentionPolicy;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static yaa.semantic.passes.fs6.F6.mw;

public class YaaMeta extends YaaInfo {
  public Map<String, YaaField> requiredFields = new HashMap<>(1);
  public Map<String, YaaField> defaultFields = new HashMap<>(1);
  public Set<ElementType> allowedPlaces = new HashSet<>(1);
  public RetentionPolicy retention = RetentionPolicy.CLASS;
  public boolean isRepeatable;

  @Override
  public String descriptor() {
    return "L" + codeName + ";";
  }
}
