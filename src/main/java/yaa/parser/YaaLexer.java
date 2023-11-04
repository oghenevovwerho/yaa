package yaa.parser;

import yaa.pojos.YaaError;

import static java.lang.Character.*;
import static yaa.parser.TkKind.*;

public class YaaLexer {
  public boolean allAllowed;
  protected int column = 0;
  protected int line = 1;
  protected int indexIntoSource = 0;
  private final String source;
  private final int source_size;
  private char current_char;

  public YaaLexer(String source) {
    this.source = source;
    this.source_size = source.length();
  }

  public YaaToken nextToken() {
    while (indexIntoSource < source_size) {
      current_char = source.charAt(indexIntoSource++);
      column++;
      switch (current_char) {
        case ' ' -> {
          if (allAllowed) {
            var token = new YaaToken(space, current_char);
            return token.set_line(line).set_column(column);
          }
        }
        case '#' -> {
          comment();
        }
        case '!' -> {
          if (indexIntoSource + 2 < source_size && source.charAt(indexIntoSource) == '=') {
            if (source.charAt(indexIntoSource + 1) == '=') {
              var idToken = new YaaToken(m_not_equal, "!==");
              idToken.set_line(line).set_column(column);
              indexIntoSource = indexIntoSource + 2;
              column = column + 2;
              return idToken;
            }
          }
          return double_tokens(not, not_equal, '=', "!=");
        }
        case '~' -> {
          return single_token(wavy);
        }
        case '`' -> {
          return single_token(b_tick);
        }
        case '.' -> {
          return single_token(dot);
        }
        case '@' -> {
          return single_token(at);
        }
        case '%' -> {
          return single_token(modulo);
        }
        case '^' -> {
          return single_token(caret);
        }
        case '&' -> {
          return double_tokens(ampersand, d_ampersand, '&', "&&");
        }
        case '*' -> {
          return double_tokens(star, star_star, '*', "**");
        }
        case '-' -> {
          return double_tokens(minus, arrow, '>', "->");
        }
        case '+' -> {
          return single_token(plus);
        }
        case '=' -> {
          if (indexIntoSource + 2 < source_size && source.charAt(indexIntoSource) == '=') {
            if (source.charAt(indexIntoSource + 1) == '=') {
              var idToken = new YaaToken(m_equal, "===");
              idToken.set_line(line).set_column(column);
              indexIntoSource = indexIntoSource + 2;
              column = column + 2;
              return idToken;
            }
          }
          if (indexIntoSource + 1 < source_size && source.charAt(indexIntoSource) == '=') {
            if (source.charAt(indexIntoSource) == '=') {
              var idToken = new YaaToken(equal_equal, "==");
              idToken.set_line(line).set_column(column);
              indexIntoSource = indexIntoSource + 1;
              column = column + 1;
              return idToken;
            }
          }
          return double_tokens(equal, equal_arrow, '>', "=>");
        }
        case '<' -> {
          if (indexIntoSource < source_size && source.charAt(indexIntoSource) == '<') {
            var idToken = new YaaToken(l_shift, "<<");
            idToken.set_line(line).set_column(column);
            indexIntoSource = indexIntoSource + 1;
            column = column + 1;
            return idToken;
          }
          return double_tokens(l_than, l_equal, '=', "<=");
        }
        case '>' -> {
          if (indexIntoSource + 1 < source_size && source.charAt(indexIntoSource) == '>') {
            if (source.charAt(indexIntoSource + 1) == '>') {
              var idToken = new YaaToken(u_r_shift, ">>>");
              idToken.set_line(line).set_column(column);
              indexIntoSource = indexIntoSource + 2;
              column = column + 1;
              return idToken;
            }
          }
          if (indexIntoSource < source_size) {
            var next$char = source.charAt(indexIntoSource);
            if (next$char == '>') {
              var right$token = new YaaToken(r_shift, ">>");
              right$token.set_line(line).set_column(column);
              indexIntoSource = indexIntoSource + 1;
              column = column + 1;
              return right$token;
            }
            if (next$char == '=') {
              var g$equal$token = new YaaToken(g_equal, ">=");
              g$equal$token.set_line(line).set_column(column);
              indexIntoSource = indexIntoSource + 1;
              column = column + 1;
              return g$equal$token;
            }
          }
          return single_token(g_than);
        }
        case '|' -> {
          return double_tokens(pipe, d_pipe, '|', "||");
        }
        case '/' -> {
          return double_tokens(f_slash, df_slash, '/', "//");
        }
        case '\\' -> {
          var possibleNext = source.charAt(indexIntoSource);
          var col_value = column;
          switch (possibleNext) {
            case 'n' -> {
              indexIntoSource++;
              var token = new YaaToken(escaped_newline, "\\n");
              token.set_line(line).set_column(col_value);
              column = column + 1;
              return token;
            }
            case '{' -> {
              indexIntoSource++;
              var token = new YaaToken(escaped_l_curly, "\\{");
              token.set_line(line).set_column(col_value);
              column = column + 1;
              return token;
            }
            case 't' -> {
              indexIntoSource++;
              var token = new YaaToken(escaped_tab, "\\t");
              token.set_line(line).set_column(col_value);
              column = column + 1;
              return token;
            }
            case '\'' -> {
              indexIntoSource++;
              var token = new YaaToken(escaped_s_quote, "\\'");
              token.set_line(line).set_column(col_value);
              column = column + 1;
              return token;
            }
            case 'u' -> {
              indexIntoSource++;
              if (!canBeInHex(source.charAt(indexIntoSource))) {
                throw new YaaError(
                    line + ": " + column,
                    "A value is expected for the unicode literal after \\u"
                );
              }
              var sb = new StringBuilder();
              while (indexIntoSource < source_size) {
                var currentInID = source.charAt(indexIntoSource);
                if (canBeInHex(currentInID)) {
                  sb.append(currentInID);
                } else {
                  break;
                }
                column++;
                indexIntoSource++;
              }
              var token = new YaaToken(unicode, sb.toString());
              token.set_line(line).set_column(col_value);
              column = column + 1;
              return token;
            }
            case '`' -> {
              indexIntoSource++;
              var token = new YaaToken(escaped_b_tick, "\\`");
              token.set_line(line).set_column(col_value);
              column = column + 1;
              return token;
            }
            case '#' -> {
              indexIntoSource++;
              var token = new YaaToken(escaped_hash, "\\#");
              token.set_line(line).set_column(col_value);
              column = column + 1;
              return token;
            }
            case '\\' -> {
              indexIntoSource++;
              var token = new YaaToken(escaped_b_slash, "\\\\");
              token.set_line(line).set_column(col_value);
              column = column + 1;
              return token;
            }
            default -> {
              throw new YaaError(
                  line + ": " + (column + 1),
                  "a backslash must be followed by a valid escaped character",
                  "\\", "#", "`", "u for unicode", "t", "n", "{"
              );
            }
          }
        }
        case '\'' -> {
          return single_token(s_quote);
        }
        case '?' -> {
          return single_token(q_mark);
        }
        case ':' -> {
          return double_tokens(colon, d_colon, ':', "::");
        }
        case '"' -> {
          return single_token(d_quote);
        }
        case ';' -> {
          return single_token(TkKind.semi_colon);
        }
        case ',' -> {
          return single_token(comma);
        }
        case '{' -> {
          return single_token(l_curly);
        }
        case '}' -> {
          return single_token(r_curly);
        }
        case '[' -> {
          return single_token(l_bracket);
        }
        case ']' -> {
          return single_token(r_bracket);
        }
        case '(' -> {
          return single_token(l_paren);
        }
        case ')' -> {
          return single_token(r_paren);
        }
        case '\n' -> {
          line++;
          column = 0;
        }
        case '\t' -> {
          column = column + 3;
        }
        case '\r' -> column--;
        default -> {
          if (isAlphabetic(current_char) || current_char == '$' || current_char == '_') {
            return handleIdentifier();
          }
          if (isDigit(current_char)) {
            return handleNumber();
          }
          return new YaaToken(
              unknown, "unknown_token").set_line(line).set_column(column + 1);
        }
      }
    }
    return new YaaToken(eof, "e").set_line(line).set_column(column + 1);
  }

