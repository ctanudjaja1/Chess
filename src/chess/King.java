package chess;

public class King extends Piece {
    public King(Chess.Player player, int row, int col) {
        super(player, row, col);
    }
    @Override
    public boolean isValidMove(int toRow, int toCol, Piece[][] board) {
        int dr = Math.abs(toRow - row);
        int dc = Math.abs(toCol - col);

        // Normal king move: one square in any direction
        if (dr > 1 || dc > 1) return false;
        if (dr == 0 && dc == 0) return false;

        // Cannot capture own piece
        if (board[toRow][toCol] != null && board[toRow][toCol].player == player) return false;

        return true;
        // NOTE: Castling (dc == 2) is handled separately in Board.isLegalMove
    }
}
