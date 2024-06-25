package websocket.commands;

public class LeaveCommand extends UserGameCommand{
    private int gameID;

    public LeaveCommand(int gameID, String authToken) {
        super(authToken);
        this.gameID = gameID;
        this.commandType = CommandType.LEAVE;
    }

    public int getGameID() {
        return gameID;
    }
}
