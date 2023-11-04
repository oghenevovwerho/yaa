package yaa.parser;

import yaa.ast.*;
import yaa.pojos.*;
import yaa.pojos.jMold.JMold;

import java.util.*;

import static java.lang.Byte.parseByte;
import static java.lang.Double.parseDouble;
import static java.lang.Float.parseFloat;
import static java.lang.Integer.parseInt;
import static java.lang.Long.parseLong;
import static java.lang.Short.parseShort;
import static java.lang.System.arraycopy;
import static yaa.parser.TkKind.*;
import static yaa.parser.TokenUtils.tokenString;

public class YaaParser {
  private final String filePath;
  private final YaaLexer lexer;
  private YaaToken ct;
  public static boolean inMainFile;
  public static MainFunction astMain;

  public YaaParser(String path, String source) {
    lexer = new YaaLexer(source);
    this.filePath = path;
  }

  private List<Import> imports;
  private final Map<String, Import> imported = new HashMap<>();

  public List<Stmt> parse() {
    final List<Stmt> parsedContent = new ArrayList<>();
    ct = lexer.nextToken();
    var inStart = ct;
    var programIn = new ProgramIn(filePath);
    programIn.start = inStart;
    parsedContent.add(programIn);
    while (ct.kind != eof) {
      switch (ct.kind) {
        //case new_line -> just$advance();
        case arrow -> {
          var start = ct;
          just$advance();
          if (ct.kind == eof) {
            throw new YaaError(
                ct.placeOfUse(),
                "The program entry point must have one or more statements"
            );
          }
          var mainFunction = new MainFunction(filePath, parseStmt());
          mainFunction.name = new YaaToken(id, "main", ct.line, ct.column + 10);
          mainFunction.start = start;
          mainFunction.close = ct;
          if (astMain != null) {
            throw new YaaError(
                mainFunction.placeOfUse(),
                "The program entry point is already in "
                    + astMain.file$name
                    + " at " + astMain.placeOfUse(),
                "Yaa programs can only contain a single entry point"
            );
          }
          astMain = mainFunction;
          parsedContent.add(mainFunction);
        }
        case kw_ode -> {
          parsedContent.add(parseVariable(FieldIsWhat.top$field));
          programIn.has$top$fields = true;
        }
        case id -> {
          var first$token = get$advance();
          if (ct.kind == dot) {//this is for imports
            if (imports == null) {
              imports = new ArrayList<>(5);
              parsedContent.add(new Imports(imports));
            }
            var importedItem = parseImport(first$token);
            var alreadyUsed = imported.get(importedItem.refName);
            if (alreadyUsed != null) {
              throw new YaaError(
                  importedItem.placeOfUse(), importedItem.toString(),
                  "The reference \"" + importedItem.refName + "\" " +
                      "for the import above clashes with that below",
                  alreadyUsed + " at " + alreadyUsed.placeOfUse(),
                  "You should consider aliasing "
                      + importedItem + " at " + importedItem.placeOfUse()
              );
            }
            cacheJvmImports1(importedItem);
            imports.add(importedItem);
            imported.put(importedItem.refName, importedItem);
          } else if (ct.kind == l_bracket) {
            var new_functional_interface = new NewFunctionalInterface(first$token);
            new_functional_interface.typeParams = parseTypeParameters();
            new_functional_interface.parameters = parseParameters();
            if (ct.kind != semi_colon) {
              new_functional_interface.type = parseObjectType();
            }
            checkAdvance(semi_colon, "a functional interface must be delimited by a semi colon");
            new_functional_interface.start = first$token;
            new_functional_interface.scope = ScopeKind.INGLOBAL;
            new_functional_interface.close = ct;
            parsedContent.add(new_functional_interface);
          } else {
            throw new YaaError(
                ct.placeOfUse(), tokenString(ct.kind) + " is invalid at this position"
            );
          }
        }
        case kw_oka -> {
          var object = parseClass();
          object.itIsTopLevelClz = true;
          parsedContent.add(object);
        }
        case kw_record -> {
          var object = parseRecord();
          object.itIsTopLevelClz = true;
          parsedContent.add(object);
        }
        case kw_enum -> {
          var object = parseNewEnum();
          object.itIsTopLevelClz = true;
          YaaParserUtils.checkEnumPropAtTop(object);
          parsedContent.add(object);
        }
        case kw_ozi -> {
          var annotation = parseAnnotation();
          annotation.itIsTopLevelClz = true;
          //YaaParserUtils.checkTraitPropAtTop(object);
          parsedContent.add(annotation);
        }
        case kw_interface -> {
          var object = parseNewTrait();
          object.itIsTopLevelClz = true;
          parsedContent.add(object);
        }
        case l_bracket -> {
          var newFun = parseFunction();
          newFun.itIsWhat = MtdIsWhat.topMtd;
          parsedContent.add(newFun);
        }
        default -> {
          throw new YaaError(
              ct.placeOfUse(), "\"" + ct.content
              + "\" is not allowed at the global scope"
          );
        }
      }
      if (ct.kind == eof) {
        break;
      }
    }
    programIn.close = ct;
    parsedContent.add(new ProgramOut());
    return parsedContent;
  }

  public static void cacheJvmImports1(Import imp) {
    var jvm_import = new JMold().cacheJvmImp(imp.fullName);
    if (jvm_import != null) {
      imp.is4Jvm = true;
    }
  }

  private Stmt parseVariable(FieldIsWhat fieldIsWhat) {
    var start = get$advance();
    var variableName = get$advance(id); //name = 90
    if (ct.kind == equal) {
      just$advance();
      var vDefinition = new VDefinition(variableName, expression(0));
      vDefinition.setStart(start).setClose(ct);
      vDefinition.itIsWhat = fieldIsWhat;
      checkAdvance(semi_colon);
      return vDefinition;
    }
    if (ct.kind == id) {
      var type = parseObjectType();
      if (ct.kind == equal) {
        just$advance();
        var vDefinition = new VDefinition(variableName, expression(0));
        vDefinition.type = type;
        vDefinition.setStart(start).setClose(ct);
        vDefinition.itIsWhat = fieldIsWhat;
        checkAdvance(semi_colon);
        return vDefinition;
      } else {
        var vDeclaration = new VDeclaration(variableName, type);
        vDeclaration.setStart(start).setClose(ct);
        vDeclaration.itIsWhat = fieldIsWhat;
        checkAdvance(semi_colon);
        return vDeclaration;
      }
    }
    throw new YaaError(ct.placeOfUse(), "invalid token in variable declaration");
  }

  public Stmt parseStmt() {
    var start = ct;
    switch (ct.kind) {
      case kw_ode -> {
        return parseVariable(FieldIsWhat.mtd$field);
      }
      case l_curly -> {
        var block = new InnerBlock();
        block.stmts = stmts();
        block.start = start;
        block.close = ct;
        return block;
      }
      case l_bracket -> {
        var function = parseFunction();
        function.itIsWhat = MtdIsWhat.staticMtd;
        return function;
      }
      case kw_tesiwaju -> {
        just$advance();
        var continued = new Continue();
        if (ct.kind == id) {
          continued.name = get$advance();
        }
        continued.start = start;
        continued.close = ct;
        checkAdvance(semi_colon);
        return continued;
      }
      case kw_fimisile -> {
        just$advance();
        var broken = new Continue();
        if (ct.kind == id) {
          broken.name = get$advance();
        }
        broken.start = start;
        broken.close = ct;
        checkAdvance(semi_colon);
        return broken;
      }
      case kw_okpetu -> {
        just$advance();
        var throw_stmt = new Throw(expression(0));
        throw_stmt.start = start;
        throw_stmt.close = ct;
        checkAdvance(semi_colon);
        return throw_stmt;
      }
      case q_mark -> {
        var loop_stmt = loopStmt();
        loop_stmt.start = start;
        loop_stmt.close = ct;
        return loop_stmt;
      }
      case kw_oka -> {
        return parseClass();
      }
      case kw_record -> {
        //YaaParserUtils.checkClzPropInStmt(object);
        return parseRecord();
      }
      case kw_idan -> {
        return parseIfStmt();
      }
      case kw_interface -> {
        return parseNewTrait();
      }
      case kw_enum -> {
        var object = parseNewEnum();
        YaaParserUtils.checkEnumPropInStmt(object);
        return object;
      }
      case kw_ufuoma -> {
        var try_stmt = parseTryCatch();
        try_stmt.start = start;
        try_stmt.close = ct;
        return try_stmt;
      }
      case kw_comot -> {
        just$advance();
        var leave = new Leave();
        leave.start = start;
        leave.close = get$advance(semi_colon);
        return leave;
      }
      case arrow -> {
        just$advance();
        var return_stmt = new Return(expression(0));
        return_stmt.start = start;
        return_stmt.close = get$advance(semi_colon, "a semicolon must end a return statement");
        return return_stmt;
      }
      case kw_mzazi, kw_hii -> {
        var expression = expression(0);
        if (ct.kind == equal) {
          just$advance();
          var assign = new Assign(expression, expression(0));
          assign.start = start;
          assign.close = ct;
          checkAdvance(semi_colon);
          return assign;
        }
        checkAdvance(semi_colon);
        return expression;
      }
      case id -> {
        return nameStartedStmt(start);
      }
    }
    if (!ctCanStartExp()) {
      throw new YaaError(
          ct.placeOfUse(), ct.content + " can not start a statement"
      );
    }
    var exp = expression(0);
    checkAdvance(semi_colon);
    return exp;
  }

  private Stmt nameStartedStmt(YaaToken start) {
    var first_token = ct;
    var leftExpression = expression(0);
    if (leftExpression instanceof Name) {
      if (ct.kind == q_mark) {
        just$advance();
        var exp = expression(0);
        var loop = new Loop();
        loop.condition = exp;
        loop.value$name = first_token;
        if (ct.kind == colon) {
          just$advance();
          var mutant$name = get$advance();
          checkAdvance(equal);
          loop.assign = new Assign(new Name(mutant$name), expression(0));
        }
        loop.stmt = parseStmt();
        loop.start = start;
        loop.close = ct;
        return loop;
      }
      if (ct.kind == equal) {
        just$advance();
        var rightExpression = expression(0);
        if (ct.kind == q_mark) {
          just$advance();
          var loop = new Loop();
          loop.init$value = rightExpression;
          loop.condition = expression(0);
          loop.value$name = first_token;
          if (ct.kind == colon) {
            just$advance();
            var mutant$name = get$advance();
            checkAdvance(equal);
            loop.assign = new Assign(new Name(mutant$name), expression(0));
          }
          loop.stmt = parseStmt();
          loop.start = first_token;
          loop.close = ct;
          return loop;
        }
        //for assignment statements such as color = `brown`;
        var assign_stmt = new Assign(leftExpression, rightExpression);
        assign_stmt.start = start;
        assign_stmt.close = ct;
        checkAdvance(semi_colon);
        return assign_stmt;
      } else {
        throw new YaaError(
            first_token.placeOfUse(), "invalid token after an ID"
        );
      }
    }
    if (ct.kind == equal) {
      //for assignment statements such as car.color = `brown`;
      just$advance();
      var exp = expression(0);
      var assign_stmt = new Assign(leftExpression, exp);
      assign_stmt.start = start;
      assign_stmt.close = ct;
      checkAdvance(semi_colon);
      return assign_stmt;
    } else {
      checkAdvance(semi_colon);
      return leftExpression;
    }
  }

