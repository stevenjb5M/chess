package server.websocket;

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
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.Collection;
import java.util.Timer;


@WebSocket
public class WebSocketHandler {

    private final ConnectionManager connections = new ConnectionManager();
    private String userName = "";

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
                    makeMove(action.getGameID(), action.getAuthToken(), session);
                } catch (DataAccessException e) {
                    throw new RuntimeException(e);
                }
            }
            case LEAVE -> {
            }
            case RESIGN -> {
            }
        }
    }

    private void connect(int gameID, String token, Session session) throws IOException, DataAccessException {

        AuthDAO authDAO = new SQLAuthDAO();
        AuthService authService = new AuthService(authDAO);

        if (!authService.checkIfAuthExisits(token)) {
            var notificationError = ServerMessage.error("Error, invalid auth token");
            var errorMessage = new Gson().toJson(notificationError);
            session.getRemote().sendString(errorMessage);
            throw new IOException("Error: Auth token was wrong");
        }

        String playername = authService.getUserByAuth(token);

        GameDAO gameDAO = new SQLGameDAO();
        GameService gameService = new GameService(gameDAO);
        Collection<GameData> games = gameService.listGames();

        connections.add(playername, session);



        boolean found = false;
        for (GameData gameData : games) {
            if (gameData.gameID() == gameID) {
                found = true;
                break;
            }
        }

        if (!found) {
            var notificationError = ServerMessage.error("Error, invalid game ID");
            connections.broadcastLoad(playername, notificationError);
            throw new IOException("Error: Game ID was wrong");
        }

        var message = String.format("%s has joined the game", playername);
        var notification = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME, gameID);
        var notification1 = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
        connections.broadcastLoad(playername, notification);
        connections.broadcastInGame(playername, notification1);
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

        GameDAO gameDAO = new SQLGameDAO();
        GameService gameService = new GameService(gameDAO);
        Collection<GameData> games = gameService.listGames();

        connections.add(playername, session);



        boolean found = false;
        for (GameData gameData : games) {
            if (gameData.gameID() == gameID) {
                found = true;
                break;
            }
        }

        if (!found) {
            var notificationError = ServerMessage.error("Error, invalid game ID");
            connections.broadcastLoad(playername, notificationError);
            throw new IOException("Error: Game ID was wrong");
        }

        var message = String.format("%s has joined the game", playername);
        var notification = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME, gameID);
        var notification1 = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
        connections.broadcastLoad(playername, notification);
        connections.broadcastInGame(playername, notification1);
    }

//    private void exit(String visitorName) throws IOException {
//        connections.remove(visitorName);
//        var message = String.format("%s left the shop", visitorName);
//        var notification = new Notification(Notification.Type.DEPARTURE, message);
//        connections.broadcast(visitorName, notification);
//    }
//
//    public void makeNoise(String petName, String sound) throws ResponseException {
//        try {
//            var message = String.format("%s says %s", petName, sound);
//            var notification = new Notification(Notification.Type.NOISE, message);
//            connections.broadcast("", notification);
//        } catch (Exception ex) {
//            throw new ResponseException(500, ex.getMessage());
//        }
//    }
}