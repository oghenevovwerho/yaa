package yaa.semantic.passes.fs5;

import yaa.ast.*;
import yaa.parser.YaaToken;
import yaa.pojos.*;
import yaa.semantic.handlers.*;

import java.lang.annotation.ElementType;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import static yaa.pojos.GlobalData.*;

public class F5 extends FileState {
  public static final Stack<YaaFun> topMtd = new Stack<>();
  public Stack<BlockKind> block = new Stack<>();
  public Stack<YaaInfo> fn$type = new Stack<>();
  public Stack<String> fn$name = new Stack<>();
  public Stack<String> fn$address = new Stack<>();
  public Stack<Stmt> fn$stmt = new Stack<>();
  public Stack<Map<String, YaaToken>> catchHolders = new Stack<>();
  public static final Stack<Anonymous> topAnonymous = new Stack<>();

  public F5(List<Stmt> stmts, String filePath) {
    super(stmts, filePath);
  }

  @Override
  public YaaInfo $programOut(ProgramOut programOut) {
    fs = null;
    fs5 = null;
    return nothing;
  }

  @Override
  public YaaInfo $programIn(ProgramIn in) {
    this.tables = GlobalData.tables4File.get(in.path);
    this.table = tables.get(in);
    fs5 = this;
    fs = this;
    YaaError.filePath = in.path;
    return nothing;
  }

  public void pushTable(Stmt ctx) {
    this.table = tables.get(ctx);
  }

  public void popTable() {
    this.table = table.parent;
  }

  @Override
  public YaaInfo $function(NewFun newFun) {
    pushTable(newFun);
    MetaCallOp.metaCalls(newFun.metaCalls, ElementType.METHOD);
    for (var param : newFun.parameters) {
      MetaCallOp.metaCalls(param.type, ElementType.PARAMETER);
    }
    topMtd.push((YaaFun) getSymbol(newFun.placeOfUse()));
    if (newFun.type != null) {
      fn$type.push(newFun.type.visit(this));
      MetaCallOp.metaCalls(newFun.type, ElementType.TYPE_USE);
    } else {
      fn$type.push(YaaClz.makePrimitive(void$name));
    }
    fn$name.push(newFun.name.content);
    block.push(BlockKind.function);
    fn$address.push(newFun.placeOfUse());
    fn$stmt.push(newFun.stmt);
    F5Callable.f5Callable();
    fn$name.pop();
    block.pop();
    fn$type.pop();
    fn$stmt.pop();
    popTable();
    topMtd.pop();
    fn$address.pop();
    return nothing;
  }

  @Override
  public YaaInfo $newMeta(NewMeta newMeta) {
    F5NewMeta.newMeta(newMeta);
    return null;
  }

  @Override
  public YaaInfo $init(Init init) {
    pushTable(init);
    fn$type.push(new YaaClz(void$name));
    fn$name.push("an initializer");
    block.push(BlockKind.init);
    fn$address.push(init.placeOfUse());
    fn$stmt.push(init.stmt);
    F5Callable.f5Callable();
    fn$name.pop();
    block.pop();
    fn$type.pop();
    fn$stmt.pop();
    popTable();
    fn$address.pop();
    return nothing;
  }

  @Override
  public YaaInfo $main(MainFunction main) {
    pushTable(main);
    fn$type.push(new YaaClz(void$name));
    fn$name.push("program entry point");
    block.push(BlockKind.init);
    fn$address.push(main.placeOfUse());
    fn$stmt.push(main.stmt);
    F5Callable.f5Callable();
    fn$name.pop();
    block.pop();
    fn$type.pop();
    fn$stmt.pop();
    popTable();
    fn$address.pop();
    return nothing;
  }

  @Override
  public YaaInfo $anonymous(Anonymous anonymous) {
    var fun = (YaaFun) getSymbol(anonymous.placeOfUse());
    topAnonymous.push(anonymous);
    return fun;
  }

  public void doNoNane() {
    pushTable(topAnonymous.peek());
    var anonymous_mtd = (YaaFun) getSymbol(topAnonymous.peek().placeOfUse());

    for (int i = 0; i < anonymous_mtd.parameterNames.size(); i++) {
      var name = anonymous_mtd.parameterNames.get(i);
      var field = (YaaField) fs.getSymbol(name);
      field.data = anonymous_mtd.parameters.get(i);
    }
    topMtd.push(anonymous_mtd);
    fn$type.push(anonymous_mtd.type);
    fn$name.push("the anonymous function");
    block.push(BlockKind.function);
    fn$address.push(topAnonymous.peek().placeOfUse());
    fn$stmt.push(topAnonymous.peek().stmt);
    F5Callable.f5Callable();
    fn$name.pop();
    topMtd.pop();
    block.pop();
    fn$type.pop();
    fn$stmt.pop();
    fn$address.pop();
    popTable();
    topAnonymous.pop();
  }

  @Override
  public YaaInfo $tryCatch(TryCatch tryCatch) {
    F5Try.tryCatchStmt(tryCatch);
    return nothing;
  }

