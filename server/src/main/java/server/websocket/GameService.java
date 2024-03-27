package server.websocket;

import chess.ChessGame;
import dataAccess.AuthDAO;
import dataAccess.DataAccessException;
import dataAccess.GameDAO;
import dataAccess.UserDAO;
import webSocketMessages.serverMessages.ErrorMessage;
import webSocketMessages.serverMessages.LoadGameMessage;
import webSocketMessages.serverMessages.NotificationMessage;
import webSocketMessages.serverMessages.ServerMessage;
import webSocketMessages.userCommands.JoinPlayerCommand;


public class GameService {
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;
    private final UserDAO userDAO;
    private final WebSocketSessions webSocketSessions;

    public GameService(GameDAO gameDAO, AuthDAO authDAO, UserDAO userDAO,WebSocketSessions webSocketSessions) {
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
        this.userDAO = userDAO;
        this.webSocketSessions = webSocketSessions;
    }

    public void joinPlayer(String authToken, JoinPlayerCommand joinPlayerCommand) throws DataAccessException {
        try {
            if(!isValidAuthToken(authToken)) {
                webSocketSessions.sendMessage(joinPlayerCommand.getGameID(), new ErrorMessage("Error joining game: Unauthorized"), authToken);
            }

            int gameID = joinPlayerCommand.getGameID();
            ChessGame game = gameDAO.getGame(gameID).game();
            LoadGameMessage notificationToRootClient = new LoadGameMessage(game);

            String userName = joinPlayerCommand.getPlayerColor() == ChessGame.TeamColor.WHITE ? gameDAO.getGame(gameID).whiteUsername() : gameDAO.getGame(gameID).blackUsername();

            String color = joinPlayerCommand.getPlayerColor() == ChessGame.TeamColor.WHITE ? "White" : "Black";

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
