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
    private ChessMove lastMove;
    public ChessGame() {
        chessBoard = new ChessBoard();
        chessBoard.resetBoard();
        turn = TeamColor.WHITE;
        lastMove = null;
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
        if(piece.getPieceType() == ChessPiece.PieceType.KING && canCastleKingSide(piece.getTeamColor()))
        {
            if(piece.getTeamColor() == TeamColor.WHITE){
                validMoves.add(new ChessMove(startPosition, new ChessPosition(1, 7), null));
            }
            else {
                validMoves.add(new ChessMove(startPosition, new ChessPosition(8, 7), null));
            }
        }
        if(piece.getPieceType() == ChessPiece.PieceType.KING && canCastleQueenSide(piece.getTeamColor()))
        {
            if(piece.getTeamColor() == TeamColor.WHITE){
                validMoves.add(new ChessMove(startPosition, new ChessPosition(1, 3), null));
            }
            else {
                validMoves.add(new ChessMove(startPosition, new ChessPosition(8, 3), null));
            }
        }

        if(piece.getPieceType() == ChessPiece.PieceType.PAWN && canEnPassantLeft(piece.getTeamColor(), startPosition))
        {
            if(piece.getTeamColor() == TeamColor.WHITE) {
                validMoves.add(new ChessMove(startPosition, new ChessPosition(startPosition.getRow() + 1, startPosition.getColumn() - 1), null));
            }
            else{
                validMoves.add(new ChessMove(startPosition, new ChessPosition(startPosition.getRow() - 1, startPosition.getColumn() - 1), null));
            }
        }

        if(piece.getPieceType() == ChessPiece.PieceType.PAWN && canEnPassantRight(piece.getTeamColor(), startPosition))
        {
            if(piece.getTeamColor() == TeamColor.WHITE) {
                validMoves.add(new ChessMove(startPosition, new ChessPosition(startPosition.getRow() + 1, startPosition.getColumn() + 1), null));
            }
            else{
                validMoves.add(new ChessMove(startPosition, new ChessPosition(startPosition.getRow() - 1, startPosition.getColumn() + 1), null));
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

        if(piece.getPieceType() == ChessPiece.PieceType.KING && (start.getColumn() - end.getColumn() == 2) && (start.getRow() == end.getRow()))
        {
            chessBoard.addPiece(end, piece);
            chessBoard.removePiece(start, piece);
            ChessPiece rook = chessBoard.getPiece(new ChessPosition(start.getRow(), 1));
            chessBoard.addPiece(new ChessPosition(start.getRow(), end.getColumn()+1), rook);
            chessBoard.removePiece(new ChessPosition(start.getRow(), 1), rook);
        }
        else if (piece.getPieceType() == ChessPiece.PieceType.KING && (start.getColumn() - end.getColumn() == -2) && (start.getRow() == end.getRow())){
            chessBoard.addPiece(end, piece);
            chessBoard.removePiece(start, piece);
            ChessPiece rook = chessBoard.getPiece(new ChessPosition(start.getRow(), 8));
            chessBoard.addPiece(new ChessPosition(start.getRow(), start.getColumn()+1), rook);
            chessBoard.removePiece(new ChessPosition(start.getRow(), 8), rook);
        }
        else if(piece.getPieceType() == ChessPiece.PieceType.PAWN && Math.abs(start.getColumn() - end.getColumn()) == 1 && chessBoard.getPiece(end) == null)
        {
            chessBoard.addPiece(end, piece);
            chessBoard.removePiece(start, piece);
            ChessPiece otherPawn = chessBoard.getPiece(new ChessPosition(start.getRow(), end.getColumn()));
            chessBoard.removePiece(new ChessPosition(start.getRow(), end.getColumn()), otherPawn);
        }
        else if(move.getPromotionPiece() == null) {
            chessBoard.addPiece(end, piece);
            chessBoard.removePiece(start, piece);
        }
        else if (move.getPromotionPiece() == ChessPiece.PieceType.QUEEN || move.getPromotionPiece() == ChessPiece.PieceType.KNIGHT || move.getPromotionPiece() == ChessPiece.PieceType. BISHOP|| move.getPromotionPiece() == ChessPiece.PieceType.ROOK)
        {
            chessBoard.addPiece(end, new ChessPiece(piece.getTeamColor(), move.getPromotionPiece()));
            chessBoard.removePiece(start, piece);
        }
        piece.setMoved();
        if (Math.abs(end.getRow() - start.getRow()) == 2 && piece.getPieceType() == ChessPiece.PieceType.PAWN)
        {
            piece.setDoubleMove();
        }
        lastMove = move;

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
    private boolean canCastleKingSide(TeamColor teamColor)
    {
        ChessPosition kingPos = findKingPosition(teamColor);

        if (kingPos == null)
        {
            return false;
        }

        ChessPiece king = chessBoard.getPiece(kingPos);

        if(king == null || king.hasMoved())
        {
            return false;
        }


        int rookCol = 8;
        if(chessBoard.getPiece(new ChessPosition(kingPos.getRow(),rookCol)) == null || chessBoard.getPiece(new ChessPosition(kingPos.getRow(),rookCol)).hasMoved())
        {
            return false;
        }

        for (int col = kingPos.getColumn() + 1; col < rookCol; col++) {
            if (chessBoard.getPiece(new ChessPosition(kingPos.getRow(), col)) != null) {
                return false;
            }
        }

        ChessPosition kingDest = new ChessPosition(kingPos.getRow(), kingPos.getColumn() + 2);
        ChessPosition rookSquare = new ChessPosition(kingPos.getRow(), kingPos.getColumn() + 1);

        if(isSquareUnderAttack(kingPos, teamColor) || isSquareUnderAttack(kingDest, teamColor) || isSquareUnderAttack(rookSquare, teamColor))
        {
            return false;
        }
        return true;
    }

    private boolean canCastleQueenSide(TeamColor teamColor)
    {
        ChessPosition kingPos = findKingPosition(teamColor);

        if (kingPos == null)
        {
            return false;
        }

        ChessPiece king = chessBoard.getPiece(kingPos);

        if(king == null || king.hasMoved())
        {
            return false;
        }

        int rookCol = 1;
        if(chessBoard.getPiece(new ChessPosition(kingPos.getRow(),rookCol)) == null || chessBoard.getPiece(new ChessPosition(kingPos.getRow(),rookCol)).hasMoved())
        {
            return false;
        }

        for (int col = rookCol + 1; col < kingPos.getColumn(); col++) {
            if (chessBoard.getPiece(new ChessPosition(kingPos.getRow(), col)) != null) {
                return false;
            }
        }

        ChessPosition kingDest = new ChessPosition(kingPos.getRow(), kingPos.getColumn() - 2);
        ChessPosition rookSquare = new ChessPosition(kingPos.getRow(), kingPos.getColumn() - 1);

        if(isSquareUnderAttack(kingPos, teamColor) || isSquareUnderAttack(kingDest, teamColor) || isSquareUnderAttack(rookSquare, teamColor))
        {
            return false;
        }
        return true;
    }
    private boolean isSquareUnderAttack (ChessPosition square, TeamColor attackingTeam)
    {
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPiece piece = chessBoard.getPiece(new ChessPosition(row, col));
                if (piece != null && piece.getTeamColor() != attackingTeam) {
                    Collection<ChessMove> moves = piece.pieceMoves(chessBoard, new ChessPosition(row, col));
                    if (moves != null && moves.contains(new ChessMove(new ChessPosition(row, col), square, null))) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean canEnPassantLeft(TeamColor teamColor, ChessPosition startPos)
    {
        int currentColumn = startPos.getColumn();
        int currentRow = startPos.getRow();

        if(currentColumn <= 1)
        {
            return false;
        }

        ChessPosition pawnNextPos = new ChessPosition(startPos.getRow(), startPos.getColumn()-1);

        if(pawnNextPos.getRow() < 1 || pawnNextPos.getRow() > 8 || pawnNextPos.getColumn() < 1 || pawnNextPos.getColumn() > 8)
        {
            return false;
        }

        ChessPiece pawnNext = chessBoard.getPiece(pawnNextPos);
        ChessPiece pawn = chessBoard.getPiece(startPos);

        if (pawnNext != null && pawnNext.getPieceType() == ChessPiece.PieceType.PAWN &&
                pawn != null && pawn.getPieceType() == ChessPiece.PieceType.PAWN) {
            if (pawnNext.hasDoubleMove() && pawn.getTeamColor() != pawnNext.getTeamColor() && lastMoveWasDouble(startPos)) {
                return true;
            }
        }
        return false;
    }

    private boolean canEnPassantRight(TeamColor teamColor, ChessPosition startPos)
    {
        int currentColumn = startPos.getColumn();
        int currentRow = startPos.getRow();

        if(currentColumn >= 8)
        {
            return false;
        }

        ChessPosition pawnNextPos = new ChessPosition(startPos.getRow(), startPos.getColumn()+1);

        if(pawnNextPos.getRow() < 1 || pawnNextPos.getRow() > 8 || pawnNextPos.getColumn() < 1 || pawnNextPos.getColumn() > 8)
        {
            return false;
        }

        ChessPiece pawnNext = chessBoard.getPiece(pawnNextPos);
        ChessPiece pawn = chessBoard.getPiece(startPos);

        if (pawnNext != null && pawnNext.getPieceType() == ChessPiece.PieceType.PAWN &&
                pawn != null && pawn.getPieceType() == ChessPiece.PieceType.PAWN) {
            if (pawnNext.hasDoubleMove() && pawn.getTeamColor() != pawnNext.getTeamColor() && lastMoveWasDouble(startPos)) {
                return true;
            }
        }
        return false;
    }

    private boolean lastMoveWasDouble (ChessPosition currentPosition)
    {
        if(lastMove != null && chessBoard.getPiece(lastMove.getEndPosition()).getPieceType() == ChessPiece.PieceType.PAWN)
        {
            ChessPosition start = lastMove.getStartPosition();
            ChessPosition end = lastMove.getEndPosition();

            int moveDirection = (chessBoard.getPiece(lastMove.getEndPosition()).getTeamColor() == TeamColor.WHITE) ? 1 : -1;

            return Math.abs(start.getRow()- end.getRow()) == 2;
        }
        return false;
    }
}
