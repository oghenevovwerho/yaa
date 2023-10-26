package yaa.semantic.passes.fs4;

import yaa.ast.NewFun;
import yaa.ast.OverBlock;
import yaa.pojos.*;

import java.util.List;
import java.util.Map;

public class F4Parent {
  public static void implementBlock(OverBlock block, YaaClz child, YaaClz parent) {
    GlobalData.fs4.pushTable(block);
    //first check the abstract methods
    if (parent.isJvm && parent.isAbstract) {
      checkAbsMtds(block, child, parent);
    }
    //then check the other methods
    doParentMtdCheck(block.methods, child, parent);
    GlobalData.fs4.popTable();
  }

  protected static void doParentMtdCheck(Map<String, List<NewFun>> parentPacks, YaaClz child, YaaClz parent) {
    overriding$loop:
    for (var overridingMtdPack : parentPacks.entrySet()) {
      for (int j = 0; j < overridingMtdPack.getValue().size(); j++) {
        var parentIndices = parent.abstractIndices.get(overridingMtdPack.getKey());
        if (parentIndices != null && parentIndices.contains(j)) {
          continue;
        }
        var overridingMtd = overridingMtdPack.getValue().get(j);
        var parentMtdName = overridingMtd.name.content;
        var matchingMtdPack = parent.getMethod(parentMtdName);
        if (matchingMtdPack == null) {
          throw new YaaError(
              overridingMtd.placeOfUse(), overridingMtd.toString(),
              "The method above does not match any method in the type below",
              parent.toString()
          );
        }
        var overridingParams = overridingMtd.parameters;
        var rawPack = GlobalData.getTouchedClass(parent.name).getMethod(parentMtdName);
        var indexInPack = -1;
        pack$loop:
        for (var overriddenMtd : matchingMtdPack.methods) {
          var parentParams = overriddenMtd.parameters;
          indexInPack++;
          if (parentParams.size() != overridingParams.size()) {
            continue;
          }
          for (int i = 0; i < parentParams.size(); i++) {
            var parentParam = parentParams.get(i);
            var overridingParam = overridingParams.get(i).type.visit(GlobalData.fs4);
            if (parentParam instanceof YaaClz clz) {
              if (!clz.isSame$Obj(overridingParam)) {
                continue pack$loop;
              }
            }
          }
          if (overriddenMtd.privacy != overridingMtd.privacy) {
            throw new YaaError(
                overridingMtd.placeOfUse(), overriddenMtd.toString(),
                "The method above has privacy level " +
                    overriddenMtd.privacy + " in "
                    + (overriddenMtd.owner.replace("/", ".")) +
                    " but privacy level " + overridingMtd.privacy
                    + " in " + child.name,
                "The visibility of the parent method and " +
                    "the overriding method must match"
            );
          }
          var expected = overriddenMtd.type;
          if (expected instanceof YaaClz clz) {
            if (overridingMtd.type != null) {
              var gotten = overridingMtd.type.visit(GlobalData.fs4);
              if (!clz.isSame$Obj(gotten)) {
                throw new YaaError(
                    overridingMtd.placeOfUse(), overridingMtd.toString(),
                    "The overridden method above expected the return type below",
                    expected.toString()
                );
              }
            } else {
              if (!clz.isSame$Obj(new YaaClz(GlobalData.void$name))) {
                throw new YaaError(
                    overridingMtd.placeOfUse(), overridingMtd.toString(),
                    "The overridden method above expected the return type below",
                    expected.toString()
                );
              }
            }
          }
          if (overriddenMtd.isFinal) {
            throw new YaaError(
                overridingMtd.placeOfUse(), overridingMtd.toString(),
                "The overridden method above is declared final",
                "Methods that are final cannot be overridden"
            );
          }
          overridingMtd.visit(GlobalData.fs4);
          var rawMtd = rawPack.methods.get(indexInPack);
          var fun_dec = new FunDecInfo();
          fun_dec.declared_type = rawMtd.type;
          fun_dec.declared_parameters = rawMtd.parameters;
          child.decInfoMap.put(overridingMtd.placeOfUse(), fun_dec);
          continue overriding$loop;
        }
        throw new YaaError(
            overridingMtd.placeOfUse(), overridingMtd.toString(),
            "The method above did not match any in the type below",
            parent.toString(), matchingMtdPack.candidates()
        );
      }
    }
  }

