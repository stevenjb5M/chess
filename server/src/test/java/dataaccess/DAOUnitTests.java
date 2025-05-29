package dataaccess;

import chess.ChessGame;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.*;
import org.mindrot.jbcrypt.BCrypt;
import passoff.model.TestCreateRequest;
import passoff.model.TestUser;
import service.*;

import java.util.Collection;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class DAOUnitTests {
    private static TestUser existingUser;
    private static TestUser newUser;
    private static TestCreateRequest createRequest;
    private static UserService userService;
    private static AuthService authService;
    private static GameService gameService;
    private static UserDAO userDAO;
    private static AuthDAO authDAO;
    private static GameDAO gameDAO;


    @BeforeAll
    public static void init() {
        userDAO = new SQLUserDAO();
        authDAO = new SQLAuthDAO();
        gameDAO = new SQLGameDAO();
        authService = new AuthService(authDAO);
        userService = new UserService(userDAO, authService);
        gameService = new GameService(gameDAO);
    }

    @BeforeEach
    public void setup() throws DataAccessException {
        userService.clearUsers();
        authService.clearAuths();
        gameService.clearGames();
    }

    @Test
    @Order(1)
    @DisplayName("addUser Positive")
    public void addUserPositive() throws DataAccessException {
        RegisterRequest request = new RegisterRequest("steven3", "test", "steven@gmail.com");

        String hashedPassword = BCrypt.hashpw("password", BCrypt.gensalt());

        UserData userData = new UserData("steven", hashedPassword, "steven@gmail.com");

        userDAO.addUser(userData);


        Assertions.assertNotNull(userDAO.getUser("steven"));

    }

    @Test
    @Order(1)
    @DisplayName("addUser Negative")
    public void addUserNegative() throws DataAccessException {
        RegisterRequest request = new RegisterRequest("steven3", "test", "steven@gmail.com");

        String hashedPassword = BCrypt.hashpw("password", BCrypt.gensalt());

        UserData userData = new UserData(null, hashedPassword, "steven@gmail.com");

        Assertions.assertThrows(DataAccessException.class, () -> {
            userDAO.addUser(userData);
        });

    }

}
