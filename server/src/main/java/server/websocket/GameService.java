package server.websocket;

import chess.*;
import dataAccess.*;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.commands.*;

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
    public void connect(String authToken, ConnectCommand connectCommand, Session session) throws DataAccessException {
        webSocketSessions.addSessionToGame(connectCommand.getGameID(), connectCommand.getAuthString(), session);

        if(!isValidAuthToken(authToken)) {
            webSocketSessions.sendMessage(connectCommand.getGameID(), new ErrorMessage("Error joining game: Unauthorized"), authToken);
            return;
        }

        int gameID = connectCommand.getGameID();
        if(gameDAO.getGame(gameID) == null) {
            webSocketSessions.sendMessage(connectCommand.getGameID(), new ErrorMessage("Error joining game: Unauthorized"), authToken);
            return;
        }

        ChessGame game = gameDAO.getGame(gameID).game();
        ChessGame.TeamColor playerColor = connectCommand.getPlayerColor();
        LoadGameMessage notificationToRootClient = new LoadGameMessage(game);

        String userName = authDAO.getAuth(authToken).username();

        String color;

        if (playerColor == ChessGame.TeamColor.WHITE) {
            color = "White";
        } else if (playerColor == ChessGame.TeamColor.BLACK) {
            color = "Black";
        } else {
            color = "Observer";
        }

        NotificationMessage notification;
        if(color.equals("White") || color.equals("Black")) {
            notification = new NotificationMessage(userName + " joined as color " + color);
        } else {
            notification = new NotificationMessage(userName + " joined as observer.");
        }
        webSocketSessions.sendMessage(gameID, notificationToRootClient, authToken);
        webSocketSessions.broadcastMessage(gameID, notification, authToken);
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

            if(game.isInCheck(ChessGame.TeamColor.BLACK) || game.isInCheck(ChessGame.TeamColor.WHITE)) {
                webSocketSessions.sendMessage(gameID, new NotificationMessage("Check!"), authToken);
                webSocketSessions.broadcastMessage(gameID, new NotificationMessage("Check!"), authToken);
                gameDAO.updateGame(gameID, new GameData(gameID, gameDAO.getGame(gameID).whiteUsername(),gameDAO.getGame(gameID).blackUsername(), gameDAO.getGame(gameID).gameName(), game));
            }

            if(game.isInCheckmate(ChessGame.TeamColor.BLACK) || game.isInCheckmate(ChessGame.TeamColor.WHITE)) {
                webSocketSessions.sendMessage(gameID, new NotificationMessage("Checkmate!"), authToken);
                webSocketSessions.broadcastMessage(gameID, new NotificationMessage("Checkmate!"), authToken);
                game.setTeamTurn(null);
                LoadGameMessage finalGame = new LoadGameMessage(game);
                webSocketSessions.sendMessage(gameID, finalGame, authToken);
                webSocketSessions.broadcastMessage(gameID, finalGame, authToken);
                gameDAO.updateGame(gameID, new GameData(gameID, gameDAO.getGame(gameID).whiteUsername(),gameDAO.getGame(gameID).blackUsername(), gameDAO.getGame(gameID).gameName(), game));
                return;
            }

            if(game.isInStalemate(ChessGame.TeamColor.BLACK) || game.isInStalemate(ChessGame.TeamColor.WHITE)) {
                webSocketSessions.sendMessage(gameID, new NotificationMessage("Stalemate!"), authToken);
                webSocketSessions.broadcastMessage(gameID, new NotificationMessage("Stalemate!"), authToken);
                game.setTeamTurn(null);
                LoadGameMessage finalGame = new LoadGameMessage(game);
                webSocketSessions.sendMessage(gameID, finalGame, authToken);
                webSocketSessions.broadcastMessage(gameID, finalGame, authToken);
                gameDAO.updateGame(gameID, new GameData(gameID, gameDAO.getGame(gameID).whiteUsername(),gameDAO.getGame(gameID).blackUsername(), gameDAO.getGame(gameID).gameName(), game));
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

    public void leaveGame(String authToken, LeaveCommand leaveCommand, Session session) throws DataAccessException {
       try {
           webSocketSessions.addSessionToGame(leaveCommand.getGameID(), leaveCommand.getAuthString(), session);

           if(!isValidAuthToken(authToken)) {
               webSocketSessions.sendMessage(leaveCommand.getGameID(), new ErrorMessage("Error: Unauthorized"), authToken);
               return;
           }

           String userName = authDAO.getAuth(authToken).username();
           int gameID = leaveCommand.getGameID();
           if (Objects.equals(gameDAO.getGame(gameID).whiteUsername(), userName)) {
               gameDAO.updateGame(gameID, new GameData(gameID, null, gameDAO.getGame(gameID).blackUsername(), gameDAO.getGame(gameID).gameName(), gameDAO.getGame(gameID).game()));
           } else if (Objects.equals(gameDAO.getGame(gameID).blackUsername(), userName)){
               gameDAO.updateGame(gameID, new GameData(gameID, gameDAO.getGame(gameID).whiteUsername(), null, gameDAO.getGame(gameID).gameName(), gameDAO.getGame(gameID).game()));
           }
           NotificationMessage notification = new NotificationMessage(userName + " has left the game.");
           webSocketSessions.broadcastMessage(gameID, notification, authToken);
           webSocketSessions.removeSessionFromGame(gameID, authToken);
           webSocketSessions.removeSession(session);
       } catch (DataAccessException e) {
           throw e;
       }
    }

    public void resignGame(String authToken, ResignCommand resignCommand, Session session) throws DataAccessException {
        try {
            webSocketSessions.addSessionToGame(resignCommand.getGameID(), resignCommand.getAuthString(), session);

            if(!isValidAuthToken(authToken)) {
                webSocketSessions.sendMessage(resignCommand.getGameID(), new ErrorMessage("Error: Unauthorized"), authToken);
                return;
            }

            String userName = authDAO.getAuth(authToken).username();
            int gameID = resignCommand.getGameID();
            ChessGame game = gameDAO.getGame(gameID).game();

            if(game.getTeamTurn() == null) {
                webSocketSessions.sendMessage(gameID, new ErrorMessage("Game already over."), authToken);
                return;
            }
            game.setTeamTurn(null);
            gameDAO.updateGame(gameID, new GameData(gameID, gameDAO.getGame(gameID).whiteUsername(), gameDAO.getGame(gameID).blackUsername(), gameDAO.getGame(gameID).gameName(), game));

            if (!Objects.equals(gameDAO.getGame(gameID).whiteUsername(), userName) && !Objects.equals(gameDAO.getGame(gameID).blackUsername(), userName)) {
                webSocketSessions.sendMessage(gameID, new ErrorMessage("Cannot Resign as an Observer."), authToken);
                return;
            }

            NotificationMessage notification = new NotificationMessage(userName + " has resigned.");
            NotificationMessage notificationRoot = new NotificationMessage("You resigned the game.");

            webSocketSessions.broadcastMessage(gameID, notification, authToken);
            webSocketSessions.sendMessage(gameID, notificationRoot, authToken);
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
