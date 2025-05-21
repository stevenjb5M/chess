package service;

public class CreateGameResult {
    private int gameID;

    public CreateGameResult(int gameId) {
        this.gameID = gameId;
    }

    public int getGameID() {
        return gameID;
    }

    public void setGameID(int gameId) {
        this.gameID = gameId;
    }


}