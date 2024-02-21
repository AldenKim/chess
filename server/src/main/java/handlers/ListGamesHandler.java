package handlers;

import com.google.gson.Gson;
import dataAccess.DataAccessException;
import results.ListGamesResult;
import service.ListGamesService;
import spark.Request;
import spark.Response;

public class ListGamesHandler {
    private final ListGamesService listGamesService;
    private final Gson gson;

    public ListGamesHandler(ListGamesService listGamesService) {
        this.listGamesService = listGamesService;
        this.gson = new Gson();
    }

    public String listGames(Request req, Response res) {
        res.type("application/json");
        String authToken = req.headers("authorization");

        try {
            ListGamesResult listGamesResult = listGamesService.listGames(authToken);
            if (listGamesResult.errorMessage() != null) {
                res.status(401); // Unauthorized
            } else {
                res.status(200); // OK
            }
            return gson.toJson(listGamesResult);
        } catch (DataAccessException e) {
            res.status(500);
            return gson.toJson(new ListGamesResult(null, "Error: " + e.getMessage()));
        }
    }
}
