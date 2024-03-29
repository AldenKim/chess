package ui;

import chess.ChessGame;
import chess.ChessMove;
import webSocketMessages.serverMessages.NotificationMessage;
import webSocketMessages.userCommands.*;

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

    public void leave (int gameID, String authToken) {
        LeaveCommand leaveCommand = new LeaveCommand(gameID, authToken);
        sendMessage(leaveCommand);
    }

    public void resign (int gameID, String authToken) {
        ResignCommand resignCommand = new ResignCommand(gameID, authToken);
        sendMessage(resignCommand);
    }

    private void sendMessage(UserGameCommand command) {
        try {
            session.getBasicRemote().sendObject(command);
        } catch (IOException | EncodeException e) {
            e.printStackTrace();
        }
    }
}
