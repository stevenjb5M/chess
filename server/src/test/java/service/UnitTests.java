package service;

import chess.ChessGame;
import dataAccess.*;
import model.AuthData;
import org.junit.jupiter.api.*;
import passoff.model.*;
import passoff.server.TestServerFacade;
import server.Server;
import service.AuthService;
import service.UserService;

import java.net.HttpURLConnection;
import java.util.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UnitTests {
    private static TestUser existingUser;
    private static TestUser newUser;
    private static TestCreateRequest createRequest;
    private static UserService userService;
    private static AuthService authService;

    @BeforeAll
    public static void init() {
        MemoryUserDAO userDAO = new MemoryUserDAO();
        MemoryAuthDAO authDAQ = new MemoryAuthDAO();
        authService = new AuthService(authDAQ);
        userService = new UserService(userDAO, authService);
    }

    @BeforeEach
    public void setup() throws DataAccessException {
        userService.clearUsers();

    }

    @Test
    @Order(1)
    @DisplayName("Register Positive")
    public void registerPositive() throws DataAccessException {
        RegisterRequest request = new RegisterRequest("Steven", "test", "steven@gmail.com");

        userService.register(request);

        Assertions.assertNotNull(userService.getUser("Steven"));

    }

    @Test
    @Order(1)
    @DisplayName("Register Negative")
    public void registerNegative() throws DataAccessException {
        RegisterRequest request = new RegisterRequest("Steven", null, "steven@gmail.com");

        Assertions.assertThrows(BadRequestException.class, () -> {
            userService.register(request);
        });

    }

    @Test
    @Order(1)
    @DisplayName("Login Positive")
    public void LoginPositive() throws DataAccessException {
        RegisterRequest request = new RegisterRequest("Steven", "test", "steven@gmail.com");

        userService.register(request);

        LoginRequest requestLogin = new LoginRequest("Steven", "test");

        LoginResult result =  userService.login(requestLogin);

        Assertions.assertTrue(authService.checkIfAuthExisits(result.getAuthToken()));

    }

    @Test
    @Order(1)
    @DisplayName("Login Negative")
    public void LoginNegative() throws DataAccessException {
        RegisterRequest request = new RegisterRequest("Steven", "test", "steven@gmail.com");

        userService.register(request);


        LoginRequest requestLogin = new LoginRequest("Steven", "test123");

        Assertions.assertThrows(UnauthorizedException.class, () -> {
            userService.login(requestLogin);
        });

    }

    @Test
    @Order(1)
    @DisplayName("Logout Positive")
    public void LogoutPositive() throws DataAccessException {
        RegisterRequest request = new RegisterRequest("Steven", "test", "steven@gmail.com");

        userService.register(request);

        LoginRequest requestLogin = new LoginRequest("Steven", "test");

        LoginResult loginResult = userService.login(requestLogin);

        LogoutRequest logoutRequest = new LogoutRequest(loginResult.getAuthToken());

        userService.Logout(logoutRequest.getAuthToken());

        Assertions.assertFalse(authService.checkIfAuthExisits(logoutRequest.getAuthToken()));

    }

    @Test
    @Order(1)
    @DisplayName("Logout Negative")
    public void LogoutNegative() throws DataAccessException {
        RegisterRequest request = new RegisterRequest("Steven", "test", "steven@gmail.com");

        userService.register(request);

        LoginRequest requestLogin = new LoginRequest("Steven", "test");

        LoginResult loginResult = userService.login(requestLogin);

        LogoutRequest logoutRequest = new LogoutRequest("");


        Assertions.assertThrows(UnauthorizedException.class, () -> {
            userService.Logout(logoutRequest.getAuthToken());
        });

    }



}
