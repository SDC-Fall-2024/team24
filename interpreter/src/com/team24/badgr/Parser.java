package com.team24.badgr;
import static com.team24.badgr.TokenType.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.team24.badgr.Statement.Expr;

import java.lang.Thread.State;
import java.util.ArrayList;
import java.util.Arrays;

class ParseException extends RuntimeException {
  public ParseException(String message) {
    super(message);
  }
}

interface PrefixParselet {
  Expression parse(Parser parser, Token token);
}

interface InfixParselet {
  Expression parse(Parser parser, Expression left, Token token);
  int getPrecedence();
}

class Parser {
  public List<Token> tokens;
  private int current = 0;

  private final Map<TokenType, PrefixParselet> mPrefixParselets =
      new HashMap<TokenType, PrefixParselet>();

  private final Map<TokenType, InfixParselet> mInfixParselets = 
      new HashMap<TokenType, InfixParselet>();

  private void setup() {

    Variable.varTypes.put("int", 0);

    registerPrefix(IDENTIFIER, new VarParselet());
    registerPrefix(NUMBER, new NumberParselet());
    registerPrefix(STRING, new NameParselet());

    registerPrefix(FALSE, new NameParselet());
    registerPrefix(TRUE, new NameParselet());
    registerPrefix(NIL, new NameParselet());
    
    registerPrefix(LPAREN, new GroupParselet());
    prefix(PLUS);
    prefix(MINUS);
    prefix(NOT);

    infix(PLUS, Precedence.SUM);
    infix(MINUS, Precedence.SUM);
    infix(STAR, Precedence.PRODUCT);
    infix(FSLASH, Precedence.PRODUCT);
    infix(LT, Precedence.CONDITIONAL);
    infix(GT, Precedence.CONDITIONAL);
    infix(LEQ, Precedence.CONDITIONAL);
    infix(GEQ, Precedence.CONDITIONAL);
    infix(EQ, Precedence.CONDITIONAL);
    infix(NEQ, Precedence.CONDITIONAL);
  }

  public Parser(List<Token> tokens) {
    this.tokens = tokens;
    setup();
  }

  public List<Statement> parse() {
    List<Statement> statements = new ArrayList<>();
    while (!isAtEnd()) {
      statements.add(declaration());
    }

    return statements;
  }

  private Statement declaration() {
    try {
      if (detectVarDec()){
        consume();
        System.out.println("Var Declaration");
        return varDeclaration();
      } 

      return statement();
    } catch (ParseException error) {
      synchronize();
      return null;
    }
  }

  private Statement statement() {
    //Substitute print with FOR to test fucntionality
    if (match(IF)) return ifStatement();
    if (match(PRINT)) return printStatement();
    if (match(FOR)) return forStatement();
    if(match(WHILE)) return whileStatement();
    if (match(LBRACE)) return new Statement.Block(block());
    return expressionStatement();
  }

  private Statement expressionStatement() {
    try{
      Expression expression = assignment();
      consume(SEMICOLON);
      return new Statement.Expr(expression);
    } catch(Exception e) {
      App.runtimeError(new RuntimeError(lookAhead(0), e.getMessage()));
      return null;
    }
  }

  private Statement ifStatement() {
    consume(LPAREN);
    Expression condition = assignment();
    consume(RPAREN); 

    Statement thenBranch = statement();
    Statement elseBranch = null;
    if (match(ELSE)) {
      elseBranch = statement();
    }

    return new Statement.If(condition, thenBranch, elseBranch);
  }
  
  private Statement printStatement() {
    Expression value = assignment();
    consume(SEMICOLON);
    return new Statement.Print(value);
  }

  private Statement forStatement() {
    consume(LPAREN);
    Statement initializer;

    if (match(SEMICOLON)) {
      System.out.println("Empty initializer");
      initializer = null;
    } else if (detectVarDec()) {
      //Works because there's only ints right now
      consume();
      initializer = varDeclaration();
    } else {
      initializer = expressionStatement();
    }

    Expression condition = null;
    if (!match(SEMICOLON)) {
      condition = assignment();
    }
    consume(SEMICOLON);

    Expression increment = null;
    if (!match(RPAREN)) {
      increment = assignment();
    }
    // System.out.println("asdf" + lookAhead(0).getText());
    consume(RPAREN);
    Statement body = statement();

    if (increment != null) {
      body = new Statement.Block(
          Arrays.asList(
              body,
              new Statement.Expr(increment)));
    }

    if (condition == null) condition = new Expression.Literal(true);
    body = new Statement.While(condition, body);

    if (initializer != null) {
      body = new Statement.Block(Arrays.asList(initializer, body));
    }

    return body;


  }

  private Statement whileStatement() {
    consume(LPAREN);
    Expression condition = assignment();
    consume(RPAREN);
    Statement body = statement();

    return new Statement.While(condition, body);
  }

  private List<Statement> block() {
    List<Statement> statements = new ArrayList<>();

    while (lookAhead(0).getType() != RBRACE && !isAtEnd()) {
      statements.add(declaration());
    }

    consume(RBRACE);
    return statements;
  }

  public Token consume() {
    if (current >= tokens.size()) {
      throw new ParseException("Unexpected end of input.");
    }

    return tokens.get(current++);
  }

