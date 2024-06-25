package server.websocket;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.RemoteEndpoint;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class WebSocketSessions {
    private static final Map<Integer, Map<String, Session>> sessionMap = new HashMap<>();

    public void addSessionToGame(int gameID, String authToken, Session session) {
        Map<String, Session> gameSessions = sessionMap.computeIfAbsent(gameID, k -> new HashMap<>());
        gameSessions.put(authToken, session);
    }

    public void removeSessionFromGame(int gameID, String authToken) {
        Map<String, Session> gameSessions = sessionMap.get(gameID);
        if(gameSessions != null) {
            gameSessions.remove(authToken);
            if(gameSessions.isEmpty()) {
                sessionMap.remove(gameID);
            }
        }
    }

    public void removeSession(Session session) {
        Set<Map.Entry<Integer, Map<String, Session>>> entrySet = sessionMap.entrySet();
        for(Map.Entry<Integer, Map<String, Session>> entry : entrySet) {
            Map<String, Session> gameSessions = entry.getValue();
            gameSessions.values().removeIf(s -> s.equals(session));
            if(gameSessions.isEmpty()) {
                entrySet.remove(entry);
            }
        }
    }

    public void sendMessage(int gameID, ServerMessage message, String authToken) {
        Map<String, Session> gameSessions = sessionMap.get(gameID);
        Session targetSession = gameSessions.get(authToken);
        if(targetSession != null && targetSession.isOpen()) {
            sendToSession(targetSession, message);
        }
    }

    public void broadcastMessage(int gameID, ServerMessage message, String exceptThisAuthToken) {
        Map<String, Session> gameSessions = sessionMap.get(gameID);
        for (Map.Entry<String, Session> entry : gameSessions.entrySet()) {
            String authToken = entry.getKey();
            if(!authToken.equals(exceptThisAuthToken)) {
                Session targetSession = entry.getValue();
                if(targetSession.isOpen()) {
                    sendToSession(targetSession, message);
                }
            }
        }
    }

    private void sendToSession(Session session, ServerMessage message) {
        RemoteEndpoint remote = session.getRemote();
        try {
            remote.sendString(new Gson().toJson(message));
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
}
