package dataAccess;

import model.AuthData;
import model.UserData;

public interface AuthDAO {

    AuthData addAuthData(AuthData authData) throws DataAccessException;

}