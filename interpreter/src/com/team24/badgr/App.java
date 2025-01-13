package com.team24.badgr;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.util.List;

public class App {
  static Boolean hadError = false;
  /**
   * This method is the entrypoint to the interpreter. It reads the arguments
   * passed by the user and determines what to do with those arguments.
   */
  public static void main(String args[]) throws IOException {
    if (args.length > 1) {
      System.out.println("Error: not enough arguments provided");
      System.exit(1);
    } else if (args.length == 1) {
      runFile(args[1]);
    } else {
      runRepl();
    }
  }

  /**
   * This method reads a source program from a file and executes it through the interpreter
   * pipeline.
   * The file contents are read as bytes and converted to a string before being passed
   * to the scanner.
   */
  private static void runFile(String path) throws IOException {
    byte[] bytes = Files.readAllBytes(Paths.get(path));
    run(new String(bytes));
  }

  /**
   * This method starts the interactive Read Eval Print Loop for the interpreter.
   * The input is passed along to the next stages of the Interpreter for
   * lexical analysis, semantic analysis, and execution.
   */
  private static void runRepl() {
    InputStreamReader input = new InputStreamReader(System.in);
    BufferedReader reader = new BufferedReader(input);
    while (true) {
      System.out.print("> ");
      String line;
      try {
        line = reader.readLine();
      } catch (IOException e) {
        System.out.println("Error - " + e.toString());
        line = "";
      }
      if (line == null)
        break;
      run(line);
    }
  }

  /**
  * This method processes the source text through the interpreter pipeline:
  * 1. Lexical analysis (Scanner) - converts text to tokens
  * 2. Parsing (TODO)
  * 3. Execution (TODO)
   *
   * @param source The source program to be executed by the
   *               interpreter.
   */
  private static void run(String source) {
    Scanner scanner = new Scanner(source);
    List<Token> tokens = scanner.scanTokens();

    for (Token token : tokens) {
      System.out.println(token);
    }


    Parser parser = new Parser(tokens);
    // System.out.println(new AstPrinter().print(parser.parse()));
    List<Statement> statements = parser.parse();

    Interpreter interpreter = new Interpreter();

    if(hadError){
      hadError = false;
      return;
    }

    interpreter.interpret(statements);
  }

  static void runtimeError(RuntimeError error) {
    System.err.println(error.getMessage() +
        "\n[line " + error.token.getLine() + "]");
    hadError = true;
  }
  
}
