package chess;

public class Board {

    Piece[][] board = new  Piece[8][8];
    public void setupInitialPosition(){

        //Clear Board
        for(int r=0;r < 8;r++){
            for(int c=0;c<8;c++){
                board[r][c] = null;
            }
        }

        //Pawns
        for(int c=0;c < 8;c++){
            board[1][c] = new Pawn(Chess.Player.white, 1, c);
            board[6][c] = new Pawn(Chess.Player.black, 6, c);
        }

        //White pieces
        // THE ROOK! *insert pog emoji*
        board[0][0] = new Rook(Chess.Player.white, 0, 0);
        board[0][7] = new Rook(Chess.Player.white, 0, 7);

        //Knight
        board[0][1] = new Knight(Chess.Player.white, 0, 1);
        board[0][6] = new Knight(Chess.Player.white, 0, 6);

        //Bishop
        board[0][2] = new Bishop(Chess.Player.white, 0, 2);
        board[0][5] = new Bishop(Chess.Player.white, 0, 5);

        //Queen
        board[0][3] = new Queen(Chess.Player.white, 0, 3);

        //King
        board[0][4] = new King(Chess.Player.white, 0, 4);

        //Black pieces
        // THE ROOK! *insert pog emoji*
        board[7][0] = new Rook(Chess.Player.black, 7, 0);
        board[7][7] = new Rook(Chess.Player.black, 7, 7);

        //Knight
        board[7][1] = new Knight(Chess.Player.black, 7, 1);
        board[7][6] = new Knight(Chess.Player.black, 7, 6);

        //Bishop
        board[7][2] = new Bishop(Chess.Player.black, 7, 2);
        board[7][5] = new Bishop(Chess.Player.black, 7, 5);

        //Queen
        board[7][3] = new Queen(Chess.Player.black, 7, 3);

        //King
        board[7][4] = new King(Chess.Player.black, 7, 4);

    }
}
