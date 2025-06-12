package server.websocket;

import chess.*;
import com.google.gson.Gson;
import dataaccess.*;
import exception.ResponseException;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import service.AuthService;
import service.GameService;
import spark.Spark;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.Collection;
import java.util.Timer;


@WebSocket
public class WebSocketHandler {

    private final ConnectionManager connections = new ConnectionManager();
    private String userName = "";
    private ChessMove chessMove;

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
        UserGameCommand action = new Gson().fromJson(message, UserGameCommand.class);
        userName = action.userName;
        switch (action.getCommandType()) {
            case CONNECT -> {
                try {
                    connect(action.getGameID(), action.getAuthToken(), session);
                } catch (DataAccessException e) {
                    throw new RuntimeException(e);
                }
            }
            case MAKE_MOVE -> {
                try {
                    MakeMoveCommand move = new Gson().fromJson(message, MakeMoveCommand.class);
                    chessMove = move.move;
                    makeMove(move.getGameID(), move.getAuthToken(), session);
                } catch (DataAccessException e) {
                    throw new RuntimeException(e);
                }
            }
            case LEAVE -> {
                try {
                    leave(action.getGameID(), action.getAuthToken(), session);
                } catch (DataAccessException e) {
                    throw new RuntimeException(e);
                }
            }
            case RESIGN -> {
                try {
                    resign(action.getGameID(), action.getAuthToken(), session);
                } catch (DataAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private void connect(int gameID, String token, Session session) throws IOException, DataAccessException {

        AuthDAO authDAO = new SQLAuthDAO();
        AuthService authServiceObject = new AuthService(authDAO);

        if (!authServiceObject.checkIfAuthExisits(token)) {
            var notification = ServerMessage.error("Error, invalid auth token");
            var errorMessage = new Gson().toJson(notification);
            session.getRemote().sendString(errorMessage);
            throw new IOException("Error: Auth token was wrong");
        }

        String playername = authServiceObject.getUserByAuth(token);

        GameDAO gameDAOObject = new SQLGameDAO();
        GameService gameService = new GameService(gameDAOObject);
        Collection<GameData> games = gameService.listGames();

        connections.add(gameID,playername, session);



        boolean found = false;
        for (GameData gameData : games) {
            if (gameData.gameID() == gameID) {
                found = true;
                break;
            }
        }

        if (!found) {
            var notificationError = ServerMessage.error("Error, invalid game ID");
session.getRemote().sendString(new Gson().toJson(notificationError));            throw new IOException("Error: Game ID was wrong");
        }

        var message = String.format("%s has joined the game", playername);
        var notification = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME, gameID);
        var notification1 = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
        session.getRemote().sendString(new Gson().toJson(notification));
        connections.broadcastInGame(gameID,playername, notification1);
    }

    private void makeMove(int gameID, String token, Session session) throws IOException, DataAccessException {

        AuthDAO authDAO = new SQLAuthDAO();
        AuthService authService = new AuthService(authDAO);

        if (!authService.checkIfAuthExisits(token)) {
            var notificationError = ServerMessage.error("Error, invalid auth token");
            var errorMessage = new Gson().toJson(notificationError);
            session.getRemote().sendString(errorMessage);
            throw new IOException("Error: Auth token was wrong");
        }

        String playername = authService.getUserByAuth(token);

        SQLGameDAO gameDAO = new SQLGameDAO();
        GameService gameService = new GameService(gameDAO);
        Collection<GameData> games = gameService.listGames();
        //connections.add(playername, session);
        GameData thisGameData = null;


        boolean found = false;
        for (GameData gameData : games) {
            if (gameData.gameID() == gameID) {
                thisGameData = gameData;
                found = true;
                break;
            }
        }

        if (!found) {
            var notificationError = ServerMessage.error("Error, invalid game ID");
session.getRemote().sendString(new Gson().toJson(notificationError));            throw new IOException("Error: Game ID was wrong");
        }

        ChessGame game = thisGameData.game();

        if (game.gameOver) {
            var notificationError = ServerMessage.error("Error, game is over");
session.getRemote().sendString(new Gson().toJson(notificationError));            throw new IOException("Error: game is over");
        }


        try {
            if (chessMove != null) {

                ChessPiece movePiece = game.getBoard().getPiece(chessMove.getStartPosition());
                var isWhite = playername.equals(thisGameData.whiteUsername());
                if (isWhite ? movePiece.getTeamColor() == ChessGame.TeamColor.WHITE : movePiece.getTeamColor() == ChessGame.TeamColor.BLACK) {
                    game.makeMove(chessMove);
                    String white = thisGameData.whiteUsername();
                    String black = thisGameData.blackUsername();
                    String name = thisGameData.gameName();
                    GameData newGameData = new GameData(thisGameData.gameID(), white, black, name, game);
                    gameDAO.updateGame(newGameData);

                    var message = String.format("%s has made a move", playername);
                    var notification = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME, gameID);
                    var notification2 = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
                    connections.broadcastAllGame(gameID, notification);
                    connections.broadcastInGame(gameID, playername, notification2);
                } else {
                    var notificationError = ServerMessage.error("Error, invalid move, wrong color");
                    session.getRemote().sendString(new Gson().toJson(notificationError));
                    throw new IOException("Error: invalid move wrong color");
                }
            } else {
                var notificationError = ServerMessage.error("Error, invalid move");
                session.getRemote().sendString(new Gson().toJson(notificationError));
                throw new IOException("Error: invalid move");
            }
        } catch (InvalidMoveException e) {
            var notificationError = ServerMessage.error("Error, invalid move");
            session.getRemote().sendString(new Gson().toJson(notificationError));
            throw new IOException("Error: invalid move");
        }



    }

    private void resign(int gameID, String token, Session session) throws IOException, DataAccessException {

        AuthDAO authDAO = new SQLAuthDAO();
        AuthService auth = new AuthService(authDAO);

        if (!auth.checkIfAuthExisits(token)) {
            var serverMessage = ServerMessage.error("Error, invalid auth token");
            var errorMessage = new Gson().toJson(serverMessage);
            session.getRemote().sendString(errorMessage);
            throw new IOException("Error: Auth token was wrong");
        }

        String playername = auth.getUserByAuth(token);

        SQLGameDAO gameDAOSQL = new SQLGameDAO();
        GameService gameService = new GameService(gameDAOSQL);
        Collection<GameData> games = gameService.listGames();
        //connections.add(playername, session);
        GameData thisGameData = null;

        boolean found = false;
        for (GameData gameData : games) {
            if (gameData.gameID() == gameID) {
                thisGameData = gameData;
                found = true;
                break;
            }
        }

        if (!found) {
            var notificationError = ServerMessage.error("Error, invalid game ID");
session.getRemote().sendString(new Gson().toJson(notificationError));            throw new IOException("Error: Game ID was wrong");
        }

        ChessGame game = thisGameData.game();

        if (game.gameOver) {
            var notificationError = ServerMessage.error("Error, invalid game ID");
session.getRemote().sendString(new Gson().toJson(notificationError));            throw new IOException("Error: Game ID was wrong");
        }

        if (game != null && (playername.equals(thisGameData.blackUsername()) || playername.equals(thisGameData.whiteUsername()))) {
            gameService.resign(gameID);

            var message = String.format("%s has resigned", playername);
            var notification2 = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
            connections.broadcastAllGame(gameID, notification2);
        } else {
            var notificationError = ServerMessage.error("Error, invalid resign");
session.getRemote().sendString(new Gson().toJson(notificationError));            throw new IOException("Error: invalid resign");
        }

    }

    private void leave(int gameID, String token, Session session) throws IOException, DataAccessException {

        AuthDAO authDAO = new SQLAuthDAO();
        AuthService authTokenService = new AuthService(authDAO);

        if (!authTokenService.checkIfAuthExisits(token)) {
            var error = ServerMessage.error("Error, invalid auth token");
            var errorMessage = new Gson().toJson(error);
            session.getRemote().sendString(errorMessage);
            throw new IOException("Error: Auth token was wrong");
        }

        String playername = authTokenService.getUserByAuth(token);

        SQLGameDAO sqlGameDAO = new SQLGameDAO();
        GameService gameServiceSQL = new GameService(sqlGameDAO);
        Collection<GameData> games = gameServiceSQL.listGames();
        GameData thisGameData = null;

        boolean gameFound = false;
        for (GameData gameData : games) {
            if (gameData.gameID() == gameID) {
                thisGameData = gameData;
                gameFound = true;
                break;
            }
        }

        if (!gameFound) {
            var notificationError = ServerMessage.error("Error, invalid game ID");
session.getRemote().sendString(new Gson().toJson(notificationError));            throw new IOException("Error: Game ID was wrong");
        }

        ChessGame game = thisGameData.game();

            if (game != null) {
                gameServiceSQL.leaveGame(gameID, playername);

                var message = String.format("%s has left the game", playername);
                var notification2 = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
                connections.remove(gameID, playername);
                connections.broadcastInGame(gameID, playername, notification2);
            } else {
                var notificationError = ServerMessage.error("Error, invalid move");
    session.getRemote().sendString(new Gson().toJson(notificationError));                throw new IOException("Error: invalid move");
            }

    }



}