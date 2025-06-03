package server;

public class CreateGameRequest {
    private String gameName;

    public CreateGameRequest(String gamename) {
        this.gameName = gamename;
    }

    public String getGameName() {
        return gameName;
    }
}