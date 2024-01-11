package chess;

import java.util.Collection;
import java.util.HashSet;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private final ChessGame.TeamColor pieceColor;
    private final PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> validMoves = new HashSet<>();

        switch(type){
            case KING:
                validMoves.addAll(getKingMoves(board, myPosition));
                break;
            case BISHOP:
                validMoves.addAll(getBishopMoves(board,myPosition));
                break;
        }

        return validMoves;
    }

    private Collection<ChessMove> getKingMoves (ChessBoard board, ChessPosition myPositions) {
        Collection<ChessMove> validMoves = new HashSet<>();

        int[] rowPositions = {-1, -1, -1, 0, 0, 1, 1, 1};
        int[] colPositions = {-1, 0, 1, -1, 1, -1, 0, 1};

        for (int i = 0; i < rowPositions.length; i++) {
            int newRowPos = myPositions.getRow() + rowPositions[i];
            int newColPos = myPositions.getColumn() + colPositions[i];

            if (newRowPos >= 1 && newRowPos <= 8 && newColPos >= 1 && newColPos <= 8) {
                ChessPosition newPos = new ChessPosition(newRowPos, newColPos);

                ChessPiece checkForPiece = board.getPiece(newPos);

                if (checkForPiece == null || checkForPiece.getTeamColor() != getTeamColor()) {
                    validMoves.add(new ChessMove(myPositions, newPos, null));
                }
            }
        }
        return validMoves;
    }

    private Collection<ChessMove> getBishopMoves(ChessBoard board, ChessPosition myPosition)
    {
        Collection<ChessMove> validMoves = new HashSet<>();

        return validMoves;
    }
}
