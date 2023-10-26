package yaa.semantic.passes.fs6;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import yaa.ast.*;
import yaa.parser.YaaToken;
import yaa.pojos.*;
import yaa.semantic.passes.fs6.ifs.*;
import yaa.semantic.passes.fs6.loops.F6ALoop;
import yaa.semantic.passes.fs6.loops.F6ILoop;
import yaa.semantic.passes.fs6.loops.F6WLoop;
import yaa.semantic.passes.fs6.results.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import static java.lang.Byte.parseByte;
import static java.lang.Integer.parseInt;
import static java.lang.Short.parseShort;
import static org.objectweb.asm.Opcodes.*;
import static yaa.pojos.GlobalData.nothing;
import static yaa.semantic.passes.fs6.F6CastMtd.handleCastMtd;
import static yaa.semantic.passes.fs6.F6Return.handle$return;
import static yaa.semantic.passes.fs6.F6Utils.*;

public class F6 extends FileState {
  public static final Stack<YaaFun> f6TopMtd = new Stack<>();
  public static Stack<MethodVisitor> mtdWriters = new Stack<>();
  public Stack<ClassWriter> cw = new Stack<>();
  public Stack<ClzFieldStore> clz$fields = new Stack<>();
  public Stack<MtdVariables> variables = new Stack<>();
  public Map<Stmt, YaaResult> results;
  public Stack<Jump> break$locations = new Stack<>();
  public Stack<Jump> continue$locations = new Stack<>();
  private ClassWriter tcw;
  public static Stack<List<VariableData>> variableMeta = new Stack<>();

  public F6(List<Stmt> stmts, String filePath) {
    super(stmts, filePath);
  }

  public static MethodVisitor mw() {
    return mtdWriters.peek();
  }

  private String topClzCodeName;

  @Override
  public YaaInfo $programIn(ProgramIn in) {
    this.tables = GlobalData.tables4File.get(in.path);
    this.table = tables.get(in);
    GlobalData.fs6 = this;
    GlobalData.fs = this;
    YaaError.filePath = path;
    results = GlobalData.results.get(in.path);


    topClzCodeName = GlobalData.topClzCodeName.get(in.path);
    cw.push(new ClassWriter(ClassWriter.COMPUTE_FRAMES));
    cw.peek().visit(
        V17, ACC_PUBLIC,
        topClzCodeName,
        null,
        "java/lang/Object",
        new String[]{}
    );
    tcw = cw.peek();

    cw.peek().visitSource(path, path + " debug information");
    if (in.has$top$fields) {
      setTopFields();
    }
    return nothing;
  }

  @Override
  public void execute() {
    for (var stmt : stmts) {
      if (stmt instanceof VDefinition) {
        continue;
      }
      if (stmt instanceof VDeclaration) {
        continue;
      }
      stmt.visit(this);
    }
  }

  public void pushTable(Stmt ctx) {
    this.table = tables.get(ctx);
  }

  public void popTable() {
    this.table = table.parent;
  }

  public void push$fields() {
    clz$fields.push(new ClzFieldStore());
  }

  public void push$variables() {
    variables.push(new MtdVariables());
  }

  public void pop$fields() {
    clz$fields.pop();
  }

  public void pop$variables() {
    variables.pop();
  }

  @Override
  public YaaInfo $loop(Loop loop) {
    pushTable(loop);
    var result = (LoopResult) results.get(loop);
    if (result.loopIsWhat == LoopIsWhat.While) {
      F6WLoop.p6WhileLoop(loop);
    } else if (result.loopIsWhat == LoopIsWhat.Array) {
      F6ALoop.f6ArrayLoop(loop, result);
    } else {
      F6ILoop.f6ILoop(loop, result);
    }
    popTable();
    return nothing;
  }

  @Override
  public YaaInfo $pointed(Pointed pointed) {
    var value = Double.parseDouble(pointed.token.neededContent);
    if (value == 0.0d) {
      mw().visitInsn(DCONST_0);
    } else if (value == 1.0d) {
      mw().visitInsn(DCONST_1);
    } else {
      mw().visitLdcInsn(value);
    }
    return GlobalData.double$clz;
  }