  private void comment() {
    var entry_line = line;
    var entry_column = column;
    while (indexIntoSource < source_size) {
      var currentInComment = source.charAt(indexIntoSource++);
      column++;
      switch (currentInComment) {
        case '\n' -> {
          line++;
          column = 0;
        }
        case '#' -> {
          return;
        }
      }
    }
    if (indexIntoSource == source_size) {
      throw new YaaError(
          entry_line + ": " + entry_column, "The comment at ["
          + entry_line + ": " + entry_column + "] must be closed"
      );
    }
  }

  private boolean canBeInHex(char charAt) {
    if (isDigit(charAt)) {
      return true;
    }
    return charAt >= 'a' && charAt <= 'f' || charAt >= 'A' && charAt <= 'F';
  }

  private YaaToken handleIdentifier() {
    var idColumn = column;
    var identifier = new StringBuilder();
    identifier.append(current_char);
    while (indexIntoSource < source_size) {
      var currentInID = source.charAt(indexIntoSource);
      if (isAcceptedInID(currentInID)) {
        identifier.append(currentInID);
      } else {
        break;
      }
      column++;
      indexIntoSource++;
    }
    var content = identifier.toString();
    if (content.equals("$")) {
      return new YaaToken(dollar, "$").set_column(idColumn).set_line(line);
    }
    if (content.equals("_")) {
      return new YaaToken(u_score, "_").set_column(idColumn).set_line(line);
    }
    if (content.equals("oka")) {
      return new YaaToken(kw_oka, content).set_line(line).set_column(idColumn);
    }
    if (content.equals("na")) {
      return new YaaToken(kw_na, content).set_line(line).set_column(idColumn);
    }
    if (content.equals("ufuoma")) {
      return new YaaToken(kw_ufuoma, content).set_line(line).set_column(idColumn);
    }
    if (content.equals("idan")) {
      return new YaaToken(kw_idan, content).set_line(line).set_column(idColumn);
    }
    if (content.equals("okpetu")) {
      return new YaaToken(kw_okpetu, content).set_line(line).set_column(idColumn);
    }
    if (content.equals("mzazi")) {
      return new YaaToken(kw_mzazi, content).set_line(line).set_column(idColumn);
    }
    if (content.equals("tesiwaju")) {
      return new YaaToken(kw_tesiwaju, content).set_line(line).set_column(idColumn);
    }
    if (content.equals("ode")) {
      return new YaaToken(kw_ode, content).set_line(line).set_column(idColumn);
    }
    if (content.equals("comot")) {
      return new YaaToken(kw_comot, content).set_line(line).set_column(idColumn);
    }
    if (content.equals("hii")) {
      return new YaaToken(kw_hii, content).set_line(line).set_column(idColumn);
    }
    if (content.equals("naso")) {
      return new YaaToken(kw_naso, content).set_line(line).set_column(idColumn);
    }
    if (content.equals("nalie")) {
      return new YaaToken(kw_nalie, content).set_line(line).set_column(idColumn);
    }
    if (content.equals("ozi")) {
      return new YaaToken(kw_ozi, content).set_line(line).set_column(idColumn);
    }
    if (content.equals("tuli")) {
      return new YaaToken(kw_tuli, content).set_line(line).set_column(idColumn);
    }
    if (content.equals("fimisile")) {
      return new YaaToken(kw_fimisile, content).set_line(line).set_column(idColumn);
    }
    if (content.equals("record")) {
      return new YaaToken(kw_record, content).set_line(line).set_column(idColumn);
    }
    if (content.equals("enum")) {
      return new YaaToken(kw_enum, content).set_line(line).set_column(idColumn);
    }
    if (content.equals("interface")) {
      return new YaaToken(kw_interface, content).set_line(line).set_column(idColumn);
    }
    if (content.equals("$".repeat(content.length()))) {
      return new YaaToken(dollar_bunch, content).set_line(line).set_column(idColumn);
    }
    if (content.equals("_".repeat(content.length()))) {
      return new YaaToken(underscore_bunch, content).set_line(line).set_column(idColumn);
    }
    return new YaaToken(id, content).set_line(line).set_column(idColumn);
  }

