package chess;

import java.util.ArrayList;
import java.util.Collection;

public class PieceMoveCalculator {
    public static Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition, ChessPiece.PieceType type, ChessGame.TeamColor color) {
        switch (type) {
            case KING:
                PawnMovesCalculator kingMovesCalculator = new PawnMovesCalculator();
                return kingMovesCalculator.pieceMoves(board, myPosition, color);
            case ROOK:
                PawnMovesCalculator rookMovesCalculator = new PawnMovesCalculator();
                return rookMovesCalculator.pieceMoves(board, myPosition, color);
            case QUEEN:
                PawnMovesCalculator queenMovesCalculator = new PawnMovesCalculator();
                return queenMovesCalculator.pieceMoves(board, myPosition, color);
            case BISHOP:
                PawnMovesCalculator bishopMovesCalculator = new PawnMovesCalculator();
                return bishopMovesCalculator.pieceMoves(board, myPosition, color);
            case KNIGHT:
                PawnMovesCalculator knightMovesCalculator = new PawnMovesCalculator();
                return knightMovesCalculator.pieceMoves(board, myPosition, color);
            case PAWN:
                PawnMovesCalculator pawnMovesCalculator = new PawnMovesCalculator();
                return pawnMovesCalculator.pieceMoves(board, myPosition, color);
        }
        return java.util.List.of();
    }
}