  @Override
  public YaaInfo $innerBlock(InnerBlock iBlock) {
    pushTable(iBlock);
    for (var stmt : iBlock.stmts) {
      stmt.visit(this);
    }
    popTable();
    return nothing;
  }

  @Override
  public YaaInfo $newClass(NewClass newClass) {
    F5NClass.newType(newClass);
    return nothing;
  }

  @Override
  public YaaInfo $newRecord(NewRecord newRecord) {
    F5NRecord.newRecord(newRecord);
    return nothing;
  }

  @Override
  public YaaInfo $objectType(ObjectType objectType) {
    return YaaClz.fsClz(objectType);
  }

  @Override
  public YaaInfo $plus(Plus plus) {
    return PlusOp.plus(plus);
  }

  @Override
  public YaaInfo $minus(Minus minus) {
    return MinusOp.minus(minus);
  }

  @Override
  public YaaInfo $modulo(Modulo modulo) {
    return ModOp.mod(modulo);
  }

  @Override
  public YaaInfo $times(Times times) {
    return TimesOp.times(times);
  }

  @Override
  public YaaInfo $divide(Divide divide) {
    return DivideOp.divide(divide);
  }

  @Override
  public YaaInfo $assign(Assign assign) {
    F5EAssign.f5EAssign(assign);
    return nothing;
  }

  @Override
  public YaaInfo $bitAnd(BitAnd bitAnd) {
    return BitAndOp.bitAnd(bitAnd);
  }

  @Override
  public YaaInfo $bitNot(BitNot bitNot) {
    return BitNegateOp.bitNot(bitNot);
  }

  @Override
  public YaaInfo $bitOr(BitOr bitOr) {
    return BitOrOp.bitOr(bitOr);
  }

  @Override
  public YaaInfo $and(And and) {
    return AndOp.and(and);
  }

  @Override
  public YaaInfo $power(Power power) {
    return PowerOp.power(power);
  }

  @Override
  public YaaInfo $root(RootTo rootTo) {
    return RootOp.root(rootTo);
  }

  @Override
  public YaaInfo $or(Or or) {
    return OrOp.or(or);
  }

  @Override
  public YaaInfo $cha(Cha cha) {
    return ChaOp.cha(cha);
  }

  @Override
  public YaaInfo $string(AstString ast) {
    return StringOp.string(ast);
  }

  @Override
  public YaaInfo $eCall(ECall eCall) {
    return ECallOp.eCall(eCall);
  }

  @Override
  public YaaInfo $eGet(EGet eGet) {
    return EGetOp.eGet(eGet);
  }

  @Override
  public YaaInfo $eMtd(EMtd eMtd) {
    return EMtdOp.eMtd(eMtd);
  }

  @Override
  public YaaInfo $gEqual(GEqual gEqual) {
    return GEqualOp.gEqual(gEqual);
  }

  @Override
  public YaaInfo $basex(Basex basex) {
    return BasexOp.basex(basex);
  }

  @Override
  public YaaInfo $mNEqual(MNEqual mnEqual) {
    return MNEqualOp.mnEqual(mnEqual);
  }

  @Override
  public YaaInfo $mEqual(MEqual mEqual) {
    return MEqualOp.mEqual(mEqual);
  }

  @Override
  public YaaInfo $group(Group group) {
    return GroupOp.groupOp(group);
  }

  @Override
  public YaaInfo $newEnum(NewEnum newEnum) {
    F5NEnum.newEnum(newEnum);
    return nothing;
  }

  @Override
  public YaaInfo $gThan(GThan gThan) {
    return GThanOp.gThan(gThan);
  }

  @Override
  public YaaInfo $eEqual(EEqual eEqual) {
    return EqualOp.eEqual(eEqual);
  }

  @Override
  public YaaInfo $nEqual(NEqual nEqual) {
    return NEqualOp.nEqual(nEqual);
  }

  @Override
  public YaaInfo $rShift(RShift rShift) {
    return RShiftOp.rShift(rShift);
  }

  @Override
  public YaaInfo $uRShift(URShift urShift) {
    return URShiftOp.uRShift(urShift);
  }

  @Override
  public YaaInfo $lShift(LShift lShift) {
    return LShiftOp.lShift(lShift);
  }

  @Override
  public YaaInfo $lEqual(LEqual lEqual) {
    return LEqualOp.lEqual(lEqual);
  }

  @Override
  public YaaInfo $lThan(LThan lThan) {
    return LThanOp.lThan(lThan);
  }

  @Override
  public YaaInfo $name(Name name) {
    return F5NameOp.f5Name(name);
  }

  @Override
  public YaaInfo $decimal(Decimal decimal) {
    return DecimalOp.decimal(decimal);
  }

  @Override
  public YaaInfo $pointed(Pointed pointed) {
    return PointedOp.pointed(pointed);
  }

  @Override
  public YaaInfo $uMinus(UMinus uMinus) {
    return UMinusOp.uMinus(uMinus);
  }

  @Override
  public YaaInfo $uNot(UNot uNot) {
    return UNotOp.uNot(uNot);
  }

  @Override
  public YaaInfo $uPlus(UPlus uPlus) {
    return UPlusOp.uPlus(uPlus);
  }

