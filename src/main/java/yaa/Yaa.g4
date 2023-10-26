grammar Yaa;

program:
  Nl* useStmt? (topStmt (Nl+ | EOF))* eof
;

eof: EOF;

useStmt:
  (impStmt (Nl+ | EOF))+
;

impStmt:
    allImp
  | Imp+=Name ('.' Imp+=Name)+ (':' Alias=Name)?
;

allImp: Name ('.' Name)+ '*' (Nl+ (singleImp (Nl+ singleImp)*)?)?;

singleImp: Name (':' alias)?;

alias: Name;

topStmt:
  newType
| newValue
| newFun
;

typeBound:
  Name
| ConstructString
;

newField:
   ConstructString ':' type
 | ConstructString '=' value
 | ConstructString type '=' value
;

newFun:
  fnSig (
    Nl*('{' Nl* '}'
  | '{' Nl+ (stmt Nl+)+ '}')
  | <assoc='right'> '->' stmt
  )
;

enumField: Name Dec?;

newValue:
  newField
;

parameter:
  Name type
;

newType:
  ConstructString ('<' typeBound (',' typeBound)* '>')?
    (('{' '}' | '{' Nl+ '}' | '{' Nl+ ((
        newFun
      | newField
      | parent
      | contract
      | newType
      | fnSig
      | init
      | enumField
    )Nl+)+'}'))
;

parent: objType '('')' (
  '{' Nl* '}' | '{'
     Nl+ (newFun Nl+)+
  '}'
)?;

contract: objType (
  '{' Nl* '}' | '{'
     Nl+ (newFun Nl+)+
  '}'
);

init:
  '(' (initParam (',' initParam)*)? ')' ConstructString? (
    '{' Nl* '}'
  | '{' Nl+
    (stmt Nl+)*
  '}')?
;

initParam: Name type;

fnSig:
  ConstructString ('(' (parameter (',' parameter)*)? ')') type?
;

stmt:
  ifStmt      //done
| loop        //done
| catchStmt   //done 2
| newFun      //done
| newValue    //done
| newType     //done
| exp         //done
| fieldSet    //done
| assign      //done
;

assign:
  Name '=' value
;

value:
  exp
| parent
| contract
;

fieldSet:
  exp '.' Name '=' exp
;

catchStmt:
  objType Name (
    '{' Nl* '}'
  | '{' Nl+
       tries catchies finalies
    '}'
  )
;

tries: (stmt Nl+)+;
catchies: (ElseLine Nl+ (stmt Nl+)*)?;
finalies: (ElseLine Nl+ (stmt Nl+)*)?;

variable:
  ConstructString ('=' exp)?
;

loop:
  (variable '@'? exp | '@' exp) ('@' mutant)?  (
    '{' Nl* '}' | '{' Nl+ (stmt Nl+)+ '}' | '->' stmt
  )
;

mutant:
  exp
;

ifStmt:
   exp  (
    '{' Nl* '}'
  | '{' Nl+
         (stmtInIf Nl+)*
         ('----' Nl+ (elseStmt Nl+)*)?
     '}'
  )
;

elseStmt:
  stmt
;

stmtInIf:
  stmt | caseBlock
;

caseBlock:
  exp '?' ('{' Nl* '}' | '{' Nl+ (stmt Nl+)+ '}' | stmt)
;

type:
  objType
| funType
;

objType:
  Name typeArgs?
;

funType:
  '('(pt+=type (',' pt+=type)*)?')' ret=type
;

exp:
  '(' exp ')' #group                                    //done
| exp ('::' | '->') objType    #memOp                   //done
| '[' (exp (',' exp)*)? ']'      #array                 //done
| Name typeArgs? arguments #nCall                       //done
| Name #name                                            //done
| exp typeArgs? arguments #expCall                      //done
| String #string                                        //done
| Dob #pointed                                          //done
| Dec #decimal                                          //done
| Cha #cha                                              //done
| exp '[' exp ']' #index                                //done
| exp '.'? Name typeArgs? mtdArgs  #mtd                 //done
| exp '.' Name #field                                   //done
| ('+' | '-' | '!' | '~') exp #unary                    //done
| <assoc='right'> exp ('**' | '//') exp #power          //done
| exp ('*' | '/' | '%') exp #mod                        //done
| exp ('+' | '-') exp #sum                              //done
| exp ('>' | '<' | '<=' | '>=') exp #comp               //done
| exp ('==' | '!=' | '===' | '!==') exp #equal          //done
| exp ('|' | '&' | '^') exp #bits                       //done
| exp '&&' exp #and                                     //done
| exp '||' exp #or                                      //done
;

mtdArgs:
  '(' (exp (',' exp)*)? ')' | exp
;

arguments:
  '(' (exp (',' exp)*)? ')'
;

typeArgs:
  '<' type (',' type)* '>'
;

String:
  '"' .*? '"'
;

Cha:
  '"' . '"' 'c'
;

HeapCha:
  '"' . '"' 'C'
;

WS:        [ \t\r\u000C]+        -> skip;
M_COMMENT: '#' .*?  ('#' | EOF)  -> skip;
D_COMMENT: '#*' .*? ('*#'| EOF)  -> skip;
Nl: '\r'? '\n';
Comma: ',';
Colon: ':';
DColon: '::';
LParen: '(';
RParen: ')';
DStar: '**';
DFSlash: '//';
LCurly: '{';
RCurly: '}';
LBrack: '[';
RBrack: ']';
Equal: '=';
Star: '*';
LThan: '<';
GThan: '>';
LEqual: '<=';
EEqual: '==';
NEQual: '!=';
GEqual: '>=';
QMark: '?';
ElseLine: '----';
FSlash: '/';
Arrow: '->';
Modulo: '%';
MemEqual: '===';
MemNotEqual: '!==';
Caret: '^';
Minus: '-';
Amp: '&';
Pipe: '|';
Plus: '+';
Tilde: '~';
AtThis: '@this';
UnderScore: '_';
Dot: '.';
Not: '!';

Dob:
  [0-9]+ (',' [0-9]+)*
  '.' [0-9]+ (',' [0-9]+)*
  (('-' | '+') [0-9]+)?  [a-zA-Z]?
;

Dec:
  [0-9]+ (',' [0-9]+)*
  (('-' | '+') [0-9]+)?  [a-zA-Z]?
;

ConstructString:
  '\'' .*? '\''
;

Name:
  Letter LetterOrDigit*
;

fragment LetterOrDigit:
  Letter | [0-9]
;

fragment Letter:
  [a-zA-Z$_]
| ~[\u0000-\u007F\uD800-\uDBFF]
| [\uD800-\uDBFF] [\uDC00-\uDFFF]
;