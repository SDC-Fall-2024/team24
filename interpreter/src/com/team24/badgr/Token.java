package com.team24.badgr;

class Token {
  final TokenType type;
  final String lexeme;
  final int line;

  public Token() {
    type = null;
    lexeme = null;
    line = 0;
  }

  public String toString() {
    return "this is a token";
  }
}
