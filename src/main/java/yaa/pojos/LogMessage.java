package yaa.pojos;

public class LogMessage {
  public LogCategory logCategory;
  public String message;

  public LogMessage(LogCategory logCategory, String message) {
    this.logCategory = logCategory;
    this.message = message;
    if (logCategory == LogCategory.error) {
      GlobalData.logData.hasError = true;
    }
  }
}
