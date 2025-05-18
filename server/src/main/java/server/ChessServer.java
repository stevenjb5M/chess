package server;

import service.AuthService;
import service.GameService;
import service.UserService;

public class ChessServer {
    private final UserService userService;
    private final AuthService authService;
    private final GameService gameService;

    public ChessServer(UserService userService, AuthService authService, GameService gameService) {
        this.userService = userService;
        this.authService = authService;
        this.gameService = gameService;
    }
}