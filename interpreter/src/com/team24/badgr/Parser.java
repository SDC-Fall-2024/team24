package com.team24.badgr;
import static com.team24.badgr.TokenType.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    registerPrefix(IDENTIFIER, new NameParselet());
    registerPrefix(NUMBER, new NameParselet());
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

  public Expression parse() {
    Expression a = parseExpression(0);
    return a;
  }

  public Token consume() {
    if (current >= tokens.size()) {
      throw new ParseException("Unexpected end of input.");
    }

    return tokens.get(current++);
  }

  public void consume(TokenType type) {
    Token token = consume();
    if (token.getType() != type) {
      throw new ParseException("Expected " + type + " but got " + token.getType());
    }
  }

  private Token lookAhead(int distance) {
    if (current + distance >= tokens.size()) {
      return new Token(EOF, -1, "", null);
    }

    return tokens.get(current + distance);
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

}

class NameParselet implements PrefixParselet {
  public Expression parse(Parser parser, Token token) {
    return new Expression.Literal(token.getText());
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