package yaa.ast;

import yaa.pojos.FileState;
import yaa.pojos.YaaInfo;

import java.util.List;

public class Imports extends Stmt {
  public final List<Import> imports;

  public Imports(List<Import> imports) {
    this.imports = imports;
  }

  @Override
  public YaaInfo visit(FileState fs) {
    return fs.$imports(this);
  }
}
