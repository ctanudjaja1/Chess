package chess;

public class GameLogic {

    private Board board;

    private int enPassantRow = -1;
    private int enPassantCol = -1;

    private boolean whiteKingMoved  = false;
    private boolean blackKingMoved  = false;
    private boolean whiteRookAMoved = false;
    private boolean whiteRookHMoved = false;
    private boolean blackRookAMoved = false;
    private boolean blackRookHMoved = false;

    public GameLogic(Board board) {
        this.board = board;
    }

    // -------------------------------------------------------------------------
    // Reset
    // -------------------------------------------------------------------------

    public void reset() {
        enPassantRow = -1;
        enPassantCol = -1;
        whiteKingMoved = blackKingMoved = false;
        whiteRookAMoved = whiteRookHMoved = false;
        blackRookAMoved = blackRookHMoved = false;
    }

    // -------------------------------------------------------------------------
    // getMessage — returns the ReturnPlay.Message after a move is executed.
    // Chess.java calls this instead of checking isInCheck/isCheckmate separately.
    //
    // Call this AFTER executeMove() and AFTER switching turns.
    // Pass in the player whose turn it now is (the one who might be in check).
    // -------------------------------------------------------------------------

    public ReturnPlay.Message getMessage(Chess.Player playerToCheck) {
        if (isCheckmate(playerToCheck)) {
            // The opponent (who just moved) wins
            return (playerToCheck == Chess.Player.black)
                    ? ReturnPlay.Message.CHECKMATE_WHITE_WINS
                    : ReturnPlay.Message.CHECKMATE_BLACK_WINS;
        }
        if (isInCheck(playerToCheck)) {
            return ReturnPlay.Message.CHECK;
        }
        return null; // normal legal move
    }

    // -------------------------------------------------------------------------
    // isLegal
    // -------------------------------------------------------------------------

    public boolean isLegal(int fromRow, int fromCol, int toRow, int toCol, Chess.Player player) {
        if (fromRow < 0 || fromRow > 7 || fromCol < 0 || fromCol > 7) return false;
        if (toRow   < 0 || toRow   > 7 || toCol   < 0 || toCol   > 7) return false;

        Piece piece = board.board[fromRow][fromCol];
        if (piece == null || piece.player != player) return false;
        if (fromRow == toRow && fromCol == toCol) return false;

        // Castling: king moves 2 squares sideways
        if (piece instanceof King && fromRow == toRow && Math.abs(toCol - fromCol) == 2) {
            return isCastlingLegal(fromRow, fromCol, toCol, player);
        }

        // En passant: pawn moves diagonally to an empty square
        if (piece instanceof Pawn) {
            int colDiff = Math.abs(toCol - fromCol);
            int rowDiff = toRow - fromRow;
            int dir     = (player == Chess.Player.white) ? 1 : -1;
            if (colDiff == 1 && rowDiff == dir && board.board[toRow][toCol] == null) {
                if (toRow == enPassantRow && toCol == enPassantCol) {
                    return !enPassantLeavesKingInCheck(fromRow, fromCol, toRow, toCol, player);
                }
                return false;
            }
        }

        // Normal move
        if (!piece.isValidMove(toRow, toCol, board.board)) return false;
        return !moveLeavesKingInCheck(fromRow, fromCol, toRow, toCol, player);
    }

    // -------------------------------------------------------------------------
    // executeMove
    // -------------------------------------------------------------------------

    public void executeMove(int fromRow, int fromCol, int toRow, int toCol,
                            Chess.Player player, String promotion) {
        Piece piece = board.board[fromRow][fromCol];

        enPassantRow = -1;
        enPassantCol = -1;

        // Castling
        if (piece instanceof King && Math.abs(toCol - fromCol) == 2) {
            board.board[toRow][toCol]     = piece;
            board.board[fromRow][fromCol] = null;
            piece.row = toRow;
            piece.col = toCol;

            if (toCol == 6) { // King-side
                Piece rook = board.board[fromRow][7];
                board.board[fromRow][5] = rook;
                board.board[fromRow][7] = null;
                if (rook != null) { rook.row = fromRow; rook.col = 5; }
            } else {          // Queen-side
                Piece rook = board.board[fromRow][0];
                board.board[fromRow][3] = rook;
                board.board[fromRow][0] = null;
                if (rook != null) { rook.row = fromRow; rook.col = 3; }
            }
        }
        // En passant
        else if (piece instanceof Pawn
                && Math.abs(toCol - fromCol) == 1
                && board.board[toRow][toCol] == null) {
            board.board[toRow][toCol]     = piece;
            board.board[fromRow][fromCol] = null;
            piece.row = toRow;
            piece.col = toCol;
            board.board[fromRow][toCol] = null; // remove captured pawn
        }
        // Normal
        else {
            board.board[toRow][toCol]     = piece;
            board.board[fromRow][fromCol] = null;
            piece.row = toRow;
            piece.col = toCol;
        }

        // Record en passant target if pawn moved two squares
        if (piece instanceof Pawn && Math.abs(toRow - fromRow) == 2) {
            enPassantRow = (fromRow + toRow) / 2;
            enPassantCol = fromCol;
        }

        // Pawn promotion
        if (piece instanceof Pawn && (toRow == 7 || toRow == 0)) {
            executePromotion(toRow, toCol, player, promotion);
        }

        updateCastlingFlags(piece, fromRow, fromCol, player);
    }

    // -------------------------------------------------------------------------
    // Promotion
    // -------------------------------------------------------------------------

    private void executePromotion(int row, int col, Chess.Player player, String promotion) {
        String promo = (promotion == null || promotion.trim().isEmpty())
                ? "Q" : promotion.trim().toUpperCase();
        Piece promoted;
        switch (promo) {
            case "R": promoted = new Rook  (player, row, col); break;
            case "N": promoted = new Knight(player, row, col); break;
            case "B": promoted = new Bishop(player, row, col); break;
            default:  promoted = new Queen (player, row, col); break;
        }
        board.board[row][col] = promoted;
    }

