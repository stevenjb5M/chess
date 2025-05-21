package service;

import chess.ChessGame;

public class JoinRequest {
    private final ChessGame.TeamColor playerColor;
    private final Integer gameID;

    public JoinRequest(ChessGame.TeamColor teamColor, Integer GameID) {
       playerColor = teamColor;
       gameID = GameID;
    }
}