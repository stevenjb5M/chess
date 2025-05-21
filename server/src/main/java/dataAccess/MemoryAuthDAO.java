package dataAccess;

import dataAccess.UserDAO;
import model.AuthData;
import model.UserData;

import javax.xml.crypto.Data;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

public class MemoryAuthDAO implements AuthDAO {
    final private HashMap<String, AuthData> auths = new HashMap<>();


    @Override
    public AuthData addAuthData(AuthData authData) throws DataAccessException {
        auths.put(authData.authToken(), authData);
        return authData;
    }

    @Override
    public Boolean checkIfAuthExists(String authToken) throws DataAccessException {
        AuthData data = auths.get(authToken);

        if (data != null) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void removeAuth(String authToken) throws DataAccessException {
        if (checkIfAuthExists(authToken)) {
            auths.remove(authToken);
        }
    }

    @Override
    public String getUserByAuth(String authToken) throws DataAccessException {
        AuthData data = auths.get(authToken);
        return data.username();
    }

    @Override
    public void clearAuths() throws DataAccessException {
        auths.clear();
    }


}
