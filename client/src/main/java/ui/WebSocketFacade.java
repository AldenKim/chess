package ui;

import chess.ChessGame;
import webSocketMessages.serverMessages.NotificationMessage;
import webSocketMessages.userCommands.JoinPlayerCommand;
import webSocketMessages.userCommands.UserGameCommand;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class WebSocketFacade extends Endpoint implements MessageHandler{

    Session session;
    MessageHandler messageHandler;

    public WebSocketFacade(String url) {
        try {
            URI uri = new URI(url.replace("http", "ws") + "/connect");

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            container.connectToServer(this, uri);
        } catch (URISyntaxException | DeploymentException | IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig){
    }

    @Override
    public void handleMessage(NotificationMessage message) {
        System.out.println(message.getMessage());
    }

    public void joinPlayer(String authToken, int gameID, ChessGame.TeamColor teamColor) {
        JoinPlayerCommand joinPlayerCommand = new JoinPlayerCommand(gameID, teamColor,authToken);
        sendMessage(joinPlayerCommand);
    }

    private void sendMessage(UserGameCommand command) {
        try {
            session.getBasicRemote().sendObject(command);
        } catch (IOException | EncodeException e) {
            e.printStackTrace();
        }
    }
}
