package yaa.pojos;

import java.util.HashMap;
import java.util.Map;

public class ClzFieldStore {
  private final Map<String, YaaField> clzVariables = new HashMap<>();

  public YaaField get(String name) {
    return clzVariables.get(name);
  }

  public void put(YaaField symbol) {
    clzVariables.put(symbol.field$name, symbol);
  }
}
