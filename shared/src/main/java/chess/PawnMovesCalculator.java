package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PawnMovesCalculator {

    public Collection<ChessMove> possibleMoves = new ArrayList<>();


    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition, ChessGame.TeamColor color) {
        int row = myPosition.getRow();
        int column = myPosition.getColumn();


        boolean isWhite = false;
        if (color == ChessGame.TeamColor.WHITE) {
            isWhite = true;
        }
        int move = isWhite ? 1 : -1;
        ChessPosition pos1 = new ChessPosition(myPosition.getRow() + move, myPosition.getColumn());
        if (pos1.getColumn() >0 && pos1.getColumn() <9 && pos1.getRow() > 0 && pos1.getRow() < 9)
        {
            ChessMove move1 = new ChessMove(myPosition, pos1, ChessPiece.PieceType.PAWN);

            ChessPiece currentPiece = board.getPiece(move1.getEndPosition());

            if (currentPiece == null) {
                if (checkPromotions(myPosition, pos1, color)){
                    addPromotions(myPosition, pos1, color);
                } else {
                    possibleMoves.add(move1);
                }
            }

        }

        if ((myPosition.getRow() == 2 && isWhite) || (myPosition.getRow() == 7 && !isWhite)) {
            ChessPosition pos2 = new ChessPosition(myPosition.getRow() + (2 * move), myPosition.getColumn());
            if (pos2.getColumn() > 0 && pos2.getColumn() < 9 && pos2.getRow() > 0 && pos2.getRow() < 9) {
                ChessMove move2 = new ChessMove(myPosition, pos2, ChessPiece.PieceType.PAWN);


                ChessPosition nextPiecePos = new ChessPosition(myPosition.getRow() + move, myPosition.getColumn());

                ChessPiece currentPiece = board.getPiece(move2.getEndPosition());
                ChessPiece nextPiece = board.getPiece(nextPiecePos);


                if (currentPiece == null && nextPiece == null) {
                    if (checkPromotions(myPosition, pos2, color)){
                        addPromotions(myPosition, pos2, color);
                    } else {
                        possibleMoves.add(move2);
                    }
                }
            }
        }

        ChessPosition right = new ChessPosition(row + move, column + 1);
        if (checkBounds(right)) {
            ChessPiece rightPiece = board.getPiece(right);

            if (rightPiece != null && rightPiece.getTeamColor() != color) {
                //move top right and capture
                //move top right and capture

                ChessMove move3 = new ChessMove(myPosition, right, ChessPiece.PieceType.PAWN);
                if (checkPromotions(myPosition, right, color)){
                    addPromotions(myPosition, right, color);
                } else {
                    possibleMoves.add(move3);
                }
            }
        }

        ChessPosition left = new ChessPosition(row + move, column - 1);
        if (checkBounds(left)) {

            ChessPiece leftPiece = board.getPiece(left);

            if (leftPiece != null && leftPiece.getTeamColor() != color) {
                //move top right and capture
                ChessMove move4 = new ChessMove(myPosition, left, ChessPiece.PieceType.PAWN);
                if (checkPromotions(myPosition, left, color)){
                    addPromotions(myPosition, left, color);
                } else {
                    possibleMoves.add(move4);
                }
            }
        }


        return possibleMoves;

    }

    public boolean checkBounds(ChessPosition move) {
        return (move.getColumn() >0 && move.getColumn() <9 && move.getRow() > 0 && move.getRow() < 9);
    }

    public boolean checkPromotions(ChessPosition myPosition, ChessPosition move, ChessGame.TeamColor color) {
        boolean isPromoted = false;

        if (color == ChessGame.TeamColor.WHITE && move.getRow() == 8) {
            isPromoted = true;
        } else if (color == ChessGame.TeamColor.BLACK && move.getRow() == 1) {
            isPromoted = true;
        }

        return isPromoted;
    }

    public void addPromotions(ChessPosition myPosition, ChessPosition move, ChessGame.TeamColor color) {
        ChessMove move5 = new ChessMove(myPosition, move, ChessPiece.PieceType.QUEEN);
        ChessMove move6 = new ChessMove(myPosition, move, ChessPiece.PieceType.ROOK);
        ChessMove move7 = new ChessMove(myPosition, move, ChessPiece.PieceType.KNIGHT);
        ChessMove move8 = new ChessMove(myPosition, move, ChessPiece.PieceType.BISHOP);

        possibleMoves.add(move5);
        possibleMoves.add(move6);
        possibleMoves.add(move7);
        possibleMoves.add(move8);
    }
}
