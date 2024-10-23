package com.team24.badgr;

enum TokenType {
  // single character tokens
  COMMA, LPAREN, RPAREN, DOT, PLUS,
  MINUS, SEMICOLON, SLASH, STAR,

  // 1 or 2 character tokens
  EQUAL, EQUAL_EQUAL,
  GREATER, GREATER_EQUAL,
  LESS, LESS_EQUAL,

  // Default Types
  IDENT, INT, STRING,

  // Keywords
  FOR, IF, ELSE, TRUE, FALSE
}
