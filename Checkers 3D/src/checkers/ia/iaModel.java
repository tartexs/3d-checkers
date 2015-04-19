package checkers.ia;

import checkers.model.Board;
import checkers.model.Model;
import checkers.common.Pair;
import checkers.common.Point;

/***
 * Class iaModel
 * Implement Model for IA Player from standar game model
 * adds methods for "state" evaluation, min max player,
 * state clone and creation from default model
 * allows store last move made in this model
 * 
 * @author Cristian Tardivo
 */
public class iaModel extends Model implements Cloneable {
    // Novement applied in this model
    private Pair<Point,Point> move;
    
    /**
     * Makes a new iaModel from Model
     * @param model standar model to copy
     */
    public iaModel(Model model){
        // Create new Model from parameter model
        super(model);
        // iaModel don't use cronometer (null to prevent changes)
        cronometer = null;
    }
    
    /**
     * Makes a clon from this model
     * @return cloned model
     */
    @Override
    public iaModel clone(){
        // use iaModel constructor to make clone of this iaModel
        iaModel res = new iaModel(this);
        // move be seated from outside
        res.move = null;
        return res;
    }
    
    /**
     * Save move made in this model
     * @param p move: origin, destiny
     */
    public void setMove(Pair<Point,Point> p){
        move = p;
    }
    
    /**
     * Retrieves movement made in this model
     * @return pair: origin, destiny
     */
    public Pair<Point,Point> getMove(){
        return move;
    }
    
    /**
     * Is current player Min (red player)
     * @return true or false
     */
    public boolean isMin(){
        return currentPlayer.isRed();
    }
    
    /**
     * Is current player Max (black player)
     * @return true or false
     */
    public boolean isMax(){
        return currentPlayer.isBlack();
    }
    
    /**
     * Evaluate current model for min max search
     * Min: red player, negative evaluation
     * Max: black player, positive evaluation
     * @return value between max value and min value
     */
    public int evaluate(){
        int value = 0;
        Point pos = new Point();
        Board.Cells cell;
        for (int i = 0; i < 8; i++){
            for (int j = 0; j < 8; j++){
                pos.setFirst(i);pos.setSecond(j);
                cell = getValueAt(pos);
                if (cell == Board.Cells.RED || cell == Board.Cells.RED_QUEEN){
                    value -= 80 - (10*(i));
                }
                if (cell == Board.Cells.BLACK || cell == Board.Cells.BLACK_QUEEN){
                    value += (i+1)*10;
                }
            }            
        }
        value -= (red_count * 60);
        value -= (red_queen_count * 200);
        value += (black_count * 60);
        value += (black_queen_count * 200);
        return value;
    }
    
    /**
     * Min value for min max evaluation
     * @return min value
     */
    public int minVal(){
        return -1000;
    }
    
    /**
     * Max value for min max evaluation
     * @return max value
     */
    public int maxVal(){
        return 1000;
    }
}