  @Override
  public YaaInfo $float(Floated floated) {
    var value = Float.parseFloat(floated.token.neededContent);
    if (value == 0.0) {
      mw().visitInsn(FCONST_0);
    } else if (value == 1.0) {
      mw().visitInsn(FCONST_1);
    } else if (value == 2.0) {
      mw().visitInsn(FCONST_2);
    } else {
      mw().visitLdcInsn(value);
    }
    return GlobalData.float$clz;
  }

  @Override
  public YaaInfo $byte(Byted byted) {
    mw().visitIntInsn(BIPUSH, parseByte(byted.token.neededContent));
    return GlobalData.byte$clz;
  }

  @Override
  public YaaInfo $short(Shorted shorted) {
    mw().visitIntInsn(SIPUSH, parseShort(shorted.token.neededContent));
    return GlobalData.short$clz;
  }

  @Override
  public YaaInfo $cha(Cha ctx) {
    if (ctx.content instanceof HashSign) {
      mw().visitIntInsn(BIPUSH, '#');
    } else if (ctx.content instanceof BTick) {
      mw().visitIntInsn(BIPUSH, '"');
    } else if (ctx.content instanceof BSlash) {
      mw().visitIntInsn(BIPUSH, '\\');
    } else if (ctx.content instanceof LCurly) {
      mw().visitIntInsn(BIPUSH, '{');
    } else if (ctx.content instanceof NewLine) {
      mw().visitIntInsn(BIPUSH, '\n');
    } else if (ctx.content instanceof SQuote) {
      mw().visitIntInsn(BIPUSH, '\'');
    } else {
      if (ctx.itIsInterpolated) {
        ((Stmt) ctx.content).visit(this);
      } else {
        mw().visitIntInsn(BIPUSH, ctx.char$content);
      }
    }
    return GlobalData.char$clz;
  }

  @Override
  public YaaInfo $long(Longed longed) {
    var value = Long.parseLong(longed.token.neededContent);
    if (value == 0L) {
      mw().visitInsn(LCONST_0);
    } else if (value == 1L) {
      mw().visitInsn(LCONST_1);
    } else {
      mw().visitLdcInsn(value);
    }
    return GlobalData.long$clz;
  }

  @Override
  public YaaInfo $decimal(Decimal decimal) {
    generateIntCode(parseInt(decimal.token.content));
    return GlobalData.int$clz;
  }

  @Override
  public YaaInfo $anonymous(Anonymous anonymous) {
    pushTable(anonymous);
    var new$fn = (YaaFun) getSymbol(anonymous.placeOfUse());
    f6TopMtd.push(new$fn);
    push$variables();
    F6.variableMeta.push(new ArrayList<>());
    F6.mtdWriters.push(tcw.visitMethod(
        ACC_PUBLIC | ACC_STATIC /*| ACC_SYNTHETIC*/,
        new$fn.name, new$fn.descriptor(), null, new String[]{}
    ));
    mw().visitCode();
    variables.peek().index = -1;
    initParam(anonymous.parameters, new$fn);
    anonymous.stmt.visit(this);
    new$fn.closeCode();
    popTable();
    return F6NoName.produceInvokeCode(new$fn, topClzCodeName);
  }

  public void initParam(List<YaaToken> ast$params, YaaFun anon) {
    for (var closed$field : anon.closures.values()) {
      var variables = GlobalData.fs6.variables.peek();
      var name = closed$field.field$name;
      var data = closed$field.data;
      switch (data.name) {
        case GlobalData.long$name, GlobalData.double$name -> {
          variables.putWideVar(name);
        }
        default -> variables.putVar(name);
      }
    }
    var variables = GlobalData.fs6.variables.peek();
    for (int i = 0; i < anon.parameters.size(); i++) {
      var param = ast$params.get(i);
      var param$name = param.content;
      var data = anon.parameters.get(i);
      switch (data.name) {
        case GlobalData.long$name, GlobalData.double$name -> {
          variables.putWideVar(param$name);
        }
        default -> variables.putVar(param$name);
      }
      var label = new Label();
      mw().visitLabel(label);
      mw().visitLineNumber(param.line, label);
      var index = variables.index;
      variableMeta.peek().add(new VariableData(
          param$name, label, data.descriptor(),
          data.clzUseSignature(), index, new ArrayList<>(), new ArrayList<>()
      ));
    }
  }

