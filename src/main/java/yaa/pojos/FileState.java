package yaa.pojos;

import yaa.ast.*;
import yaa.pojos.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class FileState {
  public final List<Stmt> stmts;
  public final String path;
  public Map<Stmt, YaaTable> tables = new HashMap<>();
  public YaaTable table;

  public FileState(List<Stmt> stmts, String fPath) {
    this.stmts = stmts;
    this.path = fPath;
  }

  public YaaInfo $long(Longed longed) {
    return null;
  }

  public YaaInfo $byte(Byted byted) {
    return null;
  }

  public YaaInfo $short(Shorted shorted) {
    return null;
  }

  public YaaInfo $float(Floated floated) {
    return null;
  }

  public YaaInfo $nEqual(NEqual nEqual) {
    return null;
  }

  public YaaInfo $uRShift(URShift urShift) {
    return null;
  }

  public YaaInfo $eEqual(EEqual eEqual) {
    return null;
  }

  public YaaInfo $rShift(RShift rShift) {
    return null;
  }

  public YaaInfo $lShift(LShift lShift) {
    return null;
  }

  public YaaInfo $main(MainFunction main) {
    return null;

  }

  public void execute() {
    for (var stmt : stmts) {
      stmt.visit(this);
    }
  }

  public YaaInfo $pointed(Pointed pointed) {
    return null;
  }

  public YaaInfo $decimal(Decimal decimal) {
    return null;
  }

  public YaaInfo $plus(Plus plus) {
    return null;
  }

  public YaaInfo $minus(Minus minus) {
    return null;
  }

  public YaaInfo $and(And and) {
    return null;
  }

  public YaaInfo $assign(Assign assign) {
    return null;

  }

  public YaaInfo $string(AstString ast) {
    return null;
  }

  public YaaInfo $bitAnd(BitAnd bitAnd) {
    return null;
  }

  public YaaInfo $bitNot(BitNot bitNot) {
    return null;
  }

  public YaaInfo $bitOr(BitOr bitOr) {
    return null;
  }

  public YaaInfo $cha(Cha cha) {
    return null;
  }

  public YaaInfo $divide(Divide divide) {
    return null;
  }

  public YaaInfo $eCall(ECall eCall) {
    return null;
  }

  public YaaInfo $eGet(EGet eGet) {
    return null;
  }

  public YaaInfo $eMtd(EMtd eMtd) {
    return null;
  }

  public YaaInfo $function(NewFun newFun) {
    return null;
  }

  public YaaInfo $fInterface(NewFunctionalInterface ctx) {
    return null;
  }

  public YaaInfo $gEqual(GEqual gEqual) {
    return null;
  }

  public YaaInfo $group(Group group) {
    return null;
  }

  public YaaInfo $gThan(GThan gThan) {
    return null;
  }

  public YaaInfo $imports(Imports imp) {
    return null;

  }

  public YaaInfo $init(Init init) {
    return null;

  }

  public YaaInfo $leave() {
    return null;
  }

  public YaaInfo $lEqual(LEqual lEqual) {
    return null;
  }

  public YaaInfo $loop(Loop loop) {
    return null;

  }

  public YaaInfo $lThan(LThan lThan) {
    return null;
  }

  public YaaInfo $modulo(Modulo modulo) {
    return null;
  }

  public YaaInfo $name(Name name) {
    return null;
  }

  public YaaInfo $newClass(NewClass newClass) {
    return null;

  }

  public YaaInfo $objectType(ObjectType objectType) {
    return null;
  }

  public YaaInfo $or(Or or) {
    return null;
  }

  public YaaInfo $power(Power power) {
    return null;
  }

  public YaaInfo $return(Return ret) {
    return null;
  }

  public YaaInfo $root(RootTo rootTo) {
    return null;
  }

  public YaaInfo $tab() {
    return null;
  }

  public YaaInfo $times(Times times) {
    return null;
  }

  public YaaInfo $tryCatch(TryCatch tryCatch) {
    return null;
  }

  public YaaInfo $uMinus(UMinus uMinus) {
    return null;
  }

  public YaaInfo $uNot(UNot uNot) {
    return null;
  }

  public YaaInfo $uPlus(UPlus uPlus) {
    return null;
  }

  public YaaInfo $vCall(VCall vCall) {
    return null;
  }

  public YaaInfo $vDec(VDeclaration dec) {
    return null;
  }

  public YaaInfo $vDef(VDefinition def) {
    return null;
  }

  public YaaInfo $vGet(VGet vGet) {
    return null;
  }

  public YaaInfo $vMtd(VMtd vMtd) {
    return null;
  }

  public YaaInfo $xor(Xor xor) {
    return null;
  }

  public YaaInfo getSymbol(String name) {
    return table.getSymbol(name);
  }

  public void putSymbol(String symbolName, YaaInfo symbol) {
    table.putSymbol(symbolName, symbol);
  }

//  public YaaInfo getFunInSameScope(String name) {
//    return table.getFunInSameScope(name);
//  }
//
//  public void putMeta(String name, YaaMeta meta) {
//    table.putMeta(name, meta);
//  }
//
//  public void putClass(String name, YaaClz yaaClz) {
//    table.putClass(name, yaaClz);
//  }
//
//  public YaaClz getClass(String name) {
//    return table.getClass(name);
//  }
//
//  public void putFunction(String name, MtdPack funInfo) {
//    table.putFunction(name, funInfo);
//  }
//
//  public void putFunction(String name, YaaFun funInfo) {
//    table.putFunction(name, funInfo);
//  }

//  public MtdPack getFunction(String name) {
//    return table.getFunction(name);
//  }
//
//  public YaaFun getSFunction(String name) {
//    return table.getSFunction(name);
//  }

//  public void putField(String name, YaaField yaaField) {
//    table.putField(name, yaaField);
//  }
//
//  public void replaceField(String name, YaaField yaaField) {
//    table.replaceField(name, yaaField);
//  }
//
//  public YaaField getField(String name) {
//    return table.getField(name);
//  }

  public YaaInfo $programIn(ProgramIn programIn) {
    return null;
  }

  public YaaInfo $ifStmt(IfStmt ifStmt) {
    return null;
  }

  public YaaInfo $blockInClz() {
    return null;
  }

  public YaaInfo $programOut(ProgramOut programOut) {
    return null;
  }

  public YaaInfo $mEqual(MEqual mEqual) {
    return null;
  }

  public YaaInfo $mNEqual(MNEqual mNEqual) {
    return null;
  }

  public YaaInfo $basex(Basex basex) {
    return null;
  }

  public YaaInfo $anonymous(Anonymous anonymous) {
    return null;
  }

  public YaaInfo $newEnum(NewEnum newEnum) {
    return null;
  }

  public YaaInfo $throwStmt(Throw throwStmt) {
    return null;
  }

  public YaaInfo $nullStmt(Null aNull) {
    return null;
  }

  public YaaInfo $superField(SuperField superField) {
    return null;
  }

  public YaaInfo $superMtd(SuperMtd superMtd) {
    return null;
  }

  public YaaInfo $ternary(Ternary ternary) {
    return null;
  }

  public YaaInfo $continue(Continue $continue) {
    return null;
  }

  public YaaInfo $break(Break $break) {
    return null;
  }

  public YaaInfo $this(This aThis) {
    return null;
  }

  public YaaInfo $thisMtd(ThisMtd thisMtd) {
    return null;
  }

  public YaaInfo $thisField(ThisField thisField) {
    return null;
  }

  public YaaInfo $thisCall(ThisCall thisCall) {
    return null;
  }

  public YaaInfo $false(False aFalse) {
    return null;
  }

  public YaaInfo $true(True aTrue) {
    return null;
  }

  public YaaInfo $cast(Cast cast) {
    return null;
  }

  public YaaInfo $is(Is is) {
    return null;
  }

  public YaaInfo cmdMain(CmdMain cmdMain) {
    return null;
  }

  public YaaInfo $innerBlock(InnerBlock iBlock) {
    return null;
  }

  public YaaInfo $newRecord(NewRecord newRecord) {
    return null;
  }

  public YaaInfo $newMeta(NewMeta newMeta) {
    return null;
  }

  public YaaInfo $metaCall(YaaMetaCall call) {
    return null;
  }

//  public YaaMeta getMeta(String meta_name) {
//    return table.getMeta(meta_name);
//  }

  public YaaInfo $stubReturn(StubReturn ctx) {
    return null;
  }

  public YaaInfo getSymbolInSameScope(String symbolName) {
    return table.getSymbolInSameScope(symbolName);
  }

  public YaaInfo getAlreadyDefinedSymbolInPass1(String symbolName) {
    return table.getAlreadyDefinedSymbolInPass1(symbolName);
  }
}