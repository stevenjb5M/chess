package server;

import com.google.gson.Gson;
import dataAccess.DataAccessException;
import service.RegisterRequest;
import service.RegisterResult;
import service.UserService;
import spark.*;

import java.util.Map;

public class Server {
    private final UserService userService;

    public Server(UserService userService) {
        this.userService = userService;
    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        Spark.post("/user/register", this::register);
        Spark.post("/session", this::register);
        Spark.delete("/session", this::register);
        Spark.get("/game", this::register);
        Spark.post("/game", this::register);
        Spark.put("/game", this::register);
        Spark.delete("/db", this::register);



        //This line initializes the server and can be removed once you have a functioning endpoint 
        Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    private Object register(Request req, Response res) throws DataAccessException {
        var request = new Gson().fromJson(req.body(), RegisterRequest.class);
        RegisterResult result = userService.register(request);
        return new Gson().toJson(result);
    }
}



