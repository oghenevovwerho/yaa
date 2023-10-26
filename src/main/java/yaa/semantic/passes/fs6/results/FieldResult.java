package yaa.semantic.passes.fs6.results;

import yaa.pojos.YaaField;

public class FieldResult extends YaaResult {
  public YaaField field;

  public FieldResult(YaaField field) {
    this.field = field;
  }
}
