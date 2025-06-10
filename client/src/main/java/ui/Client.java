package ui;

import java.util.*;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import model.GameData;
import server.*;
import model.UserData;
import exception.ResponseException;
import websocket.NotificationHandler;
import websocket.WebSocketFacade;

import static chess.ChessGame.TeamColor.WHITE;
import static ui.EscapeSequences.*;

public class Client {
    private String visitorName = null;
    private final ServerFacade server;
    private final String serverUrl;
    private Repl repl;
    private WebSocketFacade webSocketFacade;
    private NotificationHandler notificationHandler;
    private State state = State.LOGGED_OUT;
    private Map<Integer, GameData> gamesWithIDs = new HashMap<>();

    public Client(String serverUrl, Repl repl) {
        server = new ServerFacade(serverUrl);
        this.serverUrl = serverUrl;
        this.notificationHandler = repl;
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
                case "join" -> joinGame(params);
                case "observe" -> observe(params);
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
        try {
            if (params.length == 3) {

                String userName = params[0];
                String password = params[1];
                String email = params[2];

                UserData newUser = new UserData(userName, password, email);

                RegisterResult response = server.registerUser(newUser);
                server.authToken = response.getAuthToken();
                state = State.LOGGED_IN;
                repl.changeState(State.LOGGED_IN);
                visitorName = userName;
                return String.format("You signed in as %s.", visitorName);

            } else {
                throw new ResponseException(400, "Failed to Register");
            }
        } catch(ResponseException e) {
            throw new ResponseException(400, "Failed to Register");
        }
    }

    public String login(String... params) throws ResponseException {
        try {
            if (params.length == 2) {

                String userName = params[0];
                String password = params[1];

                UserData newUser = new UserData(userName, password, null);

                LoginResult response = server.loginUser(newUser);

                server.authToken = response.getAuthToken();
                state = State.LOGGED_IN;
                repl.changeState(State.LOGGED_IN);
                visitorName = userName;
                return String.format("You're signed in as %s.", visitorName);

            } else {
                throw new ResponseException(400, "Failed to login, didn't see a username and password");
            }
        } catch(ResponseException e) {
        throw new ResponseException(400, "Failed to login");
        }
    }

    public String listGames(String... params) throws ResponseException {
        try {
            if (params.length == 0) {

                ListGamesResult result = server.listGames();

                Collection<GameData> games = result.getGames();

                StringBuilder message = new StringBuilder();
                int gameNumber = 1;

                for (GameData game: games) {
                    String line = gameNumber + ": " + game.gameName() + " White:" + game.whiteUsername() + " Black:" + game.blackUsername() + "\n";
                    message.append(line);
                    gamesWithIDs.put(gameNumber,game);
                    gameNumber++;
                }

                return String.format(message.toString());

            } else {
                throw new ResponseException(400, "Failed to get games");
            }
        } catch (ResponseException e) {
            throw new ResponseException(400, "Failed to get games");
        }

    }

    public String createGame(String... params) throws ResponseException {
        try {
            if (params.length == 1) {

                String gameName = params[0];

                CreateGameRequest request = new CreateGameRequest(gameName);

                CreateGameResult response = server.createGame(request);

                return String.format("Your game has been created");

            } else {
                throw new ResponseException(400, "Failed to create game, invalid parameters");
            }
        } catch (ResponseException e) {
        throw new ResponseException(400, "Failed to create game");
        }
    }

    public String joinGame(String... params) throws ResponseException {
        try {
            if (params.length == 2) {

                String gameNumber = params[0];
                String color = params[1];
                ChessGame.TeamColor teamColor = WHITE;

                if (color.equalsIgnoreCase("WHITE")) {
                    teamColor = WHITE;
                } else if (color.equalsIgnoreCase("BLACK")){
                    teamColor = ChessGame.TeamColor.BLACK;
                } else {
                    throw new ResponseException(400, "Invalid Color");
                }

                int gameIndex = Integer.parseInt(gameNumber);
                GameData gameData = gamesWithIDs.get(gameIndex);


                JoinGameRequest request = new JoinGameRequest(teamColor, gameData.gameID());

                server.joinGame(request);

                webSocketFacade = new WebSocketFacade(serverUrl, notificationHandler);
                webSocketFacade.connect(server.authToken, request.getGameID());

                return showGameBoard(gameData, teamColor);

            } else {
                throw new ResponseException(400, "Failed to join game");
            }
        } catch (ResponseException e) {
            throw new ResponseException(400, "Failed to join game");
        }
    }

