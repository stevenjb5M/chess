package client;

import chess.ChessGame;
import dataaccess.BadRequestException;
import exception.ResponseException;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.*;
import server.*;

import java.util.Collection;


public class ServerFacadeTests {

    private static Server server;
    static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        int port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        String portString = String.valueOf(port);
        facade = new ServerFacade("http://localhost:" + portString);
    }

    @BeforeEach
    void clearDataBase() throws ResponseException {
        facade.clearDataBase();
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @Test
    public void sampleTest() {
        Assertions.assertTrue(true);
    }

    @Test
    void registerPositive() throws Exception {
        UserData userData = new UserData("player12", "password", "p1@email.com");
        RegisterResult responseData = facade.registerUser(userData);
        Assertions.assertTrue(responseData.getAuthToken().length() > 10);
    }

    @Test
    void registerNegative() throws Exception {
        UserData userData = new UserData("player12", null, "p1@email.com");

        Assertions.assertThrows(ResponseException.class, () -> {
            RegisterResult responseData = facade.registerUser(userData);
        });
    }

    @Test
    void loginPositive() throws Exception {
        UserData userData = new UserData("player12", "password", "p1@email.com");
        RegisterResult responseData = facade.registerUser(userData);


        UserData userData2 = new UserData("player12", "password", null);
        LoginResult responseData2 = facade.loginUser(userData2);
        Assertions.assertTrue(responseData2.getAuthToken().length() > 10);
    }

    @Test
    void loginNegative() throws Exception {
        UserData userData = new UserData("player12", null, "p1@email.com");

        Assertions.assertThrows(ResponseException.class, () -> {
            LoginResult responseData = facade.loginUser(userData);
        });
    }

    @Test
    void logoutPositive() throws Exception {
        UserData userData = new UserData("player12", "password", "p1@email.com");
        RegisterResult responseData = facade.registerUser(userData);
        facade.authToken = responseData.getAuthToken();

        Assertions.assertDoesNotThrow(() -> {
            facade.logoutUser(responseData.getAuthToken());
        });
    }

    @Test
    void logoutNegative() throws Exception {
        Assertions.assertThrows(ResponseException.class, () -> {
            facade.logoutUser("test");
        });
    }

    @Test
    void listGamesPositive() throws Exception {
        UserData userData = new UserData("player12", "password", "p1@email.com");
        RegisterResult responseData = facade.registerUser(userData);
        facade.authToken = responseData.getAuthToken();

        CreateGameRequest request = new CreateGameRequest("test");
        facade.createGame(request);

        ListGamesResult result = facade.listGames();
        Collection<GameData> games = result.getGames();

        Assertions.assertEquals(games.size(), 1);
    }

    @Test
    void listGamesNegative() throws Exception {

        UserData userData = new UserData("player12", "password", "p1@email.com");
        RegisterResult responseData = facade.registerUser(userData);
        facade.authToken = responseData.getAuthToken();

        ListGamesResult result = facade.listGames();
        Collection<GameData> games = result.getGames();

        Assertions.assertEquals(games.size(), 0);
    }

    @Test
    void createGamesPositive() throws Exception {
        UserData userData = new UserData("player12", "password", "p1@email.com");
        RegisterResult responseData = facade.registerUser(userData);
        facade.authToken = responseData.getAuthToken();

        CreateGameRequest request = new CreateGameRequest("testgame");
        facade.createGame(request);

        CreateGameRequest request2 = new CreateGameRequest("testgame1");
        facade.createGame(request2);


        ListGamesResult result = facade.listGames();
        Collection<GameData> games = result.getGames();

        Assertions.assertEquals(games.size(), 2);
    }

    @Test
    void createGamesNegative() throws Exception {

        UserData userData = new UserData("player12", "password", "p1@email.com");
        RegisterResult responseData = facade.registerUser(userData);
        facade.authToken = responseData.getAuthToken();


        CreateGameRequest request = new CreateGameRequest(null);

        Assertions.assertThrows(ResponseException.class, () -> {
            facade.createGame(request);
        });

    }

    @Test
    void joinGamesPositive() throws Exception {
        UserData userData = new UserData("player12", "password", "p1@email.com");
        RegisterResult responseData = facade.registerUser(userData);
        facade.authToken = responseData.getAuthToken();

        CreateGameRequest request = new CreateGameRequest("testgame");
        facade.createGame(request);


        ListGamesResult result = facade.listGames();
        Collection<GameData> games = result.getGames();

        GameData game =  games.iterator().next();

        JoinGameRequest joinRequest = new JoinGameRequest(ChessGame.TeamColor.BLACK, game.gameID());
        facade.joinGame(joinRequest);

        ListGamesResult result1 = facade.listGames();
        Collection<GameData> games1 = result1.getGames();

        GameData game1 = games1.iterator().next();
        String username = game1.blackUsername();

        Assertions.assertEquals(username, "player12");
    }

    @Test
    void joinGamesNegative() throws Exception {

        UserData userData = new UserData("player12", "password", "p1@email.com");
        RegisterResult responseData = facade.registerUser(userData);
        facade.authToken = responseData.getAuthToken();

        CreateGameRequest request = new CreateGameRequest("testgame");
        facade.createGame(request);


        ListGamesResult result = facade.listGames();
        Collection<GameData> games = result.getGames();

        GameData game =  games.iterator().next();

        JoinGameRequest joinRequest = new JoinGameRequest(ChessGame.TeamColor.BLACK, 123);

        Assertions.assertThrows(ResponseException.class, () -> {
            facade.joinGame(joinRequest);
        });

    }



}
