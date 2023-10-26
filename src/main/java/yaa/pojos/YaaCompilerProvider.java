package yaa.pojos;

import java.io.File;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;

public interface YaaCompilerProvider {
  String getProjectName();

  String getBasedir();

  List<File> getArtifacts();
}
