package dataaccess;

import model.GameData;
import server.JoinGameRequest;

import java.util.Collection;

public interface GameDAO {
    GameData addGame(GameData game) throws DataAccessException;

    GameData getGame(String gameName) throws DataAccessException;

    GameData getGame(int gameID) throws DataAccessException;

    GameData joinGame(JoinGameRequest request, String username) throws DataAccessException;

    Collection<GameData> listGames() throws DataAccessException;

    void clearGames() throws DataAccessException;
}