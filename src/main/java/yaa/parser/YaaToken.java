package yaa.parser;

import java.util.Objects;

public class YaaToken {
  public TkKind kind;
  public String content;
  public String neededContent;
  public int line;
  public int column;

  public YaaToken() {
  }

  public YaaToken(TkKind kind, char content) {
    this.kind = kind;
    this.content = String.valueOf(content);
  }

  public YaaToken(TkKind kind, String content) {
    this.kind = kind;
    this.content = content;
  }

  public YaaToken(TkKind kind, String content, int line, int column) {
    this.kind = kind;
    this.content = content;
    this.line = line;
    this.column = column;
  }

  public YaaToken set_line(int token_line) {
    this.line = token_line;
    return this;
  }

  public YaaToken set_column(int token_column) {
    this.column = token_column;
    return this;
  }

  @Override
  public String toString() {
    return line + ": " + column + "  " + content;
  }

  @Override
  public boolean equals(Object o) {
    if (o instanceof String string) {
      return Objects.equals(content, string);
    }
    return false;
  }

  @Override
  public int hashCode() {
    return Objects.hash(content);
  }

  public String placeOfUse() {
    return line + ": " + column;
  }
}