  private Loop loopStmt() {
    var start = get$advance();
    var loop = new Loop();
    loop.condition = expression(0);
    if (ct.kind == colon) {
      just$advance();
      var mutant$name = get$advance();
      checkAdvance(equal);
      loop.assign = new Assign(new Name(mutant$name), expression(0));
    }
    loop.stmt = parseStmt();
    loop.start = start;
    loop.close = ct;
    return loop;
  }

  private final Stack<List<Stmt>> currentStmts = new Stack<>();

  private List<Stmt> finalStmts(YaaToken finalStart) {
    var stmts = new ArrayList<Stmt>();
    currentStmts.push(stmts);
    stmts.add(parseStmt());
    if (ct.kind == r_curly) {
      currentStmts.pop();
      return stmts;
    }
    while (ct.kind != r_curly) {
      if (ct.kind == star_star) {
        throw new YaaError(
            ct.placeOfUse(),
            "A final block cannot come after a final block",
            "The first final block was defined at " + finalStart.placeOfUse()
        );
      }
      if (ct.kind == star) {
        throw new YaaError(
            ct.placeOfUse(),
            "A catch block cannot come after a final block",
            "The final block was defined at " + finalStart.placeOfUse()
        );
      }
      stmts.add(parseStmt());
    }
    currentStmts.pop();
    return stmts;
  }

  private List<Stmt> stmts() {
    checkAdvance(l_curly);
    if (ct.kind == r_curly) {
      just$advance();
      return new ArrayList<>(0);
    }
    var stmts = new ArrayList<Stmt>();
    currentStmts.push(stmts);
    stmts.add(parseStmt());
    if (ct.kind == r_curly) {
      just$advance();
      currentStmts.pop();
      return stmts;
    }
    while (ct.kind != r_curly) {
      stmts.add(parseStmt());
    }
    just$advance();
    currentStmts.pop();
    return stmts;
  }

  private TryCatch parseTryCatch() {
    var start = ct;
    just$advance();//ufuoma
    List<VDefinition> resource_values = new ArrayList<>(1);
    while (ct.kind == kw_ode) {
      var variable = parseVariable(FieldIsWhat.mtd$field);
      if (variable instanceof VDefinition def) {
        resource_values.add(def);
      } else {
        throw new YaaError(variable.placeOfUse(),
            "The resource value of a safe bloc must be initialized"
        );
      }
    }
    checkAdvance(l_curly);
    var tryStart = ct;
    if (ct.kind == r_curly) {
      throw new YaaError(
          ct.placeOfUse(), "A try block must have at least one statement"
      );
    }
    if (ct.kind == star_star) {
      throw new YaaError(
          ct.placeOfUse(),
          "A try block must have a statement before a final block"
      );
    }
    if (ct.kind == star) {
      throw new YaaError(
          ct.placeOfUse(),
          "A try block must have a statement before a catch block"
      );
    }
    var stmtsInTryBlock = new ArrayList<Stmt>();
    currentStmts.push(stmtsInTryBlock);
    stmtsInTryBlock.add(parseStmt());
    while (ct.kind != r_curly) {
      if (ct.kind == star || ct.kind == star_star) {
        break;
      }
      stmtsInTryBlock.add(parseStmt());
    }
    var tryClose = ct;

    currentStmts.pop();
    var catch_blocks = new ArrayList<Catch>();
    while (ct.kind == star) {
      var cStart = ct;
      var catch_block = parseCatch();
      catch_block.start = cStart;
      catch_block.close = ct;
      catch_blocks.add(catch_block);
      if (ct.kind == star_star || ct.kind == r_curly) {
        break;
      }
    }

    if (ct.kind == star_star) {
      var finalStart = ct;
      just$advance(); //finally
      if (ct.kind == r_curly) {
        throw new YaaError(
            ct.placeOfUse(),
            "A final block must have at least one statement"
        );
      }
      if (ct.kind == star_star) {
        throw new YaaError(
            ct.placeOfUse(),
            "A final block cannot come after a final block",
            "The first final block was defined at " + finalStart.placeOfUse()
        );
      }
      if (ct.kind == star) {
        throw new YaaError(
            ct.placeOfUse(),
            "A catch block cannot come after a final block",
            "The final block was defined at " + finalStart.placeOfUse()
        );
      }

      var final_block = new Final(finalStmts(finalStart));
      var tryCatchStmt = new TryCatch();
      final_block.start = finalStart;
      final_block.close = ct;
      tryCatchStmt.finals = final_block;

      var tried = new Tried(stmtsInTryBlock);
      tried.resources = resource_values;
      tried.start = tryStart;
      tried.close = tryClose;
      tryCatchStmt.setTried(tried);
      tryCatchStmt.setCaught(catch_blocks);
      tryCatchStmt.start = start;
      tryCatchStmt.close = ct;
      just$advance();//}
      return tryCatchStmt;
    }

    if (catch_blocks.size() == 0) {
      throw new YaaError(
          ct.placeOfUse(),
          "A try statement must have at least one catch block",
          "Or, it must have a catch-all final block instead"
      );
    }

    var tryCatchStmt = new TryCatch();
    var finals = new Final(new ArrayList<>(0));
    finals.start = start;
    finals.close = ct;
    tryCatchStmt.finals = finals;
    var tried = new Tried(stmtsInTryBlock);
    tried.resources = resource_values;
    tried.start = tryStart;
    tried.close = tryClose;
    tryCatchStmt.setTried(tried);
    tryCatchStmt.setCaught(catch_blocks);
    tryCatchStmt.start = start;
    tryCatchStmt.close = ct;
    just$advance();//}
    return tryCatchStmt;
  }

  private Catch parseCatch() {
    just$advance();//catch
    var types = new ArrayList<ObjectType>();
    types.add(parseObjectType());
    while (ct.kind == comma) {
      just$advance();
      types.add(parseObjectType());
    }
    var catchHolder = get$advance(id, "Expected the holder for the given exceptions");
    if (ct.kind == star_star) {
      throw new YaaError(
          ct.placeOfUse(),
          "A catch block must have a statement before a final block"
      );
    }
    if (ct.kind == star) {
      throw new YaaError(
          ct.placeOfUse(),
          "A catch block must have a statement before another catch block"
      );
    }
    var stmts = new ArrayList<Stmt>();
    stmts.add(parseStmt());
    while (ct.kind != star && ct.kind != star_star && ct.kind != r_curly) {
      stmts.add(parseStmt());
    }
    return new Catch(types, catchHolder, stmts);
  }

  private NewFun parseParentFunction() {
    var start = ct;
    var meta_calls = new ArrayList<YaaMetaCall>(1);
    while (ct.kind == at) {
      meta_calls.add(parseMetaCall());
    }
    var function_name = get$advance(id);
    var parameters = parseParameters();
    ObjectType type = null;
    if (ct.kind == colon) {
      just$advance();
      type = parseObjectType();
    }
    var newFun = new NewFun(parameters, type, parseStmt());
    newFun.name = function_name;
    newFun.metaCalls = meta_calls;
    newFun.start = start;
    newFun.close = ct;
    return newFun;
  }

  private NewFun parseFunction() {
    var start = ct;
    var typeParameters = parseTypeParameters();
    List<YaaMetaCall> meta_calls = new ArrayList<>(1);
    if (ct.kind == at) {
      while (ct.kind == at) {
        meta_calls.add(parseMetaCall());
      }
    }
    var function_name = get$advance(id);
    var parameters = parseParameters();
    ObjectType type = null;
    if (ct.kind == colon) {
      just$advance();
      type = parseObjectType();
    }
    var newFun = new NewFun(parameters, type, parseStmt());
    newFun.metaCalls = meta_calls;
    newFun.name = function_name;
    newFun.typeParams = typeParameters;
    newFun.start = start;
    newFun.close = ct;
    return newFun;
  }

  private Stmt parseIfStmt() {
    var start = get$advance();//if
    var condition = expression(0);
    if (ct.kind == q_mark) {
      //if true ? This : That
      just$advance();
      var l = expression(0);
      checkAdvance(colon);
      var r = expression(0);
      var ternary = new Ternary(condition, l, r);
      ternary.start = start;
      ternary.close = ct;
      return ternary;
    }
    checkAdvance(l_curly);
    var ifCases = new ArrayList<IfCase>();
    ifCases.add(parseIfCase());
    while (ct.kind != r_curly && ct.kind != star_star) {
      ifCases.add(parseIfCase());
    }
    var else_stmts = new ArrayList<Stmt>();
    if (ct.kind == star_star) {
      just$advance();
      else_stmts.add(parseStmt());
      while (ct.kind != r_curly) {
        else_stmts.add(parseStmt());
      }
    }
    just$advance();
    var if_stmt = new IfStmt(condition, ifCases, else_stmts);
    if_stmt.start = start;
    if_stmt.close = ct;
    return if_stmt;
  }

  private IfCase parseIfCase() {
    var start = ct;
    var case_condition = expression(0);
    YaaToken caseLabel = null;
    if (ct.kind == colon) {
      just$advance();
      caseLabel = get$advance(id);
    }

    var stmt = parseStmt();
    var ifCase = new IfCase(case_condition);
    if (stmt instanceof JumpAst jump) {
      jump.itIsLastInCase = true;
      ifCase.lastJump = jump;
    } else if (stmt instanceof RunBlock block) {
      if (block.stmts.size() > 0) {
        var lastStmtInBlock = block.stmts.get(block.stmts.size() - 1);
        if (lastStmtInBlock instanceof JumpAst jump) {
          jump.itIsLastInCase = true;
          ifCase.lastJump = jump;
        }
      }
    }
    ifCase.stmt = stmt;
    ifCase.caseLabel = caseLabel;
    ifCase.close = ct;
    ifCase.start = start;
    return ifCase;
  }

  private List<TypeParam> parseTypeParameters() {
    if (ct.kind != l_bracket) {
      return new ArrayList<>();
    }
    just$advance();
    if (ct.kind == r_bracket) {
      just$advance();
      return new ArrayList<>();
    }
    var type_params = new ArrayList<TypeParam>();
    var paramName = get$advance(id);
    var meta_calls = new ArrayList<YaaMetaCall>(1);
    while (ct.kind == at) {
      meta_calls.add(parseMetaCall());
    }
    ObjectType param_type = null;
    if (ct.kind == id) {
      param_type = parseObjectType();
    }
    var new_param = new TypeParam(paramName, param_type);
    new_param.metaCalls = meta_calls;
    if (ct.kind == id) {
      switch (ct.content) {
        case "in" -> {
          new_param.variance = YaaClzVariance.contravariant;
        }
        case "out" -> {
          new_param.variance = YaaClzVariance.covariant;
        }
      }
      new_param.variance$token = get$advance();
    }
    type_params.add(new_param);
    if (ct.kind == r_bracket) {
      just$advance();
      return type_params;
    }
    while (ct.kind != r_bracket) {
      checkAdvance(comma);
      var param$name = get$advance(id);
      var metaCalls = new ArrayList<YaaMetaCall>(1);
      while (ct.kind == at) {
        metaCalls.add(parseMetaCall());
      }
      ObjectType param$type = null;
      if (ct.kind == id) {
        param$type = parseObjectType();
      }
      var new$param = new TypeParam(param$name, param$type);
      new$param.metaCalls = metaCalls;
      if (ct.kind == id) {
        switch (ct.content) {
          case "in" -> {
            new$param.variance = YaaClzVariance.contravariant;
          }
          case "out" -> {
            new$param.variance = YaaClzVariance.covariant;
          }
        }
        new$param.variance$token = get$advance();
      }
      type_params.add(new$param);
    }
    just$advance();
    return type_params;
  }

