package chess;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;

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
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition)
    {
        Collection<ChessMove> validMoves = new HashSet<>();
        switch(type){
            case KING:
                validMoves.addAll(getKingMoves(board, myPosition));
                break;
            case BISHOP:
                validMoves.addAll(getBishopMoves(board, myPosition));
                break;
            case ROOK:
                validMoves.addAll(getRookMoves(board, myPosition));
                break;
            case QUEEN:
                validMoves.addAll(getQueenMoves(board, myPosition));
                break;
            case KNIGHT:
                validMoves.addAll(getKnightMoves(board, myPosition));
                break;
            case PAWN:
                validMoves.addAll(getPawnMoves(board,myPosition));
                break;
        }
        return validMoves;
    }

    private Collection<ChessMove> getKingMoves (ChessBoard board, ChessPosition myPosition)
    {
        Collection<ChessMove> validMoves = new HashSet<>();

        int[] rowPositions = {-1, -1, -1, 0, 0, 1, 1, 1};
        int[] colPositions = {-1, 0, 1, -1, 1, -1, 0, 1};

        for (int i = 0; i < rowPositions.length; i++) {
            int newRowPos = myPosition.getRow() + rowPositions[i];
            int newColPos = myPosition.getColumn() + colPositions[i];

            if (newRowPos >= 1 && newRowPos <= 8 && newColPos >= 1 && newColPos <= 8) {
                ChessPosition newPos = new ChessPosition(newRowPos, newColPos);
                ChessPiece checkForPiece = board.getPiece(newPos);

                if (checkForPiece == null || checkForPiece.getTeamColor() != getTeamColor()) {
                    validMoves.add(new ChessMove(myPosition, newPos, null));
                }
            }
        }
        return validMoves;
    }

    private Collection<ChessMove> getKnightMoves (ChessBoard board, ChessPosition myPosition)
    {
        Collection <ChessMove> validMoves = new HashSet<>();

        int[] rowMoves = {2,2,-2,-2,1,1,-1,-1};
        int[] colMoves = {-1,1,-1,1,-2,2,-2,2};

        for(int i = 0; i < rowMoves.length; i++)
        {
            int newRowPos = myPosition.getRow() + rowMoves[i];
            int newColPos = myPosition.getColumn() + colMoves[i];

            if (newRowPos >= 1 && newRowPos <= 8 && newColPos >= 1 && newColPos <= 8) {
                ChessPosition newPos = new ChessPosition(newRowPos, newColPos);
                ChessPiece checkForPiece = board.getPiece(newPos);

                if (checkForPiece == null || checkForPiece.getTeamColor() != getTeamColor()) {
                    validMoves.add(new ChessMove(myPosition, newPos, null));
                }
            }
        }
        return validMoves;
    }

    private Collection<ChessMove> getBishopMoves(ChessBoard board, ChessPosition myPosition)
    {
        Collection<ChessMove> validMoves = new HashSet<>();
        int[] rowMoves = {-1,-1,1,1};
        int[] colMoves = {-1,1,-1,1};

        for(int i = 0; i < rowMoves.length; i++)
        {
            int newRowPos = myPosition.getRow();
            int newColPos = myPosition.getColumn();

            while (true)
            {
                newRowPos+=rowMoves[i];
                newColPos+=colMoves[i];

                if(newRowPos < 1 || newRowPos > 8 || newColPos < 1 || newColPos > 8)
                {
                    break;
                }

                ChessPosition newPos = new ChessPosition(newRowPos,newColPos);
                ChessPiece checkForPiece = board.getPiece(newPos);
                if(checkForPiece == null)
                {
                    validMoves.add(new ChessMove(myPosition, newPos, null));
                }
                else{
                    if(checkForPiece.getTeamColor() != getTeamColor())
                    {
                        validMoves.add(new ChessMove(myPosition, newPos, null));
                    }
                    break;
                }
            }
        }
        return validMoves;
    }

    private Collection<ChessMove> getRookMoves (ChessBoard board, ChessPosition myPosition)
    {
        Collection<ChessMove> validMoves = new HashSet<>();
        int[] rowMoves = {1,-1,0,0};
        int[] colMoves = {0,0,1,-1};

        for(int i = 0; i < rowMoves.length; i++) {
            int newRowPos = myPosition.getRow();
            int newColPos = myPosition.getColumn();

            while(true)
            {
                newRowPos += rowMoves[i];
                newColPos += colMoves[i];

                if(newRowPos < 1 || newRowPos > 8 || newColPos < 1 || newColPos > 8)
                {
                    break;
                }

                ChessPosition newPos = new ChessPosition(newRowPos, newColPos);
                ChessPiece checkForPiece = board.getPiece(newPos);

                if(checkForPiece == null)
                {
                    validMoves.add(new ChessMove(myPosition, newPos, null));
                }
                else
                {
                    if (checkForPiece.getTeamColor() != getTeamColor())
                    {
                        validMoves.add(new ChessMove(myPosition,newPos,null));
                    }
                    break;
                }
            }
        }
        return validMoves;
    }

    private Collection<ChessMove> getQueenMoves (ChessBoard board, ChessPosition myPosition)
    {
        Collection<ChessMove> validMoves = new HashSet<>();

        //Queen moves like a bishop
        validMoves.addAll(getBishopMoves(board,myPosition));
        //Queen also moves like a rook
        validMoves.addAll(getRookMoves(board,myPosition));

        return validMoves;
    }

    private Collection<ChessMove> getPawnMoves (ChessBoard board, ChessPosition myPosition)
    {
        Collection<ChessMove> validMoves = new HashSet<>();

        int direction;
        int initialRowPos;

        if(getTeamColor() == ChessGame.TeamColor.WHITE)
        {
            direction = 1;
            initialRowPos = 2;
        }
        else
        {
            direction = -1;
            initialRowPos = 7;
        }

        //Pawn starting move
        if(myPosition.getRow() == initialRowPos)
        {
            ChessPosition doubleMovePos = new ChessPosition(myPosition.getRow() + 2 * direction, myPosition.getColumn());
            if(board.getPiece(doubleMovePos) == null && board.getPiece(new ChessPosition(myPosition.getRow() + direction, myPosition.getColumn())) == null)
            {
                validMoves.add(new ChessMove(myPosition, doubleMovePos, null));
            }
        }

        //Single move
        ChessPosition singleMovePos = new ChessPosition(myPosition.getRow()+direction, myPosition.getColumn());
        if(board.getPiece(singleMovePos) == null && (singleMovePos.getRow() == 8 || singleMovePos.getRow() == 1))
        {
            validMoves.add(new ChessMove(myPosition, singleMovePos, PieceType.QUEEN));
            validMoves.add(new ChessMove(myPosition, singleMovePos, PieceType.BISHOP));
            validMoves.add(new ChessMove(myPosition, singleMovePos, PieceType.ROOK));
            validMoves.add(new ChessMove(myPosition, singleMovePos, PieceType.KNIGHT));
        }
        else if (board.getPiece(singleMovePos) == null)
        {
            validMoves.add(new ChessMove(myPosition,singleMovePos,null));
        }

        //Capturing
        int[] colMoves = {-1,1};
        for(int colMove : colMoves)
        {
            ChessPosition newPos = new ChessPosition(myPosition.getRow()+direction, myPosition.getColumn()+colMove);
            if(myPosition.getColumn()+colMove < 1 || myPosition.getColumn()+colMove > 8)
            {
                continue;
            }
            ChessPiece checkForPiece = board.getPiece(newPos);

            if(checkForPiece != null && checkForPiece.getTeamColor() != getTeamColor() && (newPos.getRow() == 8 || newPos.getRow() == 1))
            {
                validMoves.add(new ChessMove(myPosition, newPos, PieceType.QUEEN));
                validMoves.add(new ChessMove(myPosition, newPos, PieceType.BISHOP));
                validMoves.add(new ChessMove(myPosition, newPos, PieceType.ROOK));
                validMoves.add(new ChessMove(myPosition, newPos, PieceType.KNIGHT));
            }
            else if (checkForPiece != null && checkForPiece.getTeamColor() != getTeamColor())
            {
                validMoves.add(new ChessMove(myPosition, newPos, null));
            }
        }
        return validMoves;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessPiece that = (ChessPiece) o;
        return pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }
}
