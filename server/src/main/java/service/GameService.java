package service;

import dataAccess.*;
import model.AuthData;
import model.GameData;
import model.UserData;

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


}

