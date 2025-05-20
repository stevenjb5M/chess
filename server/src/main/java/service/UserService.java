package service;

import dataAccess.BadRequestException;
import dataAccess.DataAccessException;
import dataAccess.UserDAO;
import dataAccess.UsernameTakenException;
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

        if (registerRequest == null) {
            throw new DataAccessException("403 Error: Invalid Request");
        }

        UserData existingUser = getUser(registerRequest.getUsername());

        if (existingUser == null) {
            String username = registerRequest.getUsername();
            String password = registerRequest.getPassword();
            String email = registerRequest.getEmail();

            if (username != null && password != null && email != null) {
                UserData newUser = new UserData(username, password, email);

                userDAO.addUser(newUser);

                String authToken = AuthService.generateToken();

                AuthData authData = new AuthData(authToken, newUser.username());

                authService.addAuthData(authData);

                return new RegisterResult(newUser.username(), authToken);
            } else {
                throw new BadRequestException();
            }

        } else {
            throw new UsernameTakenException();
        }
    }

    public UserData getUser(String username) throws DataAccessException {
        return (userDAO.getUser(username));
    }

    public void clearUsers() throws DataAccessException {
        userDAO.clearUsers();
    }

}

