package websocket.commands;

public class JoinObserverCommand extends UserGameCommand{
    private int gameID;

    public JoinObserverCommand(int gameID, String authToken) {
        super(authToken);
        this.gameID = gameID;
        this.commandType = CommandType.JOIN_OBSERVER;
    }

    public int getGameID() {
        return gameID;
    }
}
