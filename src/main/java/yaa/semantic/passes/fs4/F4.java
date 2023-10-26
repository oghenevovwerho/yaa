package yaa.semantic.passes.fs4;

import yaa.ast.*;
import yaa.pojos.*;
import yaa.semantic.handlers.*;
import yaa.pojos.FileState;

import java.util.HashMap;
import java.util.List;

import static yaa.pojos.GlobalData.*;

public class F4 extends FileState {
  public F4(List<Stmt> stmts, String filePath) {
    super(stmts, filePath);
  }

  @Override
  public YaaInfo $programIn(ProgramIn in) {
    this.tables = GlobalData.tables4File.get(in.path);
    this.table = tables.get(in);
    fs4 = this;
    fs = this;
    YaaError.filePath = in.path;
    results.put(in.path, new HashMap<>());
    return nothing;
  }

  @Override
  public YaaInfo $programOut(ProgramOut programOut) {
    fs = null;
    fs4 = null;
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
    fs4.pushTable(newFun);
    runF4Stmt(newFun.stmt);
    fs4.popTable();
    return nothing;
  }

  @Override
  public YaaInfo $init(Init init) {
    fs4.pushTable(init);
    init.stmt.visit(this);
    fs4.popTable();
    return nothing;
  }

  @Override
  public YaaInfo $newClass(NewClass newClass) {
    F4NClass.newType(newClass);
    return nothing;
  }

  @Override
  public YaaInfo $newRecord(NewRecord newRecord) {
    F4NRecord.newRecord(newRecord);
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
  public YaaInfo $group(Group group) {
    return GroupOp.groupOp(group);
  }

  @Override
  public YaaInfo $gThan(GThan gThan) {
    return GThanOp.gThan(gThan);
  }

  @Override
  public YaaInfo $basex(Basex basex) {
    return BasexOp.basex(basex);
  }

  @Override
  public YaaInfo $eEqual(EEqual eEqual) {
    return EqualOp.eEqual(eEqual);
  }

  @Override
  public YaaInfo $anonymous(Anonymous anonymous) {
    pushTable(anonymous);
    runF4Stmt(anonymous.stmt);
    popTable();
    return nothing;
  }

  @Override
  public YaaInfo $nEqual(NEqual nEqual) {
    return NEqualOp.nEqual(nEqual);
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
    return NameOp.name(name);
  }

  @Override
  public YaaInfo $decimal(Decimal decimal) {
    return DecimalOp.decimal(decimal);
  }

  @Override
  public YaaInfo $newEnum(NewEnum newEnum) {
    F4NEnum.newEnum(newEnum);
    return nothing;
  }

  @Override
  public YaaInfo $pointed(Pointed pointed) {
    return PointedOp.pointed(pointed);
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
  public YaaInfo $true(True aTrue) {
    return boole$clz;
  }

  @Override
  public YaaInfo $false(False aFalse) {
    return boole$clz;
  }

  @Override
  public YaaInfo $is(Is is) {
    return IsOp.is(is);
  }

  @Override
  public YaaInfo $cast(Cast cast) {
    return CastOp.cast(cast);
  }

  @Override
  public YaaInfo $this(This aThis) {
    return ThisOp.thisOp(aThis);
  }

//  @Override
//  public YaaInfo $thisCall(ThisCall thisCall) {
//    return ThisCallOp.thisCall(thisCall);
//  }

  @Override
  public YaaInfo $thisField(ThisField thisField) {
    return ThisFieldOp.thisField(thisField);
  }

  @Override
  public YaaInfo $thisMtd(ThisMtd thisMtd) {
    return ThisMtdOp.thisMtd(thisMtd);
  }

  @Override
  public YaaInfo $vCall(VCall vCall) {
    return VCallOp.vCall(vCall);
  }

  @Override
  public YaaInfo $nullStmt(Null aNull) {
    return null$clz;
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
  public YaaInfo $ternary(Ternary ternary) {
    return TernaryOp.ternary(ternary);
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
  public YaaInfo $xor(Xor xor) {
    return XorOp.xor(xor);
  }

  @Override
  public YaaInfo $main(MainFunction main) {
    F4Main.f4Main(main);
    return nothing;
  }

  @Override
  public YaaInfo $innerBlock(InnerBlock iBlock) {
    pushTable(iBlock);
    runF4Stmts(iBlock.stmts);
    popTable();
    return nothing;
  }

  @Override
  public YaaInfo $tryCatch(TryCatch tryCatch) {
    F4Try.f4Try(tryCatch);
    return nothing;
  }

  @Override
  public YaaInfo $loop(Loop loop) {
    fs4.pushTable(loop);
    runF4Stmt(loop.stmt);
    fs4.popTable();
    return nothing;
  }

  @Override
  public YaaInfo $vDef(VDefinition def) {
    var field = VDefOp.defOp(def);
    if (field.itIsWhat == FieldIsWhat.top$field) {
      field.owner = GlobalData.topClzCodeName.get(path);
    }
    return nothing;
  }


  @Override
  public YaaInfo $vDec(VDeclaration dec) {
    var field_name = dec.name.content;
    var field = (YaaField) fs4.getSymbol(field_name);
    field.data = dec.type.visit(fs4);
    if (field.itIsWhat == FieldIsWhat.top$field) {
      field.owner = GlobalData.topClzCodeName.get(path);
    }
    return nothing;
  }

  @Override
  public YaaInfo $newMeta(NewMeta newMeta) {
    pushTable(newMeta);
    var meta = (YaaMeta) getSymbol(newMeta.placeOfUse());
    for (var meta_call : newMeta.metaCalls) {
      var called_meta = (YaaMeta) getSymbol(meta_call.name.content);
      if (called_meta.name.equals("java.lang.annotation.Repeatable")) {
        meta.isRepeatable = true;
        break;
      }
    }
    for (var def : newMeta.vDefinitions) {
      VDefOp.defOp(def);
    }
    for (var dec : newMeta.vDeclarations) {
      var field_name = dec.name.content;
      var field = (YaaField) fs4.getSymbol(field_name);
      field.data = dec.type.visit(fs4);
    }
    popTable();
    return nothing;
  }

  protected static void runF4Stmts(List<Stmt> stmts) {
    for (var stmt : stmts) {
      runF4Stmt(stmt);
    }
  }

  protected static void runF4Stmt(Stmt stmt) {
    if (stmt instanceof NewClass newClass) {
      F4NClass.newType(newClass);
    } else if (stmt instanceof NewFun new$fun) {
      new$fun.visit(fs4);
    } else if (stmt instanceof NewEnum newEnum) {
      newEnum.visit(fs4);
    } else if (stmt instanceof InnerBlock block) {
      block.visit(fs4);
    } else if (stmt instanceof Loop loop) {
      loop.visit(fs4);
    } else if (stmt instanceof IfStmt ifStmt) {
      F4IfStmt.ifStmt(ifStmt);
    } else if (stmt instanceof TryCatch tryCatch) {
      F4Try.f4Try(tryCatch);
    } else if (stmt instanceof NewRecord newRecord) {
      F4NRecord.newRecord(newRecord);
    } else if (stmt instanceof Anonymous anonymous) {
      anonymous.visit(fs4);
    } else if (stmt instanceof NewMeta newMeta) {
      newMeta.visit(fs4);
    }
  }
}