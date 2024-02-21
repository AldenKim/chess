package service;

import dataAccess.AuthDAO;
import dataAccess.DataAccessException;
import dataAccess.GameDAO;
import model.GameData;
import requests.JoinGameRequest;
import results.JoinGameResult;

public class JoinGameService {
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;

    public JoinGameService(GameDAO gameDAO, AuthDAO authDAO) {
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
    }

    public JoinGameResult joinGame(String authToken, JoinGameRequest request) throws DataAccessException {
        try {
            if(!isValidAuthToken(authToken)) {
                return new JoinGameResult("Error: Unauthorized");
            }

            GameData game = gameDAO.getGame(request.gameID());
            if ((!game.whiteUsername().isEmpty() && request.playerColor().equalsIgnoreCase("WHITE") || !game.blackUsername().isEmpty() && request.playerColor().equalsIgnoreCase("BLACK")) && request.playerColor() != null ) {
                return new JoinGameResult("Error: Game already taken");
            }

            if("WHITE".equalsIgnoreCase(request.playerColor()) || "BLACK".equalsIgnoreCase(request.playerColor())) {
                gameDAO.updateGame(request.gameID(), game.withPlayer(request.playerColor(), authDAO.getAuth(authToken).username()));
            } else {
                gameDAO.updateGame(request.gameID(), game.withObserver());
            }

            return new JoinGameResult(null);
        } catch (DataAccessException e) {
            throw e;
        }
    }

    private boolean isValidAuthToken(String authToken) throws DataAccessException {
        return authDAO.getAuth(authToken) != null;
    }
}
