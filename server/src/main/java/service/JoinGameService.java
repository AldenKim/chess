package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
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
            if (request.playerColor() != null && (game.whiteUsername() != null && request.playerColor().equalsIgnoreCase("WHITE") || game.blackUsername() != null && request.playerColor().equalsIgnoreCase("BLACK"))) {
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
