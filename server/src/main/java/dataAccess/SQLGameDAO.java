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
        Integer gameId = new Random().nextInt(1_000_000);
        GameData gameData = new GameData(gameId, game.whiteUsername(), game.blackUsername(), game.gameName(), game.game());

        games.put(gameId, gameData);

        return gameData;
    }

    @Override
    public GameData getGame(String gameName) throws DataAccessException {
        return null;
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        GameData data = games.get(gameID);
        return data;
    }

    @Override
    public GameData joinGame(JoinGameRequest request, String username) throws DataAccessException {
        GameData data = games.get(request.getGameID());
        if (request.getPlayerColor() == ChessGame.TeamColor.WHITE) {
            if (data.whiteUsername() == null) {
                GameData newGame = new GameData(data.gameID(), username, data.blackUsername(), data.gameName(), data.game());
                games.put(data.gameID(), newGame);
            } else {
                throw new UsernameTakenException();
            }
        } else {
            if (data.blackUsername() == null) {
                GameData newGame = new GameData(data.gameID(), data.whiteUsername(), username, data.gameName(), data.game());
                games.put(data.gameID(), newGame);
            } else {
                throw new UsernameTakenException();
            }
        }
        return data;
    }

    @Override
    public Collection<GameData> listGames() throws DataAccessException {
        Collection<GameData> gamesToAdd = new ArrayList<>(games.values());
        return gamesToAdd;
    }

    @Override
    public void clearGames() throws DataAccessException {
        games.clear();
    }


}
