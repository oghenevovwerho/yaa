package yaa.semantic.passes.fs6.results;

import yaa.pojos.LoopIsWhat;
import yaa.pojos.YaaClz;

public class LoopResult extends YaaResult {
  public LoopIsWhat loopIsWhat = LoopIsWhat.While;
  public YaaClz clz;
  public YaaClz iteratorClz;
}