  private NewEnum parseNewEnum() {
    var start = get$advance();
    var type_name = get$advance(id);
    var sanitized_options = new HashMap<String, YaaToken>(2);
    while (ct.kind == id) {
      var name_token = get$advance();
      sanitized_options.put(name_token.content, name_token);
    }
    checkAdvance(l_curly);
    if (ct.kind == r_curly) {
      var new_enum = new NewEnum(type_name);
      new_enum.options = sanitized_options;
      new_enum.start = start;
      new_enum.close = get$advance();
      return new_enum;
    }
    List<OverBlock> blocks = new ArrayList<>();
    List<NewClass> newClasses = new ArrayList<>();
    List<NewEnum> newEnums = new ArrayList<>();
    List<NewFun> methods = new ArrayList<>();
    NewFun toStringParentMtd = null;
    List<RunBlock> runBlocks = new ArrayList<>();
    List<Init> inits = new ArrayList<>(2);
    List<EnumOption> enumOptions = new ArrayList<>();
    List<VDefinition> vDefinitions = new ArrayList<>();
    List<VDeclaration> vDeclarations = new ArrayList<>();
    if (ct.kind == kw_ode) {
      var declaration = parseVariable(FieldIsWhat.clz$field);
      if (declaration instanceof VDefinition def) {
        vDefinitions.add(def);
      } else {
        vDeclarations.add((VDeclaration) declaration);
      }
    } else if (ct.kind == id) {
      var name$token = get$advance();
      if (ct.kind == semi_colon) {//edited here, from new_line to semi
        var option = new EnumOption();
        option.name = name$token;
        enumOptions.add(option);
        checkAdvance(semi_colon);
      } else if (ct.kind == l_paren) {
        var option = new EnumOption();
        option.name = name$token;
        option.arguments = parseArguments();
        enumOptions.add(option);
        checkAdvance(semi_colon);
      } else {
        throw new YaaError(ct.placeOfUse(), "invalid token after ID in an enumeration");
      }
    } else if (ct.kind == l_bracket) {
      var method = parseFunction();
      method.itIsWhat = MtdIsWhat.classMtd;
      methods.add(method);
    } else if (ct.kind == star) {
      var parent_start = get$advance();
      var mtd_name = ct.content;
      if (!mtd_name.equals("toString")) {
        throw new YaaError(
            ct.placeOfUse(), "only the toString method of an enum can be overridden"
        );
      } else {
        var parentString = new NewFun();
        parentString.metaCalls = List.of();
        parentString.name = get$advance(id);
        checkAdvance(colon);
        parentString.type = parseObjectType();
        parentString.parameters = List.of();
        parentString.stmt = parseStmt();
        parentString.start = parent_start;
        parentString.close = ct;
        toStringParentMtd = parentString;
      }
    } else if (ct.kind == kw_oka) {
      newClasses.add(parseClass());
    } else if (ct.kind == kw_interface) {
      newClasses.add(parseNewTrait());
    } else if (ct.kind == kw_enum) {
      var object = parseNewEnum();
      YaaParserUtils.checkEnumPropInTrait(object);
      newEnums.add(object);
    } else if (ct.kind == kw_tuli) {
      just$advance();
      var run_block = new RunBlock(stmts());
      run_block.start = start;
      run_block.close = ct;
      runBlocks.add(run_block);
    } else if (ct.kind == l_paren) {
      inits.add(parseInit());
    } else if (ct.kind == kw_mzazi) {
      blocks.add(parseParent());
    } else {
      throw new YaaError(
          ct.placeOfUse(),
          ct.content + " is not recognised in this scope scope"
      );
    }
    if (ct.kind == r_curly) {
      var new_enum = new NewEnum(type_name);
      new_enum.options = sanitized_options;
      new_enum.methods = methods;
      new_enum.enumOptions = enumOptions;
      new_enum.runBlocks = runBlocks;
      new_enum.inits = inits;
      new_enum.toStringParentMtd = toStringParentMtd;
      new_enum.enums = newEnums;
      new_enum.classes = newClasses;
      new_enum.implementations = blocks;
      new_enum.vDefinitions = vDefinitions;
      new_enum.vDeclarations = vDeclarations;
      new_enum.start = start;
      new_enum.close = get$advance();
      return new_enum;
    }
    while (ct.kind != r_curly) {
      if (ct.kind == kw_ode) {
        var declaration = parseVariable(FieldIsWhat.clz$field);
        if (declaration instanceof VDefinition def) {
          vDefinitions.add(def);
        } else {
          vDeclarations.add((VDeclaration) declaration);
        }
      } else if (ct.kind == id) {
        var name$token = get$advance();
        if (ct.kind == semi_colon) {//edited here, from new_line to semi
          var option = new EnumOption();
          option.name = name$token;
          enumOptions.add(option);
          checkAdvance(semi_colon);
        } else if (ct.kind == l_paren) {
          var option = new EnumOption();
          option.name = name$token;
          option.arguments = parseArguments();
          enumOptions.add(option);
          checkAdvance(semi_colon);
        } else {
          throw new YaaError(ct.placeOfUse(), "invalid token after ID in an enumeration");
        }
      } else if (ct.kind == l_paren) {
        inits.add(parseInit());
      } else if (ct.kind == kw_oka) {
        newClasses.add(parseClass());
      } else if (ct.kind == l_bracket) {
        var method = parseFunction();
        method.itIsWhat = MtdIsWhat.classMtd;
        methods.add(method);
      } else if (ct.kind == kw_interface) {
        newClasses.add(parseNewTrait());
      } else if (ct.kind == star) {
        var parent_start = get$advance();
        var mtd_name = ct.content;
        if (toStringParentMtd != null) {
          throw new YaaError(
              ct.placeOfUse(), "the toString method has already " +
              "been overridden at " + toStringParentMtd.placeOfUse()
          );
        }
        if (!mtd_name.equals("toString")) {
          throw new YaaError(
              ct.placeOfUse(), "only the toString method of an enum can be overridden"
          );
        } else {
          var parentString = new NewFun();
          parentString.metaCalls = List.of();
          parentString.name = get$advance(id);
          checkAdvance(colon);
          parentString.type = parseObjectType();
          parentString.parameters = List.of();
          parentString.start = parent_start;
          parentString.stmt = parseStmt();
          parentString.close = ct;
          toStringParentMtd = parentString;
        }
      } else if (ct.kind == kw_enum) {
        var object = parseNewEnum();
        YaaParserUtils.checkEnumPropInTrait(object);
        newEnums.add(object);
      } else if (ct.kind == kw_tuli) {
        just$advance();
        var run_block = new RunBlock(stmts());
        run_block.start = start;
        run_block.close = ct;
        runBlocks.add(run_block);
      } else if (ct.kind == kw_mzazi) {
        blocks.add(parseParent());
      } else {
        throw new YaaError(
            ct.placeOfUse(),
            ct.content + " is not recognised in this scope scope"
        );
      }
    }
    var new_enum = new NewEnum(type_name);
    new_enum.options = sanitized_options;
    new_enum.methods = methods;
    new_enum.enums = newEnums;
    new_enum.runBlocks = runBlocks;
    new_enum.implementations = blocks;
    new_enum.classes = newClasses;
    new_enum.toStringParentMtd = toStringParentMtd;
    new_enum.enumOptions = enumOptions;
    new_enum.inits = inits;
    new_enum.vDefinitions = vDefinitions;
    new_enum.vDeclarations = vDeclarations;
    new_enum.start = start;
    new_enum.close = get$advance();
    return new_enum;
  }