  private YaaToken handleNumber() {
    var num$column = column;
    var number = new StringBuilder();
    number.append(current_char);

    while (indexIntoSource < source_size) {
      var currentInNumber = source.charAt(indexIntoSource);
      if (currentInNumber == '$') {
        var base = Integer.parseInt(number.toString());
        if (base < MIN_RADIX || base > MAX_RADIX) {
          throw new YaaError(
              line + ": " + num$column,
              "The base of a number must be between "
                  + MIN_RADIX + " and " + MAX_RADIX
          );
        }
        number.append("$");
        column++;
        indexIntoSource++;
        return basexToken(number, base, num$column);
      }
      if (currentInNumber == '.') {
        if (source_size <= indexIntoSource + 1 || !isDigit(source.charAt(indexIntoSource + 1))) {
          return new YaaToken(int_literal, number.toString(), line, num$column);
        }
        number.append(".");
        column++;
        indexIntoSource++;
        while (indexIntoSource < source_size) {
          column++;
          currentInNumber = source.charAt(indexIntoSource);
          if (currentInNumber == 'F') {
            column++;
            indexIntoSource++;
            number.append("F");
            return new YaaToken(
                float_literal, number.toString(), line, num$column);
          } else if (currentInNumber == 'D') {
            column++;
            indexIntoSource++;
            number.append("D");
            return new YaaToken(
                double_literal, number.toString(), line, num$column);
          } else if (isDigit(currentInNumber)) {
            number.append(currentInNumber);
          } else if (currentInNumber == '_') {
            if (source_size == indexIntoSource + 1 || !isDigit(source.charAt(indexIntoSource + 1))) {
              return new YaaToken(
                  double_literal, number.toString(), line, num$column);
            }
            number.append("_");
          } else {
            return new YaaToken(
                double_literal, number.toString(), line, num$column);
          }
          indexIntoSource++;
        }
        return new YaaToken(
            double_literal, number.toString(), line, num$column);
      } else if (isDigit(currentInNumber)) {
        number.append(currentInNumber);
      } else if (currentInNumber == '_') {
        if (source_size == indexIntoSource + 1 || !isDigit(source.charAt(indexIntoSource + 1))) {
          return new YaaToken(int_literal, number.toString(), line, num$column);
        }
        number.append(currentInNumber);
      } else if (currentInNumber == 'S') {
        number.append(currentInNumber);
        indexIntoSource++;
        column++;
        return new YaaToken(short_literal, number.toString(), line, num$column);
      } else if (currentInNumber == 'L') {
        number.append(currentInNumber);
        indexIntoSource++;
        column++;
        return new YaaToken(long_literal, number.toString(), line, num$column);
      } else if (currentInNumber == 'B') {
        number.append(currentInNumber);
        indexIntoSource++;
        column++;
        return new YaaToken(byte_literal, number.toString(), line, num$column);
      } else if (currentInNumber == 'F') {
        number.append(currentInNumber);
        indexIntoSource++;
        column++;
        return new YaaToken(float_literal, number.toString(), line, num$column);
      } else if (currentInNumber == 'D') {
        number.append(currentInNumber);
        indexIntoSource++;
        column++;
        return new YaaToken(double_literal, number.toString(), line, num$column);
      } else {
        break;
      }
      indexIntoSource++;
      column++;
    }
    return new YaaToken(int_literal, number.toString(), line, num$column);
  }

