package ui;

import chess.*;
import com.google.gson.Gson;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;

import java.io.IOException;
import java.util.Collection;
import java.util.Scanner;

public class GameUI implements GameHandler{
    private static ChessGame game;
    private static ChessBoard testBoard;
    private static Scanner scanner = new Scanner(System.in);
    private static final String IN_GAME_PREFIX = "[IN-GAME] >>> ";
    private static final String BASE_URL = "http://localhost:8080";
    private static final Gson GSON = new Gson();

    private WebSocketFacade ws = new WebSocketFacade(BASE_URL, GameUI.this);

    private static ChessGame.TeamColor teamColor;
    private static String authToken;
    private static int gameID;

    public GameUI(ChessGame.TeamColor teamColor, String authToken, int gameID) {
        this.teamColor = teamColor;
        GameUI.authToken = authToken;
        this.gameID = gameID;
    }

    public void run() {
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
        ws.connectPlayer(authToken, gameID, teamColor);

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
                    leave();
                    running = false;
                    break;
                case "4":
                case "move":
                    makeMove(testBoard);
                    break;
                case "5":
                case "resign":
                    resign();
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
        ChessPosition position = new ChessPosition(rowVal, colVal);
        ChessPiece piece = board.getPiece(position);
        if(piece == null) {
            System.out.println("No piece on the board");
        }
        Collection<ChessMove> validMoves = game.validMoves(position);
        reDrawBoardWithMoves(board, validMoves);
    }

    private void makeMove(ChessBoard board) {
        if(teamColor == null) {
            System.out.println("Cannot make move as observer");
            return;
        }
        if(game.getTeamTurn() == null) {
            System.out.println("Cannot make move, game is over.");
            return;
        }
        System.out.println("Enter Column: ");
        String column = scanner.nextLine().toLowerCase();
        int colVal = colStringToNum(column);
        System.out.println("Enter Row: ");
        String row = scanner.nextLine().toLowerCase();
        int rowVal = Integer.parseInt(row);
        ChessPiece piece = board.getPiece(new ChessPosition(rowVal, colVal));
        if(piece == null) {
            System.out.println("No piece on that square");
            return;
        }
        if(colVal < 1 || colVal > 8 || rowVal < 1 || rowVal > 8) {
            System.out.println("Incorrect input on the board");
            return;
        }
        System.out.println("Enter Column Where You Want To Move: ");
        column = scanner.nextLine().toLowerCase();
        int newColVal = colStringToNum(column);
        System.out.println("Enter Row Where You Want To Move: ");
        row = scanner.nextLine().toLowerCase();
        int newRowVal = Integer.parseInt(row);
        if(newColVal < 1 || newColVal > 8 || newRowVal < 1 || newRowVal > 8) {
            System.out.println("Incorrect input on the board");
            return;
        }
        ChessMove newMove = new ChessMove(new ChessPosition(rowVal, colVal), new ChessPosition(newRowVal, newColVal), null);
        Collection<ChessMove> validMoves = game.validMoves(new ChessPosition(rowVal, colVal));
        if(!validMoves.contains(newMove)) {
            System.out.println("Invalid move.");
            return;
        }
        if(piece.getPieceType() == ChessPiece.PieceType.PAWN) {
            if(piece.getTeamColor() == ChessGame.TeamColor.WHITE && newRowVal == 8 || piece.getTeamColor() == ChessGame.TeamColor.BLACK && newRowVal == 1) {
                System.out.println("Enter what piece you would like to promote to.(q, k, b, r)");
                String newPiece = scanner.nextLine().toLowerCase();
                switch (newPiece) {
                    case "q":
                        newMove = new ChessMove(new ChessPosition(rowVal, colVal), new ChessPosition(newRowVal, newColVal), ChessPiece.PieceType.QUEEN);
                        break;
                    case "k":
                        newMove = new ChessMove(new ChessPosition(rowVal, colVal), new ChessPosition(newRowVal, newColVal), ChessPiece.PieceType.KNIGHT);
                        break;
                    case "b":
                        newMove = new ChessMove(new ChessPosition(rowVal, colVal), new ChessPosition(newRowVal, newColVal), ChessPiece.PieceType.BISHOP);
                        break;
                    case "r":
                        newMove = new ChessMove(new ChessPosition(rowVal, colVal), new ChessPosition(newRowVal, newColVal), ChessPiece.PieceType.ROOK);
                        break;
                    default:
                        System.out.println("Not Valid.");
                }
            }
        }
        ws.makeMove(gameID, newMove, authToken);
    }

