package server;

import dataAccess.*;
import handlers.ClearHandler;
import handlers.RegisterHandler;
import service.ClearApplicationService;
import service.RegisterService;
import spark.*;

import java.nio.file.Paths;

public class Server {
    private static final UserDAO userDAO = new MemoryUserDAO();
    private static final AuthDAO authDAO = new MemoryAuthDAO();
    private static final GameDAO gameDAO = new MemoryGameDAO();

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.

        RegisterHandler registerHandler = new RegisterHandler(new RegisterService(userDAO, authDAO));
        Spark.post("/user", registerHandler::register);

        ClearApplicationService clearApplicationService = new ClearApplicationService(userDAO, gameDAO, authDAO);
        ClearHandler clearHandler = new ClearHandler(clearApplicationService);
        Spark.delete("/db", clearHandler::clearDatabase);

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
