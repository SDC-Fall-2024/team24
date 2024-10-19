import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;
import java.nio.file.Paths;
import java.nio.file.Files;

public class App {

  /**
   * Get commandline arguments to program
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

  private static void runFile(String path) throws IOException {
    byte[] bytes = Files.readAllBytes(Paths.get(path));
    run(new String(bytes));
  }

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

  private static void run(String source) {
    Scanner scanner = null;
    try {
      scanner = new Scanner(System.in);
      String scanned = scanner.nextLine();
      System.out.println(scanned); // replace with call to lexical analyzer
    } finally {
      if (scanner != null) {
        scanner.close();
      }
    }
  }
}
