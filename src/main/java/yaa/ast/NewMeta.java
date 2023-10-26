package yaa.ast;

import yaa.parser.YaaToken;
import yaa.pojos.YaaInfo;
import yaa.pojos.FileState;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NewMeta extends Stmt {
  public YaaToken name;
  public Map<String, YaaToken> options = new HashMap<>(1);
  public boolean itIsTopLevelClz;
  public List<YaaMetaCall> metaCalls = new ArrayList<>(1);

  public NewMeta(YaaToken name) {
    this.name = name;
  }

  @Override
  public String toString() {
    return name.content + "{}";
  }

  public List<VDeclaration> vDeclarations = new ArrayList<>(1);
  public List<VDefinition> vDefinitions = new ArrayList<>(1);

  @Override
  public YaaInfo visit(FileState fs) {
    return fs.$newMeta(this);
  }
}
