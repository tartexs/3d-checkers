package checkers.logic;


import checkers.model.Board.Cells;
import checkers.model.Model;
import checkers.model.Player;
import checkers.model.Player.Color;
import checkers.common.Point;
import java.util.LinkedList;
import java.util.List;

/**
 * Implements Logic for Checkers game
 * uses Game Model, allows validate moves
 * between view and model.
 * 
 * Allows check for obligatory eats, chained eats
 * and especial queen movements
 * Queen can move in any direction (valids ups and downs diagonals)
 * Queen can move max of two squares
 * 
 * @author Cristian Tardivo
 */
public class Logic {
    // Logic associated model
    private Model model;
    
    /**
     * Create a new game Logic
     * @param m Checkers model
     */
    public Logic(Model m){
        model = m;
    }
    
    /**
     * Get Model associated to this logic
     * @return Checkers model
     */
    public Model getModel(){
        return model;
    }
    
    /**
     * Set associated model to this logic
     * @param m Checkers model
     */
    public void setModel(Model m){
        model = m;
    }
    
    /**********************/ 
    /*** Public Methods ***/
    /**********************/
    
    /******************************/ 
    /***  Model Changes Methods ***/
    /******************************/ 
    
    /**
     * Change current player to rival
     */
    public void changeTurn(){
        model.setCheckPiece(null);
        model.swapPlayers();
    }
    
    /**
     * Moves a piece between positions
     * @param orig Origin Piece
     * @param dest Destination Empty valid place
     * @return if current player can move again (enchained eats)
     */
    public boolean movePiece(Point orig, Point dest){
        // Move information
        boolean eat = moveEats(orig,dest);
        boolean queen = moveMakeQueen(orig,dest); 
        // Move piece
        model.swapValues(orig,dest);
        model.getCurrentPlayer().setMovementCount(+1);
        // If move eats rivals piece
        if(eat)
            eatPiece(getPieceToEat(orig,dest));
        // If moves make a new queen
        if(queen)
            toQueen(dest);
        // If eat and can eats again from destiny position then move continues
        return !queen && (eat && moveContinues(dest));
    }
    
    /**
     * Eats a piece in position
     * @param pos Piece to eat
     */
    public void eatPiece(Point pos){
        // Update Board Numbers
        if(isRed(pos)){
            if(isQueen(pos))
               model.setRedQueenCount(-1);
            else    
               model.setRedCount(-1);
        } else {
            if(isQueen(pos))
                model.setBlackQueenCount(-1);
            else
                model.setBlackCount(-1);
        }
        // Update Rival Piece Number
        model.getRivalPlayer().setPieceNumber(-1);
        // Update Current Player Eated Pieces
        model.getCurrentPlayer().setPieceEated(+1);
        // Make Eat
        model.setValueAt(pos,Cells.BLACK_FLOOR);
    }
    
    /**
     * convert into queen piece in position
     * @param pos Piece to convert to queen
     */
    public void toQueen(Point pos){
        model.getCurrentPlayer().setQueenNumber(+1);
        if(isBlack(pos)){ 
            model.setValueAt(pos,Cells.BLACK_QUEEN);
            model.setBlackQueenCount(+1);
            model.setBlackCount(-1);
        } else {
            model.setValueAt(pos,Cells.RED_QUEEN);
            model.setRedQueenCount(+1);
            model.setRedCount(-1);
        }
    }
  
    /**************************/ 
    /*** Validation Methods ***/
    /**************************/
    
    /**
     * Checks if a movement is valid
     * @param orig start point of movement
     * @param dest end point of movement
     * @return 
     */
    public boolean isValidMove(Point orig, Point dest){        
        Color playerColor = model.getCurrentPlayer().getColor();
        // Validate all point between board limits
        if(!isValidPosition(orig) || !isValidPosition(dest)) return false;
        // Check for continue move starting piece
        if(model.getCheckPiece() != null && !model.getCheckPiece().equals(orig)) return false;
        // Validate origin piece to player color
        if((isRed(playerColor) && !isRed(orig)) || (isBlack(playerColor) && !isBlack(orig))) return false;
        // Only can move to empty Black space
        if(!isEmpty(dest)) return false;
        // If current player have obligatory eats, then need to eat
        if(obligatoryEats(playerColor) && !moveEats(orig,dest)) return false;
        // Check move direction and length
        int moveDirection = orig.getFirst() - dest.getFirst();  // Direction in Rows
        int rLength = Math.abs(moveDirection); // Length in Rows
        int cLength = Math.abs(orig.getSecond() - dest.getSecond()); // Length in Columns
        int mLength;
        // Only acepts diagonal movements in 1 or 2 places (1 normal move, 2 eats move)
        if ((rLength == 1 && cLength == 1) || (rLength == 2 && cLength == 2)){
            mLength = rLength;
        } else {
            return false;
        }
        // 1 Place movement
        if (mLength == 1){ 
            if (isRed(orig) && !isQueen(orig) && moveDirection > 0) return true;
            if (isBlack(orig) && !isQueen(orig) && moveDirection < 0) return true;
            if ((isRed(orig) && isQueen(orig)) || (isBlack(orig) && isQueen(orig))) return true;
        }
        // 2 Places movement need checks if eats rivals
        if (mLength == 2){
            // Compute mid point
            Point midPoint = getMidPoint(orig, dest);
            // Check move
            if ((isRed(orig) && isBlack(midPoint)) || (isBlack(orig) && isRed(midPoint))){
                if (isRed(orig) && !isQueen(orig) && moveDirection > 0) return true;
                if (isBlack(orig) && !isQueen(orig) && moveDirection < 0) return true;
                if ((isRed(orig) && isQueen(orig)) || (isBlack(orig) && isQueen(orig))) return true;
            }
        }
        return false;
    }
        
