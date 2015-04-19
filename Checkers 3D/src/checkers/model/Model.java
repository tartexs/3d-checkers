package checkers.model;

import checkers.model.Board.Cells;
import checkers.model.Player.Color;
import checkers.model.Player.Type;
import checkers.common.Cronometer;
import checkers.common.Point;
import java.util.Observable;


/**
 * Implements Model of Checkers  Game
 * Uses a Board and two players
 * Store information of current game
 * 
 * @author Cristian Tardivo
 */
public class Model extends Observable {
    protected Board board;
    protected Player playerA;
    protected Player playerB;
    // Current Game
    protected boolean gameStarted;
    protected Player currentPlayer;
    protected Player rivalPlayer;
    protected Point checkPiece;
    // Counters
    protected int red_count;
    protected int black_count;
    protected int red_queen_count;
    protected int black_queen_count;
    protected Cronometer cronometer;
    
    /**
     * Create a new Model
     */
    public Model(){
        playerA = new Player(Color.red,Type.local,"Player A");
        playerB = new Player(Color.black,Type.local,"Player B");
        // By default game start by PlayerA
        currentPlayer = playerA;
        rivalPlayer = playerB;
        checkPiece = null;
        //
        gameStarted = false;
        red_count = 12;
        black_count = 12;
        black_queen_count = 0;
        red_queen_count = 0;
        //
        board = new Board();
        cronometer = Cronometer.getInstance();
    }
    
    /**
     * Create Cloned model from another model
     * @param model 
     */
    protected Model(Model model){
        this.cronometer = model.cronometer;
        this.black_count = model.black_count;
        this.black_queen_count = model.black_queen_count;
        this.gameStarted = model.gameStarted;
        this.red_count = model.red_count;
        this.red_queen_count = model.red_queen_count;
        this.checkPiece = (model.checkPiece != null)? model.checkPiece.clone():null;
        this.board = model.board.clone();
        this.playerA = model.playerA.clone();
        this.playerB = model.playerB.clone();
        // Checks for current player and rival player
        if(model.currentPlayer == model.playerA){
            this.currentPlayer = this.playerA;
            this.rivalPlayer = this.playerB;
        } else {
            this.currentPlayer = this.playerB;
            this.rivalPlayer = this.playerA;
        }
    }
        
    /**
     * Start new Game
     */
    public void startGame(){
        gameStarted = true;
        cronometer.start();
    }
    
    /**
     * Stop Current Game
     */
    public void stopGame(){
        gameStarted = false;
        cronometer.pause();
    }
    
    /**
     * Reset Current Model
     */
    public void resetModel(){
        playerA = new Player(Color.red,Type.local,"Player A");
        playerB = new Player(Color.black,Type.local,"Player B");
        // By default game start by PlayerA
        currentPlayer = playerA;
        rivalPlayer = playerB;
        checkPiece = null;
        //
        gameStarted = false;
        red_count = 12;
        black_count = 12;
        black_queen_count = 0;
        red_queen_count = 0;
        //
        board.resetBoard();
        cronometer.reset();
    }
    
    /**
     * Allows consult if the game has started
     * @return started or not
     */
    public boolean isGameStarted(){
        return gameStarted;
    }
    
    /**
     * Retrieves the game Board
     * Use carefully, only for read or clone
     * @return current board
     */
    public Board getBoard(){
        return board;
    }
    
    /**
     * Retrieves the game cronometer
     * @return current Cronometer
     */
    public Cronometer getCronometer(){
        return cronometer;
    }
    
    /**
     * Change player A
     * @param player 
     */
    public void setPlayerA(Player player){
        playerA = player;
    }
    
    /**
     * Get Player A
     * @return 
     */
    public Player getPlayerA(){
        return playerA;
    }
    
    /**
     * Set Player B
     * @param player 
     */
    public void setPlayerB(Player player){
        playerB = player;
    }
    
    /**
     * Get Player B
     * @return 
     */
    public Player getPlayerB(){
        return playerB;
    }
    
    /**
     * Get Current Player
     * @return playerA or playerB
     */
    public Player getCurrentPlayer(){
        return currentPlayer;
    }
    
    /**
     * Get Rival player
     * @return playerA or playerB
     */
    public Player getRivalPlayer(){
        return rivalPlayer;        
    }
    
    /**
     * Get Red pieces count
     * @return number
     */
    public int getRedCount(){
        return red_count;
    }
    
    /**
     * Increments red pieces count
     * @param num quantity to increment
     */
    public void setRedCount(int num){
        red_count += num;
    }
    
    /**
     * Get red queen pieces count
     * @return number
     */
    public int getRedQueenCount(){
        return red_queen_count;
    }
    
    /**
     * Increments red queen pieces count
     * @param num quantity to increment
     */
    public void setRedQueenCount(int num){
        red_queen_count += num;
    }
    
    /**
     * Get Black pieces count
     * @return number
     */
    public int getBlackCount(){
        return black_count;
    }
    
    /**
     * Increments black pieces count
     * @param num quantity to increment
     */
    public void setBlackCount(int num){
        black_count += num;
    }
    
    /**
     * Get black queen pieces count
     * @return number
     */
    public int getBlackQueenCount(){
        return black_queen_count;
    }
    
    /**
     * Increments black queen pieces count
     * @param num quantity to increment
     */
    public void setBlackQueenCount(int num){
        black_queen_count += num;
    }
    
    /**
     * Retrieves cell value at position in the board
     * @param pos position to retrieves
     * @return cell value
     */
    public Cells getValueAt(Point pos){
        return board.getBoardValue(pos);
    }
    
    /**
     * Change value inside the board
     * notify observer if the value is a queen
     * or is black floor (eats)
     * @param pos  Position to change
     * @param value Value to put
     */
    public void setValueAt(Point pos,Cells value){
        // Change Value
        board.setBoardValue(pos,value);        
        // Notify Observers
        setChanged();
        // Eats a piece
        if(value == Cells.BLACK_FLOOR){
            notifyObservers("MODEL_EAT");
            return;
        }
        // Makes a queen
        if(value == Cells.BLACK_QUEEN || value == Cells.RED_QUEEN)
            notifyObservers("MODEL_QUEEN");
    }    
        
    /**
     * Set auxiliar piece value
     * used to check some valid moves
     * @param piece 
     */
    public void setCheckPiece(Point piece){
        checkPiece = piece;
    }

    /**
     * Get auxiliar piece value
     * used to check some valid moves
     * @return piece
     */
    public Point getCheckPiece(){
        return checkPiece;
    }
    
    /**
     * Swap values between to points in the board
     * allows "move" piece in board
     * @param orig Origin position
     * @param dest Destiny Position
     */
    public void swapValues(Point orig,Point dest){
        // Swap Values
        Cells aux = board.getBoardValue(dest);
        board.setBoardValue(dest,board.getBoardValue(orig));
        board.setBoardValue(orig,aux);
        // Notify Observer the movement
        setChanged();
        notifyObservers("MODEL_MOVE");
    }
    
    /**
     * Swap Players between current and rival
     * and update played time for current player
     */
    public void swapPlayers(){
        // Update timers
        if(cronometer != null)
            currentPlayer.setPlayedTime(cronometer.getSeconds() - cronometer.lap());
        // Swap players
        Player aux = currentPlayer;
        currentPlayer = rivalPlayer;
        rivalPlayer = aux;
        // Notify Observer turn has changed
        setChanged();
        notifyObservers("MODEL_CHANGE_TURN");
    } 
}
