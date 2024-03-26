package webSocketMessages.serverMessages;

public class LoadGameMessage extends ServerMessage{
    private Object game;

    public LoadGameMessage(Object game) {
        super(ServerMessageType.LOAD_GAME);
        this.game = game;
    }

    public Object getGame() {
        return game;
    }
}
