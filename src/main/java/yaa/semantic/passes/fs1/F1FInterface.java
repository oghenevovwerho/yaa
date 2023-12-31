package yaa.semantic.passes.fs1;

import yaa.ast.NewFunctionalInterface;
import yaa.pojos.*;

import java.util.ArrayList;

import static yaa.pojos.GlobalData.defineClassGlobally;
import static yaa.pojos.GlobalData.fs1;
import static yaa.pojos.NameUtils.clzCodeName;
import static yaa.pojos.NameUtils.dottedStoreName;

public class F1FInterface {
  public static YaaInfo f1FInterface(NewFunctionalInterface ctx) {
    var simpleClzName = ctx.name.content;

    var storeName = dottedStoreName(simpleClzName);
    var fs1Class = new YaaClz(storeName);
    fs1Class.category = TypeCategory.trait_c;
    fs1Class.startLine = ctx.start.line;
    fs1Class.column = ctx.start.column;
    // Fs1Utils.isItDefined(simpleClzName, ctx.address());
    fs1.putSymbol(simpleClzName, fs1Class);

    var codeName = clzCodeName(simpleClzName);
    fs1Class.codeName = codeName;
    //the lowercase is necessary so that pool/Pool won't interfere with pool/pool
    GlobalData.usedClzNames.get(fs1.path).add(codeName.toLowerCase());

    var mtd_pack = new MtdPack(simpleClzName);
    var deferred_mtd = new YaaFun(simpleClzName, codeName);
    deferred_mtd.isAutoGeneratedFInterfaceMtd = true;
    deferred_mtd.itIsTraitMtd = true;
    deferred_mtd.parameters = new ArrayList<>(ctx.parameters.size());
    mtd_pack.methods.add(deferred_mtd);
    fs1Class.functionalMtd = deferred_mtd;
    fs1Class.instanceMethods.put(simpleClzName, mtd_pack);

    if (ctx.scope == ScopeKind.INGLOBAL) {
      defineClassGlobally(storeName, fs1Class);
    }
    return fs1Class;
  }
}
