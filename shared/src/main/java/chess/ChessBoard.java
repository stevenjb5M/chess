package chess;

import java.util.Arrays;
import java.util.Objects;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {

    public ChessPiece[][] squares = new ChessPiece[8][8];
    public ChessBoard() {
        
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessBoard that = (ChessBoard) o;
        return Objects.deepEquals(squares, that.squares);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(squares);
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        squares[position.getRow()-1][position.getColumn()-1] = piece;
    }
    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return squares[position.getRow()-1][position.getColumn()-1];
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        squares = new ChessPiece[8][8];

        ChessPiece whitePawn1 = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        ChessPosition whitePawn1Pos = new ChessPosition(2, 1);
        addPiece(whitePawn1Pos, whitePawn1);

        ChessPiece whitePawn2 = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        ChessPosition whitePawn2Pos = new ChessPosition(2, 2);
        addPiece(whitePawn2Pos, whitePawn2);

        ChessPiece whitePawn3 = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        ChessPosition whitePawn3Pos = new ChessPosition(2, 3);
        addPiece(whitePawn3Pos, whitePawn3);

        ChessPiece whitePawn4 = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        ChessPosition whitePawn4Pos = new ChessPosition(2, 4);
        addPiece(whitePawn4Pos, whitePawn4);

        ChessPiece whitePawn5 = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        ChessPosition whitePawn5Pos = new ChessPosition(2, 5);
        addPiece(whitePawn5Pos, whitePawn5);

        ChessPiece whitePawn6 = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        ChessPosition whitePawn6Pos = new ChessPosition(2, 6);
        addPiece(whitePawn6Pos, whitePawn6);

        ChessPiece whitePawn7 = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        ChessPosition whitePawn7Pos = new ChessPosition(2, 7);
        addPiece(whitePawn7Pos, whitePawn7);

        ChessPiece whitePawn8 = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        ChessPosition whitePawn8Pos = new ChessPosition(2, 8);
        addPiece(whitePawn8Pos, whitePawn8);

        ChessPiece whiteKing = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KING);
        ChessPosition whiteKingPos = new ChessPosition(1, 5);
        addPiece(whiteKingPos, whiteKing);

        ChessPiece whiteQueen = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.QUEEN);
        ChessPosition whiteQueenPos = new ChessPosition(1, 4);
        addPiece(whiteQueenPos, whiteQueen);

        ChessPiece whiteRook1 = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK);
        ChessPosition whiteRook1Pos = new ChessPosition(1, 1);
        addPiece(whiteRook1Pos, whiteRook1);

        ChessPiece whiteRook2 = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK);
        ChessPosition whiteRook2Pos = new ChessPosition(1, 8);
        addPiece(whiteRook2Pos, whiteRook2);

        ChessPiece whiteKnight1 = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT);
        ChessPosition whiteKnight1Pos = new ChessPosition(1, 2);
        addPiece(whiteKnight1Pos, whiteKnight1);

        ChessPiece whiteKnight2 = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT);
        ChessPosition whiteKnight2Pos = new ChessPosition(1, 7);
        addPiece(whiteKnight2Pos, whiteKnight2);

        ChessPiece whiteBishop1 = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP);
        ChessPosition whiteBishop1Pos = new ChessPosition(1, 3);
        addPiece(whiteBishop1Pos, whiteBishop1);

        ChessPiece whiteBishop2 = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP);
        ChessPosition whiteBishop2Pos = new ChessPosition(1, 6);
        addPiece(whiteBishop2Pos, whiteBishop2);

        ChessPiece BlackPawn1 = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN);
        ChessPosition BlackPawn1Pos = new ChessPosition(7, 1);
        addPiece(BlackPawn1Pos, BlackPawn1);

        ChessPiece BlackPawn2 = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN);
        ChessPosition BlackPawn2Pos = new ChessPosition(7, 2);
        addPiece(BlackPawn2Pos, BlackPawn2);

        ChessPiece BlackPawn3 = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN);
        ChessPosition BlackPawn3Pos = new ChessPosition(7, 3);
        addPiece(BlackPawn3Pos, BlackPawn3);

        ChessPiece BlackPawn4 = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN);
        ChessPosition BlackPawn4Pos = new ChessPosition(7, 4);
        addPiece(BlackPawn4Pos, BlackPawn4);

        ChessPiece BlackPawn5 = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN);
        ChessPosition BlackPawn5Pos = new ChessPosition(7, 5);
        addPiece(BlackPawn5Pos, BlackPawn5);

        ChessPiece BlackPawn6 = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN);
        ChessPosition BlackPawn6Pos = new ChessPosition(7, 6);
        addPiece(BlackPawn6Pos, BlackPawn6);

        ChessPiece BlackPawn7 = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN);
        ChessPosition BlackPawn7Pos = new ChessPosition(7, 7);
        addPiece(BlackPawn7Pos, BlackPawn7);

        ChessPiece BlackPawn8 = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN);
        ChessPosition BlackPawn8Pos = new ChessPosition(7, 8);
        addPiece(BlackPawn8Pos, BlackPawn8);

        ChessPiece BlackKing = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KING);
        ChessPosition BlackKingPos = new ChessPosition(8, 5);
        addPiece(BlackKingPos, BlackKing);

        ChessPiece BlackQueen = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.QUEEN);
        ChessPosition BlackQueenPos = new ChessPosition(8, 4);
        addPiece(BlackQueenPos, BlackQueen);

        ChessPiece BlackRook1 = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK);
        ChessPosition BlackRook1Pos = new ChessPosition(8, 1);
        addPiece(BlackRook1Pos, BlackRook1);

        ChessPiece BlackRook2 = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK);
        ChessPosition BlackRook2Pos = new ChessPosition(8, 8);
        addPiece(BlackRook2Pos, BlackRook2);

        ChessPiece BlackKnight1 = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT);
        ChessPosition BlackKnight1Pos = new ChessPosition(8, 2);
        addPiece(BlackKnight1Pos, BlackKnight1);

        ChessPiece BlackKnight2 = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT);
        ChessPosition BlackKnight2Pos = new ChessPosition(8, 7);
        addPiece(BlackKnight2Pos, BlackKnight2);

        ChessPiece BlackBishop1 = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP);
        ChessPosition BlackBishop1Pos = new ChessPosition(8, 3);
        addPiece(BlackBishop1Pos, BlackBishop1);

        ChessPiece BlackBishop2 = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP);
        ChessPosition BlackBishop2Pos = new ChessPosition(8, 6);
        addPiece(BlackBishop2Pos, BlackBishop2);
    }
}
