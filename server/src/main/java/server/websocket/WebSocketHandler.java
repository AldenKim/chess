package server.websocket;

import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.util.Map;

@ServerEndpoint("/websocket")
public class WebSocketHandler {
    private WebSocketSessions sessionManager = new WebSocketSessions();

    @OnOpen
    public void onWebSocketConnect(Session session) {
        String gameIDString = session.getRequestParameterMap().get("gameID").get(0);
        int gameID = Integer.parseInt(gameIDString);

        String authToken = (String) session.getUserProperties().get("authToken");

        sessionManager.addSessionToGame(gameID, authToken, session);

        String notificationMessage = "A player joined the game.";
        broadcastMessage(gameID, notificationMessage, authToken);
    }

    public void sendMessage(int gameID, String message, String authToken) {
        Map<String, Session> gameSessions = sessionManager.getSessionsForGame(gameID);
        Session targetSession = gameSessions.get(authToken);

        if (targetSession != null && targetSession.isOpen()) {
            try {
                targetSession.getBasicRemote().sendText(message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void broadcastMessage(int gameID, String message, String exceptThisAuthToken) {
        Map<String, Session> gameSessions = sessionManager.getSessionsForGame(gameID);

        for (Map.Entry<String, Session> entry : gameSessions.entrySet()) {
            String authToken = entry.getKey();
            Session targetSession = entry.getValue();

            if(!authToken.equals(exceptThisAuthToken) && targetSession.isOpen()) {
                try {
                    targetSession.getBasicRemote().sendText(message);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