  @Override
  public YaaInfo $superField(SuperField superField) {
    var field = ((FieldResult) results.get(superField)).field;
    mw().visitVarInsn(ALOAD, 0);
    //the descriptor specifies the parent
    mw().visitFieldInsn(
        GETFIELD, field.owner,
        field.field$name,
        field.descriptor()
    );
    return field.data;
  }

  @Override
  public YaaInfo $superMtd(SuperMtd superMtd) {
    var mtd = ((CallResult) results.get(superMtd)).mtd;
    mw().visitVarInsn(ALOAD, 0);
    runArguments(mtd.parameters, superMtd.arguments);
    mw().visitMethodInsn(
        INVOKESPECIAL,
        mtd.owner,
        mtd.name,
        mtd.descriptor(),
        false
    );
    return mtd.type;
  }

  @Override
  public YaaInfo $cast(Cast cast) {
    return handleCastMtd(cast);
  }

  @Override
  public YaaInfo $is(Is is) {
    return F6Is.is(is, (IsResult) results.get(is), true);
  }

  @Override
  public YaaInfo $nullStmt(Null aNull) {
    mw().visitInsn(ACONST_NULL);
    return GlobalData.null$clz;
  }

  @Override
  public YaaInfo $innerBlock(InnerBlock iBlock) {
    pushTable(iBlock);
    runF6Stmts(iBlock.stmts);
    popTable();
    return nothing;
  }

  @Override
  public YaaInfo $throwStmt(Throw throwStmt) {
    return F6Throw.handle$throw(throwStmt);
  }

  @Override
  public YaaInfo $main(MainFunction main) {
    return F6Main.main(main, tcw);
  }

  @Override
  public YaaInfo $function(NewFun newFun) {
    return F6Function.function(newFun, tcw);
  }

  @Override
  public YaaInfo $fInterface(NewFunctionalInterface ctx) {
    F6FInterface.fInterface(ctx);
    return nothing;
  }

  @Override
  public YaaInfo $stubReturn(StubReturn ctx) {
    mw().visitInsn(ACONST_NULL);
    mw().visitInsn(ARETURN);
    return nothing;
  }

  @Override
  public YaaInfo $name(Name name) {
    return F6Name.id(name.token.content);
  }

  @Override
  public YaaInfo $tryCatch(TryCatch tryCatch) {
    return F6Try.tryCatch(tryCatch);
  }

  @Override
  public YaaInfo $eGet(EGet eGet) {
    return F6EGet.get(eGet);
  }

  @Override
  public YaaInfo $vMtd(VMtd vMtd) {
    return F6VMtd.vMtd(vMtd);
  }

  @Override
  public YaaInfo $newEnum(NewEnum newEnum) {
    F6NEnum.newEnum(newEnum);
    return nothing;
  }

  @Override
  public YaaInfo $mEqual(MEqual mEqual) {
    return F6MEqual.mEqual(mEqual);
  }

  @Override
  public YaaInfo $mNEqual(MNEqual mNEqual) {
    return F6MNEqual.mNEqual(mNEqual);
  }

  @Override
  public YaaInfo $vGet(VGet vGet) {
    return F6VGet.get(vGet);
  }

  @Override
  public YaaInfo $continue(Continue $continue) {
    return F6Continue.handleContinue($continue);
  }

  @Override
  public YaaInfo $this(This aThis) {
    mw().visitVarInsn(ALOAD, 0);
    return GlobalData.topClz.peek();
  }

//  @Override
//  public YaaInfo $thisCall(ThisCall thisCall) {
//    var result = (ThisCallResult) results.get(thisCall);
//    var yaaClz = result.clz;
//    var opParam = result.init.parameters;
//    mw().visitTypeInsn(NEW, yaaClz.codeName);
//    mw().visitInsn(DUP);
//
//    runArguments(opParam, thisCall.arguments);
//
//    mw().visitMethodInsn(
//        INVOKESPECIAL, yaaClz.codeName, "<init>",
//        result.init.descriptor(), false
//    );
//
//    return result.clz;
//  }

