package dataAccess;

import model.GameData;
import model.UserData;

public interface GameDAO {
    GameData addGame(GameData game) throws DataAccessException;

    GameData getGame(String gameName) throws DataAccessException;

}