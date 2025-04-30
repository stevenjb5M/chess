package chess;

import java.util.ArrayList;
import java.util.Collection;

public class PieceMoveCalculator {
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition, ChessPiece.PieceType type) {
        switch (type) {
            case KING:
                break;
            case ROOK:
                PawnMovesCalculator pawnMovesCalculator = new PawnMovesCalculator();
                return pawnMovesCalculator.pieceMoves(board, myPosition);
            break;
            case QUEEN:
                PawnMovesCalculator pawnMovesCalculator = new PawnMovesCalculator();
                return pawnMovesCalculator.pieceMoves(board, myPosition);
            break;
            case BISHOP:
                PawnMovesCalculator pawnMovesCalculator = new PawnMovesCalculator();
                return pawnMovesCalculator.pieceMoves(board, myPosition);
            break;
            case KNIGHT:
                PawnMovesCalculator pawnMovesCalculator = new PawnMovesCalculator();
                return pawnMovesCalculator.pieceMoves(board, myPosition);
            break;
            case PAWN:
                PawnMovesCalculator pawnMovesCalculator = new PawnMovesCalculator();
                return pawnMovesCalculator.pieceMoves(board, myPosition);
            break;
        }
    }
}
