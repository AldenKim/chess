package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;

public class GameUI {
    private static ChessBoard testBoard = new ChessBoard();
    public static void run() {
        testBoard.resetBoard();
        displayChessBoardFromWhite();
        System.out.println();
        displayChessBoardFromBlack();
    }

    private static void displayChessBoardFromWhite() {
        System.out.println("   a  b  c  d  e  f  g  h ");
        for (int i = 8; i >= 1; i--) {
            System.out.print(i + " ");
            for (int j = 1; j <= 8; j++) {
                ChessPiece piece = testBoard.getPiece(new ChessPosition(i, j));
                if ((i + j) % 2 == 0) {
                    System.out.print(EscapeSequences.SET_BG_COLOR_DARK_GREY + textColorForPiece(piece) + pieceType(piece));
                } else {
                    System.out.print(EscapeSequences.SET_BG_COLOR_LIGHT_GREY + textColorForPiece(piece) + pieceType(piece));
                }
            }
            System.out.print(EscapeSequences.RESET_BG_COLOR);
            System.out.println(EscapeSequences.SET_TEXT_COLOR_WHITE);
        }
        System.out.println("   a  b  c  d  e  f  g  h ");
    }

    private static void displayChessBoardFromBlack() {
        System.out.println("   h  g  f  e  d  c  b  a ");
        for (int i = 1; i <= 8; i++) {
            System.out.print(i + " ");
            for (int j = 8; j >= 1; j--) {
                ChessPiece piece = testBoard.getPiece(new ChessPosition(i, j));
                if ((i + j) % 2 == 0) {
                    System.out.print(EscapeSequences.SET_BG_COLOR_DARK_GREY + textColorForPiece(piece) + pieceType(piece));
                } else {
                    System.out.print(EscapeSequences.SET_BG_COLOR_LIGHT_GREY + textColorForPiece(piece) + pieceType(piece));
                }
                System.out.print(EscapeSequences.SET_TEXT_COLOR_WHITE);
            }
            System.out.print(EscapeSequences.RESET_BG_COLOR);
            System.out.println(EscapeSequences.SET_TEXT_COLOR_WHITE);
        }
        System.out.println("   h  g  f  e  d  c  b  a ");
    }

    private static String pieceType(ChessPiece piece) {
        if (piece == null) {
            return "   ";
        }

        char pieceSymbol;
        switch (piece.getPieceType()) {
            case PAWN:
                pieceSymbol = 'p';
                break;
            case ROOK:
                pieceSymbol = 'r';
                break;
            case KNIGHT:
                pieceSymbol = 'n';
                break;
            case BISHOP:
                pieceSymbol = 'b';
                break;
            case QUEEN:
                pieceSymbol = 'q';
                break;
            case KING:
                pieceSymbol = 'k';
                break;
            default:
                pieceSymbol = ' '; // Handle any other cases here
                break;
        }

        if (piece.getTeamColor() == ChessGame.TeamColor.BLACK) {
            pieceSymbol = Character.toUpperCase(pieceSymbol);
        }

        return " " + pieceSymbol + " ";
    }

    private static String textColorForPiece(ChessPiece piece) {
        String textColor;
        if (piece == null){
            textColor = "";
        } else if (piece.getTeamColor() == ChessGame.TeamColor.BLACK) {
             textColor = EscapeSequences.SET_TEXT_COLOR_BLUE;
        } else {
            textColor = EscapeSequences.SET_TEXT_COLOR_RED;
        }
        return textColor;
    }

    public static void main(String[] args) {
        testBoard.resetBoard();
        displayChessBoardFromWhite();
        System.out.println();
        displayChessBoardFromBlack();
    }
}