  private NewClass parseNewTrait() {
    var start = get$advance();
    var type_name = get$advance(id);
    var typeParameters = parseTypeParameters();
    checkAdvance(l_curly);
    if (ct.kind == r_curly) {
      var new_trait = new NewClass(type_name);
      new_trait.start = start;
      new_trait.isTrait = true;
      new_trait.close = get$advance();
      new_trait.typeParams = typeParameters;
      return new_trait;
    }
    List<NewClass> newClasses = new ArrayList<>();
    List<NewEnum> newEnums = new ArrayList<>();
    List<OverBlock> blocks = new ArrayList<>();
    List<RunBlock> runBlocks = new ArrayList<>();
    List<NewFun> methods = new ArrayList<>();
    List<VDefinition> vDefinitions = new ArrayList<>();
    List<VDeclaration> vDeclarations = new ArrayList<>();
    if (ct.kind == kw_oka) {
      newClasses.add(parseClass());
    } else if (ct.kind == kw_interface) {
      newClasses.add(parseNewTrait());
    } else if (ct.kind == kw_enum) {
      var object = parseNewEnum();
      YaaParserUtils.checkEnumPropInTrait(object);
      newEnums.add(object);
    } else if (ct.kind == kw_mzazi) {
      blocks.add(parseParent());
    } else if (ct.kind == l_bracket) {
      var method = parseFunction();
      method.itIsWhat = MtdIsWhat.classMtd;
      methods.add(method);
    } else if (ct.kind == kw_tuli) {
      just$advance();
      var run_block = new RunBlock(stmts());
      run_block.start = start;
      run_block.close = ct;
      runBlocks.add(run_block);
    } else if (ct.kind == id) {
      var name$token = get$advance();
      var options = new HashMap<String, YaaToken>();
      if (ct.kind == id) {
        while (ct.kind == id) {
          var token = get$advance();
          options.put(token.content, token);
        }
        if (ct.kind == colon) {
          just$advance();
          if (ct.kind == equal) {
            just$advance();
            var val$def1 = new VDefinition(name$token, expression(0));
            val$def1.setStart(name$token).setClose(ct);
            val$def1.options = options;
            val$def1.itIsWhat = FieldIsWhat.clz$field;
            vDefinitions.add(val$def1);
            checkAdvance(semi_colon);
          } else {
            var type = parseObjectType();
            if (ct.kind == equal) {
              just$advance();
              var val$def2 = new VDefinition(name$token, expression(0));
              val$def2.options = options;
              val$def2.type = type;
              val$def2.setStart(name$token).setClose(ct);
              val$def2.itIsWhat = FieldIsWhat.clz$field;
              vDefinitions.add(val$def2);
              checkAdvance(semi_colon);
            } else {
              var val$dec2 = new VDeclaration(name$token, type);
              val$dec2.setStart(name$token).setClose(ct);
              val$dec2.options = options;
              val$dec2.itIsWhat = FieldIsWhat.clz$field;
              vDeclarations.add(val$dec2);
              checkAdvance(semi_colon);
            }
          }
        } else {
          throw new YaaError(
              ct.placeOfUse(),
              "Expected a proper value after field options"
          );
        }
      } else if (ct.kind == colon) {
        just$advance();
        if (ct.kind == equal) {
          just$advance();
          var val$def1 = new VDefinition(name$token, expression(0));
          val$def1.setStart(name$token).setClose(ct);
          val$def1.itIsWhat = FieldIsWhat.clz$field;
          vDefinitions.add(val$def1);
          checkAdvance(semi_colon);
        } else {
          var type = parseObjectType();
          if (ct.kind == equal) {
            just$advance();
            var val$def2 = new VDefinition(name$token, expression(0));
            val$def2.type = type;
            val$def2.setStart(name$token).setClose(ct);
            val$def2.itIsWhat = FieldIsWhat.clz$field;
            vDefinitions.add(val$def2);
            checkAdvance(semi_colon);
          } else {
            var val$dec2 = new VDeclaration(name$token, type);
            val$dec2.setStart(name$token).setClose(ct);
            val$dec2.itIsWhat = FieldIsWhat.clz$field;
            vDeclarations.add(val$dec2);
            checkAdvance(semi_colon);
          }
        }
      }
    } else {
      throw new YaaError(
          ct.placeOfUse(),
          ct.content + " is not recognised in this scope scope"
      );
    }
    if (ct.kind == r_curly) {
      var new_trait = new NewClass(type_name);
      new_trait.enums = newEnums;
      new_trait.runBlocks = runBlocks;
      new_trait.methods = methods;
      new_trait.classes = newClasses;
      new_trait.vDeclarations = vDeclarations;
      new_trait.vDefinitions = vDefinitions;
      new_trait.start = start;
      new_trait.isTrait = true;
      new_trait.close = get$advance();
      new_trait.typeParams = typeParameters;
      new_trait.implementations = blocks;
      return new_trait;
    }
    while (ct.kind != r_curly) {
      if (ct.kind == kw_oka) {
        newClasses.add(parseClass());
      } else if (ct.kind == kw_interface) {
        newClasses.add(parseNewTrait());
      } else if (ct.kind == kw_mzazi) {
        blocks.add(parseParent());
      } else if (ct.kind == l_bracket) {
        var method = parseFunction();
        method.itIsWhat = MtdIsWhat.classMtd;
        methods.add(method);
      } else if (ct.kind == kw_enum) {
        var object = parseNewEnum();
        YaaParserUtils.checkEnumPropInTrait(object);
        newEnums.add(object);
      } else if (ct.kind == kw_tuli) {
        just$advance();
        var run_block = new RunBlock(stmts());
        run_block.start = start;
        run_block.close = ct;
        runBlocks.add(run_block);
      } else if (ct.kind == id) {
        var name$token = get$advance();
        var options = new HashMap<String, YaaToken>();
        if (ct.kind == id) {
          while (ct.kind == id) {
            var token = get$advance();
            options.put(token.content, token);
          }
          if (ct.kind == colon) {
            just$advance();
            if (ct.kind == equal) {
              just$advance();
              var val$def1 = new VDefinition(name$token, expression(0));
              val$def1.setStart(name$token).setClose(ct);
              val$def1.options = options;
              val$def1.itIsWhat = FieldIsWhat.clz$field;
              vDefinitions.add(val$def1);
              checkAdvance(semi_colon);
            } else {
              var type = parseObjectType();
              if (ct.kind == equal) {
                just$advance();
                var val$def2 = new VDefinition(name$token, expression(0));
                val$def2.options = options;
                val$def2.type = type;
                val$def2.setStart(name$token).setClose(ct);
                val$def2.itIsWhat = FieldIsWhat.clz$field;
                vDefinitions.add(val$def2);
                checkAdvance(semi_colon);
              } else {
                var val$dec2 = new VDeclaration(name$token, type);
                val$dec2.setStart(name$token).setClose(ct);
                val$dec2.options = options;
                val$dec2.itIsWhat = FieldIsWhat.clz$field;
                vDeclarations.add(val$dec2);
                checkAdvance(semi_colon);
              }
            }
          } else {
            throw new YaaError(
                ct.placeOfUse(),
                "Expected a proper value after field options"
            );
          }
        } else if (ct.kind == colon) {
          just$advance();
          if (ct.kind == equal) {
            just$advance();
            var val$def1 = new VDefinition(name$token, expression(0));
            val$def1.setStart(name$token).setClose(ct);
            val$def1.itIsWhat = FieldIsWhat.clz$field;
            vDefinitions.add(val$def1);
            checkAdvance(semi_colon);
          } else {
            var type = parseObjectType();
            if (ct.kind == equal) {
              just$advance();
              var val$def2 = new VDefinition(name$token, expression(0));
              val$def2.type = type;
              val$def2.setStart(name$token).setClose(ct);
              val$def2.itIsWhat = FieldIsWhat.clz$field;
              vDefinitions.add(val$def2);
              checkAdvance(semi_colon);
            } else {
              var val$dec2 = new VDeclaration(name$token, type);
              val$dec2.setStart(name$token).setClose(ct);
              val$dec2.itIsWhat = FieldIsWhat.clz$field;
              vDeclarations.add(val$dec2);
              checkAdvance(semi_colon);
            }
          }
        }
      } else {
        throw new YaaError(
            ct.placeOfUse(),
            ct.content + " is not recognised in this scope scope"
        );
      }
    }
    var new_trait = new NewClass(type_name);
    new_trait.isTrait = true;
    new_trait.enums = newEnums;
    new_trait.methods = methods;
    new_trait.runBlocks = runBlocks;
    new_trait.vDeclarations = vDeclarations;
    new_trait.vDefinitions = vDefinitions;
    new_trait.classes = newClasses;
    new_trait.start = start;
    new_trait.close = get$advance();
    new_trait.typeParams = typeParameters;
    new_trait.implementations = blocks;
    return new_trait;
  }

  private NewRecord parseRecord() {
    var start = get$advance();
    var type_name = get$advance(id);
    var typeParameters = parseTypeParameters();
    var sanitized_options = new HashMap<String, YaaToken>(2);
    while (ct.kind == id) {
      var name_token = get$advance();
      sanitized_options.put(name_token.content, name_token);
    }
    checkAdvance(l_curly);
    if (ct.kind == r_curly) {
      var new_record = new NewRecord(type_name, new ArrayList<>(0));
      new_record.options = sanitized_options;
      new_record.start = start;
      new_record.close = get$advance();
      new_record.typeParams = typeParameters;
      return new_record;
    }
    List<NewClass> newClasses = new ArrayList<>();
    List<NewEnum> newEnums = new ArrayList<>();
    List<RunBlock> runBlocks = new ArrayList<>(1);
    var inits = new ArrayList<Init>();
    List<OverBlock> blocks = new ArrayList<>();
    List<NewFun> methods = new ArrayList<>();
    List<VDefinition> vDefinitions = new ArrayList<>();
    List<VDeclaration> vDeclarations = new ArrayList<>();
    Init first_init = null;
    if (ct.kind == l_paren) {
      first_init = parseInit();
      inits.add(first_init);
    } else if (ct.kind == kw_oka) {
      newClasses.add(parseClass());
    } else if (ct.kind == kw_interface) {
      newClasses.add(parseNewTrait());
    } else if (ct.kind == kw_enum) {
      var object = parseNewEnum();
      YaaParserUtils.checkEnumPropInClz(object);
      newEnums.add(object);
    } else if (ct.kind == l_bracket) {
      var method = parseFunction();
      method.itIsWhat = MtdIsWhat.classMtd;
      methods.add(method);
    } else if (ct.kind == kw_tuli) {
      just$advance();
      var run_block = new RunBlock(stmts());
      run_block.start = start;
      run_block.close = ct;
      runBlocks.add(run_block);
    } else if (ct.kind == kw_mzazi) {
      blocks.add(parseParent());
    } else if (ct.kind == id) {
      var name$token = get$advance();
      var options = new HashMap<String, YaaToken>();
      if (ct.kind == id) {
        while (ct.kind == id) {
          var token = get$advance();
          options.put(token.content, token);
        }
        if (ct.kind == colon) {
          just$advance();
          if (ct.kind == equal) {
            just$advance();
            var val$def1 = new VDefinition(name$token, expression(0));
            val$def1.setStart(name$token).setClose(ct);
            val$def1.options = options;
            val$def1.itIsWhat = FieldIsWhat.clz$field;
            vDefinitions.add(val$def1);
            checkAdvance(semi_colon);
          } else {
            var type = parseObjectType();
            if (ct.kind == equal) {
              just$advance();
              var val$def2 = new VDefinition(name$token, expression(0));
              val$def2.options = options;
              val$def2.type = type;
              val$def2.setStart(name$token).setClose(ct);
              val$def2.itIsWhat = FieldIsWhat.clz$field;
              vDefinitions.add(val$def2);
              checkAdvance(semi_colon);
            } else {
              var val$dec2 = new VDeclaration(name$token, type);
              val$dec2.setStart(name$token).setClose(ct);
              val$dec2.options = options;
              val$dec2.itIsWhat = FieldIsWhat.clz$field;
              vDeclarations.add(val$dec2);
              checkAdvance(semi_colon);
            }
          }
        } else {
          throw new YaaError(
              ct.placeOfUse(),
              "Expected a proper value after field options"
          );
        }
      } else if (ct.kind == colon) {
        just$advance();
        if (ct.kind == equal) {
          just$advance();
          var val$def1 = new VDefinition(name$token, expression(0));
          val$def1.setStart(name$token).setClose(ct);
          val$def1.itIsWhat = FieldIsWhat.clz$field;
          vDefinitions.add(val$def1);
          checkAdvance(semi_colon);
        } else {
          var type = parseObjectType();
          if (ct.kind == equal) {
            just$advance();
            var val$def2 = new VDefinition(name$token, expression(0));
            val$def2.type = type;
            val$def2.setStart(name$token).setClose(ct);
            val$def2.itIsWhat = FieldIsWhat.clz$field;
            vDefinitions.add(val$def2);
            checkAdvance(semi_colon);
          } else {
            var val$dec2 = new VDeclaration(name$token, type);
            val$dec2.setStart(name$token).setClose(ct);
            val$dec2.itIsWhat = FieldIsWhat.clz$field;
            vDeclarations.add(val$dec2);
            checkAdvance(semi_colon);
          }
        }
      }
    } else {
      throw new YaaError(
          ct.placeOfUse(),
          ct.content + " is not recognised in this scope scope"
      );
    }
    if (ct.kind == r_curly) {
      var new_type = new NewRecord(type_name, inits);
      new_type.options = sanitized_options;
      new_type.parents = blocks;
      new_type.enums = newEnums;
      new_type.methods = methods;
      new_type.runBlocks = runBlocks;
      new_type.classes = newClasses;
      new_type.vDeclarations = vDeclarations;
      new_type.vDefinitions = vDefinitions;
      new_type.start = start;
      new_type.close = get$advance();
      new_type.typeParams = typeParameters;
      return new_type;
    }
    while (ct.kind != r_curly) {
      if (ct.kind == l_paren) {
        assert first_init != null;
        throw new YaaError(
            parseInit().placeOfUse(),
            "A record cannot have more than one initializer",
            "There is already a initializer defined " +
                "at " + first_init.placeOfUse()
        );
      } else if (ct.kind == kw_oka) {
        newClasses.add(parseClass());
      } else if (ct.kind == kw_interface) {
        newClasses.add(parseNewTrait());
      } else if (ct.kind == kw_enum) {
        var object = parseNewEnum();
        YaaParserUtils.checkEnumPropInClz(object);
        newEnums.add(object);
      } else if (ct.kind == l_bracket) {
        var method = parseFunction();
        method.itIsWhat = MtdIsWhat.classMtd;
        methods.add(method);
      } else if (ct.kind == kw_tuli) {
        just$advance();
        var run_block = new RunBlock(stmts());
        run_block.start = start;
        run_block.close = ct;
        runBlocks.add(run_block);
      } else if (ct.kind == kw_mzazi) {
        blocks.add(parseParent());
      } else if (ct.kind == id) {
        var name$token = get$advance();
        var options = new HashMap<String, YaaToken>();
        if (ct.kind == id) {
          while (ct.kind == id) {
            var token = get$advance();
            options.put(token.content, token);
          }
          if (ct.kind == colon) {
            just$advance();
            if (ct.kind == equal) {
              just$advance();
              var val$def1 = new VDefinition(name$token, expression(0));
              val$def1.setStart(name$token).setClose(ct);
              val$def1.options = options;
              val$def1.itIsWhat = FieldIsWhat.clz$field;
              vDefinitions.add(val$def1);
              checkAdvance(semi_colon);
              throw new YaaError(
                  val$def1.placeOfUse(),
                  "A record can not have field definitions"
              );
            } else {
              var type = parseObjectType();
              if (ct.kind == equal) {
                just$advance();
                var val$def2 = new VDefinition(name$token, expression(0));
                val$def2.options = options;
                val$def2.type = type;
                val$def2.setStart(name$token).setClose(ct);
                val$def2.itIsWhat = FieldIsWhat.clz$field;
                vDefinitions.add(val$def2);
                checkAdvance(semi_colon);
                throw new YaaError(
                    val$def2.placeOfUse(),
                    "A record can not have field definitions"
                );
              } else {
                var val$dec2 = new VDeclaration(name$token, type);
                val$dec2.setStart(name$token).setClose(ct);
                val$dec2.options = options;
                val$dec2.itIsWhat = FieldIsWhat.clz$field;
                vDeclarations.add(val$dec2);
                checkAdvance(semi_colon);
                throw new YaaError(
                    val$dec2.placeOfUse(),
                    "A record can not have field declarations"
                );
              }
            }
          } else {
            throw new YaaError(
                ct.placeOfUse(),
                "Expected a proper value after field options"
            );
          }
        } else if (ct.kind == colon) {
          just$advance();
          if (ct.kind == equal) {
            just$advance();
            var val$def1 = new VDefinition(name$token, expression(0));
            val$def1.setStart(name$token).setClose(ct);
            val$def1.itIsWhat = FieldIsWhat.clz$field;
            vDefinitions.add(val$def1);
            checkAdvance(semi_colon);
            throw new YaaError(
                val$def1.placeOfUse(),
                "A record can not have field definitions"
            );
          } else {
            var type = parseObjectType();
            if (ct.kind == equal) {
              just$advance();
              var val$def2 = new VDefinition(name$token, expression(0));
              val$def2.type = type;
              val$def2.setStart(name$token).setClose(ct);
              val$def2.itIsWhat = FieldIsWhat.clz$field;
              vDefinitions.add(val$def2);
              checkAdvance(semi_colon);
              throw new YaaError(
                  val$def2.placeOfUse(),
                  "A record can not have field definitions"
              );
            } else {
              var val$dec2 = new VDeclaration(name$token, type);
              val$dec2.setStart(name$token).setClose(ct);
              val$dec2.itIsWhat = FieldIsWhat.clz$field;
              vDeclarations.add(val$dec2);
              checkAdvance(semi_colon);
              throw new YaaError(
                  val$dec2.placeOfUse(),
                  "A record can not have field declarations"
              );
            }
          }
        }
      } else {
        throw new YaaError(
            ct.placeOfUse(),
            ct.content + " is not recognised in this scope scope"
        );
      }
    }
    var new_record = new NewRecord(type_name, inits);
    new_record.options = sanitized_options;
    new_record.parents = blocks;
    new_record.methods = methods;
    new_record.runBlocks = runBlocks;
    new_record.enums = newEnums;
    new_record.vDeclarations = vDeclarations;
    new_record.vDefinitions = vDefinitions;
    new_record.classes = newClasses;
    new_record.start = start;
    new_record.close = get$advance();
    new_record.typeParams = typeParameters;
    return new_record;
  }

