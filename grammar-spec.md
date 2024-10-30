TODO:
- Add classes
- Add array syntax (perhaps allowing ranges)
- Add receivers?
- add structs
- add mutibility
- add option types (monads?)

```
program -> declaration* EOF ;

declaration ->  varDecl |
                funcDecl |
                stmt ;

funcDecl ->     "fn" function ;
varDecl ->      type IDENTIFIER ( "=" expression)? ";" ;
stmt ->         exprStmt |
                ifStmt |
                forStmt |
                returnStmt |
                block ;

expressionStmt -> expression ";" ;
ifStmt -> "if" "(" expression ")" statement ( "else" stmt )? ;
forStmt -> "for" "(" ( forIterExpr | rangeExpr )? ")" stmt ;
forIterExpr -> (varDecl | expression)? ";" expression? ";" expression? ;
returnStmt -> "return" expression? ";" ;
block -> "{" declaration* "}" ;

expression -> assignment ;

rangeExpr -> IDENTIFIER ":" range
range -> ( NUMBER | IDENTIFIER ) ( ".." | "..=" ) ( NUMBER | IDENTIFIER ) ( ":" IDENTIFIER | NUMBER )? ;

assignment -> ( IDENTIFIER "=" )? logic_or ;

logic_or ->     logic_and ( "||" logic_and )* ;
logic_and ->    equality ( "&&" equality )* ;
equality ->     comparison ( ( "==" | "!=" ) comparison )* ;
comparison ->   term ( ( "<" | "<=" | ">" " >=" ) term )* ;
term ->         factor ( ( "+" | "-" ) factor )* ;
factor ->       unary ( ( "/" | "*") unary )* ;
unary ->        ( "-" | "!" ) unary | primary ;
primary ->      "false" | "true" | IDENTIFIER | NUMBER | "nil" |
                STRING | "(" expression ")" ;

function -> IDENTIFIER "(" parameters? ")" block ;
parameters -> IDENTIFIER ( "," IDENTIFIER )* ;
arguments -> expression ( "," expression )* ;

NUMBER -> DIGIT+ ;
STRING -> "\"" <any character excluding "\""> "\"" ;
IDENTIFIER -> ALPHA ( ALPHA | DIGIT )* ;
ALPHA -> "a" ... "z" | "A" ... "Z" | "_" ;
DIGIT -> "0" ... "9" ;
```

```
Tokens:
NUMBER, STRING, IDENTIFIER,
LT, LEQ, GT, GEQ, EQ, NEQ,
DIVIDE, MULT, PLUS, MINUS, FSLASH, BSLASH
COMMA, SEMICOLON, COLON, DOT, PERCENT, DOLLAR, HASHTAG, AT, POWER
LPAREN, RPAREN, LBRACE, RBRACE, LBRAC, RBRAC
TRUE, FALSE, NOT, AND, OR, ASSIGN, RANGE, RANGE_INCLUSIVE,

Keywords:
RETURN, FOR, IF, FUNCTION, ELSE
```
