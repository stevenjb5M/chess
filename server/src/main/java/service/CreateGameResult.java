package service;

public class CreateGameResult {
    private String gameName;
    private String gameId;

    public CreateGameResult(String gameName, String gameId) {
        this.gameName = gameName;
        this.gameId = gameId;
    }

    public String getGameName() {
        return gameName;
    }

    public String getGameId() {
        return gameId;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }


}