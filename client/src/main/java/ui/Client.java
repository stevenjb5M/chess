package ui;

import java.util.*;

import chess.*;
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
    private GameData currentGame;
    private ChessGame.TeamColor currentColor;
    private GameBoard gameBoard;

    public Client(String serverUrl, Repl repl) {
        server = new ServerFacade(serverUrl);
        this.serverUrl = serverUrl;
        this.notificationHandler = repl;
        this.repl = repl;
        gameBoard = new GameBoard();
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
                case "redraw" -> redraw();
                case "leave" -> leave();
                case "move" -> move(params);
                case "resign" -> resign();
                case "highlight" -> highlight(params);
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
                webSocketFacade.connect(server.authToken, request.getGameID(), visitorName);

                state = State.GAME;
                repl.changeState(State.GAME);
                currentGame = gameData;
                currentColor = teamColor;

                return "Joined Game";
                //showGameBoard(gameData, teamColor);

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
                    webSocketFacade.connect(server.authToken, gameData.gameID(),visitorName);

                    gameBoard.showGameBoard(gameData, WHITE);

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
                return """
                - redraw - redraws the game for visability
                - leave - removes you from the game
                - move <CurrentPiecePosition> <NewPiecePosition> - make a chess move on your turn
                - resign - resign from a game
                - highlight - highlights user moves
                - quit - playing chess
                - help - with possible commands
                """;
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

    public String redraw() throws ResponseException {
        try {
            if (currentGame != null && currentColor != null) {
                updateCurrentGame(currentGame.gameID());
                return gameBoard.showGameBoard(currentGame, currentColor);
            } else {
                throw new ResponseException(400, "Error redrawing board");
            }
        } catch (ResponseException e) {
            throw new ResponseException(400, "Error redrawing board");
        }
    }

    public String leave() throws ResponseException {
        try {
            if (currentGame != null && server.authToken != null) {
                webSocketFacade.leave(server.authToken, currentGame.gameID(), visitorName);
                return String.format("You have left the game");
            } else {
                throw new ResponseException(400, "Error leaving game");
            }
        } catch (ResponseException e) {
            throw new ResponseException(400, "Error leaving game");
        }
    }

    public String resign() throws ResponseException {
        try {
            if (currentGame != null && server.authToken != null) {
                webSocketFacade.resign(server.authToken, currentGame.gameID(), visitorName);
                return String.format("You have resigned");
            } else {
                throw new ResponseException(400, "Error resigning");
            }
        } catch (ResponseException e) {
            throw new ResponseException(400, "Error resigning");
        }
    }

    public String move(String... params) throws ResponseException {
        try {
            if (params.length == 4 && currentGame != null) {

                int row = Integer.parseInt(params[0]);
                int col = getNumber(params[1]);

                int rowMove = Integer.parseInt(params[2]);
                int colMove = getNumber(params[3]);

                ChessPosition currentPosition = new ChessPosition(row,col);
                ChessPosition nextPosition = new ChessPosition(rowMove,colMove);

                Collection<ChessMove> moves = currentGame.game().validMoves(currentPosition);

                    var found = false;
                    for (ChessMove move : moves) {
                        int moveStartRow = move.getStartPosition().getRow();
                        int moveStartCol = move.getStartPosition().getColumn();
                        int currentRow = currentPosition.getRow();
                        int currentCol = currentPosition.getColumn();

                        int moveEndRow = move.getEndPosition().getRow();
                        int moveEndCol = move.getEndPosition().getColumn();
                        int nextRow = nextPosition.getRow();
                        int nextCol = nextPosition.getColumn();
                        if (moveStartRow == currentRow && moveStartCol == currentCol && moveEndRow == nextRow && moveEndCol == nextCol) {
                            webSocketFacade.makeMove(server.authToken, currentGame.gameID(), visitorName, move);
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        throw new ResponseException(400, "Error moving piece");
                    }
                return String.format("Piece Moved!");
            } else {
                throw new ResponseException(400, "Error moving piece");
            }
        } catch (ResponseException e) {
            throw new ResponseException(400, "Error moving piece");
        }
    }

    public String highlight(String... params) throws ResponseException {
        try {
            if (params.length == 2 && currentGame != null) {

                int row = Integer.parseInt(params[0]);
                int col = getNumber(params[1]);

                ChessPosition pos = new ChessPosition(row,col);
                Collection<ChessMove> moves = currentGame.game().validMoves(pos);

                highlightGameBoard(currentGame, currentColor, moves, pos);

                return String.format("Piece Highlighted!");
            } else {
                throw new ResponseException(400, "Error highlighting piece");
            }
        } catch (ResponseException e) {
            throw new ResponseException(400, "Error highlighting piece");
        }
    }

    private int getNumber(String letter) {
        var isWhite = currentColor == WHITE;

        switch (letter) {
            case "a" :
                return 1;
            case "b" :
                return 2;
            case "c" :
                return 3;
            case "d" :
                return 4;
            case "e" :
                return 5;
            case "f" :
                return 6;
            case "g" :
                return 7;
            case "h" :
                return 8;
        }
        return 1;
    }

    private String highlightGameBoard(GameData game, ChessGame.TeamColor playerColor, Collection<ChessMove> moves, ChessPosition currentPiece) {
        Collection<ChessPosition> positionsToHighlights = new ArrayList<>();
        for (ChessMove move : moves) {
            positionsToHighlights.add(move.getEndPosition());
        }

        String resetString = "\u001B[0m";

        gameBoard.runHeaders(playerColor,resetString);

        System.out.println();

        ChessBoard chessBoard = game.game().getBoard();

        boolean isLight = true;
        int startRR = playerColor == WHITE ? 8 : 1;
        int endRR = playerColor == WHITE ? 1 : 8;
        int dirR = playerColor == WHITE ? -1 : 1;

        int startCC = playerColor == WHITE ? 1 : 8;
        int endCC = playerColor == WHITE ? 8 : 1;
        int dirCC = playerColor == WHITE ? 1 : -1;

        for (int row = startRR; row != endRR + dirR; row += dirR) {
            System.out.print(SET_BG_COLOR_LIGHT_GREY + " " + row + " " + resetString);
            isLight = playerColor == WHITE ? (row % 2 == 0) : (row % 2 != 0);

            for (int col = startCC; col != endCC + dirCC; col += dirCC) {

                String color = isLight ? SET_BG_COLOR_WHITE : SET_BG_COLOR_BLACK;
                ChessPosition pos = new ChessPosition(row,col);

                if (positionsToHighlights.contains(pos)) {
                    color = isLight ? SET_BG_COLOR_GREEN : SET_BG_COLOR_DARK_GREEN;
                }

                if (currentPiece.getRow() == pos.getRow() && currentPiece.getColumn() == pos.getColumn()) {
                    color = SET_BG_COLOR_YELLOW;
                }
                ChessPiece piece = chessBoard.getPiece(pos);

                if (piece != null) {
                    String textColor = piece.getTeamColor() == WHITE ? SET_TEXT_COLOR_RED : SET_TEXT_COLOR_BLUE;
                    String letterName = gameBoard.getOneLetterName(piece.getPieceType());
                    System.out.print(textColor + color + " " + letterName + " " + resetString);
                } else {
                    System.out.print(color + "   " + resetString);
                }

                isLight = !isLight;
            }
            System.out.println(SET_BG_COLOR_LIGHT_GREY + " " + row + " " + resetString);
        }

        gameBoard.runHeaders(playerColor, resetString);

        System.out.println();

        return "";
    }

    private void updateCurrentGame(int gameID) {
        ListGamesResult result = null;
        try {
            result = server.listGames();
            Collection<GameData> games = result.getGames();

            int gameNumber = 1;

            gamesWithIDs.clear();

            for (GameData game: games) {
                if (game.gameID() == currentGame.gameID()) {
                    currentGame = game;
                }
                gamesWithIDs.put(gameNumber,game);
                gameNumber++;
            }

        } catch (ResponseException e) {
            throw new RuntimeException(e);
        }
    }
}
