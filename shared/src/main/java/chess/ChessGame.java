package chess;

import java.util.Collection;
import java.util.HashSet;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    private TeamColor turn;
    private ChessBoard chessBoard;
    public ChessGame() {
        chessBoard = new ChessBoard();
        chessBoard.resetBoard();

        turn = TeamColor.WHITE;
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return turn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        this.turn = team;
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
        ChessPiece piece = chessBoard.getPiece(startPosition);

        if (piece == null){
            return null;
        }

        Collection<ChessMove> allMoves = piece.pieceMoves(chessBoard, startPosition);
        Collection<ChessMove> validMoves = new HashSet<>();

        for(ChessMove move : allMoves)
        {
            ChessPosition start = move.getStartPosition();
            ChessPosition end = move.getEndPosition();
            ChessPiece checkForPiece = chessBoard.getPiece(end);

            chessBoard.addPiece(end, piece);
            chessBoard.removePiece(start, piece);

            if(!isInCheck(piece.getTeamColor()))
            {
                validMoves.add(move);
            }
            chessBoard.addPiece(start, piece);
            chessBoard.removePiece(end, piece);

            if(checkForPiece != null)
            {
                chessBoard.addPiece(end, checkForPiece);
            }
        }
        return validMoves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPiece piece = chessBoard.getPiece(move.getStartPosition());
        ChessPosition start = move.getStartPosition();
        ChessPosition end = move.getEndPosition();

        if(piece.getTeamColor() != turn)
        {
            throw new InvalidMoveException("Incorrect team turn");
        }

        Collection<ChessMove> validMoves = validMoves(move.getStartPosition());
        if(!validMoves.contains(move))
        {
            throw new InvalidMoveException("Illegal move given");
        }

        if(move.getPromotionPiece() == null) {
            chessBoard.addPiece(end, piece);
            chessBoard.removePiece(start, piece);
        }
        else if (move.getPromotionPiece() == ChessPiece.PieceType.QUEEN || move.getPromotionPiece() == ChessPiece.PieceType.KNIGHT || move.getPromotionPiece() == ChessPiece.PieceType. BISHOP|| move.getPromotionPiece() == ChessPiece.PieceType.ROOK)
        {
            chessBoard.addPiece(end, new ChessPiece(piece.getTeamColor(), move.getPromotionPiece()));
            chessBoard.removePiece(start, piece);
        }

        turn = (turn == TeamColor.WHITE) ? TeamColor.BLACK : TeamColor.WHITE;
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition kingPos = findKingPosition(teamColor);

        if(kingPos == null)
        {
            return false;
        }

        for(int row = 1; row < 9; row++)
        {
            for (int col = 1; col < 9; col++)
            {
                ChessPiece getPiece = chessBoard.getPiece(new ChessPosition(row, col));
                if(getPiece != null && getPiece.getTeamColor() != teamColor)
                {
                    Collection<ChessMove> getMoves = getPiece.pieceMoves(chessBoard, new ChessPosition(row, col));
                    if(getMoves != null && (getMoves.contains(new ChessMove(new ChessPosition(row, col), kingPos, null)) || getMoves.contains(new ChessMove(new ChessPosition(row, col), kingPos, ChessPiece.PieceType.QUEEN))))
                    {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        if(!isInCheck(teamColor))
        {
            return false;
        }

        for(int row = 1; row < 9; row++)
        {
            for (int col = 1; col < 9; col++)
            {
                ChessPiece getPiece = chessBoard.getPiece(new ChessPosition(row, col));
                if(getPiece != null && getPiece.getTeamColor() == teamColor)
                {
                    Collection<ChessMove> getMoves = validMoves(new ChessPosition(row, col));
                    if(getMoves != null && !getMoves.isEmpty())
                    {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        if(isInCheck(teamColor))
        {
            return false;
        }

        for(int row = 1; row < 9; row++)
        {
            for (int col = 1; col < 9; col++)
            {
                ChessPiece getPiece = chessBoard.getPiece(new ChessPosition(row, col));
                if(getPiece != null && getPiece.getTeamColor() == teamColor)
                {
                    Collection<ChessMove> getMoves = validMoves(new ChessPosition(row, col));
                    if(getMoves != null && !getMoves.isEmpty())
                    {
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
        this.chessBoard = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return chessBoard;
    }

    private ChessPosition findKingPosition(TeamColor teamColor)
    {
        for(int row = 1; row < 9; row++)
        {
            for(int col = 1; col < 9; col++)
            {
                ChessPiece getPiece = chessBoard.getPiece(new ChessPosition(row, col));
                if (getPiece != null && getPiece.getTeamColor() == teamColor && getPiece.getPieceType() == ChessPiece.PieceType.KING)
                {
                    return new ChessPosition(row,col);
                }
            }
        }
        return null;
    }
}
