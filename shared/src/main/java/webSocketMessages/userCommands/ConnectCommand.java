package webSocketMessages.userCommands;

import chess.ChessGame;

public class ConnectCommand extends UserGameCommand{
    private int gameID;
    private ChessGame.TeamColor playerColor;

    public ConnectCommand (int gameID, ChessGame.TeamColor playerColor, String authToken) {
        super(authToken);
        this.gameID = gameID;
        this.playerColor = playerColor;
        this.commandType = CommandType.CONNECT;
    }

    public int getGameID() {
        return gameID;
    }

    public ChessGame.TeamColor getPlayerColor() {
        return playerColor;
    }
}
