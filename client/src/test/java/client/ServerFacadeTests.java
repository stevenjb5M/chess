package client;

import dataaccess.BadRequestException;
import exception.ResponseException;
import model.UserData;
import org.junit.jupiter.api.*;
import server.*;


public class ServerFacadeTests {

    private static Server server;
    static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        int port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        String portString = String.valueOf(port);
        facade = new ServerFacade("http://localhost:8080");
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

}