  private NewMeta parseAnnotation() {
    var start = get$advance();
    var type_name = get$advance(id);
    var sanitized_options = new HashMap<String, YaaToken>(2);
    while (ct.kind == id) {
      var name_token = get$advance();
      sanitized_options.put(name_token.content, name_token);
    }
    checkAdvance(l_curly);
    if (ct.kind == r_curly) {
      var new_class = new NewMeta(type_name);
      new_class.options = sanitized_options;
      new_class.start = start;
      new_class.close = get$advance();
      return new_class;
    }
    List<YaaMetaCall> meta_calls = new ArrayList<>(1);
    List<VDefinition> vDefinitions = new ArrayList<>(1);
    List<VDeclaration> vDeclarations = new ArrayList<>(1);
    if (ct.kind == id) {
      var name$token = get$advance();
      if (ct.kind == colon) {
        just$advance();
        if (ct.kind == equal) {
          just$advance();
          var val$def1 = new VDefinition(name$token, expression(0));
          val$def1.setStart(name$token).setClose(ct);
          val$def1.itIsWhat = FieldIsWhat.clz$field;
          vDefinitions.add(val$def1);
          checkAdvance(semi_colon);
        } else {
          var type = parseObjectType();
          if (ct.kind == equal) {
            just$advance();
            var val$def2 = new VDefinition(name$token, expression(0));
            val$def2.type = type;
            val$def2.setStart(name$token).setClose(ct);
            val$def2.itIsWhat = FieldIsWhat.clz$field;
            vDefinitions.add(val$def2);
            checkAdvance(semi_colon);
          } else {
            var val$dec2 = new VDeclaration(name$token, type);
            val$dec2.setStart(name$token).setClose(ct);
            val$dec2.itIsWhat = FieldIsWhat.clz$field;
            vDeclarations.add(val$dec2);
            checkAdvance(semi_colon);
          }
        }
      } else {
        throw new YaaError(
            ct.placeOfUse(), ct.content + " is illegal after a name in an annotation scope"
        );
      }
    } else if (ct.kind == at) {
      meta_calls.add(parseMetaCall());
      checkAdvance(semi_colon);
    } else {
      throw new YaaError(
          ct.placeOfUse(),
          ct.content + " is an invalid token in an annotation scope"
      );
    }
    if (ct.kind == r_curly) {
      var new_type = new NewMeta(type_name);
      new_type.options = sanitized_options;
      new_type.vDeclarations = vDeclarations;
      new_type.vDefinitions = vDefinitions;
      new_type.metaCalls = meta_calls;
      new_type.start = start;
      new_type.close = get$advance();
      return new_type;
    }
    while (ct.kind != r_curly) {
      if (ct.kind == id) {
        var name$token = get$advance();
        if (ct.kind == colon) {
          just$advance();
          if (ct.kind == equal) {
            just$advance();
            var val$def1 = new VDefinition(name$token, expression(0));
            val$def1.setStart(name$token).setClose(ct);
            val$def1.itIsWhat = FieldIsWhat.clz$field;
            vDefinitions.add(val$def1);
            checkAdvance(semi_colon);
          } else {
            var type = parseObjectType();
            if (ct.kind == equal) {
              just$advance();
              var val$def2 = new VDefinition(name$token, expression(0));
              val$def2.type = type;
              val$def2.setStart(name$token).setClose(ct);
              val$def2.itIsWhat = FieldIsWhat.clz$field;
              vDefinitions.add(val$def2);
              checkAdvance(semi_colon);
            } else {
              var val$dec2 = new VDeclaration(name$token, type);
              val$dec2.setStart(name$token).setClose(ct);
              val$dec2.itIsWhat = FieldIsWhat.clz$field;
              vDeclarations.add(val$dec2);
              checkAdvance(semi_colon);
            }
          }
        } else {
          throw new YaaError(
              ct.placeOfUse(),
              ct.content + " is illegal after a name in an annotation scope"
          );
        }
      } else if (ct.kind == at) {
        meta_calls.add(parseMetaCall());
        checkAdvance(semi_colon);
      } else {
        throw new YaaError(
            ct.placeOfUse(),
            ct.content + " is an invalid token in an annotation scope"
        );
      }
    }
    var new_type = new NewMeta(type_name);
    new_type.options = sanitized_options;
    new_type.vDeclarations = vDeclarations;
    new_type.vDefinitions = vDefinitions;
    new_type.metaCalls = meta_calls;
    new_type.start = start;
    new_type.close = get$advance();
    return new_type;
  }

