package chess;

public class Queen extends Piece {
    public Queen(Chess.Player player, int row, int col) {
        super(player, row, col);
    }

    @Override
    public boolean isValidMove(int toRow, int toCol, Piece[][] board) {
        if (toRow == row && toCol == col) return false;

        int dr = Math.abs(toRow - row);
        int dc = Math.abs(toCol - col);

        // Must move like a rook or bishop
        boolean rookMove   = (toRow == row || toCol == col);
        boolean bishopMove = (dr == dc);
        if (!rookMove && !bishopMove) return false;

        // Cannot capture own piece
        if (board[toRow][toCol] != null && board[toRow][toCol].player == player) return false;

        // Check path is clear
        int rowStep = Integer.signum(toRow - row);
        int colStep = Integer.signum(toCol - col);
        int r = row + rowStep;
        int c = col + colStep;
        while (r != toRow || c != toCol) {
            if (board[r][c] != null) return false;
            r += rowStep;
            c += colStep;
        }

        return true;
    }
}