  @Override
  public YaaInfo $thisField(ThisField thisField) {
    var name = thisField.name.content;
    var owner = GlobalData.topClz.peek().codeName;
    var field = ((FieldResult) results.get(thisField)).field;
    mw().visitVarInsn(ALOAD, 0);
    mw().visitFieldInsn(GETFIELD, owner, name, field.descriptor());
    return field.data;
  }

  @Override
  public YaaInfo $thisMtd(ThisMtd thisMtd) {
    var mtd$name = thisMtd.name.content;
    var mtd = ((CallResult) results.get(thisMtd)).mtd;
    mw().visitVarInsn(ALOAD, 0);
    runArguments(mtd.parameters, thisMtd.arguments);
    mw().visitMethodInsn(
        INVOKEVIRTUAL,
        mtd.owner,
        mtd$name,
        mtd.descriptor(),
        mtd.itIsTraitMtd
    );
    return mtd.type;
  }

  @Override
  public YaaInfo $break(Break $break) {
    return F6Break.handleBreak($break);
  }

  @Override
  public YaaInfo $ternary(Ternary ternary) {
    return F6Ternary.ternary(ternary);
  }

  @Override
  public YaaInfo $assign(Assign assign) {
    var e1 = assign.e1;
    if (assign.e1 instanceof SuperField) {
      var result = (FieldResult) results.get(assign);
      var field = result.field;
      mw().visitVarInsn(ALOAD, 0);
      assign.e2.visit(GlobalData.fs6);
      mw().visitFieldInsn(
          PUTFIELD,
          field.owner,
          field.field$name,
          field.descriptor()
      );
    } else if (assign.e1 instanceof ThisField) {
      var result = (FieldResult) results.get(assign);
      var field = result.field;
      mw().visitVarInsn(ALOAD, 0);
      assign.e2.visit(GlobalData.fs6);
      mw().visitFieldInsn(
          PUTFIELD,
          GlobalData.topClz.peek().codeName,
          field.field$name,
          field.descriptor()
      );
    } else if (e1 instanceof VGet vGet) {
      F6NDotAssign.nameDotAssign(assign, vGet);
    } else if (e1 instanceof EGet eGet) {
      F6EAssign.e$assign(assign, eGet);
    } else {
      F6NameAssign.plain$assign(assign, ((Name) e1).token.content);
    }
    return nothing;
  }


  @Override
  public YaaInfo $true(True aTrue) {
    mw().visitInsn(ICONST_1);
    return GlobalData.boole$clz;
  }

  @Override
  public YaaInfo $false(False aFalse) {
    mw().visitInsn(ICONST_0);
    return GlobalData.boole$clz;
  }

  @Override
  public YaaInfo $eMtd(EMtd eMtd) {
    return F6EMtd.eMtd(eMtd);
  }

  @Override
  public YaaInfo $vCall(VCall call) {
    return F6VCall.call(call);
  }

  @Override
  public YaaInfo $vDef(VDefinition def) {
    F6VDef.def(def);
    return nothing;
  }

  @Override
  public YaaInfo $vDec(VDeclaration dec) {
    F6VDec.dec(dec);
    return nothing;
  }

  @Override
  public YaaInfo $return(Return ret) {
    return handle$return(ret);
  }

  @Override
  public YaaInfo $group(Group group) {
    return group.e.visit(this);
  }

  @Override
  public YaaInfo $or(Or or) {
    return F6Or.or(or);
  }

  @Override
  public YaaInfo $xor(Xor xor) {
    return F6Xor.xor(xor);
  }

  @Override
  public YaaInfo $bitOr(BitOr bitOr) {
    return F6BitOr.or(bitOr);
  }

  @Override
  public YaaInfo $bitAnd(BitAnd bitAnd) {
    return F6BitAnd.and(bitAnd);
  }

  @Override
  public YaaInfo $newMeta(NewMeta newMeta) {
    F6NewMeta.newMeta(newMeta);
    return null;
  }

