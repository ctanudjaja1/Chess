package chess;

public class Rook extends Piece {
    public Rook(Chess.Player player, int row, int col) {
        super(player, row, col);
    }
    @Override
    public boolean isValidMove(int toRow, int toCol, Piece[][] board) {
        if (toRow == row && toCol == col) return false;

        // Must stay on same row or same column
        if (toRow != row && toCol != col) return false;

        // Cannot capture own piece
        if (board[toRow][toCol] != null && board[toRow][toCol].player == player) return false;

        // Check path is clear
        if (toRow == row) {
            int step = (toCol > col) ? 1 : -1;
            for (int c = col + step; c != toCol; c += step) {
                if (board[row][c] != null) return false;
            }
        } else {
            int step = (toRow > row) ? 1 : -1;
            for (int r = row + step; r != toRow; r += step) {
                if (board[r][col] != null) return false;
            }
        }

        return true;
    }
}
