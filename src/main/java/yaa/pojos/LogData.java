package yaa.pojos;

import java.util.ArrayList;
import java.util.List;

public class LogData {
  public List<LogMessage> messages = new ArrayList<>(10);
  public boolean hasError;
}
