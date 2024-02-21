package model;

import chess.ChessGame;
import com.google.gson.Gson;

public record GameData(int gameID, String whiteUsername, String blackUsername, String gameName, ChessGame game) {
    public GameData withPlayer(String playerColor, String username) {
        if ("WHITE".equalsIgnoreCase(playerColor)) {
            return new GameData(gameID, username, blackUsername, gameName, game);
        } else if ("BLACK".equalsIgnoreCase(playerColor)) {
            return new GameData(gameID, whiteUsername, username, gameName, game);
        } else {
            return this;
        }
    }

    public GameData withObserver() {
        return new GameData(gameID, whiteUsername, blackUsername, gameName, game);
    }

    public String toString() {
        return new Gson().toJson(this);
    }
}
