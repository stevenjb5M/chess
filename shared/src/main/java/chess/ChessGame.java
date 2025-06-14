package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {



    private ChessBoard board;
    private TeamColor teamTurn;
    public boolean gameOver = false;
    public ChessGame() {
        this.board = new ChessBoard();
        this.board.resetBoard();
        this.teamTurn = TeamColor.WHITE;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return Objects.equals(board, chessGame.board) && teamTurn == chessGame.teamTurn;
    }

    @Override
    public int hashCode() {
        return Objects.hash(board, teamTurn);
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
        Collection<ChessMove> validMoves = validMoves(move.getStartPosition());
        ChessPosition startPos = move.getStartPosition();
        ChessPiece piece = board.getPiece(startPos);

        if (piece == null) {
            throw new InvalidMoveException("Invalid Move Error");
        }

        if (piece.getTeamColor() != getTeamTurn()) {
            throw new InvalidMoveException("Invalid Move Error");
        }

        if (!validMoves.contains(move)) {
            throw new InvalidMoveException("Invalid Move Error");
        }

        if (makeTheMove(board, move)) {
            if (getTeamTurn() == TeamColor.WHITE) {
                setTeamTurn(TeamColor.BLACK);
            } else {
                setTeamTurn(TeamColor.WHITE);
            }
        } else {
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
        return isEnd(teamColor, true);
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        return isEnd(teamColor, false);
    }

    private boolean isEnd(TeamColor teamColor, boolean needCheck) {
        if (inCheck(board, teamColor) != needCheck) {
            return false;
        }

        for (int i = 1; i < 9; i++) {
            for (int j = 1; j < 9; j++) {
                ChessPosition piecePos = new ChessPosition(i, j);
                ChessPiece pieceToCheck = board.getPiece(piecePos);

                if (pieceToCheck != null && pieceToCheck.getTeamColor() == teamColor) {
                    Collection<ChessMove> validMoves = validMoves(piecePos);

                    if (!validMoves.isEmpty()) {
                        return false;
                    }
                }
            }
        }
        return true;
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
                    Collection<ChessMove> moves = piece.pieceMoves(board1, position);
                    if (isKingReachable(moves, board1, teamColor)) {
                        return true;
                    }
                }
            }

        }

        return false;
    }

    private boolean isKingReachable(Collection<ChessMove> moves, ChessBoard board1, TeamColor teamColor) {
        for (ChessMove move : moves){
            ChessPiece landingPiece = board1.getPiece(move.getEndPosition());
            if (isKingOfTeam(landingPiece, teamColor)) {
                return true;
            }
        }
        return false;
    }



    private boolean isKingOfTeam(ChessPiece landingPiece, TeamColor teamColor) {
        if (landingPiece != null && landingPiece.getPieceType() == ChessPiece.PieceType.KING && landingPiece.getTeamColor() == teamColor) {
            return true;
        } else {
            return false;
        }
    }

    private boolean makeTheMove(ChessBoard board1, ChessMove move) {
        ChessPiece piece = board1.getPiece(move.getStartPosition());

        if (piece != null)
        {
            if (move.getPromotionPiece() != null) {
                piece.setPieceType(move.getPromotionPiece());
            }

            board1.addPiece(move.getEndPosition(), null);
            board1.addPiece(move.getEndPosition(), piece);

            int row = move.getStartPosition().getRow();
            int col = move.getStartPosition().getColumn();

            board1.squares[row-1][col-1] = null;

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
