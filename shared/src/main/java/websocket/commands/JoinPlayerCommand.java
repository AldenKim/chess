package websocket.commands;

import chess.ChessGame;

public class JoinPlayerCommand extends UserGameCommand {
    private int gameID;
    private ChessGame.TeamColor playerColor;

    public JoinPlayerCommand(int gameID, ChessGame.TeamColor playerColor, String authToken) {
        super(authToken);
        this.gameID = gameID;
        this.playerColor = playerColor;
        this.commandType = CommandType.JOIN_PLAYER;
    }

    public int getGameID() {
        return gameID;
    }

    public ChessGame.TeamColor getPlayerColor() {
        return playerColor;
    }
}
