import chess.*;
import dataAccess.AuthDAO;
import dataAccess.DataAccessException;
import dataAccess.MemoryUserDAO;
import dataAccess.UserDAO;
import model.AuthData;
import server.Server;
import service.AuthService;
import service.UserService;

public class Main {
    public static void main(String[] args) {
        UserService service = new UserService(new MemoryUserDAO(), new AuthService(new AuthDAO() {
            @Override
            public AuthData addAuthData(AuthData authData) throws DataAccessException {
                return null;
            }
        }));
        Server myServer = new Server(service);

        myServer.run(8080);

        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Server: " + piece);
    }
}