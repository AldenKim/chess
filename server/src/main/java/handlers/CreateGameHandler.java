package handlers;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import requests.CreateGameRequest;
import results.CreateGameResult;
import service.CreateGameService;
import spark.Request;
import spark.Response;

public class CreateGameHandler {
    private final CreateGameService createGameService;
    private final Gson gson;

    public CreateGameHandler(CreateGameService createGameService) {
        this.createGameService = createGameService;
        this.gson = new Gson();
    }

    public String createGame(Request req, Response res) {
        res.type("application/json");

        CreateGameRequest createGameRequest = gson.fromJson(req.body(), CreateGameRequest.class);

        String authToken = req.headers("authorization");

        if (createGameRequest == null || createGameRequest.gameName() == null || createGameRequest.gameName().isEmpty()) {
            res.status(400); // Bad Request
            return gson.toJson(new CreateGameResult(null, "Error: Bad Request"));
        }

        try {
            CreateGameResult createGameResult = createGameService.createGame(authToken, createGameRequest);

            if (createGameResult.message() != null) {
                res.status(401); // Unauthorized
            } else {
                res.status(200); // OK
            }
            return gson.toJson(createGameResult);
        } catch (DataAccessException e) {
            res.status(500);
            return gson.toJson(new CreateGameResult(null, "Error: " + e.getMessage()));
        }
    }
}
