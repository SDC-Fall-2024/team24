package com.team24.badgr;

public enum TokenType {
  PLUS, MINUS, STAR,
  STRING, NUMBER, IDENTIFIER,
  LT, LEQ, GT, GEQ, EQ, NEQ,
  LBRACE, RBRACE, LPAREN, RPAREN, LBRAC, RBRAC,
  COMMA, SEMICOLON, COLON, DOT, FSLASH, BSLASH,
  PERCENT, DOLLAR, OCTOTHORPE, AT, // These are possible uses
  ASSIGN, NOT, AND, OR, RANGE, RANGE_INCLUSIVE, TYPEARROW,
  RETURN, FOR, IF, FUNCTION, TRUE, FALSE, ELSE, NIL, INT, DEBUG, STING, DOUBLE

  EOF, ERR,
}
