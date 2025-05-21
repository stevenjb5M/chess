package dataAccess;

import dataAccess.UserDAO;
import model.AuthData;
import model.UserData;

import javax.xml.crypto.Data;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

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
