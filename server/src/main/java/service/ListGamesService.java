package service;

import dataAccess.AuthDAO;
import dataAccess.DataAccessException;
import dataAccess.GameDAO;
import model.GameData;
import requests.ListGamesRequest;
import results.ListGamesResult;

public class ListGamesService {
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;


    public ListGamesService(GameDAO gameDAO, AuthDAO authDAO) {
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
    }

    public ListGamesResult listGames(ListGamesRequest request) throws DataAccessException {
        try {
            if (!isValidAuthToken(request.authToken())) {
                return new ListGamesResult(null, "Error: Unauthorized");
            }
            GameData[] games = gameDAO.listGames();
            return new ListGamesResult(games,null);
        } catch (DataAccessException e) {
            throw e;
        }
    }

    private boolean isValidAuthToken(String authToken) throws DataAccessException {
        return authDAO.getAuth(authToken) != null;
    }
}
