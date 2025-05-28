package dataaccess;

import model.UserData;
import javax.xml.crypto.Data;
import java.util.HashMap;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.Collection;
import java.sql.*;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public class SQLUserDAO implements UserDAO {

    public SQLUserDAO() {
        try {
            DatabaseManager.createDatabase();
            DatabaseManager.configureDatabase(createStatements);
            //example();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public UserData addUser(UserData user) throws DataAccessException {
        UserData existingUser = getUser(user.username());

        if (existingUser == null) {
            var statement = "INSERT INTO user (username, json) VALUES (?, ?)";
            var json = new Gson().toJson(user);
            var id = executeUpdate(statement, user.username(), json);

            return user;
        } else {
            throw new UsernameTakenException("Error: already taken");
        }
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
            try (var conn = DatabaseManager.getConnection()) {
                var statement = "SELECT username, json FROM user WHERE username=?";
                try (var ps = conn.prepareStatement(statement)) {
                    ps.setString(1,username);
                    try (var rs = ps.executeQuery()) {
                        if (rs.next()) {
                            return readUser(rs);
                        }
                    }
                }
            } catch (SQLException | RuntimeException e) {
                throw new InternalServerException("Internal Server Error");
            }
            return null;
    }

    private UserData readUser(ResultSet rs) throws SQLException {
        var username = rs.getString("username");
        var json = rs.getString("json");
        UserData userData = new Gson().fromJson(json, UserData.class);
        return userData;
    }

    @Override
    public void clearUsers() throws DataAccessException {
        var statement = "TRUNCATE user";
        executeUpdate(statement);
    }

    private int executeUpdate(String statement, Object... params) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
                for (var i = 0; i < params.length; i++) {
                    var param = params[i];
                    if (param instanceof String p) ps.setString(i + 1, p);
                    else if (param instanceof Integer p) ps.setInt(i + 1, p);
                    else if (param instanceof UserData p) ps.setString(i + 1, p.toString());
                    else if (param == null) ps.setNull(i + 1, NULL);
                }
                ps.executeUpdate();

                var rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }

                return 0;
            }
        } catch (SQLException | DataAccessException e) {
            throw new DataAccessException("unable to update database");
        }
    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS  user (
              username VARCHAR(256) NOT NULL PRIMARY KEY,
              json TEXT
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
    };

}
