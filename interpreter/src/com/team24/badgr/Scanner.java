package com.team24.badgr;

import java.util.List;
import java.util.ArrayList;
import static com.team24.badgr.TokenType.*;

public class Scanner {
  private final List<Token> tokens = new ArrayList<>();
  private int line = 1;
  private final String source;
  private char start = 0;
  private char current = 0;

  public Scanner(String source) {
    this.source = source;
  }

  public List<Token> scanTokens() {
    while (!isAtEnd()) {
      start = current;
      scanToken();
    }

    tokens.add(new Token(EOF, line, "", null));
    return tokens;
  }

  private void scanToken() {
    char ch = advance();
    switch (ch) {
      case ',':
        newToken(COMMA);
        break;
      case ';':
        newToken(SEMICOLON);
        break;
      case ':':
        newToken(COLON);
        break;
      case '+':
        newToken(PLUS);
        break;
      case '-':
        newToken(MINUS);
        break;
      case '/': // Could be a comment, divide
        newToken(FSLASH);
        break;
      case '*':
        newToken(STAR);
        break;
      case '!': // Could be !=, !, !IDENT
        newToken(match('=') ? NEQ : NOT);
        break;
      case '=': // Count be ==, =
        newToken(match('=') ? ASSIGN : EQ);
        break;
      case '(':
        newToken(LPAREN);
        break;
      case ')':
        newToken(RPAREN);
        break;
      case '{':
        newToken(LBRACE);
        break;
      case '}':
        newToken(RBRACE);
        break;
      case '[':
        newToken(LBRAC);
        break;
      case ']':
        newToken(RBRAC);
        break;
      case '\\': // perhaps used for escape sequences
        newToken(BSLASH);
        break;
      case '<':
        newToken(match('=') ? LEQ : LT);
        break;
      case '>':
        newToken(match('=') ? GEQ : GT);
        break;
      case '&':
        newToken(match('&') ? AND : ERR);
        break;
      case '|':
        newToken(match('|') ? OR : ERR);
        break;
      case '.':
        if (match('.')) {
          if (match('=')) {
            newToken(RANGE_INCLUSIVE);
          } else {
            newToken(RANGE);
          }
        } else {
          newToken(DOT);
        }
        break;
      default:
        if (isDigit(ch)) {
          consumeNumber();
        } else if (isAlpha(ch)) {
          consumeIdentifier();
        } else {
          newToken(ERR);
        }
    }
    return;
  }

  private void consumeIdentifier() {
    while (isAlphaNumeric(peek()))
      advance();

    newToken(IDENTIFIER);
  }

  private void consumeNumber() {
    while (isDigit(peek()))
      advance();

    newToken(NUMBER, Integer.parseInt(source.substring(start, current)));
  }

  private void newToken(TokenType type) {
    newToken(type, null);
  }

  private void newToken(TokenType type, Object literal) {
    tokens.add(new Token(type, line, source.substring(start, current), literal));
  }

  private boolean isDigit(char ch) {
    return '0' <= ch && ch <= '9';
  }

  private boolean isAlpha(char ch) {
    return ('a' <= ch && ch <= 'z') || ('A' <= ch && ch <= 'Z') || (ch == '_');
  }

  private boolean isAlphaNumeric(char ch) {
    return isAlpha(ch) || isDigit(ch);
  }

  private char peek() {
    if (isAtEnd()) {
      return '\0';
    }
    return source.charAt(current);
  }

  private boolean isAtEnd() {
    return current >= source.length();
  }

  private boolean match(char expected) {
    if (isAtEnd())
      return false;
    if (source.charAt(current) != expected)
      return false;

    current++;
    return true;
  }

  private char advance() {
    return source.charAt(current++);
  }
}
