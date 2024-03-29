package ui;

import chess.ChessGame;

public interface GameHandler {
    ChessGame updateGame(ChessGame game);
    void printMessage(String message);
}
