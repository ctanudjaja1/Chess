package chess;
import java.util.ArrayList;

public class Chess {

    enum Player { white, black }

    /**
     * Plays the next move for whichever player has the turn.
     *
     * @param move String for next move, e.g. "a2 a3"
     *
     * @return A ReturnPlay instance that contains the result of the move.
     *         See the section "The Chess class" in the assignment description for details of
     *         the contents of the returned ReturnPlay instance.
     */
    static Board board = new Board();
    static GameLogic logic       = new GameLogic(board);
    static Player currentPlayer = Player.white;

    private static ReturnPiece.PieceType convertPieceType(Piece p) {
        String color = (p.player == Player.white) ? "W" : "B";
        String type = "";

        if (p instanceof Pawn) type = "P";
        else if (p instanceof Rook) type = "R";
        else if (p instanceof Knight) type = "N";
        else if (p instanceof Bishop) type = "B";
        else if (p instanceof Queen) type = "Q";
        else if (p instanceof King) type = "K";

        return ReturnPiece.PieceType.valueOf(color + type);
    }

    private static ArrayList<ReturnPiece> getPiecesOnBoard() {
        ArrayList<ReturnPiece> list = new ArrayList<>();

        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                Piece p = board.board[r][c];
                if (p == null) continue;

                ReturnPiece rp = new ReturnPiece();
                rp.pieceRank = r + 1; // rank 1..8
                rp.pieceFile = ReturnPiece.PieceFile.values()[c];
                rp.pieceType = convertPieceType(p);

                list.add(rp);
            }
        }
        return list;
    }

    public static ReturnPlay play(String move) {

        /* FILL IN THIS METHOD */

        /* FOLLOWING LINE IS A PLACEHOLDER TO MAKE COMPILER HAPPY */
        /* WHEN YOU FILL IN THIS METHOD, YOU NEED TO RETURN A ReturnPlay OBJECT */
        ReturnPlay result = new ReturnPlay();
        if (move == null || move.isBlank()) {
            result.message = ReturnPlay.Message.ILLEGAL_MOVE;
            result.piecesOnBoard = getPiecesOnBoard();
            return result;
        }
        move = move.trim();

        // --- RESIGN ---
        if(move.equalsIgnoreCase("resign")){
            result.piecesOnBoard = getPiecesOnBoard();
            result.message = (currentPlayer == Player.white)
                    ? ReturnPlay.Message.RESIGN_BLACK_WINS
                    : ReturnPlay.Message.RESIGN_WHITE_WINS;
            return result;
        }

        /*Parsing */
        // Parse the move string, e.g. "e2 e4"
        String[] parts = move.split("\\s+");

        // Need at least two parts: "e2" and "e4"
        if (parts.length < 2) {
            result.message = ReturnPlay.Message.ILLEGAL_MOVE;
            result.piecesOnBoard = getPiecesOnBoard();
            return result;
        }

        String fromStr = parts[0]; // e.g. "e2"
        String toStr   = parts[1]; // e.g. "e4"
        String promotion = null;
        boolean drawRequest = false;

        for(int i = 2; i < parts.length; i++){
            if(parts[i].equalsIgnoreCase("draw?")){drawRequest = true;}
            else promotion = parts[i];
        }

        // Convert file letter to column index: 'a'=0, 'b'=1, ... 'h'=7
        int fromCol = fromStr.charAt(0) - 'a';
        // Convert rank digit to row index: '1'=0, '2'=1, ... '8'=7
        int fromRow = fromStr.charAt(1) - '1';

        int toCol = toStr.charAt(0) - 'a';
        int toRow = toStr.charAt(1) - '1';

        //Illegal Move
        if(!logic.isLegal(fromRow, fromCol, toRow, toCol, currentPlayer)){
            result.piecesOnBoard = getPiecesOnBoard();
            result.message = ReturnPlay.Message.ILLEGAL_MOVE;
            return result;
        }

        //Execute
        logic.executeMove(fromRow, fromCol, toRow, toCol, currentPlayer, promotion);

        //Draw (move execute first, then draw)
        if(drawRequest){
            result.piecesOnBoard = getPiecesOnBoard();
            result.message = ReturnPlay.Message.DRAW;
            return result;
        }


        // Switch turns
        currentPlayer = (currentPlayer == Player.white) ? Player.black : Player.white;

        result.piecesOnBoard = getPiecesOnBoard();
        result.message = logic.getMessage(currentPlayer);
        return result;
    }


    /**
     * This method should reset the game, and start from scratch.
     */
    public static void start() {
        /* FILL IN THIS METHOD */
        board.setupInitialPosition();
        logic.reset();
        currentPlayer = Player.white;
    }
}
