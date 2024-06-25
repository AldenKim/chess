package websocket.commands;

import chess.ChessMove;

public class MakeMoveCommand extends UserGameCommand{
    private ChessMove move;

    public MakeMoveCommand(int gameID, ChessMove move, String authToken) {
        super(CommandType.MAKE_MOVE,authToken, gameID);
        this.move = move;
    }


    public ChessMove getMove() {
        return move;
    }
}
