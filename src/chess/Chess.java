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

        // Check there is a piece at the source square belonging to current player

        if (fromStr.length() != 2 || toStr.length() != 2
                || fromStr.charAt(0) < 'a' || fromStr.charAt(0) > 'h'
                || toStr.charAt(0) < 'a' || toStr.charAt(0) > 'h'
                || fromStr.charAt(1) < '1' || fromStr.charAt(1) > '8'
                || toStr.charAt(1) < '1' || toStr.charAt(1) > '8') {
            result.message = ReturnPlay.Message.ILLEGAL_MOVE;
            result.piecesOnBoard = getPiecesOnBoard();
            return result;
        }

        // Convert file letter to column index: 'a'=0, 'b'=1, ... 'h'=7
        int fromCol = fromStr.charAt(0) - 'a';
        // Convert rank digit to row index: '1'=0, '2'=1, ... '8'=7
        int fromRow = fromStr.charAt(1) - '1';

        int toCol = toStr.charAt(0) - 'a';
        int toRow = toStr.charAt(1) - '1';
        Piece piece = board.board[fromRow][fromCol];
        // Move the piece
        board.board[toRow][toCol]     = piece;
        board.board[fromRow][fromCol] = null;
        piece.row = toRow;
        piece.col = toCol;

        // Switch turns
        currentPlayer = (currentPlayer == Player.white) ? Player.black : Player.white;

        result.message = null;
        result.piecesOnBoard = getPiecesOnBoard();
        return result;
    }


    /**
     * This method should reset the game, and start from scratch.
     */
    public static void start() {
        /* FILL IN THIS METHOD */
        board.setupInitialPosition();
        currentPlayer = Player.white;
    }
}