  private void checkBase(YaaToken numb$token, String content, int base) {
    if (base >= 16) {
      for (int i = 0; i < content.length(); i++) {
        var cInBase = content.charAt(i);
        if (Character.isDigit(cInBase)) {
          continue;
        }
        if (Character.isAlphabetic(cInBase) && !isAcceptedInHex(cInBase)) {
          var toAdd = numb$token.content.length() - content.length();
          var col = (numb$token.column + i + toAdd);
          throw new YaaError(numb$token.line + ": " + col,
              cInBase + " is not allowed in a number of base " + base);
        }
      }
    } else {
      for (int i = 0; i < content.length(); i++) {
        var cInBase = content.charAt(i);
        if (Character.isDigit(cInBase) && base <= getNumericValue(cInBase)) {
          var toAdd = numb$token.content.length() - content.length();
          var col = (numb$token.column + i + toAdd);
          throw new YaaError(numb$token.line + ": " + col,
              cInBase + " is not allowed in a number of base " + base
          );
        }
      }
    }
  }

  private boolean isAcceptedInHex(char cInBase) {
    if (cInBase >= 'a' && cInBase <= 'f') {
      return true;
    }
    return cInBase >= 'A' && cInBase <= 'F';
  }

  private YaaToken basexToken(StringBuilder number, int base, int num$column) {
    while (indexIntoSource < source_size) {
      var currentInNumber = source.charAt(indexIntoSource);
      if (currentInNumber == '.') {
        if (source_size <= indexIntoSource + 1 || !isDigit(source.charAt(indexIntoSource + 1))) {
          var base_x = new BasexToken(
              basex, number.toString(), line, num$column);
          var dc = number.toString();
          var content = dc.substring(dc.indexOf("$") + 1);
          checkBase(base_x, content, base);
          base_x.number = content;
          base_x.base = base;
          return base_x;
        }
        number.append(".");
        column++;
        indexIntoSource++;
        while (indexIntoSource < source_size) {
          column++;
          currentInNumber = source.charAt(indexIntoSource);
          if (currentInNumber == 'F') {
            column++;
            indexIntoSource++;
            number.append("F");
            var base_x = new BasexToken(
                basex, number.toString(), line, num$column);
            var dc = number.toString();
            var content = dc.substring(dc.indexOf("$") + 1);
            checkBase(base_x, content, base);
            base_x.number = content;
            base_x.isFloated = true;
            base_x.base = base;
            return base_x;
          } else if (currentInNumber == 'D') {
            column++;
            indexIntoSource++;
            number.append("D");
            var base_x = new BasexToken(
                basex, number.toString(), line, num$column);
            var dc = number.toString();
            var content = dc.substring(dc.indexOf("$") + 1);
            checkBase(base_x, content, base);
            base_x.number = content;
            base_x.isPointed = true;
            base_x.base = base;
            return base_x;
          } else if (isDigit(currentInNumber)) {
            number.append(currentInNumber);
          } else if (currentInNumber == '_') {
            if (source_size == indexIntoSource + 1 || !isDigit(source.charAt(indexIntoSource + 1))) {
              var base_x = new BasexToken(
                  basex, number.toString(), line, num$column);
              var dc = number.toString();
              var content = dc.substring(dc.indexOf("$") + 1);
              checkBase(base_x, content, base);
              base_x.number = content;
              base_x.base = base;
              base_x.isPointed = true;
              return base_x;
            }
            number.append("_");
          } else {
            var base_x = new BasexToken(
                basex, number.toString(), line, num$column);
            var dc = number.toString();
            var content = dc.substring(dc.indexOf("$") + 1);
            checkBase(base_x, content, base);
            base_x.number = content;
            base_x.base = base;
            base_x.isPointed = true;
            return base_x;
          }
          indexIntoSource++;
        }
        var base_x = new BasexToken(
            basex, number.toString(), line, num$column);
        var dc = number.toString();
        var content = dc.substring(dc.indexOf("$") + 1);
        checkBase(base_x, content, base);
        base_x.number = content;
        base_x.base = base;
        base_x.isPointed = true;
        return base_x;
      } else if (isDigit(currentInNumber)) {
        number.append(currentInNumber);
      } else if (currentInNumber == '_') {
        if (source_size == indexIntoSource + 1 || !isDigit(source.charAt(indexIntoSource + 1))) {
          var base_x = new BasexToken(
              basex, number.toString(), line, num$column);
          var dc = number.toString();
          var content = dc.substring(dc.indexOf("$") + 1);
          checkBase(base_x, content, base);
          base_x.number = content;
          base_x.base = base;
          base_x.isInt = true;
          return base_x;
        }
        number.append(currentInNumber);
      } else if (currentInNumber == 'S') {
        number.append(currentInNumber);
        indexIntoSource++;
        column++;
        var base_x = new BasexToken(
            basex, number.toString(), line, num$column);
        var dc = number.toString();
        var content = dc.substring(dc.indexOf("$") + 1);
        checkBase(base_x, content, base);
        base_x.number = content;
        base_x.base = base;
        base_x.isShorted = true;
        return base_x;
      } else if (currentInNumber == 'L') {
        number.append(currentInNumber);
        indexIntoSource++;
        column++;
        var base_x = new BasexToken(
            basex, number.toString(), line, num$column);
        var dc = number.toString();
        var content = dc.substring(dc.indexOf("$") + 1);
        checkBase(base_x, content, base);
        base_x.number = content;
        base_x.base = base;
        base_x.isLong = true;
        return base_x;
      } else if (currentInNumber == 'B') {
        number.append(currentInNumber);
        indexIntoSource++;
        column++;
        var base_x = new BasexToken(
            basex, number.toString(), line, num$column);
        var dc = number.toString();
        var content = dc.substring(dc.indexOf("$") + 1);
        checkBase(base_x, content, base);
        base_x.number = content;
        base_x.base = base;
        base_x.isByte = true;
        return base_x;
      } else if (currentInNumber == 'F') {
        number.append(currentInNumber);
        indexIntoSource++;
        column++;
        var base_x = new BasexToken(
            basex, number.toString(), line, num$column);
        var dc = number.toString();
        var content = dc.substring(dc.indexOf("$") + 1);
        checkBase(base_x, content, base);
        base_x.number = content;
        base_x.base = base;
        base_x.isFloated = true;
        return base_x;
      } else if (currentInNumber == 'D') {
        number.append(currentInNumber);
        indexIntoSource++;
        column++;
        var base_x = new BasexToken(
            basex, number.toString(), line, num$column);
        var dc = number.toString();
        var content = dc.substring(dc.indexOf("$") + 1);
        checkBase(base_x, content, base);
        base_x.number = content;
        base_x.base = base;
        base_x.isPointed = true;
        return base_x;
      } else {
        break;
      }
      indexIntoSource++;
      column++;
    }
    var base_x = new BasexToken(
        basex, number.toString(), line, num$column);
    var dc = number.toString();
    var content = dc.substring(dc.indexOf("$") + 1);
    checkBase(base_x, content, base);
    base_x.number = content;
    base_x.base = base;
    base_x.isInt = true;
    return base_x;
  }

  private boolean isAcceptedInID(char currentInID) {
    if (currentInID == '$') {
      return true;
    }
    if (currentInID == '_') {
      return true;
    }
    if (Character.isAlphabetic(currentInID)) {
      return true;
    }
    return Character.isDigit(currentInID);
  }

  private YaaToken single_token(TkKind token_kind) {
    var token = new YaaToken(token_kind, current_char);
    return token.set_line(line).set_column(column);
  }

  private YaaToken double_tokens(TkKind ts, TkKind td, char next, String out) {
    if (indexIntoSource < source_size && source.charAt(indexIntoSource) == next) {
      indexIntoSource++;
      var token = new YaaToken(td, out);
      token.set_line(line).set_column(++column);
      return token;
    }
    return single_token(ts);
  }
}
