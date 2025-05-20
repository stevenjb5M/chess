package dataAccess;

import model.AuthData;
import model.UserData;

public interface AuthDAO {

    AuthData addAuthData(AuthData authData) throws DataAccessException;

    Boolean checkIfAuthExists(String authToken) throws DataAccessException;

    void removeAuth(String authToken) throws  DataAccessException;
}