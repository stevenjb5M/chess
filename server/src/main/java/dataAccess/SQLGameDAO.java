package dataAccess;

import chess.ChessGame;
import model.GameData;
import service.CreateGameResult;
import service.JoinGameRequest;

import java.util.*;

public class SQLGameDAO implements GameDAO {
    final private HashMap<Integer, GameData> games = new HashMap<>();


    @Override
    public GameData addGame(GameData game) throws DataAccessException {
        return null;
    }

    @Override
    public GameData getGame(String gameName) throws DataAccessException {
        return null;
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        return null;
    }

    @Override
    public GameData joinGame(JoinGameRequest request, String username) throws DataAccessException {
        return null;
    }

    @Override
    public Collection<GameData> listGames() throws DataAccessException {
        return List.of();
    }

    @Override
    public void clearGames() throws DataAccessException {

    }
}
