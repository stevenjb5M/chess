package service;

import chess.ChessGame;

public class JoinGameRequest {
    private final ChessGame.TeamColor playerColor;
    private final Integer gameID;

    public JoinGameRequest(ChessGame.TeamColor teamColor, Integer gameId) {
       playerColor = teamColor;
       gameID = gameId;
    }

    public Integer getGameID() {
        return gameID;
    }

    public ChessGame.TeamColor getPlayerColor() {
        return playerColor;
    }
}