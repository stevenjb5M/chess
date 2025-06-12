package ui;

import exception.ResponseException;
import websocket.NotificationHandler;
import websocket.messages.ServerMessage;

import java.util.Scanner;

import static java.awt.Color.RED;
import static ui.EscapeSequences.*;

public class Repl implements NotificationHandler {
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
            state = State.GAME;
        }
    }

    public void notify(ServerMessage notification) {
        if (notification.getServerMessageType() == ServerMessage.ServerMessageType.LOAD_GAME) {
            try {
                client.redraw();
                printPrompt();
            } catch (ResponseException e) {
                throw new RuntimeException(e);
            }
        } else if (notification.message != null) {
            System.out.println(SET_TEXT_COLOR_RED + notification.message);
            printPrompt();
        }
    }

    private void printPrompt() {
        System.out.print("\n" + SET_TEXT_COLOR_BLACK + state + ">>> ");
    }

}