    public String observe(String... params) throws ResponseException {
        try {
            if (params.length == 1) {

                String gameNumber = params[0];

                int gameIndex = Integer.parseInt(gameNumber);
                GameData gameData = gamesWithIDs.get(gameIndex);

                if (gameData != null) {
                    webSocketFacade = new WebSocketFacade(serverUrl, notificationHandler);
                    webSocketFacade.connect(server.authToken, gameData.gameID());

                    showGameBoard(gameData, WHITE);

                } else {
                    throw new ResponseException(400, "No game found, run list to see what games are available");
                }

                return String.format("You are now observing the game");

            } else {
                throw new ResponseException(400, "Failed to observe game");
            }
        } catch (ResponseException e) {
            throw new ResponseException(400, "Failed to observe game");
        }
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

    public String logOut(String... params) throws ResponseException {
        try {
            if (params.length == 0 && server.authToken != null) {

                server.logoutUser(repl.currentAuthToken);
                state = State.LOGGED_OUT;
                repl.changeState(State.LOGGED_OUT);
                return String.format("You're signed out");

            } else {
                throw new ResponseException(400, "Error logging you out");
            }
        } catch (ResponseException e) {
            throw new ResponseException(400, "Error logging you out");
        }
    }

    private String showGameBoard(GameData game, ChessGame.TeamColor playerColor) {

        String reset = "\u001B[0m";

        runHeaders(playerColor,reset);

        System.out.println();

        ChessBoard board = game.game().getBoard();

        boolean isLight = true;
        int startR = playerColor == WHITE ? 8 : 1;
        int endR = playerColor == WHITE ? 1 : 8;
        int dir = playerColor == WHITE ? -1 : 1;

        int startC = playerColor == WHITE ? 1 : 8;
        int endC = playerColor == WHITE ? 8 : 1;
        int dirC = playerColor == WHITE ? 1 : -1;


        for (int row = startR; row != endR + dir; row += dir) {
            System.out.print(SET_BG_COLOR_LIGHT_GREY + " " + row + " " + reset);
            isLight = playerColor == WHITE ? (row % 2 == 0) : (row % 2 != 0);

            for (int col = startC; col != endC + dirC; col += dirC) {

                String color = isLight ? SET_BG_COLOR_WHITE : SET_BG_COLOR_BLACK;


                ChessPosition pos = new ChessPosition(row,col);
                ChessPiece piece = board.getPiece(pos);


                if (piece != null) {
                    String textcolor = piece.getTeamColor() == WHITE ? SET_TEXT_COLOR_RED : SET_TEXT_COLOR_BLUE;
                    String letter = getOneLetterName(piece.getPieceType());
                    System.out.print(textcolor + color + " " + letter + " " + reset);
                } else {
                    System.out.print(color + "   " + reset);
                }

                isLight = !isLight;
            }
            System.out.println(SET_BG_COLOR_LIGHT_GREY + " " + row + " " + reset);
        }

        runHeaders(playerColor, reset);

        System.out.println();

        return "";
    }

    private String getOneLetterName(ChessPiece.PieceType type) {
        switch (type) {
            case KING: { return "K";}
            case QUEEN: { return "Q";}
            case KNIGHT: { return "N";}
            case ROOK: { return "R";}
            case BISHOP: { return "B";}
            case PAWN: { return "P";}
            default: return "";
        }
    }

    private void runHeaders(ChessGame.TeamColor color, String reset) {
        if (color == WHITE) {
            System.out.print(SET_BG_COLOR_LIGHT_GREY + "   " + reset);
            System.out.print(SET_BG_COLOR_LIGHT_GREY + " a " + reset);
            System.out.print(SET_BG_COLOR_LIGHT_GREY + " b " + reset);
            System.out.print(SET_BG_COLOR_LIGHT_GREY + " c " + reset);
            System.out.print(SET_BG_COLOR_LIGHT_GREY + " d " + reset);
            System.out.print(SET_BG_COLOR_LIGHT_GREY + " e " + reset);
            System.out.print(SET_BG_COLOR_LIGHT_GREY + " f " + reset);
            System.out.print(SET_BG_COLOR_LIGHT_GREY + " g " + reset);
            System.out.print(SET_BG_COLOR_LIGHT_GREY + " h " + reset);
            System.out.print(SET_BG_COLOR_LIGHT_GREY + "   " + reset);
        } else {
            System.out.print(SET_BG_COLOR_LIGHT_GREY + "   " + reset);
            System.out.print(SET_BG_COLOR_LIGHT_GREY + " h " + reset);
            System.out.print(SET_BG_COLOR_LIGHT_GREY + " g " + reset);
            System.out.print(SET_BG_COLOR_LIGHT_GREY + " f " + reset);
            System.out.print(SET_BG_COLOR_LIGHT_GREY + " e " + reset);
            System.out.print(SET_BG_COLOR_LIGHT_GREY + " d " + reset);
            System.out.print(SET_BG_COLOR_LIGHT_GREY + " c " + reset);
            System.out.print(SET_BG_COLOR_LIGHT_GREY + " b " + reset);
            System.out.print(SET_BG_COLOR_LIGHT_GREY + " a " + reset);
            System.out.print(SET_BG_COLOR_LIGHT_GREY + "   " + reset);
        }
    }
}
