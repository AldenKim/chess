package server.websocket;

import com.google.gson.Gson;
import dataAccess.DataAccessException;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.*;
import webSocketMessages.userCommands.JoinObserverCommand;
import webSocketMessages.userCommands.JoinPlayerCommand;
import webSocketMessages.userCommands.MakeMoveCommand;
import webSocketMessages.userCommands.UserGameCommand;

import java.io.IOException;


@WebSocket
public class WebSocketHandler {
    private final GameService gameService;
    private final Gson gson;
    public WebSocketHandler(GameService gameService) {
        this.gameService = gameService;
        this.gson = new Gson();
    }

   /* @OnWebSocketConnect
    public void onConnect(Session session) {
        System.out.println("New session connected");
    }

    @OnWebSocketClose
    public void onClose(Session session) {
        System.out.println("Session closed");
    }

    @OnWebSocketError
    public void onError(Session session, Throwable throwable) {
        System.err.println("Error occurred in session");
        throwable.printStackTrace();
    }*/

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
        UserGameCommand gameCommand = new Gson().fromJson(message, UserGameCommand.class);
        switch(gameCommand.getCommandType()) {
            case JOIN_PLAYER:
                JoinPlayerCommand joinPlayerCommand = gson.fromJson(message, JoinPlayerCommand.class);
                try {
                    gameService.joinPlayer(gameCommand.getAuthString(), joinPlayerCommand, session);
                } catch (DataAccessException e) {
                    e.printStackTrace();
                }
                break;
            case JOIN_OBSERVER:
                JoinObserverCommand joinObserverCommand = gson.fromJson(message, JoinObserverCommand.class);
                try {
                    gameService.joinObserver(gameCommand.getAuthString(), joinObserverCommand, session);
                } catch (DataAccessException e) {
                    e.printStackTrace();
                }
                break;
            case MAKE_MOVE:
                MakeMoveCommand makeMoveCommand = gson.fromJson(message, MakeMoveCommand.class);
                try {
                    gameService.makeMove(gameCommand.getAuthString(), makeMoveCommand, session);
                } catch (DataAccessException e) {
                    e.printStackTrace();
                }
                break;
        }
    }
}
