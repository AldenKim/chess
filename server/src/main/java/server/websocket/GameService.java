package server.websocket;

import chess.ChessGame;
import dataAccess.AuthDAO;
import dataAccess.DataAccessException;
import dataAccess.GameDAO;
import dataAccess.UserDAO;
import org.eclipse.jetty.websocket.api.Session;
import webSocketMessages.serverMessages.ErrorMessage;
import webSocketMessages.serverMessages.LoadGameMessage;
import webSocketMessages.serverMessages.NotificationMessage;
import webSocketMessages.userCommands.JoinPlayerCommand;

import java.util.Objects;


public class GameService {
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;
    private final UserDAO userDAO;
    private final WebSocketSessions webSocketSessions;

    public GameService(GameDAO gameDAO, AuthDAO authDAO, UserDAO userDAO) {
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
        this.userDAO = userDAO;
        this.webSocketSessions = new WebSocketSessions();
    }

    public void joinPlayer(String authToken, JoinPlayerCommand joinPlayerCommand, Session session) throws DataAccessException {
        try {
            webSocketSessions.addSessionToGame(joinPlayerCommand.getGameID(), joinPlayerCommand.getAuthString(), session);

            if(!isValidAuthToken(authToken)) {
                webSocketSessions.sendMessage(joinPlayerCommand.getGameID(), new ErrorMessage("Error joining game: Unauthorized"), authToken);
                return;
            }

            int gameID = joinPlayerCommand.getGameID();
            if(gameDAO.getGame(gameID) == null) {
                webSocketSessions.sendMessage(joinPlayerCommand.getGameID(), new ErrorMessage("Error joining game: Unauthorized"), authToken);
                return;
            }
            ChessGame game = gameDAO.getGame(gameID).game();
            ChessGame.TeamColor playerColor = joinPlayerCommand.getPlayerColor();
            LoadGameMessage notificationToRootClient = new LoadGameMessage(game);

            String userName = playerColor == ChessGame.TeamColor.WHITE ? gameDAO.getGame(gameID).whiteUsername() : gameDAO.getGame(gameID).blackUsername();
            if(!Objects.equals(userName, authDAO.getAuth(authToken).username())) {
                webSocketSessions.sendMessage(joinPlayerCommand.getGameID(), new ErrorMessage("Error joining game: Wrong Color"), authToken);
                return;
            }

            String color = playerColor == ChessGame.TeamColor.WHITE ? "White" : "Black";

            NotificationMessage notification = new NotificationMessage(userName + " joined as color " + color);

            webSocketSessions.sendMessage(gameID, notificationToRootClient, authToken);
            webSocketSessions.broadcastMessage(gameID, notification, authToken);
        } catch (DataAccessException e){
            throw e;
        }
    }

    private boolean isValidAuthToken(String authToken) throws DataAccessException {
        return authDAO.getAuth(authToken) != null;
    }
}
