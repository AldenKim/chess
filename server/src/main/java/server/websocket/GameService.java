package server.websocket;

import chess.*;
import dataAccess.AuthDAO;
import dataAccess.DataAccessException;
import dataAccess.GameDAO;
import dataAccess.UserDAO;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import webSocketMessages.serverMessages.ErrorMessage;
import webSocketMessages.serverMessages.LoadGameMessage;
import webSocketMessages.serverMessages.NotificationMessage;
import webSocketMessages.serverMessages.ServerMessage;
import webSocketMessages.userCommands.JoinObserverCommand;
import webSocketMessages.userCommands.JoinPlayerCommand;
import webSocketMessages.userCommands.MakeMoveCommand;

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

    public void joinObserver(String authToken, JoinObserverCommand joinObserverCommand, Session session) throws DataAccessException {
        try {
            webSocketSessions.addSessionToGame(joinObserverCommand.getGameID(), joinObserverCommand.getAuthString(), session);

            if(!isValidAuthToken(authToken)) {
                webSocketSessions.sendMessage(joinObserverCommand.getGameID(), new ErrorMessage("Error joining: Unauthorized"), authToken);
                return;
            }

            int gameID = joinObserverCommand.getGameID();
            if(gameDAO.getGame(gameID) == null) {
                webSocketSessions.sendMessage(joinObserverCommand.getGameID(), new ErrorMessage("Error joining game: Unauthorized"), authToken);
                return;
            }
            ChessGame game = gameDAO.getGame(gameID).game();
            LoadGameMessage notificationToRootClient = new LoadGameMessage(game);

            String userName = authDAO.getAuth(authToken).username();

            NotificationMessage notification = new NotificationMessage(userName + " joined as observer.");

            webSocketSessions.sendMessage(gameID, notificationToRootClient, authToken);
            webSocketSessions.broadcastMessage(gameID, notification, authToken);
        } catch (DataAccessException e) {
            throw e;
        }
    }

    public void makeMove(String authToken, MakeMoveCommand makeMoveCommand, Session session) throws DataAccessException {
        try {
            webSocketSessions.addSessionToGame(makeMoveCommand.getGameID(), makeMoveCommand.getAuthString(), session);

            if(!isValidAuthToken(authToken)) {
                webSocketSessions.sendMessage(makeMoveCommand.getGameID(), new ErrorMessage("Error: Unauthorized"), authToken);
                return;
            }

            String userName = authDAO.getAuth(authToken).username();
            int gameID = makeMoveCommand.getGameID();
            ChessGame game = gameDAO.getGame(gameID).game();
            ChessMove move = makeMoveCommand.getMove();
            ChessPiece piece = game.getBoard().getPiece(move.getStartPosition());
            ChessGame.TeamColor userColor = null;

            if (Objects.equals(gameDAO.getGame(gameID).whiteUsername(), userName)) {
                userColor = ChessGame.TeamColor.WHITE;
            } else if (Objects.equals(gameDAO.getGame(gameID).blackUsername(), userName)){
                userColor = ChessGame.TeamColor.BLACK;
            }

            try {
                game.makeMove(move);
            } catch (InvalidMoveException e) {
                webSocketSessions.sendMessage(gameID, new ErrorMessage("Invalid move."), authToken);
                return;
            }

            if(piece.getTeamColor() != userColor) {
                webSocketSessions.sendMessage(gameID, new ErrorMessage("Cannot move that piece."), authToken);
                return;
            }

            gameDAO.updateGame(gameID, new GameData(gameID, gameDAO.getGame(gameID).whiteUsername(),gameDAO.getGame(gameID).blackUsername(), gameDAO.getGame(gameID).gameName(), game));

            LoadGameMessage notificationToRootClient = new LoadGameMessage(game);

            NotificationMessage notification = new NotificationMessage(userName + " moved to " + positionToString(makeMoveCommand.getMove().getEndPosition()));

            webSocketSessions.sendMessage(gameID, notificationToRootClient, authToken);
            webSocketSessions.broadcastMessage(gameID, notificationToRootClient, authToken);
            webSocketSessions.broadcastMessage(gameID, notification, authToken);
        } catch (DataAccessException e) {
            throw e;
        }
    }

    private boolean isValidAuthToken(String authToken) throws DataAccessException {
        return authDAO.getAuth(authToken) != null;
    }

    private String positionToString(ChessPosition position) {
        String pos = "";
        switch (position.getColumn()) {
            case 1:
                pos = "a";
                break;
            case 2:
                pos = "b";
                break;
            case 3:
                pos = "c";
                break;
            case 4:
                pos = "d";
                break;
            case 5:
                pos = "e";
                break;
            case 6:
                pos = "f";
                break;
            case 7:
                pos = "g";
                break;
            case 8:
                pos = "h";
                break;
        }

        switch (position.getRow()) {
            case 1:
                pos = pos+"1";
                break;
            case 2:
                pos = pos+"2";
                break;
            case 3:
                pos = pos+"3";
                break;
            case 4:
                pos = pos+"4";
                break;
            case 5:
                pos = pos+"5";
                break;
            case 6:
                pos = pos+"6";
                break;
            case 7:
                pos = pos+"7";
                break;
            case 8:
                pos = pos+"8";
                break;
        }
        return pos;
    }
}
