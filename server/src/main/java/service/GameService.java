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

    public CreateGameResult createGame(CreateGameRequest registerRequest) throws DataAccessException {
        if (this.gameDAO.getGame(registerRequest.getGameName()) == null) {
            GameData gameData = new GameData(0, null, null, registerRequest.getGameName(), null);

            GameData result = this.gameDAO.addGame(gameData);
            //cast to result

            return result;
        } else {
            throw new BadRequestException();
        }
    }


}

