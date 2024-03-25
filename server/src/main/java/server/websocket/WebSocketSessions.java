package server.websocket;

import spark.Session;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

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

    public static void removeSession(Session session) {
        Set<Map.Entry<Integer, Map<String, Session>>> entrySet = sessionMap.entrySet();
        for(Map.Entry<Integer, Map<String, Session>> entry : entrySet) {
            Map<String, Session> gameSessions = entry.getValue();
            gameSessions.values().removeIf(s -> s.equals(session));
            if(gameSessions.isEmpty()) {
                entrySet.remove(entry);
            }
        }
    }

    public static Map<String, Session> getSessionsForGame(int gameID) {
        return sessionMap.get(gameID);
    }
}
