package yaa.parser;

public class ParserState {
  int lexerIndex;
  int lexerLine;
  int lexerColumn;
  yaa.parser.YaaToken ct;

  public ParserState(int lexerIndex, int lexerLine, int lexerColumn, yaa.parser.YaaToken ct) {
    this.lexerIndex = lexerIndex;
    this.lexerLine = lexerLine;
    this.lexerColumn = lexerColumn;
    this.ct = ct;
  }
}
