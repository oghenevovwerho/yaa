package yaa.semantic.passes.fs3;

import yaa.ast.OverBlock;
import yaa.pojos.GlobalData;

public class F3BlockInClz {
  public static void f3BlockInClz(OverBlock block) {
    GlobalData.fs3.pushTable(block);

    for (var parent$mtd$pack : block.methods.values()) {
      for (var parent$mtd : parent$mtd$pack) {
        F3NFun.f3NewFunction(parent$mtd);
      }
    }

    GlobalData.fs3.popTable();
  }
}