    /**
     * Compute piece to eat between 2 points
     * @param orig Origin point
     * @param dest Destinacion point
     * @return position on piece to eat
     */
    public Point getPieceToEat(Point orig, Point dest){
        return getMidPoint(orig, dest);
    }
        
    /**
     * Checks if current game can continue
     * @return can or can't continuing
     */
    public boolean gameEnd(){
        return !hasMovements(model.getCurrentPlayer().getColor());
    }
    
    /**
     * Get Winner of the current game
     * @return Player winner
     */
    public Player getWinner(){
        if(!hasMovements(model.getPlayerA().getColor()))
            return model.getPlayerB();
        if(!hasMovements(model.getPlayerB().getColor()))
            return model.getPlayerA();
        // In case of draw
        return null;
    }
    
    /**
     * Retrieves all piece positions of a specific color
     * @param pColor player pieces color
     * @return linked list of pieces positions
     */
    public List<Point> getPieces(Color pColor){
        List<Point> result = new LinkedList<>();
        Point pos = new Point();
        for (int i = 0; i < 8; i++){
            pos.setFirst(i);
            for (int j = 0; j < 8; j++){
                pos.setSecond(j);
                if((isBlack(pColor) && isBlack(pos)) || (isRed(pColor) && isRed(pos))){
                    result.add(pos.clone());
                }
            }
        }        
        return result;
    }
    
    /***********************/ 
    /*** Private Methods ***/
    /***********************/
    
    /**
     * Compute Mid Point between 2 Points
     * @param first First Point
     * @param second Second Point
     * @return Middle Point
     */
    public Point getMidPoint(Point first, Point second){
        return new Point((first.getFirst() + second.getFirst()) / 2, 
                         (first.getSecond() + second.getSecond()) / 2);            
    }
    
    /**
     * Validate Position in board
     * @param pos Point to validate
     * @return valid or invalid
     */
    private boolean isValidPosition(Point pos){
        return (0 <= pos.getFirst() && pos.getFirst() < 8 && 
                0 <= pos.getSecond() && pos.getSecond() < 8);
    }
    
    /**
     * Checks for a queen at position
     * @param pos Point to check
     * @return queen or not queen
     */
    private boolean isQueen(Point pos){
        Cells aux = model.getValueAt(pos);
        return (aux == Cells.BLACK_QUEEN || aux == Cells.RED_QUEEN);
    }
    
    /**
     * Checks for a empty valid position at point
     * @param pos Point to check
     * @return empty or not empty
     */
    private boolean isEmpty(Point pos){
        Cells aux = model.getValueAt(pos);
        return (aux == Cells.BLACK_FLOOR);
    }
    
    /**
     * Checks for a red piece at position
     * @param pos Point to check
     * @return red or not red
     */
    private boolean isRed(Point pos){
        Cells aux = model.getValueAt(pos);
        return (aux == Cells.RED || aux == Cells.RED_QUEEN);
    }
    
    /**
     * Checks if a color is red
     * @param color Color to check
     * @return red or not red
     */
    private boolean isRed(Color color){
        return color == Color.red;
    }
    
    /**
     * Checks for a black piece at position
     * @param pos Point to check
     * @return black or not black
     */
    private boolean isBlack(Point pos){
        Cells aux = model.getValueAt(pos);
        return (aux == Cells.BLACK || aux == Cells.BLACK_QUEEN);
    }
    
    /**
     * Checks if a color is black
     * @param color Color to check
     * @return black or not black
     */
    private boolean isBlack(Color color){
        return color == Color.black;
    }
    
    /**
     * Check if movement between 2 points eats a rivals piece
     * @param orig Origin point
     * @param dest Destination point
     * @return movement eats or not eats
     */
    private boolean moveEats(Point orig, Point dest){
        int mLength = Math.abs(orig.getSecond() - dest.getSecond());
        Point mid = getPieceToEat(orig,dest);
        return (mLength == 2) && ((isRed(orig) && isBlack(mid)) || 
                (isBlack(orig) && isRed(mid))) && isEmpty(dest);
    }
    
