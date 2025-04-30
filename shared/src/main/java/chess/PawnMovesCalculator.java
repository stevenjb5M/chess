package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PawnMovesCalculator {
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> possibleMoves = List.of();
        ChessPosition pos1 = new ChessPosition(myPosition.getRow(), myPosition.getColumn() + 1);
        ChessMove move1 = new ChessMove(myPosition, pos1, ChessPiece.PieceType.PAWN);
        possibleMoves.add(move1);

        ChessPosition pos2 = new ChessPosition(myPosition.getRow(), myPosition.getColumn() + 2);
        ChessMove move2 = new ChessMove(myPosition, pos2, ChessPiece.PieceType.PAWN);
        possibleMoves.add(move2);

        return possibleMoves;

    }
}
