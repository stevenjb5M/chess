package server;

import com.google.gson.Gson;
import dataaccess.*;
import model.GameData;
import server.websocket.WebSocketHandler;
import service.*;
import spark.*;
import java.util.Collection;
import java.util.Map;

public class Server {
    private final UserService userService;
    private final GameService gameService;
    private final AuthService authService;
    private final WebSocketHandler webSocketHandler;


    public Server() {
        SQLUserDAO userDAO = new SQLUserDAO();
        SQLAuthDAO authDAQ = new SQLAuthDAO();
        SQLGameDAO gameDAQ = new SQLGameDAO();
        this.authService = new AuthService(authDAQ);
        this.userService = new UserService(userDAO, authService);
        this.gameService = new GameService(gameDAQ);
        webSocketHandler = new WebSocketHandler();
    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        Spark.webSocket("/ws", webSocketHandler);

        // Register your endpoints and handle exceptions here.
        Spark.post("/user", this::register);
        Spark.post("/session", this::login);
        Spark.delete("/session", this::logout);
        Spark.get("/game", this::listGames);
        Spark.post("/game", this::createGame);
        Spark.put("/game", this::joinGame);
        Spark.delete("/db", this::clear);



        //This line initializes the server and can be removed once you have a functioning endpoint 
        Spark.init();



        Spark.awaitInitialization();

        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    public Object errorHandler(Exception e, Request req, Response res) {
        var body = new Gson().toJson(Map.of("message", String.format("Error: %s", e.getMessage()), "success", false));
        res.type("application/json");
        res.body(body);
        return body;
    }

    private Object register(Request req, Response res) throws DataAccessException {
        try {
            var request = new Gson().fromJson(req.body(), RegisterRequest.class);

            RegisterResult result = userService.register(request);

            return new Gson().toJson(result);

        } catch (UsernameTakenException e) {
            res.status(403);
            return errorHandler(new Exception(e.getMessage()), req, res);
        } catch (BadRequestException e) {
            res.status(400);
            return errorHandler(new Exception(e.getMessage()), req, res);
        } catch (InternalServerException e) {
            res.status(500);
            return errorHandler(new Exception(e.getMessage()), req, res);
        }
    }

    private Object login(Request req, Response res) throws DataAccessException {
        try {
            var request = new Gson().fromJson(req.body(), LoginRequest.class);

            LoginResult result = userService.login(request);

            return new Gson().toJson(result);

        } catch (BadRequestException e) {
            res.status(400);
            return errorHandler(new Exception(e.getMessage()), req, res);
        } catch (UnauthorizedException e) {
            res.status(401);
            return errorHandler(new Exception(e.getMessage()), req, res);
        } catch (InternalServerException e) {
            res.status(500);
            return errorHandler(new Exception(e.getMessage()), req, res);
        }

    }

    private Object logout(Request req, Response res) throws DataAccessException {
        try {
            String authToken = req.headers("authorization");

            if (authToken == null) {
                res.status(401);
                return errorHandler(new Exception(new UnauthorizedException()), req, res);
            }

            userService.logout(authToken);

            return new Gson().toJson(Map.of("message", "Success"));

        } catch (UnauthorizedException e) {
            res.status(401);
            return errorHandler(new Exception(e.getMessage()), req, res);
        } catch (InternalServerException e) {
            res.status(500);
            return errorHandler(new Exception(e.getMessage()), req, res);
        }
    }

    private Object getGame(Request req, Response res) throws DataAccessException {
        return new Gson().toJson("");
    }

    private Object createGame(Request req, Response res) throws DataAccessException {
        try {
            var request = new Gson().fromJson(req.body(), CreateGameRequest.class);
            String authToken = req.headers("authorization");

            if (authToken == null || !authService.checkIfAuthExisits(authToken)) {
                res.status(401);
                return errorHandler(new Exception(new UnauthorizedException()), req, res);
            }

            CreateGameResult result = gameService.createGame(request);
            int test = result.getGameID();

            return new Gson().toJson(result);

        } catch (BadRequestException e) {
            res.status(400);
            return errorHandler(new Exception(e.getMessage()), req, res);
        } catch (InternalServerException e) {
            res.status(500);
            return errorHandler(new Exception(e.getMessage()), req, res);
        }

    }

    private Object joinGame(Request req, Response res) throws DataAccessException {
        try {
            var request = new Gson().fromJson(req.body(), JoinGameRequest.class);
            String authToken = req.headers("authorization");

            if (authToken == null || !authService.checkIfAuthExisits(authToken)) {
                res.status(401);
                return errorHandler(new Exception(new UnauthorizedException()), req, res);
            }

            String username = this.authService.getUserByAuth(authToken);

            this.gameService.joinGame(request, username);

            return new Gson().toJson(Map.of("message", "Success"));
        } catch (BadRequestException e) {
            res.status(400);
            return errorHandler(new Exception(e.getMessage()), req, res);
        } catch (UsernameTakenException e) {
            res.status(403);
            return errorHandler(new Exception(e.getMessage()), req, res);
        } catch (InternalServerException e) {
            res.status(500);
            return errorHandler(new Exception(e.getMessage()), req, res);
        }
    }

    private Object listGames(Request req, Response res) throws DataAccessException {
        try {
            String authToken = req.headers("authorization");

            if (authToken == null || !authService.checkIfAuthExisits(authToken)) {
                res.status(401);
                return errorHandler(new Exception(new UnauthorizedException()), req, res);
            }

            Collection<GameData> games = this.gameService.listGames();
            ListGamesResult result = new ListGamesResult(games);
            return new Gson().toJson(result);

        } catch (BadRequestException e) {
            res.status(400);
            return errorHandler(new Exception(e.getMessage()), req, res);
        } catch (UsernameTakenException e) {
            res.status(403);
            return errorHandler(new Exception(e.getMessage()), req, res);
        } catch (InternalServerException e) {
            res.status(500);
            return errorHandler(new Exception(e.getMessage()), req, res);
        }
    }

    private Object clear(Request req, Response res) throws DataAccessException {
       try {
           userService.clearUsers();
           gameService.clearGames();
           authService.clearAuths();
           res.status(200);
           return "";
       } catch (DataAccessException e) {
            res.status(500);
            return errorHandler(new Exception(e.getMessage()), req, res);
        }
    }

}



