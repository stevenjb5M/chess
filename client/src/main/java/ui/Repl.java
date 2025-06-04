package ui;

import java.util.Scanner;

import static ui.EscapeSequences.SET_TEXT_COLOR_BLACK;
import static ui.EscapeSequences.SET_TEXT_COLOR_GREEN;

public class Repl {
    private Client client;
    private String serverURL;
    private State state = State.LOGGED_OUT;
    public String currentAuthToken;

    public Repl(String serverUrl) {
        serverURL = serverUrl;
        client = new Client(serverUrl, this);
    }

    public void run() {
        System.out.println("\uD83D\uDC36 Welcome to 240 chess. Type Help to get started.");

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
            printPrompt();
            String line = scanner.nextLine();

            try {
                result = client.eval(line);
                System.out.print(SET_TEXT_COLOR_BLACK + result);
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg);
            }
        }
        System.out.println();
    }

    public void changeState(State newState) {
        if (newState == State.LOGGED_IN) {
            state = State.LOGGED_IN;
        } else if (newState == State.LOGGED_OUT) {
            state = State.LOGGED_OUT;
        } else if (newState == State.GAME) {
            //client = new GameClient(serverURL);
        }
    }

    private void printPrompt() {
        System.out.print("\n" + state + ">>> " + SET_TEXT_COLOR_GREEN);
    }

}
