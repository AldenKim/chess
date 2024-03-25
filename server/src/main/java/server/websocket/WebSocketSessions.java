package server.websocket;

import spark.Session;

import java.util.HashMap;
import java.util.Map;

public class WebSocketSessions {
    private static final Map<Integer, Map<String, Session>> sessionMap = new HashMap<>();

    public static void addSessionToGame(int gameID, String authToken, Session session) {
        Map<String, Session> gameSessions = sessionMap.computeIfAbsent(gameID, k -> new HashMap<>());
        gameSessions.put(authToken, session);
    }

    public static void removeSessionFromGame(int gameID, String authToken) {
        Map<String, Session> gameSessions = sessionMap.get(gameID);
        if(gameSessions != null) {
            gameSessions.remove(authToken);
            if(gameSessions.isEmpty()) {
                sessionMap.remove(gameID);
            }
        }
    }


}