    private void leave() {
        try {
            ws.leave(gameID, authToken);
            System.out.println("You have left the game.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void resign() {
        System.out.println("Are you sure you want to resign? (Y/N)");
        String answer = scanner.nextLine().toLowerCase();
        if(answer.equals("y")) {
            ws.resign(gameID, authToken);
            game.setTeamTurn(null);
        } else if (answer.equals("n")){
            System.out.println("Continuing with the game.");
        }
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

    private void reDrawBoardWithMoves(ChessBoard board, Collection<ChessMove> moves) {
        if(teamColor == ChessGame.TeamColor.WHITE) {
            displayChessBoardFromWhiteWithMoves(board, moves);
        } else if(teamColor == ChessGame.TeamColor.BLACK) {
            displayChessBoardFromBlackWithMoves(board, moves);
        } else {
            displayChessBoardFromWhiteWithMoves(board, moves);
        }
    }

    public static void displayChessBoardFromWhiteWithMoves(ChessBoard board, Collection<ChessMove> moves) {
        System.out.println("   a  b  c  d  e  f  g  h ");
        for (int i = 8; i >= 1; i--) {
            System.out.print(i + " ");
            helperForLegal(board, moves, i);
            System.out.print(EscapeSequences.RESET_BG_COLOR);
            System.out.println(EscapeSequences.SET_TEXT_COLOR_WHITE);
        }
        System.out.println("   a  b  c  d  e  f  g  h ");
    }

    public static void displayChessBoardFromBlackWithMoves(ChessBoard board, Collection<ChessMove> moves) {
        System.out.println("   h  g  f  e  d  c  b  a ");
        for (int i = 1; i <= 8; i++) {
            System.out.print(i + " ");
            helperForLegal(board, moves, i);
            System.out.print(EscapeSequences.RESET_BG_COLOR);
            System.out.println(EscapeSequences.SET_TEXT_COLOR_WHITE);
        }
        System.out.println("   h  g  f  e  d  c  b  a ");
    }

    private static void helperForLegal(ChessBoard board, Collection<ChessMove> moves, int i) {
        for (int j = 8; j >= 1; j--) {
            ChessPiece piece = board.getPiece(new ChessPosition(i, j));
            ChessPosition currentPosition = new ChessPosition(i, j);
            boolean isLegalMove = false;
            boolean highlightedPiece = false;
            for (ChessMove move : moves) {
                if (move.getEndPosition().equals(currentPosition)) {
                    isLegalMove = true;
                    break;
                } if(move.getStartPosition().equals(currentPosition)) {
                    highlightedPiece = true;
                    break;
                }
            }
            if (isLegalMove) {
                System.out.print(EscapeSequences.SET_BG_COLOR_YELLOW);
            }else if (highlightedPiece) {
                System.out.print(EscapeSequences.SET_BG_COLOR_GREEN);
            } else if ((i + j) % 2 == 0) {
                System.out.print(EscapeSequences.SET_BG_COLOR_DARK_GREY);
            } else {
                System.out.print(EscapeSequences.SET_BG_COLOR_LIGHT_GREY);
            }
            System.out.print(textColorForPiece(piece) + pieceType(piece));
        }
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
        ChessGame chessGame = GSON.fromJson(updatedGame.toString(), ChessGame.class);
        System.out.println("RECEIVED GAME MESSAGE");
        this.game = chessGame;
        this.testBoard = chessGame.getBoard();
        if(this.game.isInCheckmate(ChessGame.TeamColor.WHITE) || this.game.isInCheckmate(ChessGame.TeamColor.BLACK)
        || this.game.isInStalemate(ChessGame.TeamColor.WHITE) || this.game.isInStalemate(ChessGame.TeamColor.BLACK)) {
            this.game.setTeamTurn(null);
        }
        if(teamColor == ChessGame.TeamColor.WHITE) {
            displayChessBoardFromWhite(testBoard);
        } else if (teamColor == ChessGame.TeamColor.BLACK){
            displayChessBoardFromBlack(testBoard);
        } else {
            displayChessBoardFromWhite(testBoard);
        }
        System.out.print("[IN-GAME] >>> ");
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
