package yaa.parser;

public class NumberToken extends yaa.parser.YaaToken {
  public NumberToken(yaa.parser.TkKind kind, char content) {
    super(kind, content);
  }

  public NumberToken(yaa.parser.TkKind kind, String content) {
    super(kind, content);
  }

  public NumberToken(yaa.parser.TkKind kind, String content, int line, int column) {
    super(kind, content, line, column);
  }
}