  private NewClass parseClass() {
    var start = get$advance();
    var new_class = new NewClass(get$advance(id));
    var typeParameters = parseTypeParameters();
    if (ct.kind == id) {
      new_class.parent = parseObjectType();
    }
    checkAdvance(l_curly);
    if (ct.kind == r_curly) {
      just$advance();
      new_class.metaCalls = new ArrayList<>(1);
      new_class.start = start;
      new_class.close = ct;
      new_class.typeParams = typeParameters;
      return new_class;
    }
    List<YaaMetaCall> annotations = new ArrayList<>();
    while (ct.kind == at) {
      annotations.add(parseMetaCall());
      checkAdvance(semi_colon);
    }
    if (ct.kind == r_curly) {
      just$advance();
      new_class.metaCalls = annotations;
      new_class.start = start;
      new_class.close = ct;
      new_class.typeParams = typeParameters;
      return new_class;
    }
    Map<String, List<NewFun>> parentMtds = new HashMap<>(2);
    List<NewClass> newClasses = new ArrayList<>();
    List<NewEnum> newEnums = new ArrayList<>();
    List<RunBlock> runBlocks = new ArrayList<>(1);
    List<OverBlock> blocks = new ArrayList<>();
    List<NewFun> methods = new ArrayList<>();
    List<NewFunctionalInterface> functionalInterfaces = new ArrayList<>();
    List<VDefinition> vDefinitions = new ArrayList<>();
    List<VDeclaration> vDeclarations = new ArrayList<>();
    if (ct.kind == kw_oka) {
      newClasses.add(parseClass());
    } else if (ct.kind == l_paren) {
      new_class.init = parseInit();
    } else if (ct.kind == kw_interface) {
      newClasses.add(parseNewTrait());
    } else if (ct.kind == star) {
      just$advance();
      var mtd_name = ct.content;
      //since this is the first we don't check to see if its already populated
      var functions = new ArrayList<NewFun>(2);
      functions.add(parseParentFunction());
      parentMtds.put(mtd_name, functions);
    } else if (ct.kind == kw_enum) {
      var object = parseNewEnum();
      YaaParserUtils.checkEnumPropInClz(object);
      newEnums.add(object);
    } else if (ct.kind == l_bracket) {
      var method = parseFunction();
      method.itIsWhat = MtdIsWhat.classMtd;
      methods.add(method);
    } else if (ct.kind == kw_tuli) {
      just$advance();
      var run_block = new RunBlock(stmts());
      run_block.start = start;
      run_block.close = ct;
      runBlocks.add(run_block);
    } else if (ct.kind == kw_mzazi) {
      blocks.add(parseParent());
    } else if (ct.kind == kw_ode) {
      var declaration = parseVariable(FieldIsWhat.clz$field);
      if (declaration instanceof VDefinition def) {
        vDefinitions.add(def);
      } else {
        vDeclarations.add((VDeclaration) declaration);
      }
    } else if (ct.kind == id) {
      var name$token = get$advance();
      if (ct.kind == l_bracket) {
        var new_functional_interface = new NewFunctionalInterface(name$token);
        new_functional_interface.typeParams = parseTypeParameters();
        new_functional_interface.parameters = parseParameters();
        if (ct.kind != semi_colon) {
          new_functional_interface.type = parseObjectType();
        }
        checkAdvance(semi_colon, "a functional interface must be delimited by a semi colon");
        new_functional_interface.start = name$token;
        new_functional_interface.scope = ScopeKind.INGLOBAL;
        new_functional_interface.close = ct;
        functionalInterfaces.add(new_functional_interface);
      } else {
        throw new YaaError(
            ct.placeOfUse(),
            ct.content + " is illegal after a name in a class scope"
        );
      }
    } else {
      throw new YaaError(
          ct.placeOfUse(),
          ct.content + " is not recognised in a class scope"
      );
    }
    if (ct.kind == r_curly) {
      new_class.implementations = blocks;
      new_class.enums = newEnums;
      new_class.methods = methods;
      new_class.runBlocks = runBlocks;
      new_class.classes = newClasses;
      new_class.fInterfaces = functionalInterfaces;
      new_class.vDeclarations = vDeclarations;
      new_class.vDefinitions = vDefinitions;
      new_class.start = start;
      new_class.metaCalls = annotations;
      new_class.parentMtds = parentMtds;
      new_class.close = get$advance();
      new_class.typeParams = typeParameters;
      return new_class;
    }
    while (ct.kind != r_curly) {
      if (ct.kind == kw_oka) {
        newClasses.add(parseClass());
      } else if (ct.kind == l_paren) {
        if (new_class.init != null) {
          throw new YaaError(ct.placeOfUse(),
              "The initializer is already at " + new_class.init.placeOfUse()
          );
        }
        new_class.init = parseInit();
      } else if (ct.kind == star) {
        just$advance();
        var mtd_name = ct.content;
        //since this is the first we don't check to see if its already populated
        var functions = parentMtds.get(mtd_name);
        if (functions == null) {
          functions = new ArrayList<>(2);
          functions.add(parseParentFunction());
        } else {
          functions.add(parseParentFunction());
          parentMtds.put(mtd_name, functions);
        }
      } else if (ct.kind == kw_interface) {
        newClasses.add(parseNewTrait());
      } else if (ct.kind == kw_enum) {
        var object = parseNewEnum();
        YaaParserUtils.checkEnumPropInClz(object);
        newEnums.add(object);
      } else if (ct.kind == l_bracket) {
        var method = parseFunction();
        method.itIsWhat = MtdIsWhat.classMtd;
        methods.add(method);
      } else if (ct.kind == kw_tuli) {
        just$advance();
        var run_block = new RunBlock(stmts());
        run_block.start = start;
        run_block.close = ct;
        runBlocks.add(run_block);
      } else if (ct.kind == kw_mzazi) {
        blocks.add(parseParent());
      } else if (ct.kind == kw_ode) {
        var declaration = parseVariable(FieldIsWhat.clz$field);
        if (declaration instanceof VDefinition def) {
          vDefinitions.add(def);
        } else {
          vDeclarations.add((VDeclaration) declaration);
        }
      } else if (ct.kind == id) {
        var name$token = get$advance();
        if (ct.kind == l_bracket) {
          var new_functional_interface = new NewFunctionalInterface(name$token);
          new_functional_interface.typeParams = parseTypeParameters();
          new_functional_interface.parameters = parseParameters();
          if (ct.kind != semi_colon) {
            new_functional_interface.type = parseObjectType();
          }
          checkAdvance(semi_colon, "a functional interface must be delimited by a semi colon");
          new_functional_interface.start = name$token;
          new_functional_interface.scope = ScopeKind.INGLOBAL;
          new_functional_interface.close = ct;
          functionalInterfaces.add(new_functional_interface);
        } else {
          throw new YaaError(
              ct.placeOfUse(),
              ct.content + " is illegal after a name in a class scope"
          );
        }
      } else {
        throw new YaaError(ct.placeOfUse(), ct.content + " is not recognised in a class scope");
      }
    }
    new_class.implementations = blocks;
    new_class.methods = methods;
    new_class.parentMtds = parentMtds;
    new_class.runBlocks = runBlocks;
    new_class.enums = newEnums;
    new_class.fInterfaces = functionalInterfaces;
    new_class.vDeclarations = vDeclarations;
    new_class.vDefinitions = vDefinitions;
    new_class.classes = newClasses;
    new_class.start = start;
    new_class.metaCalls = annotations;
    new_class.close = get$advance();
    new_class.typeParams = typeParameters;
    return new_class;
  }

  private YaaMetaCall parseMetaCall() {
    var start = ct;
    just$advance();//@
    var name = get$advance(id);
    var meta_call = new YaaMetaCall();
    meta_call.start = start;
    meta_call.close = ct;
    meta_call.name = name;
    meta_call.arguments = parseMetaArguments();
    return meta_call;
  }

  private Init parseInit() {
    var start = ct;
    var parameters = initParameters();
    var meta_calls = new ArrayList<YaaMetaCall>(1);
    while (ct.kind == at) {
      meta_calls.add(parseMetaCall());
    }
    var init = new Init();
    if (ct.kind == l_bracket) {
      var parentStart = ct;
      var parentCall = new ParentCall(parentArgs());
      parentCall.start = parentStart;
      parentCall.close = ct;
      init.parentCall = parentCall;
    }
    init.parameters = parameters;
    init.stmt = parseStmt();
    init.metaCalls = meta_calls;
    init.close = ct;
    init.start = start;
    return init;
  }

  private List<Stmt> parentArgs() {
    just$advance();
    if (ct.kind == r_bracket) {
      just$advance();
      return new ArrayList<>(1);
    }
    var arguments = new ArrayList<Stmt>();
    arguments.add(expression(0));
    if (ct.kind == r_bracket) {
      just$advance();
      return arguments;
    }
    while (ct.kind != r_bracket) {
      checkAdvance(comma);
      arguments.add(expression(0));
    }
    just$advance();
    return arguments;
  }

  private List<Parameter> initParameters() {
    just$advance();
    if (ct.kind == r_paren) {
      just$advance();
      return List.of();
    }
    var parameters = new ArrayList<Parameter>(5);
    parameters.add(parseParameter());
    if (ct.kind == r_paren) {
      just$advance();
      return parameters;
    }
    while (ct.kind != r_paren) {
      checkAdvance(comma);
      parameters.add(parseParameter());
    }
    just$advance();
    return parameters;
  }

  private OverBlock parseParent() {
    var start = get$advance();
    var type = parseObjectType();
    checkAdvance(l_curly);
    if (ct.kind == r_curly) {
      just$advance();
      var pb = new OverBlock(type, new HashMap<>(0));
      pb.start = start;
      pb.close = ct;
      return pb;
    }
    var methods = new HashMap<String, List<NewFun>>();
    var o = parseParentFunction();
    var mtds = new ArrayList<NewFun>();
    mtds.add(o);
    methods.put(o.name.content, mtds);
    if (ct.kind == r_curly) {
      just$advance();
      var pb = new OverBlock(type, methods);
      pb.start = start;
      pb.close = ct;
      return pb;
    }
    while (ct.kind != r_curly) {
      var parsedMtd = parseParentFunction();
      var mtdName = parsedMtd.name;
      var defined = methods.get(mtdName.content);
      if (defined != null) {
        defined.add(parsedMtd);
      } else {
        var newMtds = new ArrayList<NewFun>();
        newMtds.add(parsedMtd);
        methods.put(mtdName.content, newMtds);
      }
    }
    just$advance();
    var pb = new OverBlock(type, methods);
    pb.start = start;
    pb.close = ct;
    return pb;
  }

  private List<ObjectType> parseTypeArguments() {
    var type_arguments = new ArrayList<ObjectType>(1);
    just$advance();
    type_arguments.add(parseObjectType());
    if (ct.kind == r_bracket) {
      just$advance();
      return type_arguments;
    }
    while (ct.kind != r_bracket) {
      checkAdvance(comma, ct.placeOfUse(), "A comma is the accepted delimiter for type arguments");
      type_arguments.add(parseObjectType());
    }
    just$advance();
    return type_arguments;
  }

  private List<YaaToken> parseAnonParams(YaaToken first) {
    checkAdvance(comma);
    var parameters = new ArrayList<YaaToken>(3);
    parameters.add(first);
    parameters.add(get$advance(id));
    if (ct.kind == r_curly) {
      return parameters;
    }
    while (ct.kind != r_curly) {
      checkAdvance(comma);
      parameters.add(get$advance(id));
    }
    return parameters;
  }

  private List<Parameter> parseParameters() {
    if (ct.kind == l_paren) {
      just$advance();
      if (ct.kind == r_paren) {
        throw new YaaError(
            ct.placeOfUse(), "a Yaa parameter block must have at least one parameter"
        );
      }
      var parameters = new ArrayList<Parameter>(5);
      parameters.add(parseParameter());
      if (ct.kind == r_paren) {
        just$advance();
        return parameters;
      }
      while (ct.kind != r_paren) {
        checkAdvance(comma);
        parameters.add(parseParameter());
      }
      just$advance();
      return parameters;
    }
    return List.of();
  }

  private Parameter parseParameter() {
    var param$name = get$advance(id, "Expected the name of a parameter");
    var param = new Parameter();
    param.name = param$name;
    param.type = parseObjectType();
    var options = new ArrayList<YaaToken>();
    while (ct.kind == id) {
      options.add(get$advance());
    }
    param.options = options;
    param.start = param$name;
    param.close = ct;
    return param;
  }

  private Import parseImport(YaaToken first$token) {
    var tokens = new ArrayList<YaaToken>(5);
    tokens.add(first$token);
    while (ct.kind == id || ct.kind == dot) {
      checkAdvance(dot);
      tokens.add(get$advance(id));
    }

    if (ct.kind == colon) {
      if (tokens.size() < 2) {
        throw new YaaError(
            first$token.placeOfUse(),
            "The import statement is not well formed"
        );
      }
      just$advance();
      var aliasedImport = new Import(tokens, get$advance(id));
      aliasedImport.start = first$token;
      aliasedImport.close = ct;
      checkAdvance(semi_colon);
      return aliasedImport;
    }
    if (tokens.size() < 2) {
      throw new YaaError(
          first$token.placeOfUse(),
          "The import statement is not well formed"
      );
    }
    var imp = new Import(tokens);
    imp.start = first$token;
    imp.close = ct;
    checkAdvance(semi_colon);
    return imp;
  }

