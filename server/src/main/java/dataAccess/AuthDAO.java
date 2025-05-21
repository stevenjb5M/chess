package dataAccess;

import model.AuthData;
import model.UserData;

import javax.xml.crypto.Data;

public interface AuthDAO {

    AuthData addAuthData(AuthData authData) throws DataAccessException;

    Boolean checkIfAuthExists(String authToken) throws DataAccessException;

    void removeAuth(String authToken) throws  DataAccessException;

    String getUserByAuth(String authToken) throws DataAccessException;

    void clearAuths() throws DataAccessException;
}