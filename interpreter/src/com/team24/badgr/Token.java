package com.team24.badgr;

public class Token {
  private TokenType type;
  private int line;
  private String lexeme;
  private Object literal;

  public Token(TokenType type, int line, String lexeme, Object literal) {
    this.type = type;
    this.line = line;
    this.lexeme = lexeme;
    this.literal = literal;
  }

  public String toString() {
    return type + " " + line + " " + literal;
  }

  public TokenType getType() {
    return type;
  }

  public String getText() {
    return lexeme;
  }
}
