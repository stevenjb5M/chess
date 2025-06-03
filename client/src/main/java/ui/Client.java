package ui;

import java.util.Arrays;

import chess.ChessGame;
import server.*;
import model.UserData;
import exception.ResponseException;
import server.JoinGameRequest;

public class Client {
    private String visitorName = null;
    private final ServerFacade server;
    private final String serverUrl;
    private Repl repl;
    private State state = State.LOGGED_OUT;

    public Client(String serverUrl, Repl repl) {
        server = new ServerFacade(serverUrl);
        this.serverUrl = serverUrl;
        this.repl = repl;
    }

    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "register" -> register(params);
                case "login" -> login(params);
                case "list" -> listGames();
                case "logout" -> logOut();
                case "create" -> createGame(params);
//                case "list" -> listPets();
//                case "signout" -> signOut();
//                case "adopt" -> adoptPet(params);
//                case "adoptall" -> adoptAllPets();
                case "quit" -> "quit";
                default -> help();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    public State getState() {
        return state;
    }

    public String register(String... params) throws ResponseException {
        if (params.length == 3) {

            String userName = params[0];
            String password = params[1];
            String email = params[2];

            UserData newUser = new UserData(userName, password, email);

            RegisterResult response = server.registerUser(newUser);
            state = State.LOGGED_IN;
            repl.ChangeState(State.LOGGED_IN);
            visitorName = userName;
            return String.format("You signed in as %s.", visitorName);

        }
        throw new ResponseException(400, "Expected: <yourname>");
    }

    public String login(String... params) throws ResponseException {
        if (params.length == 2) {

            String userName = params[0];
            String password = params[1];

            UserData newUser = new UserData(userName, password, null);

            LoginResult response = server.loginUser(newUser);

            server.authToken = response.getAuthToken();
            state = State.LOGGED_IN;
            repl.ChangeState(State.LOGGED_IN);
            visitorName = userName;
            return String.format("You're signed in as %s.", visitorName);

        }
        throw new ResponseException(400, "Expected: <yourname>");
    }

    public String createGame(String... params) throws ResponseException {
        if (params.length == 1) {

            String gameName = params[0];

            CreateGameRequest request = new CreateGameRequest(gameName);

            CreateGameResult response = server.createGame(request);

            return String.format("Your game has been created");

        }
        throw new ResponseException(400, "Failed to create game");
    }

    public String joinGame(String... params) throws ResponseException {
        if (params.length == 2) {

            String gameNumber = params[0];
            String color = params[1];

            ChessGame.TeamColor test = ChessGame.TeamColor.WHITE;
            int ID = 123;

            JoinGameRequest request = new JoinGameRequest(ChessGame.TeamColor.WHITE, ID);

            server.joinGame(request);

            return String.format("You have joined the game successfully");

        }
        throw new ResponseException(400, "Failed to join game");
    }

    public String help() {
        switch (state) {
            case State.LOGGED_OUT:
                return """
                    - register <USERNAME> <PASSWORD> <EMAIL> - to create an account
                    - login <USERNAME> <PASSWORD> - to play chess
                    - quit - playing chess
                    - help - with possible commands
                    """;
            case State.LOGGED_IN:
                return """
                - create <NAME> - a game
                - list - games
                - join <ID> [WHITE|BLACK] - a game
                - observe <ID> - a game
                - logout - when you are done
                - quit - playing chess
                - help - with possible commands
                """;
            case State.GAME:
                return "";
        }

        return """
                    - register <USERNAME> <PASSWORD> <EMAIL> - to create an account
                    - login <USERNAME> <PASSWORD> - to play chess
                    - quit - playing chess
                    - help - with possible commands
                    """;
    }

    private void assertSignedIn() throws ResponseException {
        if (state == State.LOGGED_OUT) {
            throw new ResponseException(400, "You must sign in");
        }
    }

    public String listGames(String... params) throws ResponseException {

        throw new ResponseException(400, "Expected: <yourname>");
    }

    public String logOut(String... params) throws ResponseException {
        if (params.length == 0 && server.authToken != null) {

            server.logoutUser(repl.currentAuthToken);
            state = State.LOGGED_OUT;
            repl.ChangeState(State.LOGGED_OUT);
            return String.format("You're signed out");

        }
        throw new ResponseException(400, "Error logging you out");
    }

}
