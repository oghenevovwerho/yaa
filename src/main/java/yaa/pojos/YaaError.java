package yaa.pojos;

import java.io.File;
import java.util.List;

import static java.io.File.separator;
import static java.lang.Integer.parseInt;
import static java.nio.file.Files.readAllBytes;
import static java.nio.file.Path.of;

public class YaaError extends Error {
  private final String[] messages;

  public YaaError(String... messages) {
    super(messages[0]);
    this.messages = messages;
  }

  public static String filePath;

  @Override
  public String getLocalizedMessage() {
    return constructMsg();
  }

  @Override
  public String getMessage() {
    return constructMsg();
  }

  @Override
  public String toString() {
    return constructMsg();
  }

  private String constructMsg() {
    var complete_path = "src" + separator + "main" + separator + "yaa" + separator + filePath;
    var msb = new StringBuilder();
    msb.append("\n");
    int z = 1;
    int totalMsgLength = 0;
    for (; z < messages.length - 1; z++) {
      var message = messages[z];
      if (messages[z].length() > totalMsgLength) {
        totalMsgLength = messages[z].length();
      }
      msb.append("   ").append(message).append("\n");
    }
    msb.append("   ").append(messages[z]).append("\n");
    msb.append("   ").append("+".repeat(totalMsgLength));
    msb.append("\n");
    var l$msg = new StringBuilder();
    try {
      var indexOfColumn = messages[0].indexOf(':');
      var line = parseInt(messages[0].substring(0, indexOfColumn));
      var column = getColumn();
      var line$list = new String(readAllBytes(of(complete_path))).lines().toList();
      if (appendedLinesB4(msb, line$list, line)) {
        var unStripped = line$list.get(line - 1);
        msb.append("   ").append(unStripped).append("\n");
        l$msg.append(column == 1 ? "^" : "-".repeat(column - 1) + "^");
        l$msg.append(" ");
        l$msg.append(filePath).append(" ").append("[");
        l$msg.append(messages[0]).append("]").append("\n");
      } else {
        var unStripped = line$list.get(line$list.size() > 1 ? line - 1 : 0);
        var stripped = line$list.get(line$list.size() > 1 ? line - 1 : 0).strip();
        msb.append("   ").append(stripped).append("\n");
        var strip$difference =
            (unStripped.length() - (stripped.length() - 1));
        l$msg.append("-".repeat(column - strip$difference)).append("^");
        l$msg.append(" ");
        l$msg.append(filePath).append(" ").append("[");
        l$msg.append(messages[0]).append("]").append("\n");
      }
      msb.append("   ").append(l$msg);
      appendLinesAfter(msb, line$list, line);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return msb.toString();
  }

  private boolean appendedLinesB4
      (StringBuilder msb, List<String> line$list, int line) {
    var max$back = 0;
    for (int i = 0; i < 7; i++) {
      if (line - i == -1) {
        break;
      }
      max$back = i;
    }
    for (int i = max$back; i > 1; i--) {
      msb.append("   ").append(line$list.get(line - i)).append("\n");
    }
    return max$back != 0;
  }

  private void appendLinesAfter
      (StringBuilder msb, List<String> line$list, int line) {
    var lineSize = line$list.size();
    var max$back = 0;
    for (int i = 0; i < 7; i++) {
      if (lineSize < (line + i)) {
        break;
      }
      max$back = i;
    }
    for (int i = 0; i < max$back; i++) {
      msb.append("   ").append(line$list.get(line + i)).append("\n");
    }
  }

  private int getColumn() {
    if (messages[0].contains("->")) {
      var indexOfColumn = messages[0].indexOf(':');
      var stop$index = messages[0].indexOf('-');
      return parseInt(messages[0]
          .substring(indexOfColumn + 1, stop$index).strip());
    }
    var indexOfColumn = messages[0].indexOf(':');
    return parseInt(messages[0].substring(indexOfColumn + 1).strip());
  }
}
