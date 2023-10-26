package yaa.pojos;

public class MetaPosition {
  public int index;
  public MetaPosition parent;

  public MetaPosition(int index, MetaPosition parent) {
    this.index = index;
    this.parent = parent;
  }
}
