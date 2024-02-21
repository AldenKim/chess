package handlers;

import com.google.gson.Gson;
import dataAccess.DataAccessException;
import service.ClearApplicationService;
import spark.Request;
import spark.Response;

import java.util.Map;

public class ClearHandler {
    private final ClearApplicationService clearApplicationService;
    private final Gson gson;

    public ClearHandler(ClearApplicationService clearApplicationService) {
        this.clearApplicationService = clearApplicationService;
        this.gson = new Gson();
    }

    public String clearDatabase(Response res) {
        res.type("application/json");

        try {
            clearApplicationService.clearApplication();
            res.status(200);
            return gson.toJson(Map.of("message", "Database cleared successfully."));
        } catch (DataAccessException e) {
            res.status(500);
            return gson.toJson(Map.of("error", "Failed to clear database: " + e.getMessage()));
        }
    }
}