  private Stmt expression(int precedence) {
    var start = ct;
    var e = literal();
    e.start = start;
    while (pre(ct) > precedence) {
      var rightOp = get$advance();
      switch (rightOp.kind) {
        case dot -> {
          var nameToken = get$advance(id);
          List<ObjectType> typeArguments = new ArrayList<>(1);
          if (ct.kind == l_bracket) {
            typeArguments = parseTypeArguments();
          }
          if (ct.kind == l_paren) {
            e = new EMtd(e, nameToken, typeArguments, parseArguments());
          } else {
            e = new EGet(e, nameToken);
          }
          e.start = start;
          e.close = ct;
        }
        case l_paren -> {
          e = new ECall(e, parseArgumentsAfterLParen());
          e.close = ct;
        }
        case kw_na -> {
          e = new Is(e, rightOp, parseObjectType());
          e.close = ct;
          e.start = start;
        }
        case equal_arrow -> {
          e = new Cast(e, rightOp, parseObjectType());
          e.close = ct;
          e.start = start;
        }
        case l_than -> {
          e = new LThan(e, rightOp, expression(pre(rightOp)));
          e.close = ct;
        }
        case l_equal -> {
          e = new LEqual(e, rightOp, expression(pre(rightOp)));
          e.close = ct;
        }
        case g_than -> {
          e = new GThan(e, rightOp, expression(pre(rightOp)));
          e.close = ct;
        }
        case g_equal -> {
          e = new GEqual(e, rightOp, expression(pre(rightOp)));
          e.close = ct;
        }
        case caret -> {
          e = new Xor(e, rightOp, expression(pre(rightOp)));
          e.close = ct;
        }
        case l_shift -> {
          e = new LShift(e, rightOp, expression(pre(rightOp)));
          e.close = ct;
        }
        case r_shift -> {
          e = new RShift(e, rightOp, expression(pre(rightOp)));
          e.close = ct;
        }
        case u_r_shift -> {
          e = new URShift(e, rightOp, expression(pre(rightOp)));
          e.close = ct;
        }
        case equal_equal -> {
          e = new EEqual(e, rightOp, expression(pre(rightOp)));
          e.close = ct;
        }
        case m_equal -> {
          e = new MEqual(e, rightOp, expression(pre(rightOp)));
          e.close = ct;
        }
        case m_not_equal -> {
          e = new MNEqual(e, rightOp, expression(pre(rightOp)));
          e.close = ct;
        }
        case not_equal -> {
          e = new NEqual(e, rightOp, expression(pre(rightOp)));
          e.close = ct;
        }
        case pipe -> {
          e = new BitOr(e, rightOp, expression(pre(rightOp)));
          e.close = ct;
        }
        case ampersand -> {
          e = new BitAnd(e, rightOp, expression(pre(rightOp)));
          e.close = ct;
        }
        case d_pipe -> {
          e = new Or(e, rightOp, expression(pre(rightOp)));
          e.close = ct;
        }
        case d_ampersand -> {
          e = new And(e, rightOp, expression(pre(rightOp)));
          e.close = ct;
        }
        case plus -> {
          e = new Plus(e, rightOp, expression(pre(rightOp)));
          e.close = ct;
        }
        case minus -> {
          e = new Minus(e, rightOp, expression(pre(rightOp)));
          e.close = ct;
        }
        case modulo -> {
          e = new Modulo(e, rightOp, expression(pre(rightOp)));
          e.close = ct;
        }
        case f_slash -> {
          e = new Divide(e, rightOp, expression(pre(rightOp)));
          e.close = ct;
        }
        case star -> {
          e = new Times(e, rightOp, expression(pre(rightOp)));
          e.close = ct;
        }
        case star_star -> {
          e = new Power(e, rightOp, expression(pre(rightOp) - 1));
          e.close = ct;
        }
        case df_slash -> {
          e = new RootTo(e, rightOp, expression(pre(rightOp) - 1));
          e.close = ct;
        }
        default -> {
          throw new YaaError(
              rightOp.placeOfUse(),
              "\"" + rightOp.content + "\" is not a recognised binary operator"
          );
        }
      }
    }
    return e;
  }

  private Stmt completeLiteral(Stmt parsed) {
    if (ct.kind == dot) {
      just$advance();
      var nameToken = get$advance(id);
      List<ObjectType> typeArguments = new ArrayList<>(1);
      if (ct.kind == l_bracket) {
        typeArguments = parseTypeArguments();
      }
      if (ct.kind == l_paren) {
        parsed = new EMtd(parsed, nameToken, typeArguments, parseArguments());
      } else {
        parsed = new EGet(parsed, nameToken);
      }
    } else if (ct.kind == l_bracket) {
      parsed = new ECall(parsed, parseTypeArguments(), parseArguments());
    }
    parsed.close = ct;
    return parsed;
  }

  private boolean ctCanStartExp() {
    return switch (ct.kind) {
      case l_paren,
          kw_mzazi,
          double_literal,
          int_literal,
          float_literal,
          short_literal,
          byte_literal,
          long_literal,
          kw_hii,
          basex,
          not,
          wavy,
          plus,
          minus,
          b_tick,
          s_quote,
          id -> true;
      default -> false;
    };
  }

  private Stmt literal() {
    var start = ct;
    switch (ct.kind) {
      case l_paren -> {
        just$advance();
        if (ct.kind == r_paren) {
          //zero parameter anonymous
          just$advance();
          var anonymous = new Anonymous(new ArrayList<>(0));
          anonymous.start = start;
          anonymous.close = ct;
          anonymous.stmt = parseStmt();
          currentStmts.peek().add(anonymous);
          return anonymous;
        }
        var exp = expression(0);
        if (exp instanceof Name name) {
          if (ct.kind == r_paren) {
            just$advance();
            if (ct.kind == l_curly) {
              var anon_params = new ArrayList<YaaToken>(1);
              anon_params.add(name.token);
              var anonymous = new Anonymous(anon_params);
              anonymous.start = start;
              anonymous.close = ct;
              anonymous.stmt = parseStmt();
              currentStmts.peek().add(anonymous);
              return anonymous;
            } else {
              var group = new Group(exp);
              group.start = start;
              group.close = ct;
              checkAdvance(r_paren);
              return completeLiteral(group);
            }
          }
          //meaning the anonymous has more than one parameter.
          var anon_params = parseAnonParams(name.token);
          var anonymous = new Anonymous(anon_params);
          anonymous.start = start;
          anonymous.close = ct;
          anonymous.stmt = parseStmt();
          currentStmts.peek().add(anonymous);
          return anonymous;
        } else {
          var group = new Group(exp);
          group.start = start;
          group.close = ct;
          checkAdvance(r_paren);
          return completeLiteral(group);
        }
      }
      case kw_idan -> {
        just$advance();//if
        var e = expression(0);
        checkAdvance(q_mark);
        var l = expression(0);
        checkAdvance(colon);
        var r = expression(0);
        var ternary = new Ternary(e, l, r);
        ternary.start = start;
        ternary.close = ct;
        return ternary;
      }
      case kw_naso -> {
        just$advance();
        return completeLiteral(new True());
      }
      case kw_nalie -> {
        just$advance();
        return completeLiteral(new False());
      }
      case kw_hii -> {
        just$advance();
        if (ct.kind == dot) {
          just$advance();
          var name = get$advance(id);
          if (ct.kind == l_paren) {
            var arguments = parseArguments();
            var this_mtd = new ThisMtd();
            this_mtd.name = name;
            this_mtd.arguments = arguments;
            this_mtd.start = start;
            this_mtd.close = ct;
            return completeLiteral(this_mtd);
          }
          var this_field = new ThisField();
          this_field.name = name;
          this_field.start = start;
          this_field.close = ct;
          return completeLiteral(this_field);
        }
        var $this = new This();
        $this.start = start;
        $this.close = ct;
        return completeLiteral($this);
      }
      case kw_mzazi -> {
        just$advance();
        if (ct.kind == dot) {
          just$advance();
          var name = get$advance(id);
          if (ct.kind == l_paren) {
            var arguments = parseArguments();
            var super_mtd = new SuperMtd();
            super_mtd.name = name;
            super_mtd.arguments = arguments;
            super_mtd.start = start;
            super_mtd.close = ct;
            return completeLiteral(super_mtd);
          }
          var super_field = new SuperField();
          super_field.name = name;
          super_field.start = start;
          super_field.close = ct;
          return completeLiteral(super_field);
        }
        throw new YaaError(
            start.placeOfUse(), "Dangling reference of super type is illegal"
        );
      }
      case int_literal -> {
        try {
          var needed = ct.content;
          parseInt(needed);
          ct.neededContent = needed;
        } catch (NumberFormatException e) {
          throw new YaaError(
              ct.placeOfUse(), "An integer value " +
              "must be between " + Integer.MIN_VALUE
              + " and " + Integer.MAX_VALUE
          );
        }
        return completeLiteral(new Decimal(get$advance()));
      }
      case double_literal -> {
        try {
          var content = ct.content;
          var needed = content.substring(0, content.length() - 1);
          parseDouble(needed);
          ct.neededContent = needed;
        } catch (NumberFormatException e) {
          throw new YaaError(
              ct.placeOfUse(), "A double value " +
              "must be between " + Double.MIN_VALUE
              + " and " + Double.MAX_VALUE
          );
        }
        return completeLiteral(new Pointed(get$advance()));
      }
      case float_literal -> {
        try {
          var content = ct.content;
          var needed = content.substring(0, content.length() - 1);
          parseFloat(needed);
          ct.neededContent = needed;
        } catch (NumberFormatException e) {
          throw new YaaError(
              ct.placeOfUse(), "A float value " +
              "must be between " + Float.MIN_VALUE
              + " and " + Float.MAX_VALUE
          );
        }
        return completeLiteral(new Floated(get$advance()));
      }
      case short_literal -> {
        try {
          var content = ct.content;
          var needed = content.substring(0, content.length() - 1);
          parseShort(needed);
          ct.neededContent = needed;
        } catch (NumberFormatException e) {
          throw new YaaError(
              ct.placeOfUse(), "A short value " +
              "must be between " + Short.MIN_VALUE
              + " and " + Short.MAX_VALUE
          );
        }
        return completeLiteral(new Shorted(get$advance()));
      }
      case byte_literal -> {
        try {
          var content = ct.content;
          var needed = content.substring(0, content.length() - 1);
          parseByte(needed);
          ct.neededContent = needed;
        } catch (NumberFormatException e) {
          throw new YaaError(
              ct.placeOfUse(), "A byte value " +
              "must be between " + Byte.MIN_VALUE
              + " and " + Byte.MAX_VALUE
          );
        }
        return completeLiteral(new Byted(get$advance()));
      }
      case long_literal -> {
        try {
          var content = ct.content;
          var needed = content.substring(0, content.length() - 1);
          parseLong(needed);
          ct.neededContent = needed;
        } catch (NumberFormatException e) {
          throw new YaaError(
              ct.placeOfUse(), "A long value " +
              "must be between " + Long.MIN_VALUE
              + " and " + Long.MAX_VALUE
          );
        }
        return completeLiteral(new Longed(get$advance()));
      }
      case basex -> {
        var x_token = (BasexToken) get$advance();
        var basex = new Basex(x_token);
        basex.start = x_token;
        basex.close = ct;
        return completeLiteral(basex);
      }
      case id -> {
        var nameToken = get$advance();
        if (ct.kind == l_bracket) {
          var types = parseTypeArguments();
          var name$call = new VCall(nameToken);
          while (ct.kind == at) {
            name$call.metaCalls.add(parseMetaCall());
          }
          var arguments = parseArguments();
          name$call.types = types;
          name$call.arguments = arguments;
          return completeLiteral(name$call.setClose(ct).setStart(start));
        } else if (ct.kind == at) {
          var name$call = new VCall(nameToken);
          while (ct.kind == at) {
            name$call.metaCalls.add(parseMetaCall());
          }
          name$call.arguments = parseArguments();
          return completeLiteral(name$call.setClose(ct).setStart(start));
        } else if (ct.kind == l_paren) {
          var name$call = new VCall(nameToken, parseArguments());
          return completeLiteral(name$call.setClose(ct).setStart(start));
        } else if (ct.kind == dot) {
          just$advance();
          var mName = get$advance(id);
          List<ObjectType> mtd_type_arguments = new ArrayList<>();
          if (ct.kind == l_bracket) {
            mtd_type_arguments = parseTypeArguments();
          }
          if (ct.kind == l_paren) {
            just$advance();
            if (ct.kind == r_paren) {
              just$advance();
              var close = ct;
              var vMtd = new VMtd(
                  nameToken, mName, mtd_type_arguments, new ArrayList<>(0)
              );
              vMtd.close = close;
              vMtd.start = start;
              return completeLiteral(vMtd);
            }
            var expressions = new ArrayList<Stmt>();
            expressions.add(expression(0));
            if (ct.kind == r_paren) {
              just$advance();
              var close = ct;
              var vMtd =
                  new VMtd(nameToken, mName, mtd_type_arguments, expressions);
              vMtd.close = close;
              vMtd.start = start;
              return completeLiteral(vMtd);
            }
            while (ct.kind != r_paren) {
              just$advance();
              expressions.add(expression(0));
            }
            just$advance();
            var close = ct;
            var vMtd =
                new VMtd(nameToken, mName, mtd_type_arguments, expressions);
            vMtd.close = close;
            vMtd.start = start;
            return completeLiteral(vMtd);
          } else {
            var vGet = new VGet(nameToken, mName);
            vGet.start = start;
            vGet.close = ct;
            return completeLiteral(vGet);
          }
        }
        var name = new Name(nameToken);
        name.start = start;
        name.close = ct;
        return completeLiteral(name);
      }
      case not -> {
        var negate = new UNot(get$advance(), literal());
        negate.start = start;
        negate.close = ct;
        return completeLiteral(negate);
      }
      case wavy -> {
        var bit$negate = new BitNot(get$advance(), literal());
        bit$negate.start = start;
        bit$negate.close = ct;
        return completeLiteral(bit$negate);
      }
      case plus -> {
        var unary$plus = new UPlus(get$advance(), literal());
        unary$plus.start = start;
        unary$plus.close = ct;
        return completeLiteral(unary$plus);
      }
      case minus -> {
        var unary$minus = new UMinus(get$advance(), literal());
        unary$minus.start = start;
        unary$minus.close = ct;
        return completeLiteral(unary$minus);
      }
      case b_tick -> {
        var stringStart = ct;
        var string = new AstString();
        var contents = new ArrayList<>();
        lexer.allAllowed = true;
        just$advance();
        while (ct.kind != b_tick) {
          switch (ct.kind) {
            case l_curly -> {
              lexer.allAllowed = false;
              just$advance();
              if (ct.kind != r_curly) {
                string.itIsInterpolated = true;
                contents.add(expression(0));
              }
              lexer.allAllowed = true;
              just$advance();
            }
            case escaped_hash -> contents.add(new HashSign(get$advance()));
            case escaped_newline -> contents.add(new NewLine(get$advance()));
            //case new_line -> just$advance();
            case escaped_s_quote -> contents.add(new SQuote(get$advance()));
            case escaped_b_slash -> contents.add(new BSlash(get$advance()));
            case escaped_b_tick -> contents.add(new BTick(get$advance()));
            case escaped_l_curly -> contents.add(new LCurly(get$advance()));
            case unicode -> contents.add(new UniKode(get$advance()));
            case eof -> {
              if (string.itIsInterpolated) {
                throw new YaaError(
                    ct.placeOfUse(), "The string is not properly terminated"
                );
              } else {
                throw new YaaError(
                    ct.placeOfUse(), "The string at ["
                    + stringStart.placeOfUse() + "] must be closed"
                );
              }
            }
            default -> {
              contents.add(get$advance().content);
            }
          }
        }
        lexer.allAllowed = false;
        just$advance();
        string.start = start;
        string.close = ct;
        string.content = contents;
        return completeLiteral(string);
      }
      case s_quote -> {
        lexer.allAllowed = true;
        just$advance();
        if (ct.kind == s_quote) {
          throw new YaaError(ct.placeOfUse(),
              "A character must have one element to be valid"
          );
        }
        var cha = new Cha();
        Object content;
        switch (ct.kind) {
          case l_curly -> {
            just$advance();
            lexer.allAllowed = false;
            content = expression(0);
            cha.itIsInterpolated = true;
            lexer.allAllowed = true;
            just$advance();
          }
          case escaped_hash -> {
            content = new Cha(new HashSign(get$advance()));
            cha.char$content = '#';
          }
          case escaped_newline -> {
            content = new Cha(new NewLine(get$advance()));
            cha.char$content = '\n';
          }
          case escaped_s_quote -> {
            content = new Cha(new SQuote(get$advance()));
            cha.char$content = '\'';
          }
          case escaped_b_tick -> {
            content = new Cha(new BTick(get$advance()));
            cha.char$content = '"';
          }
          case escaped_b_slash -> {
            content = new Cha(new BSlash(get$advance()));
            cha.char$content = '\\';
          }
          case escaped_l_curly -> {
            content = new Cha(new LCurly(get$advance()));
            cha.char$content = '{';
          }
          case eof -> {
            throw new YaaError(
                ct.line + ": " + ct.column, "The character at ["
                + start.line + ": " + start.column + "] must be closed"
            );
          }
          default -> {
            var ch = get$advance();
            content = new Cha(ch);
            cha.char$content = ch.content.charAt(0);
          }
        }
        lexer.allAllowed = false;
        just$advance();
        cha.start = start;
        cha.close = ct;
        cha.content = content;
        return completeLiteral(cha);
      }
    }
    throw new YaaError(
        ct.placeOfUse(), "\"" + ct.content
        + "\" is not a valid starter for an expression"
    );
  }

