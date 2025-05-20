package service;

import dataAccess.DataAccessException;
import dataAccess.UserDAO;
import model.AuthData;
import model.UserData;

public class UserService {

    private final UserDAO userDAO;
    private final AuthService authService;


    public UserService(UserDAO userDAO, AuthService authService) {
        this.userDAO = userDAO;
        this.authService = authService;
    }

    public RegisterResult register(RegisterRequest registerRequest) throws DataAccessException {

        UserData existingUser = getUser(registerRequest.getUsername());

        if (existingUser == null) {
            UserData newUser = new UserData(registerRequest.getUsername(), registerRequest.getPassword(), registerRequest.getEmail());
            userDAO.addUser(newUser);

            String authToken = AuthService.generateToken();

            AuthData authData = new AuthData(authToken, newUser.username());

            authService.addAuthData(authData);

            return new RegisterResult(authToken, newUser.username());

        } else {
            throw new DataAccessException("403 Error: Username already taken");
        }
    }

    public UserData getUser(String username) throws DataAccessException {
        return (userDAO.getUser(username));
    }

}

