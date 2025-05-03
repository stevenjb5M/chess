package chess;

import java.util.ArrayList;
import java.util.Collection;

public class QueenMovesCalculator {

    public Collection<ChessMove> possibleMoves = new ArrayList<>();


    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition, ChessGame.TeamColor color) {
        int row = myPosition.getRow();
        int column = myPosition.getColumn();

        int[][] directions = {{1,1}, {1,-1}, {-1, -1}, {-1 , 1}, {1,0}, {-1, 0}, {0, 1}, {0, -1}};

        boolean isWhite = false;
        if (color == ChessGame.TeamColor.WHITE) {
            isWhite = true;
        }
        int move = isWhite ? 1 : -1;


        for (int[] dir: directions) {
            for (int i = 1; i < 8; i++) {
                ChessPosition pos1 = new ChessPosition(myPosition.getRow() + dir[0] * i, myPosition.getColumn() + dir[1] * i);
                ChessMove move1 = new ChessMove(myPosition, pos1, null);
                if (checkBounds(pos1)) {
                    ChessPiece currentPiece = board.getPiece(move1.getEndPosition());

                    if (currentPiece == null) {
                        possibleMoves.add(move1);
                    } else {
                        if (currentPiece.getTeamColor() != color) {
                            possibleMoves.add(move1);
                            break;
                        } else {
                            break;
                        }
                    }
                }
            }
        }


        return possibleMoves;

    }

    public boolean checkBounds(ChessPosition move) {
        return (move.getColumn() >0 && move.getColumn() <9 && move.getRow() > 0 && move.getRow() < 9);
    }

}
