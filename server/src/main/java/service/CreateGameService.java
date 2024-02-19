package service;

import dataAccess.AuthDAO;
import dataAccess.DataAccessException;
import dataAccess.GameDAO;
import model.GameData;
import requests.CreateGameRequest;
import results.CreateGameResult;

public class CreateGameService {
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;

    public CreateGameService(GameDAO gameDAO, AuthDAO authDAO) {
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
    }

    public CreateGameResult createGame(CreateGameRequest request) throws DataAccessException {
        try {
            if(!isValidAuthToken(request.authToken())) {
                return new CreateGameResult(-1, "Error: Unauthorized");
            }

            GameData newGame = new GameData(0, "","", request.gameName(), null);
            GameData createdGame = gameDAO.createGame(newGame);
            return new CreateGameResult(createdGame.gameID(), null);
        } catch (DataAccessException e) {
            throw e;
        }
    }

    private boolean isValidAuthToken(String authToken) throws DataAccessException {
        return authDAO.getAuth(authToken) != null;
    }
}
