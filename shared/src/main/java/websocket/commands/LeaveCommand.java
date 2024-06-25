package websocket.commands;

public class LeaveCommand extends UserGameCommand{

    public LeaveCommand(int gameID, String authToken) {
        super(CommandType.LEAVE,authToken, gameID);
    }
}
