package yaa.ast;

import yaa.parser.YaaToken;
import yaa.parser.TkKind;
import yaa.pojos.FileState;
import yaa.pojos.YaaInfo;

import java.util.Objects;

public class ProgramIn extends Stmt {
  public final String path;
  public boolean has$top$fields;

  public ProgramIn(String path) {
    this.path = path;
    this.start = new YaaToken(TkKind.id, path).set_line(-2).set_column(-2);
    this.close = new YaaToken(TkKind.id, path).set_column(-2).set_line(-2);
  }

  @Override
  public YaaInfo visit(FileState fs) {
    return fs.$programIn(this);
  }

  @Override
  public boolean equals(Object otherObject) {
    if (otherObject instanceof ProgramIn in) {
      return Objects.equals(path, in.path);
    }
    return false;
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(path);
  }
}
