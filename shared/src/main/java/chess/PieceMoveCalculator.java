package chess;

import java.util.ArrayList;
import java.util.Collection;

public class PieceMoveCalculator {
    public static Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition, ChessPiece.PieceType type, ChessGame.TeamColor color) {
        switch (type) {
            case KING:
                KingMovesCalculator kingMovesCalculator = new KingMovesCalculator();
                return kingMovesCalculator.pieceMoves(board, myPosition, color);
            case ROOK:
                RookMovesCalculator rookMovesCalculator = new RookMovesCalculator();
                return rookMovesCalculator.pieceMoves(board, myPosition, color);
            case QUEEN:
                QueenMovesCalculator queenMovesCalculator = new QueenMovesCalculator();
                return queenMovesCalculator.pieceMoves(board, myPosition, color);
            case BISHOP:
                BishopMovesCalculator bishopMovesCalculator = new BishopMovesCalculator();
                return bishopMovesCalculator.pieceMoves(board, myPosition, color);
            case KNIGHT:
                KnightMovesCalculator knightMovesCalculator = new KnightMovesCalculator();
                return knightMovesCalculator.pieceMoves(board, myPosition, color);
            case PAWN:
                PawnMovesCalculator pawnMovesCalculator = new PawnMovesCalculator();
                return pawnMovesCalculator.pieceMoves(board, myPosition, color);
        }
        return java.util.List.of();
    }
}
