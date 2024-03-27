package server.websocket;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketError;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import webSocketMessages.serverMessages.ServerMessage;
import webSocketMessages.userCommands.UserGameCommand;

import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.util.Map;

@ServerEndpoint("/websocket")
public class WebSocketHandler {
    private WebSocketSessions sessionManager = new WebSocketSessions();

    @OnOpen
    public void onWebSocketConnect(Session session) {

    }

    @OnWebSocketClose
    public void onClose(Session session) {

    }

    @OnWebSocketError
    public void onError(Throwable throwable) {

    }

    @OnWebSocketMessage
    public void onMessage(Session session, String string) {
        UserGameCommand userGameCommand = new Gson().fromJson(string, UserGameCommand.class);

        switch (userGameCommand.getCommandType()) {
            case JOIN_PLAYER:
                break;
            case JOIN_OBSERVER:
                break;
            case MAKE_MOVE:
                break;
            case LEAVE:
                break;
            case RESIGN:
        }
    }

    public void sendMessage(int gameID, ServerMessage message, String authToken) {
        Map<String, Session> gameSessions = sessionManager.getSessionsForGame(gameID);
        Session targetSession = gameSessions.get(authToken);

        if (targetSession != null && targetSession.isOpen()) {
            try {
                String jsonMessage = new Gson().toJson(message);
                targetSession.getBasicRemote().sendText(jsonMessage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void broadcastMessage(int gameID, ServerMessage message, String exceptThisAuthToken) {
        Map<String, Session> gameSessions = sessionManager.getSessionsForGame(gameID);

        for(Map.Entry<String, Session> entry : gameSessions.entrySet()) {
            String authToken = entry.getKey();
            Session targetSession = entry.getValue();

            if(!authToken.equals(exceptThisAuthToken) && targetSession.isOpen()) {
                try {
                    String jsonMessage = new Gson().toJson(message);
                    targetSession.getBasicRemote().sendText(jsonMessage);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
