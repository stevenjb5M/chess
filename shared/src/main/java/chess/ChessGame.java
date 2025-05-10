package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private ChessBoard board;
    private TeamColor teamTurn;
    public ChessGame() {
        this.board = new ChessBoard();
        this.board.resetBoard();
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return teamTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        teamTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece currentPiece = board.getPiece(startPosition);

        if (currentPiece != null) {
            Collection<ChessMove> validMoves = new ArrayList<>();
            validMoves = currentPiece.pieceMoves(board, startPosition);

            if (validMoves.size() == 0) {
                return validMoves;
            }

            Iterator<ChessMove> iterator = validMoves.iterator();
            while (iterator.hasNext()) {
                ChessMove move = iterator.next();
                ChessBoard testBoard = copyBoard();
                makeTheMove(testBoard, move);

                if (inCheck(testBoard, currentPiece.getTeamColor())) {
                    iterator.remove();
                }

            }

            return validMoves;
        }

        return null;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        if (!makeTheMove(board, move)) {
            throw new InvalidMoveException("Invalid Move Error");
        }
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */

    //Optimize to find kings position first
    public boolean isInCheck(TeamColor teamColor) {
        return inCheck(board, teamColor);
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }

    private boolean inCheck(ChessBoard board1, TeamColor teamColor) {
        Collection<ChessPiece> piecesToCheck = new ArrayList<>();
        TeamColor teamToCheck = teamColor == TeamColor.BLACK ? TeamColor.WHITE : TeamColor.BLACK;

        for (int i = 1; i < 9; i++) {
            for (int j = 1; j < 9; j++) {
                ChessPosition position = new ChessPosition(i,j);
                ChessPiece piece = board1.getPiece(position);

                if (piece != null && piece.getTeamColor() == teamToCheck) {
                    Collection<ChessMove> moves = piece.pieceMoves(board, position);

                    for (ChessMove move : moves){
                        ChessPiece landingPiece = board.getPiece(move.getEndPosition());
                        if (landingPiece != null && landingPiece.getPieceType() == ChessPiece.PieceType.KING && landingPiece.getTeamColor() == teamColor) {
                            return true;
                        }
                    }
                }
            }

        }

        return false;
    }

    private boolean makeTheMove(ChessBoard board1, ChessMove move) {
        ChessPiece piece = board1.getPiece(move.getStartPosition());

        if (piece != null && piece.getTeamColor() == getTeamTurn())
        {
            if (move.getPromotionPiece() != null) {
                piece.setPieceType(move.getPromotionPiece());
            }
            board1.addPiece(move.getEndPosition(), piece);

            int row = move.getStartPosition().getRow();
            int col = move.getStartPosition().getColumn();

            board1.squares[row][col] = null;

            if (getTeamTurn() == TeamColor.WHITE) {
                setTeamTurn(TeamColor.BLACK);
            } else {
                setTeamTurn(TeamColor.WHITE);
            }
            return true;
        } else {
            return false;
        }
    }

    private ChessBoard copyBoard() {
        ChessBoard newBoard = new ChessBoard();

        for (int i = 1; i < 9; i++) {
            for (int j = 1; j < 9; j++) {
                ChessPosition position = new ChessPosition(i,j);
                ChessPiece pieceToCopy = board.getPiece(position);

                if (pieceToCopy != null) {
                    ChessPiece newPiece = new ChessPiece(pieceToCopy.getTeamColor(), pieceToCopy.getPieceType());
                    newBoard.addPiece(position, newPiece);
                }
            }

        }

        return newBoard;
    }

}
