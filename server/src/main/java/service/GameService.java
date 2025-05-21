package service;

import dataAccess.*;
import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.Collection;

public class GameService {

    private final GameDAO gameDAO;

    public GameService(GameDAO gameDAQ) {
        this.gameDAO = gameDAQ;
    }

    public CreateGameResult createGame(CreateGameRequest createGameRequest) throws DataAccessException {
        if (this.gameDAO.getGame(createGameRequest.getGameName()) == null && createGameRequest.getGameName() != null) {
            GameData gameData = new GameData(0, null, null, createGameRequest.getGameName(), null);

            GameData gameData1 = this.gameDAO.addGame(gameData);
            //cast to result
            CreateGameResult result = new CreateGameResult(gameData1.gameID());

            return result;
        } else {
            throw new BadRequestException();
        }
    }

    public void joinGame(JoinGameRequest joinGameRequest, String username) throws DataAccessException {
        if (joinGameRequest.getGameID() != null && this.gameDAO.getGame(joinGameRequest.getGameID()) != null && joinGameRequest.getPlayerColor() != null && username != null) {

            this.gameDAO.joinGame(joinGameRequest, username);

        } else {
            throw new BadRequestException();
        }
    }

    public Collection<GameData> listGames() throws DataAccessException {
        return gameDAO.listGames();
    }

    public void clearGames() throws DataAccessException {
        gameDAO.clearGames();
    }

    public String getBlackPlayerUsername(Integer gameID) throws DataAccessException {
        GameData data = gameDAO.getGame(gameID);

        return data.blackUsername();
    }

    public String getWhitePlayerUsername(Integer gameID) throws DataAccessException {
        GameData data = gameDAO.getGame(gameID);

        return data.whiteUsername();
    }


}

