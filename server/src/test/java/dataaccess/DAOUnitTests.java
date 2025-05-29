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

    @Test
    @Order(1)
    @DisplayName("getUser Positive")
    public void getUserPositive() throws DataAccessException {
        String hashedPassword = BCrypt.hashpw("password", BCrypt.gensalt());

        UserData userData = new UserData("steven", hashedPassword, "steven@gmail.com");

        userDAO.addUser(userData);


        Assertions.assertNotNull(userDAO.getUser("steven"));

    }

    @Test
    @Order(1)
    @DisplayName("getUser Negative")
    public void getUserNegative() throws DataAccessException {

        Assertions.assertNull(userDAO.getUser("Steven12345"));

    }

    @Test
    @Order(1)
    @DisplayName("clearUsers")
    public void clearUsers() throws DataAccessException {
        String hashedPassword = BCrypt.hashpw("password", BCrypt.gensalt());

        UserData userData = new UserData("steven", hashedPassword, "steven@gmail.com");

        userDAO.addUser(userData);

        userDAO.clearUsers();


        Assertions.assertNull(userDAO.getUser("steven"));

    }

    @Test
    @Order(1)
    @DisplayName("addGame Positive")
    public void addGamePositive() throws DataAccessException {

        GameData gameData = new GameData(1234, "", "", "Game1", new ChessGame());

        gameDAO.addGame(gameData);

        Assertions.assertNotNull(gameDAO.getGame("Game1"));

    }

    @Test
    @Order(1)
    @DisplayName("addGame Negative")
    public void addGameNegative() throws DataAccessException {

        GameData gameData = new GameData(1234, "", "", "Game1", new ChessGame());

        gameDAO.addGame(gameData);

        Assertions.assertNull(gameDAO.getGame("Game12"));

    }

    @Test
    @Order(1)
    @DisplayName("getGame Positive")
    public void getGamePositive() throws DataAccessException {

        GameData gameData = new GameData(1234, "", "", "Game1", new ChessGame());

        gameDAO.addGame(gameData);

        Assertions.assertNotNull(gameDAO.getGame("Game1"));

    }

    @Test
    @Order(1)
    @DisplayName("getGame Negative")
    public void getGameNegative() throws DataAccessException {

        Assertions.assertNull(gameDAO.getGame("Game121"));

    }

    @Test
    @Order(1)
    @DisplayName("joinGame Positive")
    public void joinGamePositive() throws DataAccessException {

        GameData gameData = new GameData(1234, null, "", "Game12", new ChessGame());

        gameDAO.addGame(gameData);

        GameData gameData1 = gameDAO.getGame("Game12");

        JoinGameRequest joinGameRequest = new JoinGameRequest(ChessGame.TeamColor.WHITE, gameData1.gameID());

        gameDAO.joinGame(joinGameRequest, "Steven");


        GameData newGame = gameDAO.getGame(gameData1.gameID());

        Assertions.assertTrue(newGame.whiteUsername().equals("Steven"));

    }

    @Test
    @Order(1)
    @DisplayName("joinGame Negative")
    public void joinGameNegative() throws DataAccessException {

        GameData gameData = new GameData(1234, null, "", "Game12", new ChessGame());

        gameDAO.addGame(gameData);

        GameData gameData1 = gameDAO.getGame("Game12");

        JoinGameRequest joinGameRequest = new JoinGameRequest(ChessGame.TeamColor.WHITE, null);

        Assertions.assertThrows(NullPointerException.class, () -> {
            gameDAO.joinGame(joinGameRequest, "Steven");
        });

    }

    @Test
    @Order(1)
    @DisplayName("listGame Positive")
    public void listGamePositive() throws DataAccessException {

        GameData gameData = new GameData(1234, null, "", "Game123", new ChessGame());

        gameDAO.addGame(gameData);

        GameData gameData1 = new GameData(1234, null, "", "Game1234", new ChessGame());

        gameDAO.addGame(gameData);
        gameDAO.addGame(gameData1);

        int len = gameDAO.listGames().size();

        Assertions.assertEquals(len, 3);

    }

    @Test
    @Order(1)
    @DisplayName("listGame Negative")
    public void listGameNegative() throws DataAccessException {

        GameData gameData = new GameData(1234, null, "", "Game123", new ChessGame());

        gameDAO.addGame(gameData);

        GameData gameData1 = new GameData(1234, null, "", "Game1234", new ChessGame());

        gameDAO.addGame(gameData);
        gameDAO.addGame(gameData1);

        int len = gameDAO.listGames().size();

        Assertions.assertNotEquals(len, 5);

    }

    @Test
    @Order(1)
    @DisplayName("clearGames")
    public void clearGames() throws DataAccessException {
        GameData gameData = new GameData(1234, null, "", "Game123", new ChessGame());

        gameDAO.addGame(gameData);

        GameData gameData1 = new GameData(1234, null, "", "Game1234", new ChessGame());

        gameDAO.addGame(gameData);
        gameDAO.addGame(gameData1);


        gameDAO.clearGames();

        int len = gameDAO.listGames().size();

        Assertions.assertEquals(len, 0);

    }

}
