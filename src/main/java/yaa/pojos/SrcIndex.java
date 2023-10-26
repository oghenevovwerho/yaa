package yaa.pojos;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Stack;

public class SrcIndex extends SimpleFileVisitor<Path> {
  private Stack<String> currentDirectories;

  public SrcIndex() {

  }

  public void createIndex() {
    this.currentDirectories = new Stack<>();
    try {
      Files.walkFileTree(Path.of("src/com"), this);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Override
  public FileVisitResult visitFile(Path path, BasicFileAttributes bA) {
    FileVisitResult visitResult = null;
    try {
      visitResult = super.visitFile(path, bA);
      var fileName = path.getFileName().toString();
      var desiredPath = packagePath().replace("java.base.", "");
      var firstPart = "C:/Users/Oghenevovwerho/Programming/Java/Sources/Yaa1/src/";
      var value = firstPart + desiredPath.replace(".", "/") + fileName;
      var key = desiredPath + fileName.replace(".java", "");
      JvmIndex.indexedClz.put(key, value);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return visitResult;
  }

  private String packagePath() {
    var dirBuilder = new StringBuilder();
    currentDirectories.forEach(directoryName -> {
      dirBuilder.append(directoryName).append(".");
    });
    return dirBuilder.toString();
  }

  @Override
  public FileVisitResult visitFileFailed(Path p, IOException ioe) {
    FileVisitResult visitResult = null;
    try {
      visitResult = super.visitFileFailed(p, ioe);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return visitResult;
  }

  @Override
  public FileVisitResult preVisitDirectory(Path p, BasicFileAttributes a) {
    FileVisitResult visitResult = null;
    try {
      visitResult = super.preVisitDirectory(p, a);
      currentDirectories.push(p.getFileName().toString());
    } catch (IOException e) {
      e.printStackTrace();
    }
    return visitResult;
  }

  @Override
  public FileVisitResult postVisitDirectory(Path p, IOException ioe) {
    FileVisitResult visitResult = null;
    try {
      visitResult = super.postVisitDirectory(p, ioe);
      currentDirectories.pop();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return visitResult;
  }
}
