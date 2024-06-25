package websocket.commands;

import chess.ChessGame;

public class ConnectCommand extends UserGameCommand{
    private ChessGame.TeamColor playerColor;

    public ConnectCommand (int gameID, ChessGame.TeamColor playerColor, String authToken) {
        super(CommandType.CONNECT, authToken, gameID);
        this.playerColor = playerColor;
    }

    public ChessGame.TeamColor getPlayerColor() {
        return playerColor;
    }
}
