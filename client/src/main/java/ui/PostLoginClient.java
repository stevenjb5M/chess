package ui;

import model.UserData;
import server.LoginResult;
import server.RegisterResult;
import  ui.State;
import com.google.gson.Gson;
import exception.ResponseException;
import server.ServerFacade;

import java.util.Arrays;

public class PostLoginClient extends Client {
    private final ServerFacade server;
    private final String serverUrl;
    private State state = State.LOGGED_IN;
    private Repl repl;

    public PostLoginClient(String serverUrl, Repl repl) {
        this.serverUrl = serverUrl;
        server = new ServerFacade(serverUrl);
        this.repl = repl;
    }

    @Override
    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "list" -> listGames();
                case "logout" -> logOut();
//                case "adopt" -> adoptPet(params);
//                case "adoptall" -> adoptAllPets();
                case "quit" -> "quit";
                default -> help();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    public String listGames(String... params) throws ResponseException {

        throw new ResponseException(400, "Expected: <yourname>");
    }

    public String logOut(String... params) throws ResponseException {
        if (params.length == 2) {

            String userName = params[0];
            String password = params[1];

            UserData newUser = new UserData(userName, password, null);

            var response = server.logoutUser(newUser);
            repl.ChangeState(State.LOGGED_OUT);
            return String.format("You're signed out");

        }
        throw new ResponseException(400, "Error logging you out");
    }

    public String help() {
        return """
                - create <NAME> - a game
                - list - games
                - join <ID> [WHITE|BLACK] - a game
                - observe <ID> - a game
                - logout - when you are done
                - quit - playing chess
                - help - with possible commands
                """;
    }

}
