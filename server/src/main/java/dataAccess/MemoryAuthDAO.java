package dataAccess;

import dataAccess.UserDAO;
import model.AuthData;
import model.UserData;

import javax.xml.crypto.Data;
import java.util.Collection;
import java.util.HashMap;

public class MemoryAuthDAO implements AuthDAO {
    final private HashMap<Integer, UserData> users = new HashMap<>();


    @Override
    public AuthData addAuthData(AuthData authData) throws DataAccessException {
        return null;
    }
}