    // -------------------------------------------------------------------------
    // Castling legality
    // -------------------------------------------------------------------------

    private boolean isCastlingLegal(int row, int fromCol, int toCol, Chess.Player player) {
        if (player == Chess.Player.white && whiteKingMoved) return false;
        if (player == Chess.Player.black && blackKingMoved) return false;
        if (isInCheck(player)) return false;

        Chess.Player opp = opponent(player);

        if (toCol == 6) { // King-side
            if (player == Chess.Player.white && whiteRookHMoved) return false;
            if (player == Chess.Player.black && blackRookHMoved) return false;
            if (board.board[row][5] != null || board.board[row][6] != null) return false;
            if (!(board.board[row][7] instanceof Rook) || board.board[row][7].player != player) return false;
            if (isSquareAttacked(row, 5, opp) || isSquareAttacked(row, 6, opp)) return false;

        } else if (toCol == 2) { // Queen-side
            if (player == Chess.Player.white && whiteRookAMoved) return false;
            if (player == Chess.Player.black && blackRookAMoved) return false;
            if (board.board[row][1] != null || board.board[row][2] != null || board.board[row][3] != null) return false;
            if (!(board.board[row][0] instanceof Rook) || board.board[row][0].player != player) return false;
            if (isSquareAttacked(row, 3, opp) || isSquareAttacked(row, 2, opp)) return false;

        } else {
            return false;
        }

        return true;
    }

    // -------------------------------------------------------------------------
    // Check / Checkmate (private — Chess.java uses getMessage() instead)
    // -------------------------------------------------------------------------

    private boolean isInCheck(Chess.Player player) {
        int[] kingPos = findKing(player);
        if (kingPos == null) return false;
        return isSquareAttacked(kingPos[0], kingPos[1], opponent(player));
    }

    public boolean isSquareAttacked(int targetRow, int targetCol, Chess.Player byPlayer) {
        for (int r = 0; r < 8; r++)
            for (int c = 0; c < 8; c++) {
                Piece p = board.board[r][c];
                if (p != null && p.player == byPlayer && p.attacks(targetRow, targetCol, board.board))
                    return true;
            }
        return false;
    }

    private int[] findKing(Chess.Player player) {
        for (int r = 0; r < 8; r++)
            for (int c = 0; c < 8; c++)
                if (board.board[r][c] instanceof King && board.board[r][c].player == player)
                    return new int[]{r, c};
        return null;
    }

    private boolean isCheckmate(Chess.Player player) {
        if (!isInCheck(player)) return false;
        return !hasAnyLegalMove(player);
    }

    private boolean hasAnyLegalMove(Chess.Player player) {
        for (int r = 0; r < 8; r++)
            for (int c = 0; c < 8; c++) {
                Piece p = board.board[r][c];
                if (p == null || p.player != player) continue;
                for (int tr = 0; tr < 8; tr++)
                    for (int tc = 0; tc < 8; tc++)
                        if (isLegal(r, c, tr, tc, player)) return true;
            }
        return false;
    }

    // -------------------------------------------------------------------------
    // Temporary move helpers
    // -------------------------------------------------------------------------

    private boolean moveLeavesKingInCheck(int fromRow, int fromCol,
                                          int toRow,   int toCol,
                                          Chess.Player player) {
        Piece moved    = board.board[fromRow][fromCol];
        Piece captured = board.board[toRow][toCol];
        int savedRow   = moved.row;
        int savedCol   = moved.col;

        board.board[toRow][toCol]     = moved;
        board.board[fromRow][fromCol] = null;
        moved.row = toRow;
        moved.col = toCol;

        boolean inCheck = isInCheck(player);

        board.board[fromRow][fromCol] = moved;
        board.board[toRow][toCol]     = captured;
        moved.row = savedRow;
        moved.col = savedCol;

        return inCheck;
    }

    private boolean enPassantLeavesKingInCheck(int fromRow, int fromCol,
                                               int toRow,   int toCol,
                                               Chess.Player player) {
        Piece moving       = board.board[fromRow][fromCol];
        Piece capturedPawn = board.board[fromRow][toCol];

        board.board[toRow][toCol]     = moving;
        board.board[fromRow][fromCol] = null;
        board.board[fromRow][toCol]   = null;
        moving.row = toRow;
        moving.col = toCol;

        boolean inCheck = isInCheck(player);

        board.board[fromRow][fromCol] = moving;
        board.board[toRow][toCol]     = null;
        board.board[fromRow][toCol]   = capturedPawn;
        moving.row = fromRow;
        moving.col = fromCol;

        return inCheck;
    }

    // -------------------------------------------------------------------------
    // Castling flags
    // -------------------------------------------------------------------------

    private void updateCastlingFlags(Piece piece, int fromRow, int fromCol, Chess.Player player) {
        if (piece instanceof King) {
            if (player == Chess.Player.white) whiteKingMoved = true;
            else                              blackKingMoved = true;
        }
        if (piece instanceof Rook) {
            if (player == Chess.Player.white) {
                if (fromCol == 0) whiteRookAMoved = true;
                if (fromCol == 7) whiteRookHMoved = true;
            } else {
                if (fromCol == 0) blackRookAMoved = true;
                if (fromCol == 7) blackRookHMoved = true;
            }
        }
    }

    // -------------------------------------------------------------------------
    // Utility
    // -------------------------------------------------------------------------

    private Chess.Player opponent(Chess.Player p) {
        return (p == Chess.Player.white) ? Chess.Player.black : Chess.Player.white;
    }
}