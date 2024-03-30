package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import com.google.gson.Gson;
import webSocketMessages.serverMessages.LoadGameMessage;
import webSocketMessages.serverMessages.NotificationMessage;

import java.util.Scanner;

public class GameUI implements GameHandler{
    private static ChessGame game;
    private static ChessBoard testBoard;
    private static Scanner scanner = new Scanner(System.in);
    private static final String IN_GAME_PREFIX = "[IN-GAME] >>> ";
    private static final String BASE_URL = "http://localhost:8080";
    private static final Gson gson = new Gson();

    private WebSocketFacade ws = new WebSocketFacade(BASE_URL, GameUI.this);

    private static ChessGame.TeamColor teamColor;
    public GameUI(ChessGame.TeamColor teamColor, ChessGame game) {
        this.teamColor = teamColor;
        this.game = game;
        testBoard = game.getBoard();
    }

    public void run() {
        reDrawBoard();

        boolean running = true;
        System.out.print(EscapeSequences.SET_TEXT_COLOR_WHITE);
        System.out.print(EscapeSequences.SET_BG_COLOR_BLACK);
        System.out.println("\nOptions:");
        System.out.println("1. Help");
        System.out.println("2. Redraw Chess Board");
        System.out.println("3. Leave");
        System.out.println("4. Make Move");
        System.out.println("5. Resign");
        System.out.println("6. Highlight Legal Moves");
        while (running) {
            System.out.print(IN_GAME_PREFIX);
            String userInput = scanner.nextLine().toLowerCase();

            switch (userInput) {
                case "1":
                case "help":
                    displayHelpText();
                    break;
                case "2":
                case "redraw":
                    reDrawBoard();
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
                    displayLegalMoves(testBoard);
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

    private void reDrawBoard() {
        if(teamColor == ChessGame.TeamColor.WHITE) {
            displayChessBoardFromWhite(testBoard);
        } else if(teamColor == ChessGame.TeamColor.BLACK) {
            displayChessBoardFromBlack(testBoard);
        } else {
            displayChessBoardFromWhite(testBoard);
        }
    }

    private void displayLegalMoves (ChessBoard board) {
        System.out.println("Enter Column: ");
        String column = scanner.nextLine().toLowerCase();
        int colVal = colStringToNum(column);
        System.out.println("Enter Row: ");
        String row = scanner.nextLine().toLowerCase();
        int rowVal = Integer.parseInt(row);
        if(colVal < 1 || colVal > 8 || rowVal < 1 || rowVal > 8) {
            System.out.println("Incorrect input on the board");
            return;
        }


    }

    private void leave() {

    }

    public static void displayChessBoardFromWhite(ChessBoard board) {
        System.out.println("   a  b  c  d  e  f  g  h ");
        for (int i = 8; i >= 1; i--) {
            System.out.print(i + " ");
            for (int j = 1; j <= 8; j++) {
                ChessPiece piece = board.getPiece(new ChessPosition(i, j));
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

    public static void displayChessBoardFromBlack(ChessBoard board) {
        System.out.println("   h  g  f  e  d  c  b  a ");
        for (int i = 1; i <= 8; i++) {
            System.out.print(i + " ");
            for (int j = 8; j >= 1; j--) {
                ChessPiece piece = board.getPiece(new ChessPosition(i, j));
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

    @Override
    public void updateGame(LoadGameMessage game) {
        Object updatedGame = game.getGame();
        ChessGame chessGame = gson.fromJson(updatedGame.toString(), ChessGame.class);
        System.out.println("RECEIVED GAME MESSAGE");
        this.game = chessGame;
        this.testBoard = chessGame.getBoard();
        if(teamColor == ChessGame.TeamColor.WHITE) {
            displayChessBoardFromWhite(testBoard);
        } else if (teamColor == ChessGame.TeamColor.BLACK){
            displayChessBoardFromBlack(testBoard);
        } else {
            displayChessBoardFromWhite(testBoard);
        }
    }

    @Override
    public void printMessage(NotificationMessage message) {
        System.out.println(message.getMessage());
        System.out.print("[IN-GAME] >>> ");
    }

    private int colStringToNum(String col) {
        int colNum = 0;
        switch (col) {
            case "a":
                colNum = 1;
                break;
            case "b":
                colNum = 2;
                break;
            case "c":
                colNum = 3;
                break;
            case "d":
                colNum = 4;
                break;
            case "e":
                colNum = 5;
                break;
            case "f":
                colNum = 6;
                break;
            case "g":
                colNum = 7;
                break;
            case "h":
                colNum = 8;
                break;
        }
        return colNum;
    }
}
