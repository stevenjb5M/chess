package dataaccess;

import com.google.gson.Gson;
import model.AuthData;
import model.UserData;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public class SQLAuthDAO implements AuthDAO {

    public SQLAuthDAO() {
        DatabaseManager.initDAO(createStatements);
    }


    @Override
    public AuthData addAuthData(AuthData authData) throws DataAccessException {
        String token = authData.authToken();
        var statement = "INSERT INTO auth (token, json) VALUES(?, ?)";
        var json = new Gson().toJson(authData);
        executeUpdate(statement, token, json);

        return authData;
    }

    @Override
    public Boolean checkIfAuthExists(String authToken) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            AuthData data = getAuthData(authToken);
            if (data != null) {
                return true;
            }
        } catch (Exception e) {
            throw new InternalServerException("");
        }
        return false;
    }

    public AuthData getAuthData(String authToken) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT token, json FROM auth WHERE token=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1,authToken);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readAuthData(rs);
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException("");
        }
        return null;
    }

    private AuthData readAuthData(ResultSet rs) throws SQLException {
        var username = rs.getString("token");
        var json = rs.getString("json");
        AuthData authData = new Gson().fromJson(json, AuthData.class);
        return authData;
    }

    @Override
    public void removeAuth(String authToken) throws DataAccessException {
        var statement = "DELETE FROM auth WHERE token=?";
        executeUpdate(statement,authToken);
    }

    @Override
    public String getUserByAuth(String authToken) throws DataAccessException {
        try {
            AuthData authData = getAuthData(authToken);

            if (authData.username() != null) {
                return authData.username();
            }
            return "";
        } catch (Exception e) {
            throw new DataAccessException("user does not have access");
        }
    }

    @Override
    public void clearAuths() throws DataAccessException {
        var statement = "TRUNCATE auth";
        executeUpdate(statement);
    }

    private int executeUpdate(String statement, Object... params) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
                for (var i = 0; i < params.length; i++) {
                    var param = params[i];
                    if (param instanceof String p) { ps.setString(i + 1, p); }
                    else if (param instanceof Integer p) { ps.setInt(i + 1, p);}
                    else if (param instanceof AuthData p) {ps.setString(i + 1, p.toString());}
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
            CREATE TABLE IF NOT EXISTS  auth (
              token VARCHAR(256) NOT NULL PRIMARY KEY,
              json TEXT
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
    };

}
