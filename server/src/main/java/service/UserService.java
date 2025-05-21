package service;

import dataaccess.*;
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

    public LoginResult login(LoginRequest loginRequest) throws DataAccessException {

        if (loginRequest == null || loginRequest.getUsername() == null || loginRequest.getPassword() == null) {
            throw new BadRequestException();
        }

        UserData user = getUser(loginRequest.getUsername());

        if (user != null) {
            String username = loginRequest.getUsername();
            String password = loginRequest.getPassword();

            if (username != null && password != null) {

                UserData userData = userDAO.getUser(username);
                String userPassword = userData.password();

                if (userPassword.equals(password)) {
                    String authToken = AuthService.generateToken();

                    AuthData authData = new AuthData(authToken, userData.username());

                    authService.addAuthData(authData);

                    return new LoginResult(username, authToken);
                } else {
                    throw new UnauthorizedException();
                }

            } else {
                throw new BadRequestException();
            }

        } else {
            throw new UnauthorizedException();
        }
    }

    public void logout(String authToken) throws DataAccessException {
        if (authService.checkIfAuthExisits(authToken)) {
            authService.removeAuth(authToken);
        } else {
            throw new UnauthorizedException();
        }
    }

}