  public Token consume(TokenType type) {
    Token token = consume();
    if (token.getType() != type) {
      throw new ParseException("Expected " + type + " but got " + token.getType());
    }
    return token;
  }

  private Boolean match(TokenType type) {
    if (lookAhead(0).getType() == type) {
      consume();
      return true;
    }

    return false;
  }

  private Token lookAhead(int distance) {
    if (current + distance >= tokens.size()) {
      return new Token(EOF, -1, "", null);
    }

    return tokens.get(current + distance);
  }

  private Boolean isAtEnd() {
    return lookAhead(0).getType() == EOF;
  }

  private Token previous() {
    return tokens.get(current - 1);
  }
  
  public void registerPrefix(TokenType token, PrefixParselet parselet) {
    mPrefixParselets.put(token, parselet);
  }

  public void registerInfix(TokenType token, InfixParselet parselet) {
    mInfixParselets.put(token, parselet);
  }
  
  public void prefix(TokenType token) {
    registerPrefix(token, new PrefixOperatorParselet());
  }

  public void infix(TokenType token, int precedence) {
    registerInfix(token, new BinaryOperatorParselet(precedence));
  }

  private int getPrecedence() {
    InfixParselet parser = mInfixParselets.get(
        lookAhead(0).getType());
    if (parser != null) return parser.getPrecedence();
    return 0;
  }

  private Expression assignment() {
    Expression left = or();
    if (match(ASSIGN)) {
      Token equals = previous();
      Expression right = assignment();

      if (left instanceof Expression.Variable) {
        Token name = ((Expression.Variable)left).name;
        return new Expression.Assign(name, right);
      }

      throw new ParseException("Invalid assignment target.");
    }
    
    return left;
  }

  private Expression or() {
    Expression expr = and();

    while (match(OR)) {
      Token operator = previous();
      Expression right = and();
      expr = new Expression.Logical(expr, operator, right);
    }

    return expr;
  }

  private Expression and() {
    Expression expr = parseExpression(0);

    while (match(AND)) {
      Token operator = previous();
      Expression right = parseExpression(0);
      expr = new Expression.Logical(expr, operator, right);
    }
    return expr;
  }

  public Expression parseExpression(int precedence) {

    Token token = consume();
    PrefixParselet prefix = mPrefixParselets.get(token.getType());

    if (prefix == null) throw new ParseException(
        "Could not parse \"" + token.getText() + "\".");
    Expression left = prefix.parse(this, token);
    while (precedence < getPrecedence()){
      token = lookAhead(0);
      InfixParselet infix = mInfixParselets.get(token.getType());
      if (infix == null) return left;
      token = consume();
      left = infix.parse(this, left, token);
    }
    return left;
  }

  private void synchronize() {
    advance();

    while (!isAtEnd()) {
      if (previous().getType() == SEMICOLON) return;

      switch (lookAhead(0).getType()) {
        case FUNCTION:
        case ASSIGN:
        case FOR:
        case IF:
        case RETURN:
          return;
        default:
          break;
      }

      advance();
    }
  }

  private Token advance() {
    if (!isAtEnd()) current++;
    return previous();
  }

  private Statement varDeclaration() {

    Token name = consume(IDENTIFIER);

    Expression initializer = null;
    if (match(ASSIGN)) {
      initializer = parseExpression(0);
    }

    consume(SEMICOLON);
    return new Statement.Var(name, initializer);
  }

  private Boolean detectVarDec(){
    return Variable.varTypes.containsKey(lookAhead(0).getText());
  }

}

class VarParselet implements PrefixParselet {
  public Expression parse(Parser parser, Token token) {
    return new Expression.Variable(token);
  }
}

class NameParselet implements PrefixParselet {
  public Expression parse(Parser parser, Token token) {
    if(token.getType() == TokenType.FALSE) {
      return new Expression.Literal(false);
    } else if(token.getType() == TokenType.TRUE) {
      return new Expression.Literal(true);
    } else if(token.getType() == TokenType.NIL) {
      return new Expression.Literal(null);
    }
    
    return new Expression.Literal(token.getText());
  }
}

class NumberParselet implements PrefixParselet {
  public Expression parse(Parser parser, Token token) {
    return new Expression.Literal(Double.parseDouble(token.getText()));
  }
}

class PrefixOperatorParselet implements PrefixParselet {
  public Expression parse(Parser parser, Token token) {
    Expression operand = parser.parseExpression(getPrecedence());
    return new Expression.Unary(token, operand);
  }

  public int getPrecedence() {
    return Precedence.PREFIX;
  }

}

class BinaryOperatorParselet implements InfixParselet {
  private int precedence;

  public BinaryOperatorParselet(int precedence) {
    this.precedence = precedence;
  }

  public Expression parse(Parser parser,
      Expression left, Token token) {
    Expression right = parser.parseExpression(this.precedence);
    return new Expression.Binary(left, token, right);
  }

  @Override
  public int getPrecedence() {
    return this.precedence;
  }

}

class GroupParselet implements PrefixParselet {
  public Expression parse(Parser parser, Token token) {
    Expression expression = parser.parseExpression(0);
    parser.consume(TokenType.RPAREN);
    return expression;
  }

}