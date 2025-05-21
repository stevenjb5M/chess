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
        CreatePawn(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN, 2, 1);
        CreatePawn(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN, 2, 2);
        CreatePawn(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN, 2, 3);
        CreatePawn(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN, 2, 4);
        CreatePawn(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN, 2, 5);
        CreatePawn(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN, 2, 6);
        CreatePawn(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN, 2, 7);
        CreatePawn(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN, 2, 8);
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

        CreatePawn(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN, 7, 1);
        CreatePawn(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN, 7, 2);
        CreatePawn(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN, 7, 3);
        CreatePawn(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN, 7, 4);
        CreatePawn(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN, 7, 5);
        CreatePawn(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN, 7, 6);
        CreatePawn(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN, 7, 7);
        CreatePawn(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN, 7, 8);

        ChessPiece blackKing = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KING);
        ChessPosition blackKingPos = new ChessPosition(8, 5);
        addPiece(blackKingPos, blackKing);

        ChessPiece blackQueen = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.QUEEN);
        ChessPosition blackQueenPos = new ChessPosition(8, 4);
        addPiece(blackQueenPos, blackQueen);

        ChessPiece blackRook1 = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK);
        ChessPosition blackRook1Pos = new ChessPosition(8, 1);
        addPiece(blackRook1Pos, blackRook1);

        ChessPiece blackRook2 = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK);
        ChessPosition blackRook2Pos = new ChessPosition(8, 8);
        addPiece(blackRook2Pos, blackRook2);

        ChessPiece blackKnight1 = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT);
        ChessPosition blackKnight1Pos = new ChessPosition(8, 2);
        addPiece(blackKnight1Pos, blackKnight1);

        ChessPiece blackKnight2 = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT);
        ChessPosition blackKnight2Pos = new ChessPosition(8, 7);
        addPiece(blackKnight2Pos, blackKnight2);

        ChessPiece blackBishop1 = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP);
        ChessPosition blackBishop1Pos = new ChessPosition(8, 3);
        addPiece(blackBishop1Pos, blackBishop1);

        ChessPiece blackBishop2 = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP);
        ChessPosition blackBishop2Pos = new ChessPosition(8, 6);
        addPiece(blackBishop2Pos, blackBishop2);
    }

    private void CreatePawn(ChessGame.TeamColor color, ChessPiece.PieceType type, int row, int col) {
        ChessPiece whitePawn1 = new ChessPiece(color, type);
        ChessPosition whitePawn1Pos = new ChessPosition(row, col);
        addPiece(whitePawn1Pos, whitePawn1);
    }
}
