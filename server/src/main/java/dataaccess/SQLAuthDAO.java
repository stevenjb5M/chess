package dataaccess;

import model.AuthData;

import java.util.HashMap;

public class SQLAuthDAO implements AuthDAO {
    final private HashMap<String, AuthData> auths = new HashMap<>();


    @Override
    public AuthData addAuthData(AuthData authData) throws DataAccessException {
        return null;
    }

    @Override
    public Boolean checkIfAuthExists(String authToken) throws DataAccessException {
        return null;
    }

    @Override
    public void removeAuth(String authToken) throws DataAccessException {

    }

    @Override
    public String getUserByAuth(String authToken) throws DataAccessException {
        return "";
    }

    @Override
    public void clearAuths() throws DataAccessException {

    }
}
