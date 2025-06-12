package websocket.commands;

import chess.ChessMove;
import chess.ChessPosition;

import java.util.Objects;

/**
 * Represents a command a user can send the server over a websocket
 *
 * Note: You can add to this class, but you should not alter the existing
 * methods.
 */
public class MakeMoveCommand extends UserGameCommand {

    public ChessMove move;

    public MakeMoveCommand(CommandType commandType, String authToken, Integer gameID, ChessMove move, String userName) {
        super(commandType,authToken,gameID, userName);

        this.move = move;
    }

}
