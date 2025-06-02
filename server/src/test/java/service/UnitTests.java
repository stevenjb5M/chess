package service;

import chess.ChessGame;
import dataaccess.*;
import model.GameData;
import org.junit.jupiter.api.*;
import passoff.model.*;
import server.LoginRequest;
import server.LoginResult;
import server.RegisterRequest;

import java.util.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UnitTests {
    private static TestUser existingUser;
    private static TestUser newUser;
    private static TestCreateRequest createRequest;
    private static UserService userService;
    private static AuthService authService;
    private static GameService gameService;


    @BeforeAll
    public static void init() {
        SQLUserDAO userDAO = new SQLUserDAO();
        SQLAuthDAO authDAQ = new SQLAuthDAO();
        SQLGameDAO gameDAQ = new SQLGameDAO();
        authService = new AuthService(authDAQ);
        userService = new UserService(userDAO, authService);
        gameService = new GameService(gameDAQ);
    }

    @BeforeEach
    public void setup() throws DataAccessException {
        userService.clearUsers();
        authService.clearAuths();
        gameService.clearGames();
    }

    @Test
    @Order(1)
    @DisplayName("Register Positive")
    public void registerPositive() throws DataAccessException {
        RegisterRequest request = new RegisterRequest("steven3", "test", "steven@gmail.com");

        userService.register(request);

        Assertions.assertNotNull(userService.getUser("steven3"));

    }

    @Test
    @Order(1)
    @DisplayName("Register Negative")
    public void registerNegative() throws DataAccessException {
        RegisterRequest request = new RegisterRequest("testuser2", null, "steven@gmail.com");

        Assertions.assertThrows(BadRequestException.class, () -> {
            userService.register(request);
        });

    }

    @Test
    @Order(1)
    @DisplayName("Login Positive")
    public void loginPositive() throws DataAccessException {
        RegisterRequest request = new RegisterRequest("steven9", "test", "steven@gmail.com");

        userService.register(request);

        LoginRequest requestLogin = new LoginRequest("steven9", "test");

        LoginResult result =  userService.login(requestLogin);

        Assertions.assertTrue(authService.checkIfAuthExisits(result.getAuthToken()));

    }

    @Test
    @Order(1)
    @DisplayName("Login Negative")
    public void loginNegative() throws DataAccessException {
        RegisterRequest request = new RegisterRequest("Steven12", "test", "steven@gmail.com");

        userService.register(request);


        LoginRequest requestLogin = new LoginRequest("Steven12", "test123");

        Assertions.assertThrows(UnauthorizedException.class, () -> {
            userService.login(requestLogin);
        });

    }

    @Test
    @Order(1)
    @DisplayName("Logout Positive")
    public void logoutPositive() throws DataAccessException {
        RegisterRequest request = new RegisterRequest("Steven11", "test", "steven@gmail.com");

        userService.register(request);

        LoginRequest requestLogin = new LoginRequest("Steven11", "test");

        LoginResult loginResult = userService.login(requestLogin);

        LogoutRequest logoutRequest = new LogoutRequest(loginResult.getAuthToken());

        userService.logout(logoutRequest.getAuthToken());

        Assertions.assertFalse(authService.checkIfAuthExisits(logoutRequest.getAuthToken()));

    }

    @Test
    @Order(1)
    @DisplayName("Logout Negative")
    public void logoutNegative() throws DataAccessException {
        RegisterRequest request = new RegisterRequest("Steven13", "test", "steven@gmail.com");

        userService.register(request);

        LoginRequest requestLogin = new LoginRequest("Steven13", "test");

        LoginResult loginResult = userService.login(requestLogin);

        LogoutRequest logoutRequest = new LogoutRequest("");


        Assertions.assertThrows(UnauthorizedException.class, () -> {
            userService.logout(logoutRequest.getAuthToken());
        });

    }

    @Test
    @Order(1)
    @DisplayName("Create Game Positive")
    public void createGamePositive() throws DataAccessException {
        RegisterRequest request = new RegisterRequest("Steven", "test", "steven@gmail.com");

        userService.register(request);

        CreateGameRequest createGameRequest = new CreateGameRequest("test");

        CreateGameResult result = gameService.createGame(createGameRequest);

        Assertions.assertNotNull(result.getGameID());

    }

    @Test
    @Order(1)
    @DisplayName("Create Game Negative")
    public void createGameNegative() throws DataAccessException {
        RegisterRequest request = new RegisterRequest("Steven", "test", "steven@gmail.com");

        userService.register(request);

        CreateGameRequest createGameRequest = new CreateGameRequest(null);

        Assertions.assertThrows(BadRequestException.class, () -> {
            CreateGameResult result = gameService.createGame(createGameRequest);
        });
    }

    @Test
    @Order(1)
    @DisplayName("Join Game Positive")
    public void joinGamePositive() throws DataAccessException {
        RegisterRequest request = new RegisterRequest("Steven", "test", "steven@gmail.com");

        userService.register(request);

        CreateGameRequest createGameRequest = new CreateGameRequest("test");

        CreateGameResult result = gameService.createGame(createGameRequest);

        JoinGameRequest joinRequest = new JoinGameRequest(ChessGame.TeamColor.BLACK, result.getGameID());

        gameService.joinGame(joinRequest, "Steven");

        String blackUsername = gameService.getBlackPlayerUsername(result.getGameID());

        Assertions.assertNotNull(blackUsername);

    }

    @Test
    @Order(1)
    @DisplayName("Join Game Negative")
    public void joinGameNegative() throws DataAccessException {
        RegisterRequest request = new RegisterRequest("Steven", "test", "steven@gmail.com");

        userService.register(request);

        CreateGameRequest createGameRequest = new CreateGameRequest("test");

        CreateGameResult result = gameService.createGame(createGameRequest);

        JoinGameRequest joinRequest = new JoinGameRequest(ChessGame.TeamColor.BLACK, result.getGameID());

        gameService.joinGame(joinRequest, "Steven");

        String whiteUsername = gameService.getWhitePlayerUsername(result.getGameID());

        Assertions.assertNull(whiteUsername);

    }

    @Test
    @Order(1)
    @DisplayName("List Games Positive")
    public void listGamesPositive() throws DataAccessException {
        RegisterRequest request = new RegisterRequest("Steven", "test", "steven@gmail.com");

        userService.register(request);

        CreateGameRequest createGameRequest = new CreateGameRequest("test");

        CreateGameResult result = gameService.createGame(createGameRequest);

        Collection<GameData> games = gameService.listGames();

        Assertions.assertNotNull(games);

    }

    @Test
    @Order(1)
    @DisplayName("List Games Negative")
    public void listGamesNegative() throws DataAccessException {
        RegisterRequest request = new RegisterRequest("Steven", "test", "steven@gmail.com");

        userService.register(request);

        Collection<GameData> games = gameService.listGames();

        Assertions.assertNotNull(games);

    }

    @Test
    @Order(1)
    @DisplayName("Clear Database")
    public void clearDatabase() throws DataAccessException {
        RegisterRequest request = new RegisterRequest("Steven", "test", "steven@gmail.com");

        userService.register(request);

        CreateGameRequest createGameRequest = new CreateGameRequest("test");

        CreateGameResult result = gameService.createGame(createGameRequest);

        JoinGameRequest joinRequest = new JoinGameRequest(ChessGame.TeamColor.BLACK, result.getGameID());

        gameService.joinGame(joinRequest, "Steven");

        String whiteUsername = gameService.getWhitePlayerUsername(result.getGameID());

        userService.clearUsers();
        gameService.clearGames();
        authService.clearAuths();

        Collection<GameData> games = gameService.listGames();

        Assertions.assertTrue(games.isEmpty());
    }

}
