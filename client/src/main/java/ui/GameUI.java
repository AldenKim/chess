package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.Scanner;

public class GameUI {
    private static ChessBoard testBoard = new ChessBoard();
    private static Scanner scanner = new Scanner(System.in);
    private static final String IN_GAME_PREFIX = "[IN-GAME] >>> ";
    public static void run() {
        testBoard.resetBoard();
        displayChessBoardFromWhite();
        System.out.println();
        displayChessBoardFromBlack();

        boolean running = true;
        System.out.print(EscapeSequences.SET_TEXT_COLOR_WHITE);
        System.out.print(EscapeSequences.SET_BG_COLOR_BLACK);
        while (running) {
            System.out.println("\nOptions:");
            System.out.println("1. Help");
            System.out.println("2. Redraw Chess Board");
            System.out.println("3. Leave");
            System.out.println("4. Make Move");
            System.out.println("5. Resign");
            System.out.println("6. Highlight Legal Moves");

            System.out.print(IN_GAME_PREFIX);
            String userInput = scanner.nextLine().toLowerCase();

            switch (userInput) {
                case "1":
                case "help":
                    displayHelpText();
                    break;
                case "2":
                case "redraw":
                    break;
                case "3":
                case "leave":
                    break;
                case "4":
                case "move":
                    break;
                case "5":
                case "resign":
                    break;
                case "6":
                case "legal moves":
                    break;
            }
        }

    }

    private static void displayHelpText() {
        System.out.println("Help - Possible commands");
        System.out.println("Redraw - Reprint the Chess Board");
        System.out.println("Leave - Exit Game, quit playing chess");
        System.out.println("Move - Make a move if it is your turn");
        System.out.println("Resign - Surrender the game");
        System.out.println("Legal Moves - Enter a piece and show where it can move");
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
}