  private YaaToken get$advance(TkKind expected) {
    if (ct.kind != expected) {
      throw new YaaError(ct.placeOfUse(),
          "Expected " + tokenString(expected) + " but got "
              + tokenString(ct.kind)
      );
    }
    var oldCt = ct;
    ct = lexer.nextToken();
    return oldCt;
  }

  private YaaToken get$advance() {
    var oldCt = ct;
    ct = lexer.nextToken();
    return oldCt;
  }

  private YaaToken get$advance(TkKind expected, String... errorMessages) {
    if (ct.kind != expected) {
      var length = errorMessages.length + 2;
      var messages = new String[length];
      messages[0] = ct.placeOfUse();
      arraycopy(errorMessages, 0, messages, 1, errorMessages.length);
      messages[length - 1] = "Expected " + tokenString(expected)
          + " but got " + tokenString(ct.kind);
      throw new YaaError(messages);
    }
    var oldCt = ct;
    ct = lexer.nextToken();
    return oldCt;
  }

  private void just$advance() {
    ct = lexer.nextToken();
  }

  private void checkAdvance(TkKind expected) {
    if (ct.kind != expected) {
      throw new YaaError(ct.placeOfUse(),
          "Expected " + tokenString(expected) + " but got "
              + tokenString(ct.kind)
      );
    }
    ct = lexer.nextToken();
  }

  private void checkAdvance(TkKind expected, String... errorMessages) {
    if (ct.kind != expected) {
      var length = errorMessages.length + 2;
      var messages = new String[length];
      messages[0] = ct.placeOfUse();
      arraycopy(errorMessages, 0, messages, 1, errorMessages.length);
      messages[length - 1] = "Expected " + tokenString(expected)
          + " but got " + tokenString(ct.kind);
      throw new YaaError(messages);
    }
    ct = lexer.nextToken();
  }

  private List<Stmt> parseArgumentsAfterLParen() {
    if (ct.kind == r_paren) {
      just$advance();
      return new ArrayList<>(1);
    }
    var arguments = new ArrayList<Stmt>();
    arguments.add(expression(0));
    if (ct.kind == r_paren) {
      just$advance();
      return arguments;
    }
    while (ct.kind != r_paren) {
      checkAdvance(comma);
      arguments.add(expression(0));
    }
    just$advance();
    return arguments;
  }

  private Map<YaaToken, Stmt> parseMetaArguments() {
    checkAdvance(l_paren);
    if (ct.kind == r_paren) {
      just$advance();
      return new HashMap<>(1);
    }
    var arg_name = get$advance(id);
    var arguments = new HashMap<YaaToken, Stmt>();
    checkAdvance(equal);
    arguments.put(arg_name, expression(0));
    if (ct.kind == r_paren) {
      just$advance();
      return arguments;
    }
    while (ct.kind != r_paren) {
      checkAdvance(comma);
      var arg_name1 = get$advance(id);
      checkAdvance(equal);
      arguments.put(arg_name1, expression(0));
    }
    just$advance();
    return arguments;
  }

  private List<Stmt> parseArguments() {
    checkAdvance(l_paren);
    if (ct.kind == r_paren) {
      just$advance();
      return new ArrayList<>(1);
    }
    var arguments = new ArrayList<Stmt>();
    arguments.add(expression(0));
    if (ct.kind == r_paren) {
      just$advance();
      return arguments;
    }
    while (ct.kind != r_paren) {
      checkAdvance(comma);
      arguments.add(expression(0));
    }
    just$advance();
    return arguments;
  }

  private ObjectType parseObjectType() {
    var start = ct;
    var type_name = get$advance(id, "expected the name of the type, but got " + tokenString(ct.kind));
    if (ct.kind == l_bracket) {
      just$advance();
      if (ct.kind == r_bracket) {
        //for types like ArrayList[]
        just$advance();
        var type = new ObjectType(type_name, new ArrayList<>(0));
        type.close = ct;
        type.start = start;
        while (ct.kind == at) {
          type.metaCalls.add(parseMetaCall());
        }
        return type;
      }

      var type_arguments = new ArrayList<ObjectType>(2);
      var first_type_arg = parseObjectType();
      type_arguments.add(first_type_arg);
      if (ct.kind == r_bracket) {
        just$advance();
        var type_with_one_arg = new ObjectType(type_name, type_arguments);
        type_with_one_arg.close = ct;
        type_with_one_arg.start = start;
        while (ct.kind == at) {
          type_with_one_arg.metaCalls.add(parseMetaCall());
        }
        type_with_one_arg.hasInternalMeta = first_type_arg.metaCalls.size() > 0;
        return type_with_one_arg;
      }
      var any_other_type_arg_has_meta = false;
      while (ct.kind != r_bracket) {
        checkAdvance(comma);
        var subsequent_type_arg = parseObjectType();
        if (subsequent_type_arg.metaCalls.size() > 0) {
          any_other_type_arg_has_meta = true;
        }
        type_arguments.add(subsequent_type_arg);
      }
      just$advance();
      var type_with_multi_args = new ObjectType(type_name, type_arguments);
      type_with_multi_args.close = ct;
      type_with_multi_args.start = start;
      type_with_multi_args.hasInternalMeta = any_other_type_arg_has_meta;
      while (ct.kind == at) {
        type_with_multi_args.metaCalls.add(parseMetaCall());
      }
      return type_with_multi_args;
    }

    var non_generic_type = new ObjectType(type_name);
    non_generic_type.close = ct;
    non_generic_type.start = start;
    while (ct.kind == at) {
      non_generic_type.metaCalls.add(parseMetaCall());
    }
    return non_generic_type;
  }

  private static Integer pre(YaaToken tk) {
    return switch (tk.kind) {
      case d_pipe -> 1;
      case d_ampersand -> 2;
      case pipe -> 3;
      case caret -> 4;
      case ampersand -> 5;
      case equal_equal, not_equal, m_equal, m_not_equal -> 6;
      case l_than, g_than, l_equal, g_equal -> 7;
      case l_shift, r_shift, u_r_shift -> 8;
      case plus, minus -> 9;
      case star, f_slash, modulo -> 10;
      case star_star, df_slash -> 11;
      case not -> 12;
      case l_paren, dot -> 13;
      case kw_na, equal_arrow -> 14;
      //These are for is, and cast. They must be very high.
      default -> 0;
    };
  }
}