package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import model.GameData;
import results.ListGamesResult;

public class ListGamesService {
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;


    public ListGamesService(GameDAO gameDAO, AuthDAO authDAO) {
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
    }

    public ListGamesResult listGames(String authToken) throws DataAccessException {
        try {
            if (!isValidAuthToken(authToken)) {
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