    /**
     * Check if movement between 2 points make a new queen
     * @param orig Origin point
     * @param dest Destination point
     * @return movements makes or not makes a new queen
     */
    private boolean moveMakeQueen(Point orig, Point dest){
        if(isQueen(orig)) return false;
        return (isBlack(orig) && dest.getFirst() == 7) || (isRed(orig) && dest.getFirst() == 0);
    }
    
    /**
     * Checks if current player can continues eating rivals pieces
     * @param piece last piece moved by current player
     * @return can or can't make a new move
     */
    private boolean moveContinues(Point piece){
        boolean result = canEats(piece);
        // if move continues, next move has to start with the same piece
        model.setCheckPiece((result)?piece:null);
        return result;
    }
    
    /**
     * Checks if has movements for a any piece of a color
     * @param color piece color to check
     * @return has or hasn't movements
     */
    private boolean hasMovements(Color playerColor){
        for(Point pos : getPieces(playerColor)){
            if(canMove(pos) || canEats(pos)) return true;
        }
        return false;
    }  
    
    /**
     * Checks if player color has obligatory eats movements
     * @param pColor Player color pieces to check
     * @return has or hasn't obligatory eats
     */
    private boolean obligatoryEats(Color pColor){
        Point pos = new Point();
        for (int i = 0; i < 8; i++){
            pos.setFirst(i);
            for (int j = 0; j < 8; j++){
                pos.setSecond(j);
                if(isBlack(pColor) && !isEmpty(pos) && isBlack(pos) && canEats(pos))
                    return true;
                if(isRed(pColor) && !isEmpty(pos) && isRed(pos) && canEats(pos))
                    return true;
            }
        }
        return false;
    }
    
    /**
     * Check if a piece can eats a rival piece from their place
     * @param piece Piece to check
     * @return can or can't eats
     */
    private boolean canEats(Point piece){
	// direction -1: red pieces  +1: black pieces
        int dir = isRed(piece)? -1:+1;
        int dest = dir * 2; // eat move destination
        // compute movement points
        Point left = new Point(piece.getFirst() + dir, piece.getSecond() - 1);
        Point left2 = new Point(piece.getFirst()+ dest, piece.getSecond() - 2);
        Point right = new Point(piece.getFirst() + dir, piece.getSecond() + 1);
        Point right2 = new Point(piece.getFirst() + dest, piece.getSecond() + 2);
        // check left eat
        if(isValidPosition(left) && isValidPosition(left2)){
            if(isRed(piece) && isBlack(left) && isEmpty(left2)) return true;
            if(isBlack(piece) && isRed(left) && isEmpty(left2)) return true;
        }
        // check right eat
        if(isValidPosition(right) && isValidPosition(right2)){
            if(isRed(piece) && isBlack(right) && isEmpty(right2)) return true;
            if(isBlack(piece) && isRed(right) && isEmpty(right2)) return true;
        }
        // if is a queen
        if(isQueen(piece)){
            // compute queen move points (invert direction)
            Point leftQ = new Point(piece.getFirst() - dir, piece.getSecond() - 1);
            Point leftQ2 = new Point(piece.getFirst() - dest, piece.getSecond() - 2);
            Point rightQ = new Point(piece.getFirst() - dir, piece.getSecond() + 1);
            Point rightQ2 = new Point(piece.getFirst() - dest, piece.getSecond() + 2);
            // check left eat
            if(isValidPosition(leftQ) && isValidPosition(leftQ2)){
                if(isRed(piece) && isBlack(leftQ) && isEmpty(leftQ2)) return true;
                if(isBlack(piece) && isRed(leftQ) && isEmpty(leftQ2)) return true;
            }
            // check right eat
            if(isValidPosition(rightQ) && isValidPosition(rightQ2)){
                if(isRed(piece) && isBlack(rightQ) && isEmpty(rightQ2)) return true;
                if(isBlack(piece) && isRed(rightQ) && isEmpty(rightQ2)) return true;
            }
        }
        return false;
    }
    
    /**
     * Checks a piece can move
     * @param piece Piece to check
     * @return can or can't move
     */
    private boolean canMove(Point piece){
        // direction -1: red pieces  +1: black pieces
        int dir = isRed(piece)? -1:+1;
        // normal movement
        Point left = new Point(piece.getFirst() + dir, piece.getSecond() - 1);
        Point right = new Point(piece.getFirst() + dir, piece.getSecond() + 1);
        // check for normal movements
        if(isValidPosition(left) && isEmpty(left)) return true;
        if(isValidPosition(right) && isEmpty(right)) return true;
        // if is a queen
        if(isQueen(piece)){
            // compute queen move points (invert direction)
            Point leftQ = new Point(piece.getFirst() - dir, piece.getSecond() - 1);
            Point rightQ = new Point(piece.getFirst() - dir, piece.getSecond() + 1);
            // check for down movements
            if(isValidPosition(leftQ) && isEmpty(leftQ)) return true;
            if(isValidPosition(rightQ) && isEmpty(rightQ)) return true;
        }
        return false;
    }
}