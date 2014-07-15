package checkers.model;

/**
 * Implements a Checkers Player
 * define player Colors and players Types
 * save current player information
 * 
 * @author Cristian Tardivo
 */
public class Player implements Cloneable {
    // Player Colors
    public enum Color {red,black};
    // Player Types
    public enum Type  {local,remote,artificial};
    // Player Data
    private Type playerType;
    private String playerName;
    private int pieceNumber;
    private int pieceEated;
    private int queenNumber;
    private int movementNumber;
    private int playedTime;
    private Color pieceColor;
    
    /**
     * Create a new Player 
     * by default has 12 pieces
     */
    public Player(Color color,Type type,String name){
        pieceColor = color;
        playerType = type;
        playerName = name;
        pieceNumber = 12;
        pieceEated = 0;
        queenNumber = 0;
        movementNumber = 0;
        playedTime = 0;
    }
    
    /**
     * Get type of a player
     * @return Player Type
     */
    public Type getType(){
        return playerType;
    }
    
    /**
     * Set Type of a player
     * @param type new player type
     */
    public void setType(Type type){
        playerType = type;
    }
    
    /**
     * Get player name
     * @return String name
     */
    public String getName(){
        return playerName;
    }
    
    /**
     * Set player name
     * @param name new player name
     */
    public void setName(String name){
        playerName = name;
    }
    
    /**
     * Get player piece count
     * @return number
     */
    public int getPieceNumber(){
        return pieceNumber;
    }
    
    /**
     * Increments player pieces count
     * @param number increment
     */
    public void setPieceNumber(int number){
        pieceNumber += number;
    }
    
    /**
     * Get rivals pieces count eated
     * @return number
     */
    public int getPieceEated(){
        return pieceEated;
    }
    
    /**
     * Increments rivals pieces eated count
     * @param number increment
     */
    public void setPieceEated(int number){
        pieceEated += number;
    }
    
    /**
     * Get player queen pieces count
     * @return number
     */
    public int getQueenNumber(){
        return queenNumber;
    }
    
    /**
     * Increments player queen pieces count
     * @param number increment
     */
    public void setQueenNumber(int number){
        queenNumber += number;
    }
    
    /**
     * Get player movements count
     * @return number
     */
    public int getMovementCount(){
        return movementNumber;
    }
    
    /**
     * Increments player movement counts
     * @param number increment
     */
    public void setMovementCount(int number){
        movementNumber += number;        
    }
    
    /**
     * Get Player played time
     * @return time
     */
    public int getPlayedTime(){
        return playedTime;
    }
    
    /**
     * Increments player played time
     * @param time increment
     */
    public void setPlayedTime(int time){
        playedTime += time;
    }
    
    /**
     * Set player color pieces
     * @param color new color
     */
    public void setColor(Color color){
        pieceColor = color;
    }
    
    /**
     * Get player color pieces
     * @return Color
     */
    public Color getColor(){
        return pieceColor;
    }
    
    /**
     * Checks if current player is Red
     * @return true or false
     */
    public boolean isRed(){
        return pieceColor == Color.red;
    }
    
    /**
     * Checks if current player is black
     * @return true or false
     */
    public boolean isBlack(){
        return pieceColor == Color.black;
    }
    
    /**
     * Checks if current player is Artificial
     * @return true or false
     */
    public boolean isIA(){
        return playerType == Type.artificial;
    }
    
    /**
     * Checks if current player is Remote/Network
     * @return true or false
     */
    public boolean isRemote(){
        return playerType == Type.remote;
    }
    
    /**
     * Checks if current player is Local
     * @return true or false
     */
    public boolean isLocal(){
        return playerType == Type.local;
    }
        
    /**
     * Clones Current Player
     * @return cloned player
     */
    @Override
    public Player clone(){
        Player res = new Player(pieceColor, playerType, playerName);
        res.movementNumber = this.movementNumber;
        res.pieceEated = this.pieceEated;
        res.pieceNumber = this.pieceNumber;
        res.playedTime = this.playedTime;
        res.queenNumber = this.queenNumber;
        return res;
    }
}
