package yaa.pojos;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class MtdPack extends YaaInfo {
  public List<YaaFun> methods;

  public MtdPack(List<YaaFun> methods) {
    this.methods = methods;
  }

  private MtdPack() {
  }

  public MtdPack(String name) {
    this.name = name;
    this.methods = new LinkedList<>();
  }

  public MtdPack(List<YaaFun> methods, String name) {
    this.methods = methods;
    this.name = name;
  }

  @Override
  public String toString() {
    var bd = new StringBuilder();
    for (var mtd : methods) {
      bd.append(mtd).append("\n   ");
    }
    return bd.toString();
  }

  public String candidates() {
    var bd = new StringBuilder();
    if (methods.size() == 1) {
      bd.append("The method below is the only valid candidate\n");
    } else if (methods.size() > 1) {
      bd.append("The methods below are the only valid candidates\n");
    }
    for (var mtd : methods) {
      bd.append("   ").append(mtd).append("\n");
    }
    return bd.toString();
  }

  public YaaFun choseMtd(List<YaaClz> t$args, List<YaaInfo> v$args) {
    var unvisited_methods = new ArrayList<YaaFun>(methods.size());
    mtd_loop:
    for (var mtd : methods) {
      var params = mtd.parameters;
      var expectedTypeArgSize = mtd.inputted.size();
      var gottenTypeArgSize = t$args.size();
      if (expectedTypeArgSize != gottenTypeArgSize || params.size() != v$args.size()) {
        continue;
      }
      if (expectedTypeArgSize > 0) {
        unvisited_methods.add(mtd);
        continue;
      }
      for (int i = 0; i < params.size(); i++) {
        var param = params.get(i);
        var arg = v$args.get(i);
        if (param.inputtedSize() > 0 || arg.inputtedSize() > 0) {
          unvisited_methods.add(mtd);
          continue mtd_loop;
        }
        if (!params.get(i).name.equals(v$args.get(i).name)) {
          unvisited_methods.add(mtd);
          continue mtd_loop;
        }
      }
      return mtd.setCallInfo(v$args);
    }
    for (var mtd : unvisited_methods) {
      var expected_size = mtd.inputted.size();
      if (expected_size > 0) {
        var fun = mtd.acceptsMtd(v$args);
        if (fun != null) {
          return fun;
        }
      }
      var descriptor = mtd.acceptsMtd(v$args);
      if (descriptor != null) {
        return descriptor;
      }
    }
    return null;
  }

  public YaaFun choseOpMtd(YaaInfo right_op) {
    for (var mtd : methods) {
      if (mtd.acceptsOpMtd(right_op)) {
        return mtd;
      }
    }
    return null;
  }

  public MtdPack changeCPack(List<YaaClz> inputted) {
    var new_pack = new MtdPack(name);
    for (var mtd : methods) {
      if (!mtd.hasClzTypeParam) {
        new_pack.methods.add(mtd);
        continue;
      }
      var cloned_mtd = (YaaFun) mtd.cloneInfo();
      var raw_parameters = cloned_mtd.parameters;
      cloned_mtd.raw_parameters = raw_parameters;
      var new$parameters = new ArrayList<YaaInfo>(raw_parameters.size());
      for (var param : raw_parameters) {
        if (param.cbIndex > -1) {
          new$parameters.add(inputted.get(param.cbIndex));
        } else if (param instanceof YaaClz pClz && pClz.inputted.size() != 0) {
          new$parameters.add(pClz.changeCBounds(inputted));
        } else {
          new$parameters.add(param);
        }
      }
      cloned_mtd.parameters = new$parameters;

      cloned_mtd.rawType = cloned_mtd.type;
      if (cloned_mtd.type instanceof YaaClz mtd$type) {
        if (mtd$type.inputted.size() > 0) {
          cloned_mtd.type = mtd$type.changeCBounds(inputted);
        } else if (mtd$type.cbIndex > -1) {
          cloned_mtd.type = inputted.get(mtd$type.cbIndex);
        }
      }

      new_pack.methods.add(cloned_mtd);
    }
    return new_pack;
  }
}
