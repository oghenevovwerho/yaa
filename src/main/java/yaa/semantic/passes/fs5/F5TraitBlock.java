package yaa.semantic.passes.fs5;

import yaa.ast.OverBlock;
import yaa.pojos.GlobalData;

public class F5TraitBlock {
  public static void f5TraitBlock(OverBlock block) {
    GlobalData.fs5.pushTable(block);
    for (var parent$mtd$pack : block.methods.values()) {
      for (var parent$mtd : parent$mtd$pack) {
        parent$mtd.visit(GlobalData.fs5);
      }
    }
    GlobalData.fs5.popTable();
  }
}
