package handlers;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import requests.RegisterRequest;
import results.RegisterResult;
import service.RegisterService;
import spark.Request;
import spark.Response;

public class RegisterHandler {
    private final RegisterService registerService;
    private final Gson gson;

    public RegisterHandler(RegisterService registerService) {
        this.registerService = registerService;
        this.gson = new Gson();
    }

    public String register(Request req, Response res) {
        res.type("application/json");

        RegisterRequest registerRequest = gson.fromJson(req.body(), RegisterRequest.class);

        if (registerRequest.username() == null || registerRequest.password() == null || registerRequest.email() == null) {
            res.status(400); // Bad Request
            return gson.toJson(new RegisterResult(null, null, "Error: Bad Request"));
        }

        try {
            RegisterResult registerResult = registerService.register(registerRequest);
            if (registerResult.message() != null) {
                res.status(403); // Bad Request
            } else {
                res.status(200); // OK
                if (registerResult.authToken() != null) {
                    res.header("authorization", registerResult.authToken());
                }
            }
            return gson.toJson(registerResult);
        } catch (DataAccessException e) {
            res.status(500);
            return gson.toJson(new RegisterResult(null, null, "Error: " + e.getMessage()));
        }
    }
}
