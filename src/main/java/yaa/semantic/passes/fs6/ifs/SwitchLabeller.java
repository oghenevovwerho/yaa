package yaa.semantic.passes.fs6.ifs;

import org.objectweb.asm.Label;

public class SwitchLabeller {
  public Label[] labels;
  private int i = 0;

  public SwitchLabeller(int labelSize) {
    this.labels = new Label[labelSize];
  }

  public void putLabel(Label label) {
    labels[i++] = label;
  }
}
