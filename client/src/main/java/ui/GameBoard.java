package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import model.GameData;

import static chess.ChessGame.TeamColor.WHITE;
import static ui.EscapeSequences.*;
import static ui.EscapeSequences.SET_BG_COLOR_LIGHT_GREY;

public class GameBoard {
    public String showGameBoard(GameData game, ChessGame.TeamColor playerColor) {

        String reset = "\u001B[0m";

        System.out.println();

        runHeaders(playerColor,reset);

        System.out.println();

        ChessBoard board = game.game().getBoard();

        boolean isLight = true;
        int startR = playerColor == WHITE ? 8 : 1;
        int endR = playerColor == WHITE ? 1 : 8;
        int dir = playerColor == WHITE ? -1 : 1;

        int startC = playerColor == WHITE ? 1 : 8;
        int endC = playerColor == WHITE ? 8 : 1;
        int dirC = playerColor == WHITE ? 1 : -1;


        for (int row = startR; row != endR + dir; row += dir) {
            System.out.print(SET_BG_COLOR_LIGHT_GREY + " " + row + " " + reset);
            isLight = playerColor == WHITE ? (row % 2 == 0) : (row % 2 != 0);

            for (int col = startC; col != endC + dirC; col += dirC) {

                String color = isLight ? SET_BG_COLOR_WHITE : SET_BG_COLOR_BLACK;


                ChessPosition pos = new ChessPosition(row,col);
                ChessPiece piece = board.getPiece(pos);


                if (piece != null) {
                    String textcolor = piece.getTeamColor() == WHITE ? SET_TEXT_COLOR_RED : SET_TEXT_COLOR_BLUE;
                    String letter = getOneLetterName(piece.getPieceType());
                    System.out.print(textcolor + color + " " + letter + " " + reset);
                } else {
                    System.out.print(color + "   " + reset);
                }

                isLight = !isLight;
            }
            System.out.println(SET_BG_COLOR_LIGHT_GREY + " " + row + " " + reset);
        }

        runHeaders(playerColor, reset);

        System.out.println();

        return "";
    }

    public void runHeaders(ChessGame.TeamColor color, String reset) {
        if (color == WHITE) {
            System.out.print(SET_BG_COLOR_LIGHT_GREY + "   " + reset);
            System.out.print(SET_BG_COLOR_LIGHT_GREY + " a " + reset);
            System.out.print(SET_BG_COLOR_LIGHT_GREY + " b " + reset);
            System.out.print(SET_BG_COLOR_LIGHT_GREY + " c " + reset);
            System.out.print(SET_BG_COLOR_LIGHT_GREY + " d " + reset);
            System.out.print(SET_BG_COLOR_LIGHT_GREY + " e " + reset);
            System.out.print(SET_BG_COLOR_LIGHT_GREY + " f " + reset);
            System.out.print(SET_BG_COLOR_LIGHT_GREY + " g " + reset);
            System.out.print(SET_BG_COLOR_LIGHT_GREY + " h " + reset);
            System.out.print(SET_BG_COLOR_LIGHT_GREY + "   " + reset);
        } else {
            System.out.print(SET_BG_COLOR_LIGHT_GREY + "   " + reset);
            System.out.print(SET_BG_COLOR_LIGHT_GREY + " h " + reset);
            System.out.print(SET_BG_COLOR_LIGHT_GREY + " g " + reset);
            System.out.print(SET_BG_COLOR_LIGHT_GREY + " f " + reset);
            System.out.print(SET_BG_COLOR_LIGHT_GREY + " e " + reset);
            System.out.print(SET_BG_COLOR_LIGHT_GREY + " d " + reset);
            System.out.print(SET_BG_COLOR_LIGHT_GREY + " c " + reset);
            System.out.print(SET_BG_COLOR_LIGHT_GREY + " b " + reset);
            System.out.print(SET_BG_COLOR_LIGHT_GREY + " a " + reset);
            System.out.print(SET_BG_COLOR_LIGHT_GREY + "   " + reset);
        }
    }

    public String getOneLetterName(ChessPiece.PieceType type) {
        switch (type) {
            case KING: { return "K";}
            case QUEEN: { return "Q";}
            case KNIGHT: { return "N";}
            case ROOK: { return "R";}
            case BISHOP: { return "B";}
            case PAWN: { return "P";}
            default: return "";
        }
    }
}
