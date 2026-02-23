package chess;

public class Bishop extends Piece {
    public Bishop(Chess.Player player, int row, int col) {
        super(player, row, col);
    }
    @Override
    public boolean isValidMove(int toRow, int toCol, Piece[][] board) {
        int dr = Math.abs(toRow - row);
        int dc = Math.abs(toCol - col);

        if (dr == 0 || dr != dc) return false;

        // Cannot capture own piece
        if (board[toRow][toCol] != null && board[toRow][toCol].player == player) return false;

        // Check path is clear
        int rowStep = (toRow > row) ? 1 : -1;
        int colStep = (toCol > col) ? 1 : -1;
        int r = row + rowStep;
        int c = col + colStep;
        while (r != toRow) {
            if (board[r][c] != null) return false;
            r += rowStep;
            c += colStep;
        }

        return true;
    }
}
