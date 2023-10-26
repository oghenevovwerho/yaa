package yaa;

import org.codehaus.plexus.util.FileUtils;
import yaa.pojos.GlobalData;
import yaa.pojos.LogData;
import yaa.pojos.YaaCompilerProvider;

import static java.nio.file.Files.exists;
import static java.nio.file.Path.of;

public class YaaInitializer {
  private YaaCompilerProvider provider;

  public YaaInitializer(YaaCompilerProvider provider) {
    this.provider = provider;
  }

  public LogData initialize() {
    if (exists(of(provider.getBasedir() + "/target"))) {
      try {
        FileUtils.cleanDirectory(provider.getBasedir() + "/target");
      } catch (Exception e) {
        GlobalData.addErrorLog("Failed to delete 'target' directory for code generation");
      }
    }
    return GlobalData.logData;
  }
}
