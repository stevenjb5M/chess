package ui;

import java.util.Scanner;
import static java.awt.Color.BLUE;
import static java.awt.Color.GREEN;

public class Repl {
    private final PreLoginClient client;

    public Repl(String serverUrl) {
        client = new PreLoginClient(serverUrl);
    }

    public void run() {
        System.out.println("\uD83D\uDC36 Welcome to 240 chess. Type Help to get started.");
        System.out.print(client.help());

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
            printPrompt();
            String line = scanner.nextLine();

            try {
                result = client.eval(line);
                System.out.print(BLUE + result);
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg);
            }
        }
        System.out.println();
    }

    private void printPrompt() {
        System.out.print("\n" + ">>> " + GREEN);
    }

}
