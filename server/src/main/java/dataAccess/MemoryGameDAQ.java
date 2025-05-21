package dataAccess;

import model.GameData;
import service.CreateGameResult;

import java.util.HashMap;
import java.util.Random;

public class MemoryGameDAQ implements GameDAO {
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
}
