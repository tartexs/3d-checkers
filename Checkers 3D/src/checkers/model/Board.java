package checkers.model;

import checkers.util.Point;

/**
 * Class Board
 * Implements data structure for Checkers board
 * Define cells value, pieces and tiles
 * 
 * @author Cristian Tardivo
 */
public final class Board implements Cloneable {

    public enum Cells {BLACK,BLACK_QUEEN,RED,RED_QUEEN,BLACK_FLOOR,WHITE_FLOOR}; 
    private Cells[][] board;
    
    /**
     * Create a new standar board
     */
    public Board(){
        resetBoard();
    }

    /**
     * Restart Current Board
     */
    public void resetBoard(){
        board = new Cells[][] {{Cells.WHITE_FLOOR,Cells.BLACK,Cells.WHITE_FLOOR,Cells.BLACK,Cells.WHITE_FLOOR,Cells.BLACK,Cells.WHITE_FLOOR,Cells.BLACK},
                               {Cells.BLACK,Cells.WHITE_FLOOR,Cells.BLACK,Cells.WHITE_FLOOR,Cells.BLACK,Cells.WHITE_FLOOR,Cells.BLACK,Cells.WHITE_FLOOR},
                               {Cells.WHITE_FLOOR,Cells.BLACK,Cells.WHITE_FLOOR,Cells.BLACK,Cells.WHITE_FLOOR,Cells.BLACK,Cells.WHITE_FLOOR,Cells.BLACK},
                               {Cells.BLACK_FLOOR,Cells.WHITE_FLOOR,Cells.BLACK_FLOOR,Cells.WHITE_FLOOR,Cells.BLACK_FLOOR,Cells.WHITE_FLOOR,Cells.BLACK_FLOOR,Cells.WHITE_FLOOR},
                               {Cells.WHITE_FLOOR,Cells.BLACK_FLOOR,Cells.WHITE_FLOOR,Cells.BLACK_FLOOR,Cells.WHITE_FLOOR,Cells.BLACK_FLOOR,Cells.WHITE_FLOOR,Cells.BLACK_FLOOR},
                               {Cells.RED,Cells.WHITE_FLOOR,Cells.RED,Cells.WHITE_FLOOR,Cells.RED,Cells.WHITE_FLOOR,Cells.RED,Cells.WHITE_FLOOR},
                               {Cells.WHITE_FLOOR,Cells.RED,Cells.WHITE_FLOOR,Cells.RED,Cells.WHITE_FLOOR,Cells.RED,Cells.WHITE_FLOOR,Cells.RED},
                               {Cells.RED,Cells.WHITE_FLOOR,Cells.RED,Cells.WHITE_FLOOR,Cells.RED,Cells.WHITE_FLOOR,Cells.RED,Cells.WHITE_FLOOR}};
    }
    
    /**
     * Get Board Value at position
     * Point must be a valid position [0..7][0..7]
     */
    public Cells getBoardValue(Point pos){
        return board[pos.getFirst()][pos.getSecond()];
    }
    
    /**
     * Set a value in board at position
     * Point must be a valid position [0..7][0..7]
     */
    public void setBoardValue(Point pos, Cells value){
       board[pos.getFirst()][pos.getSecond()] = value;
    }
    
    /**
     * Clone current board
     * @return cloned board
     */
    @Override
    public Board clone(){
        Board res = new Board();
        for (int i = 0; i < board.length; i++){
            for (int j = 0; j < board[i].length; j++){
                res.board[i][j] = this.board[i][j];
            }            
        }
        return res;
    }
}
