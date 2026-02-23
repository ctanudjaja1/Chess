package chess;

public class Knight extends Piece {
    public Knight(Chess.Player player, int row, int col) {
        super(player, row, col);
    }
    @Override
    public boolean isValidMove(int toRow, int toCol, Piece[][] board) {
        int dr = Math.abs(toRow - row);
        int dc = Math.abs(toCol - col);

        if (!((dr == 2 && dc == 1) || (dr == 1 && dc == 2))) return false;

        // Cannot capture own piece
        if (board[toRow][toCol] != null && board[toRow][toCol].player == player) return false;

        return true;
    }

}
