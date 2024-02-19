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

    public JoinGameResult joinGame(JoinGameRequest request) throws DataAccessException {
        try {
            if(!isValidAuthToken(request.authToken())) {
                return new JoinGameResult("Error: Unauthorized");
            }

            GameData game = gameDAO.getGame(request.gameID());
            if (game == null) {
                return new JoinGameResult("Error: Game not found");
            }

            if("WHITE".equalsIgnoreCase(request.playerColor()) || "BLACK".equalsIgnoreCase(request.playerColor())) {
                gameDAO.updateGame(request.gameID(), game.withPlayer(request.playerColor(), authDAO.getAuth(request.authToken()).username()));
            } else {
                gameDAO.updateGame(request.gameID(), game.withObserver(authDAO.getAuth(request.authToken()).username()));
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
