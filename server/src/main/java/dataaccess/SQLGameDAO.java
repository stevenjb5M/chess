package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;
import server.JoinGameRequest;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public class SQLGameDAO implements GameDAO {

    public SQLGameDAO() {
        DatabaseManager.initDAO(createStatements);
    }

    @Override
    public GameData addGame(GameData game) throws DataAccessException {
        Integer gameId = new Random().nextInt(1_000_000);
        GameData gameData = new GameData(gameId, game.whiteUsername(), game.blackUsername(), game.gameName(), game.game());
        String gameName = gameData.gameName();
        var statement = "INSERT INTO game (gameId, gameName, json) VALUES(?, ?, ?)";
        var json = new Gson().toJson(gameData);
        executeUpdate(statement, gameId, gameName, json);

        return gameData;
    }

    @Override
    public GameData getGame(String gameName) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT gameName, json FROM game WHERE gameName=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1,gameName);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readGameData(rs);
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException("");
        }
        return null;
    }

    @Override
    public GameData getGame(int gameId) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT gameId, json FROM game WHERE gameId=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setInt(1,gameId);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readGameData(rs);
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException("");
        }
        return null;
    }

    private GameData readGameData(ResultSet rs) throws SQLException {
        var json = rs.getString("json");
        GameData gameData = new Gson().fromJson(json, GameData.class);
        return gameData;
    }

    @Override
    public GameData joinGame(JoinGameRequest request, String username) throws DataAccessException {
        GameData data = getGame(request.getGameID());
        if (request.getPlayerColor() == ChessGame.TeamColor.WHITE) {
            if (data.whiteUsername() == null) {
                GameData newGame = new GameData(data.gameID(), username, data.blackUsername(), data.gameName(), data.game());
                updateGame(newGame);
            } else {
                throw new UsernameTakenException();
            }
        } else {
            if (data.blackUsername() == null) {
                GameData newGame = new GameData(data.gameID(), data.whiteUsername(), username, data.gameName(), data.game());
                updateGame(newGame);
            } else {
                throw new UsernameTakenException();
            }
        }
        return data;
    }

    @Override
    public Collection<GameData> listGames() throws DataAccessException {
        List<GameData> games = new ArrayList<>();

        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT gameId, json FROM game";
            try (var ps = conn.prepareStatement(statement)) {
                try (var rs = ps.executeQuery()) {
                    while (rs.next()) {
                        GameData game = readGameData(rs);
                        games.add(game);
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException("");
        }
        return games;
    }


    public void removePlayer(int gameID, String playerName) throws DataAccessException {
        try {
        GameData game = getGame(gameID);
        if (game != null) {
            GameData newGame = null;
            if (playerName != null && playerName.equals(game.whiteUsername())) {
                newGame = new GameData(game.gameID(), null, game.blackUsername(), game.gameName(), game.game());
            } else if (playerName != null && playerName.equals(game.blackUsername())) {
                newGame = new GameData(game.gameID(), game.whiteUsername(), null, game.gameName(), game.game());
            }
            if (newGame != null) {
                updateGame(newGame);
            }
        }} catch (DataAccessException e) {
            throw new DataAccessException("unable to update database:", e);
        }
    }

    public void resign(int gameID) throws DataAccessException {
        try {
            GameData game = getGame(gameID);
            if (game != null) {

                ChessGame game1 = game.game();

                game1.gameOver = true;

                GameData gameData = new GameData(game.gameID(), game.whiteUsername(), game.blackUsername(), game.gameName(), game1);

                updateGame(gameData);

            }} catch (DataAccessException e) {
            throw new DataAccessException("unable to update database:", e);
        }
    }

    @Override
    public void clearGames() throws DataAccessException {
        var statement = "TRUNCATE game";
        executeUpdate(statement);
    }

    private int executeUpdate(String statement, Object... params) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
                for (var i = 0; i < params.length; i++) {
                    var param = params[i];
                    if (param instanceof String p) {ps.setString(i + 1, p);}
                    else if (param instanceof Integer p) {ps.setInt(i + 1, p);}
                    else if (param instanceof GameData p) {ps.setString(i + 1, p.toString());}
                    else if (param == null) {ps.setNull(i + 1, NULL);}
                }
                ps.executeUpdate();

                var rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }

                return 0;
            }
        } catch (SQLException | DataAccessException e) {
            throw new DataAccessException("unable to update database:", e);
        }
    }


    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS  game (
              gameId VARCHAR(256) NOT NULL PRIMARY KEY,
              gameName VARCHAR(256) NOT NULL,
              json TEXT
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
    };

    public void updateGame(GameData game) throws DataAccessException {
        String sql = "UPDATE game set gameName = ?, json = ? WHERE gameId = ?";

        try (var conn = DatabaseManager.getConnection();
             var ps = conn.prepareStatement(sql)) {

            ps.setString(1, game.gameName());
            var json = new Gson().toJson(game);
            ps.setString(2, json); // ChessGame to JSON
            ps.setInt(3, game.gameID());

            ps.executeUpdate();

        } catch (Exception e) {
            throw new DataAccessException("Error updating game", e);
        }
    }
}
