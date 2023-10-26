package yaa.semantic.passes.fs3;

import yaa.ast.Anonymous;
import yaa.pojos.GlobalData;

import static yaa.pojos.GlobalData.fs3;

public class F3NoName {
  public static void f3Main(Anonymous anonymous) {
    fs3.pushTable(anonymous);
    anonymous.stmt.visit(fs3);
    fs3.popTable();
  }
}
