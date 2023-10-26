package yaa.semantic.handlers;

public class ThisCallOp {
//  public static YaaInfo thisCall(ThisCall ctx) {
//    var clz = topClz.peek();
//    if (clz == null) {
//      throw new YaaError(
//        ctx.placeOfUse(), "\"this\" must be used from within a type"
//      );
//    }
//    if (clz.category == TypeCategory.enum_c) {
//      throw new YaaError(
//        ctx.placeOfUse(), clz.toString(),
//        "An enum cannot be called"
//      );
//    }
//    if (clz.cbIndex > -1 || clz.mbIndex > -1) {
//      throw new YaaError(
//        ctx.placeOfUse(), clz.toString(),
//        "A bound cannot be called"
//      );
//    }
//    if (clz.category == TypeCategory.trait_c) {
//      throw new YaaError(
//        ctx.placeOfUse(), clz.toString(),
//        "A trait type cannot be initialized"
//      );
//    }
//    if (clz.isAbstract) {
//      throw new YaaError(
//        ctx.placeOfUse(), clz.toString(),
//        "An abstract class cannot be called"
//      );
//    }
//    var values = OpUtils.v$arguments(ctx.arguments);
//    for (var init : clz.inits) {
//      var descriptor = init.acceptsInit(values);
//      if (descriptor != null) {
//        results.get(fs.path).put(ctx, new ThisCallResult(init, clz));
//        return clz;
//      }
//    }
//    throw new YaaError(
//      ctx.placeOfUse(), clz.toString(),
//      "The class above does not define " +
//        "any initializer with the given arguments",
//      clz.initCandidates(new ArrayList<>(0))
//    );
//  }
}
