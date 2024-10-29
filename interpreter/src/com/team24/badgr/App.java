package com.team24.badgr;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.util.List;

public class App {

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
   * This method reads a file as a source program and passes it along to the
   * Interpreter.
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
   * This method communicates with the rest of the interpreter, passing along
   * the source text to be Tokenized in the Scanner.
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
  }
}
