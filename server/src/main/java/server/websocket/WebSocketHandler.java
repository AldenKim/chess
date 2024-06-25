package server.websocket;

import com.google.gson.Gson;
import dataAccess.DataAccessException;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.*;
import websocket.commands.*;

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
    }

    @OnWebSocketClose
    public void onClose(Session session) {
    }

    @OnWebSocketError
    public void onError(Session session, Throwable throwable) {
    } */

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
        UserGameCommand gameCommand = new Gson().fromJson(message, UserGameCommand.class);
        switch(gameCommand.getCommandType()) {
            case CONNECT:
                ConnectCommand connectCommand = gson.fromJson(message, ConnectCommand.class);
                try {
                    gameService.connect(connectCommand.getAuthString(), connectCommand, session);
                } catch (DataAccessException e) {
                    e.printStackTrace();
                }
                break;
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
            case LEAVE:
                LeaveCommand leaveCommand = gson.fromJson(message, LeaveCommand.class);
                try {
                    gameService.leaveGame(gameCommand.getAuthString(), leaveCommand, session);
                } catch (DataAccessException e) {
                    e.printStackTrace();
                }
                break;
            case RESIGN:
                ResignCommand resignCommand = gson.fromJson(message, ResignCommand.class);
                try {
                    gameService.resignGame(gameCommand.getAuthString(), resignCommand, session);
                } catch (DataAccessException e) {
                    e.printStackTrace();
                }
                break;
        }
    }
}