  private static void checkAbsMtds(OverBlock block, YaaClz child, YaaClz parent) {
    top$loop:
    for (var abMtdPack : parent.instanceMethods.values()) {
      var abstractIndices = parent.abstractIndices.get(abMtdPack.name);
      if (abstractIndices == null || abstractIndices.size() == 0) {
        continue;
      }
      var overridingPack = block.methods.get(abMtdPack.name);
      if (overridingPack == null) {
        throw new YaaError(
            block.placeOfUse(), abMtdPack.toString(),
            "The methods above must be implemented by " + child
        );
      }
      var rawPack = GlobalData.getTouchedClass(parent.name).getMethod(abMtdPack.name);
      overridden$loop:
      for (int a = 0; a < abMtdPack.methods.size(); a++) {
        if (!abstractIndices.contains(a)) {
          continue top$loop;
        }
        var overriddenMtd = abMtdPack.methods.get(a);
        var indexInPack = -1;
        overriding$loop:
        for (var overridingMtd : overridingPack) {
          indexInPack++;
          var overridingParams = overridingMtd.parameters;
          var parentParams = overriddenMtd.parameters;
          if (parentParams.size() != overridingParams.size()) {
            continue;
          }
          for (int i = 0; i < parentParams.size(); i++) {
            var parentParam = parentParams.get(i);
            var overridingParam = overridingParams.get(i).type.visit(GlobalData.fs4);
            if (parentParam instanceof YaaClz clz) {
              if (!clz.isSame$Obj(overridingParam)) {
                continue overriding$loop;
              }
            }
          }
          if (overriddenMtd.privacy != overridingMtd.privacy) {
            throw new YaaError(
                overridingMtd.placeOfUse(), overriddenMtd.toString(),
                "The method above has privacy level " +
                    overriddenMtd.privacy + " in "
                    + (overriddenMtd.owner.replace("/", ".")) +
                    " but privacy level " + overridingMtd.privacy
                    + " in " + child.name,
                "The visibility of the parent method and " +
                    "the overriding method must match"
            );
          }
          var expected = overriddenMtd.type;
          if (expected instanceof YaaClz clz) {
            if (overridingMtd.type != null) {
              var gotten = overridingMtd.type.visit(GlobalData.fs4);
              if (!clz.isSame$Obj(gotten)) {
                throw new YaaError(
                    overridingMtd.placeOfUse(), overridingMtd.toString(),
                    "The overridden method above expected the return type below",
                    expected.toString()
                );
              }
            } else {
              if (!clz.isSame$Obj(new YaaClz(GlobalData.void$name))) {
                throw new YaaError(
                    overridingMtd.placeOfUse(), overridingMtd.toString(),
                    "The overridden method above expected the return type below",
                    expected.toString()
                );
              }
            }
          }
          overridingMtd.visit(GlobalData.fs4);
          var rawMtd = rawPack.methods.get(indexInPack);
          var fun_dec = new FunDecInfo();
          fun_dec.declared_type = rawMtd.type;
          fun_dec.declared_parameters = rawMtd.parameters;
          child.decInfoMap.put(overridingMtd.placeOfUse(), fun_dec);
          continue overridden$loop;
        }
        throw new YaaError(
            overridingPack.get(a).placeOfUse(), overriddenMtd.toString(),
            "The method above did not match any in the type below",
            parent.toString(), abMtdPack.candidates()
        );
      }
    }
  }
}