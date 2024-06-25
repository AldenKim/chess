package websocket.commands;

public class ResignCommand extends UserGameCommand{
    private int gameID;

    public ResignCommand(int gameID, String authToken) {
        super(authToken);
        this.gameID = gameID;
        this.commandType = CommandType.RESIGN;
    }

    public int getGameID() {
        return gameID;
    }
}
