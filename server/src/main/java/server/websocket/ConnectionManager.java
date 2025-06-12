package server.websocket;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    private final ConcurrentHashMap<Integer, ConcurrentHashMap<String, Connection>> gameConnections = new ConcurrentHashMap<>();

    public void add(int gameID, String visitorName, Session session) {
        gameConnections.putIfAbsent(gameID, new ConcurrentHashMap<>());
        var connection = new Connection(visitorName, session);
        gameConnections.get(gameID).put(visitorName, connection);
    }

    public void remove(int gameID, String visitorName) {
        gameConnections.get(gameID).remove(visitorName);
        if (gameConnections.get(gameID).isEmpty()) {
            gameConnections.remove(gameID);
        }
    }

    public void broadcastInGame(int gameID, String excludeVisitorName, ServerMessage notification) throws IOException {
        var removeList = new ArrayList<Connection>();

        var game = gameConnections.get(gameID);

        for (var c : game.values()) {
            if (c.session.isOpen()) {
                if (!c.visitorName.equals(excludeVisitorName)) {
                var messageAsJson =  new Gson().toJson(notification);
                c.send(messageAsJson);
                }
            } else {
                removeList.add(c);
            }
        }

        // Clean up any connections that were left open.
        for (var c : removeList) {
            game.remove(c.visitorName);
        }
    }

    public void broadcastAllGame(int gameID, ServerMessage notification) throws IOException {
        var removeList = new ArrayList<Connection>();

        var game = gameConnections.get(gameID);

        for (var c : game.values()) {
            if (c.session.isOpen()) {
                    var messageAsJson =  new Gson().toJson(notification);
                    c.send(messageAsJson);
            } else {
                removeList.add(c);
            }
        }

        // Clean up any connections that were left open.
        for (var c : removeList) {
            game.remove(c.visitorName);
        }
    }

}
