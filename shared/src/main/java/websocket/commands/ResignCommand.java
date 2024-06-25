package websocket.commands;

public class ResignCommand extends UserGameCommand{

    public ResignCommand(int gameID, String authToken) {
        super(CommandType.RESIGN, authToken, gameID);
    }
}