  @Override
  public YaaInfo $ifStmt(IfStmt ifStmt) {
    if (ifStmt.isEnumSwitch) {
      var target = CondUtils.getIfTarget(ifStmt);
      mw().visitMethodInsn(
          INVOKEVIRTUAL,
          target.codeName,
          "ordinal",
          "()I",
          false
      );
      EnumSwitch.handleEnumSwitch(ifStmt, (YaaClz) target);
      return nothing;
    }
    var target = CondUtils.getIfTarget(ifStmt);
    pushTable(ifStmt);
    if (target.isPrimitive()) {
      if (ifStmt.allCasesAreIntegral) {
        if (ifStmt.canBeTableSwitched) {
          TableSwitch.handleTableSwitch(ifStmt);
        } else {
          LookUpSwitch.handleLookUpSwitch(ifStmt);
        }
      } else {
        PlainPrimitive.handlePlainPrimitive(ifStmt, target);
      }
    } else {
      if (ifStmt.enumOptions != null) {
        EnumSwitch.handleEnumSwitch(ifStmt, (YaaClz) target);
      } else {
        ObjectIf.ifWithObjectTarget(ifStmt, target);
      }
    }
    popTable();
    return nothing;
  }

  @Override
  public YaaInfo $lShift(LShift lShift) {
    return F6LShift.lShift(lShift);
  }

  @Override
  public YaaInfo $rShift(RShift rShift) {
    return F6RShift.rShift(rShift);
  }

  @Override
  public YaaInfo $uRShift(URShift urShift) {
    return F6URShift.uRShift(urShift);
  }

  @Override
  public YaaInfo $bitNot(BitNot bitNot) {
    return F6BitNot.not(bitNot);
  }

  @Override
  public YaaInfo $uMinus(UMinus uMinus) {
    return F6UMinus.minus(uMinus);
  }

  @Override
  public YaaInfo $uPlus(UPlus uPlus) {
    return F6UPlus.plus(uPlus);
  }

  @Override
  public YaaInfo $uNot(UNot uNot) {
    return F6UNot.not(uNot);
  }

  @Override
  public YaaInfo $and(And and) {
    return F6And.and(and);
  }

  @Override
  public YaaInfo $gEqual(GEqual gEqual) {
    return F6GEqual.gEqual(gEqual);
  }

  @Override
  public YaaInfo $eEqual(EEqual eEqual) {
    return F6Equal.eEqual(eEqual);
  }

  @Override
  public YaaInfo $nEqual(NEqual nEqual) {
    return F6NEqual.nEqual(nEqual);
  }

  @Override
  public YaaInfo $lEqual(LEqual lEqual) {
    return F6LEqual.lEqual(lEqual);
  }

  @Override
  public YaaInfo $lThan(LThan lThan) {
    return F6LThan.lThan(lThan);
  }

  @Override
  public YaaInfo $gThan(GThan gThan) {
    return F6GThan.gThan(gThan);
  }

  @Override
  public YaaInfo $divide(Divide divide) {
    return F6Div.div(divide);
  }

  @Override
  public YaaInfo $modulo(Modulo modulo) {
    return F6Mod.mod(modulo);
  }

  @Override
  public YaaInfo $times(Times times) {
    return F6Times.times(times);
  }

  @Override
  public YaaInfo $power(Power power) {
    return F6Pow.pow(power);
  }

  @Override
  public YaaInfo $basex(Basex basex) {
    return F6Basex.basex(basex);
  }

  @Override
  public YaaInfo $root(RootTo rootTo) {
    return F6Root.root(rootTo);
  }

  @Override
  public YaaInfo $plus(Plus plus) {
    return F6Plus.plus(plus);
  }

  @Override
  public YaaInfo $minus(Minus minus) {
    return F6Minus.minus(minus);
  }

  @Override
  public YaaInfo $string(AstString ast) {
    return F6String.f6String(ast);
  }

  @Override
  public YaaInfo $newClass(NewClass newClass) {
    F6NClass.new$class(newClass);
    return nothing;
  }

  @Override
  public YaaInfo $newRecord(NewRecord newRecord) {
    F6NRecord.newRecord(newRecord);
    return nothing;
  }

  public static class Jump {
    public Label label;
    public String name;

    public Jump(String name, Label label) {
      this.name = name;
      this.label = label;
    }
  }

  @Override
  public YaaInfo $programOut(ProgramOut programOut) {
    saveClass4Writing(topClzCodeName);
    cw.pop();
    GlobalData.fs6 = null;
    tcw = null;
    GlobalData.fs = null;
    return nothing;
  }
}