package chess;

public class Pawn extends Piece {

    public Pawn(Chess.Player player, int row, int col) {
        super(player, row, col);
    }

    @Override
    public boolean isValidMove(int toRow, int toCol, Piece[][] board) {
        int direction = (player == Chess.Player.white) ? 1 : -1;
        int startRow  = (player == Chess.Player.white) ? 1 : 6;

        int rowDiff = toRow - row;
        int colDiff = Math.abs(toCol - col);

        // Forward one square
        if (colDiff == 0 && rowDiff == direction) {
            return board[toRow][toCol] == null;
        }

        // Forward two squares from starting row
        if (colDiff == 0 && rowDiff == 2 * direction && row == startRow) {
            int midRow = row + direction;
            return board[midRow][toCol] == null && board[toRow][toCol] == null;
        }

        // Diagonal capture
        if (colDiff == 1 && rowDiff == direction) {
            Piece target = board[toRow][toCol];
            return target != null && target.player != player;
        }

        return false;
    }

    // Pawns attack diagonally only (regardless of whether there is a piece there)
    @Override
    public boolean attacks(int targetRow, int targetCol, Piece[][] board) {
        int direction = (player == Chess.Player.white) ? 1 : -1;
        return (targetRow - row) == direction && Math.abs(targetCol - col) == 1;
    }
    // double pawn push
}
