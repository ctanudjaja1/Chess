package chess;

public abstract class Piece {
    /*
    * 1) Color
    * 2) The position
    * 3) Is it a validMove?
    * */
    Chess.Player player;
    int row, col;

    public Piece(Chess.Player player, int row, int col) {
        this.player = player;
        this.row = row;
        this.col = col;
    }

    public abstract boolean isValidMove(int row, int col, Piece[][] board);

    public boolean attacks(int row, int col, Piece[][] board) {
        return isValidMove(row, col, board);
    }

}
