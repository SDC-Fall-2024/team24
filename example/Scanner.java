package example;

import java.util.ArrayList;
import java.util.List;

enum TokenType {
  STRING, COMMA, HASHTAG, IDENTIFIER, EOF
}

class Token {
  final TokenType type;
  final int line;
  final String lexeme;

  Token(TokenType type, int line, String lexeme) {
    this.type = type;
    this.line = line;
    this.lexeme = lexeme;
  }

  public String toString() {
    return type + " " + line + " " + lexeme;
  }
}

public class Scanner {
  String source;
  List<Token> tokens = new ArrayList<>();
  private int start = 0;
  private int current = 0;
  private int line = -1;

  Scanner(String source) {
    this.source = source;
  }

  List<Token> scanTokens() {
    while (!isAtEnd()) {
      start = current;
      scanToken();
    }

    tokens.add(new Token(TokenType.EOF, line, ""));
    return tokens;
  }

  void scanToken() {
    char c = advance();
    switch (c) {
      case ',':
        addToken(TokenType.COMMA);
        break;
      case '#':
        addToken(TokenType.HASHTAG);
        break;
      default:
        addToken(TokenType.EOF);
    }
  }

  private char advance() {
    return source.charAt(current++);
  }

  private void addToken(TokenType type) {
    String text = source.substring(start, current);
    tokens.add(new Token(type, line, text));
  }

  private boolean isAtEnd() {
    return current >= source.length();
  }

  public static void main(String[] args) {
    run("val,value");
  }

  public static void run(String source) {
    Scanner scanner = new Scanner(source);
    List<Token> tokens = scanner.scanTokens();
    for (Token token : tokens) {
      System.out.println(token);
    }
  }

}
