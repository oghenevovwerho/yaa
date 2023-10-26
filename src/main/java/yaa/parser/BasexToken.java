package yaa.parser;

public class BasexToken extends yaa.parser.YaaToken {
  public int base;
  public boolean isFloated;
  public boolean isPointed;
  public boolean isInt;
  public boolean isShorted;
  public boolean isByte;
  public boolean isLong;
  public String number;

  public BasexToken(yaa.parser.TkKind kind, String content, int line, int column) {
    super(kind, content, line, column);
  }
}
