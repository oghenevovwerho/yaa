package yaa.pojos;

public class MetaConfig {
  public static YaaMeta config() {
    var meta = new YaaMeta();
    meta.name = GlobalData.configMetaClzName;
    meta.isRepeatable = false;
    return meta;
  }
}
