package ui;

import chess.ChessGame;
import chess.ChessMove;
import com.google.gson.Gson;
import webSocketMessages.serverMessages.ErrorMessage;
import webSocketMessages.serverMessages.LoadGameMessage;
import webSocketMessages.serverMessages.NotificationMessage;
import webSocketMessages.serverMessages.ServerMessage;
import webSocketMessages.userCommands.*;

import javax.websocket.*;
import javax.websocket.MessageHandler;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class WebSocketFacade extends Endpoint {

    Session session;
    GameHandler gameHandler;

    public WebSocketFacade(String url, GameHandler gameHandler) {
        try {
            URI uri = new URI(url.replace("http", "ws") + "/connect");
            this.gameHandler = gameHandler;

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, uri);

            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    Gson gson = new Gson();
                    ServerMessage serverMessage = gson.fromJson(message, ServerMessage.class);

                    switch (serverMessage.getServerMessageType()) {
                        case NOTIFICATION:
                            NotificationMessage notificationMessage = gson.fromJson(message, NotificationMessage.class);
                            gameHandler.printMessage(notificationMessage);
                        case LOAD_GAME:
                            LoadGameMessage loadGameMessage = gson.fromJson(message, LoadGameMessage.class);
                            gameHandler.updateGame(loadGameMessage);
                            break;
                        case ERROR:
                            NotificationMessage errorMessage = gson.fromJson(message, NotificationMessage.class);
                            gameHandler.printMessage(errorMessage);
                    }
                }
            });
        } catch (URISyntaxException | DeploymentException | IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig){
    }

    public void joinPlayer(String authToken, int gameID, ChessGame.TeamColor teamColor) {
        JoinPlayerCommand joinPlayerCommand = new JoinPlayerCommand(gameID, teamColor, authToken);
        sendMessage(joinPlayerCommand);
    }

    public void joinObserver(int gameID, String authToken) {
        JoinObserverCommand joinObserverCommand = new JoinObserverCommand(gameID, authToken);
        sendMessage(joinObserverCommand);
    }

    public void makeMove (int gameID, ChessMove move, String authToken) {
        MakeMoveCommand makeMoveCommand = new MakeMoveCommand(gameID, move, authToken);
        sendMessage(makeMoveCommand);
    }

    public void leave (int gameID, String authToken) throws IOException {
        LeaveCommand leaveCommand = new LeaveCommand(gameID, authToken);
        sendMessage(leaveCommand);
        this.session.close();
    }

    public void resign (int gameID, String authToken) {
        ResignCommand resignCommand = new ResignCommand(gameID, authToken);
        sendMessage(resignCommand);
    }

    private void sendMessage(UserGameCommand command) {
        try {
            session.getBasicRemote().sendText(new Gson().toJson(command));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
