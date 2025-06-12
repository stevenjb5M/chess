package dataaccess;

import model.GameData;
import server.JoinGameRequest;

import javax.xml.crypto.Data;
import java.util.Collection;

public interface GameDAO {
    GameData addGame(GameData game) throws DataAccessException;

    GameData getGame(String gameName) throws DataAccessException;

    GameData getGame(int gameID) throws DataAccessException;

    GameData joinGame(JoinGameRequest request, String username) throws DataAccessException;

    void removePlayer(int gameID, String playername) throws DataAccessException;
    void resign(int gameID) throws DataAccessException;

    Collection<GameData> listGames() throws DataAccessException;

    void clearGames() throws DataAccessException;
}