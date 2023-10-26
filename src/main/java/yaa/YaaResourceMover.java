package yaa;

import org.codehaus.plexus.util.FileUtils;
import yaa.pojos.GlobalData;
import yaa.pojos.LogData;
import yaa.pojos.YaaCompilerProvider;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class YaaResourceMover {
  private final YaaCompilerProvider provider;

  public YaaResourceMover(YaaCompilerProvider provider) {
    this.provider = provider;
  }

  public LogData moveResources() {
    if (Files.exists(Path.of("src/main/resources"))) {
      try {
        FileUtils.copyDirectoryStructure(
            new File("src/main/resources"),
            new File(provider.getBasedir() + "/target/classes/" + provider.getProjectName())
        );
      } catch (IOException e) {
        GlobalData.addErrorLog(e.getLocalizedMessage());
      }
    }
    return GlobalData.logData;
  }
}
