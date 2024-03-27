package server.websocket;

import com.google.gson.Gson;
import dataAccess.DataAccessException;
import org.eclipse.jetty.websocket.api.annotations.*;
import service.CreateGameService;
import webSocketMessages.serverMessages.LoadGameMessage;
import webSocketMessages.serverMessages.NotificationMessage;
import webSocketMessages.serverMessages.ServerMessage;
import webSocketMessages.userCommands.JoinPlayerCommand;
import webSocketMessages.userCommands.UserGameCommand;

import javax.websocket.Session;
import java.io.IOException;
import java.util.Map;

@WebSocket
public class WebSocketHandler {
    private WebSocketSessions sessionManager = new WebSocketSessions();
    private final GameService gameService;
    private final Gson gson;
    public WebSocketHandler(GameService gameService) {
        this.gameService = gameService;
        this.gson = new Gson();
    }

    @OnWebSocketConnect
    public void onConnect(Session session) {
        System.out.println("New session connected: " + session.getId());
    }

    @OnWebSocketClose
    public void onClose(Session session) {
        System.out.println("Session closed: " + session.getId());
    }

    @OnWebSocketError
    public void onError(Session session, Throwable throwable) {
        System.err.println("Error occurred in session: " + session.getId());
        throwable.printStackTrace();
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
        UserGameCommand gameCommand = new Gson().fromJson(message, UserGameCommand.class);
        switch(gameCommand.getCommandType()) {
            case JOIN_PLAYER:
                JoinPlayerCommand joinPlayerCommand = gson.fromJson(message, JoinPlayerCommand.class);
                try {
                    gameService.joinPlayer(gameCommand.getAuthString(), joinPlayerCommand);
                } catch (DataAccessException e) {
                    e.printStackTrace();
                }
                break;
            case JOIN_OBSERVER:

        }
    }
}