  @Override
  public YaaInfo $vCall(VCall vCall) {
    return VCallOp.vCall(vCall);
  }

  @Override
  public YaaInfo $superField(SuperField superField) {
    return SuperFieldOp.superField(superField);
  }

  @Override
  public YaaInfo $superMtd(SuperMtd superMtd) {
    return SuperMtdOp.superMtd(superMtd);
  }

  @Override
  public YaaInfo $break(Break $break) {
    var currentBlock = fs5.block;

    for (int i = currentBlock.size() - 1; i >= 0; i--) {
      var block = currentBlock.elementAt(i);
      if (block == BlockKind.loop || block == BlockKind.case$block) {
        if ($break.name == null) {
          return nothing;
        }
        var name = $break.name.content;
        var jumpTarget = fs5.getSymbol(name);
        if (jumpTarget instanceof YaaField tg && tg.is4loop) {
          return nothing;
        }
        if (catchHolders.peek().get(name) != null) {
          return nothing;
        } else {
          throw new YaaError(
              $break.placeOfUse(),
              "Arguments to break must be loop, or case variables"
          );
        }
      }
    }
    throw new YaaError(
        $break.placeOfUse(),
        "A break statement is only allowed within loop bodies"
    );
  }

  @Override
  public YaaInfo $true(True aTrue) {
    return boole$clz;
  }

  @Override
  public YaaInfo $false(False aFalse) {
    return boole$clz;
  }

  @Override
  public YaaInfo $cast(Cast cast) {
    return CastOp.cast(cast);
  }

  @Override
  public YaaInfo $is(Is is) {
    return IsOp.is(is);
  }

  @Override
  public YaaInfo $this(This aThis) {
    return ThisOp.thisOp(aThis);
  }

  @Override
  public YaaInfo $thisField(ThisField thisField) {
    return ThisFieldOp.thisField(thisField);
  }

  @Override
  public YaaInfo $thisMtd(ThisMtd thisMtd) {
    return ThisMtdOp.thisMtd(thisMtd);
  }

  @Override
  public YaaInfo $continue(Continue $continue) {
    var currentBlock = fs5.block;

    for (int i = currentBlock.size() - 1; i >= 0; i--) {
      var block = currentBlock.elementAt(i);
      if (block == BlockKind.loop || block == BlockKind.case$block) {
        if ($continue.name == null) {
          return nothing;
        }
        var name = $continue.name.content;
        var jumpTarget = fs5.getSymbol(name);
        if (jumpTarget instanceof YaaField tg && tg.is4loop) {
          return nothing;
        }
        if (catchHolders.peek().get(name) != null) {
          return nothing;
        } else {
          throw new YaaError(
              $continue.placeOfUse(),
              "Arguments to continue must be loop, or case variables"
          );
        }
      }
    }
    throw new YaaError(
        $continue.placeOfUse(),
        "A continue statement is only allowed within loop bodies"
    );
  }

  @Override
  public YaaInfo $ternary(Ternary ternary) {
    return TernaryOp.ternary(ternary);
  }

  @Override
  public YaaInfo $nullStmt(Null aNull) {
    return null$clz;
  }

  @Override
  public YaaInfo $vGet(VGet vGet) {
    return VGetOp.vGet(vGet);
  }

  @Override
  public YaaInfo $vMtd(VMtd vMtd) {
    return VMtdOp.vMtd(vMtd);
  }

  @Override
  public YaaInfo $float(Floated floated) {
    return FloatOp.floated(floated);
  }

  @Override
  public YaaInfo $byte(Byted byted) {
    return ByteOp.byted(byted);
  }

  @Override
  public YaaInfo $short(Shorted shorted) {
    return ShortOp.shorted(shorted);
  }

  @Override
  public YaaInfo $long(Longed longed) {
    return LongOp.longed(longed);
  }

  @Override
  public YaaInfo $xor(Xor xor) {
    return XorOp.xor(xor);
  }

  @Override
  public YaaInfo $vDec(VDeclaration dec) {
    if (dec.itIsWhat == FieldIsWhat.clz$field) {
      MetaCallOp.metaCalls(dec.type, ElementType.FIELD);
    } else if (dec.itIsWhat == FieldIsWhat.top$field) {
      //top fields are compiled to class fields
      MetaCallOp.metaCalls(dec.type, ElementType.FIELD);
    } else {
      MetaCallOp.metaCalls(dec.type, ElementType.LOCAL_VARIABLE);
    }
    var field = (YaaField) fs.getSymbol(dec.name.content);
    field.data = dec.type.visit(fs);
    return nothing;
  }

  @Override
  public YaaInfo $vDef(VDefinition def) {
    if (def.type != null) {
      if (def.itIsWhat == FieldIsWhat.clz$field) {
        MetaCallOp.metaCalls(def.type, ElementType.FIELD);
      } else if (def.itIsWhat == FieldIsWhat.top$field) {
        //top fields are compiled to class fields
        MetaCallOp.metaCalls(def.type, ElementType.FIELD);
      } else {
        MetaCallOp.metaCalls(def.type, ElementType.LOCAL_VARIABLE);
      }
    }
    VDefOp.defOp(def);
    return nothing;
  }
}