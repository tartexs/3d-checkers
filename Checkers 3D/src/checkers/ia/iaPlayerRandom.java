package checkers.ia;

import checkers.logic.Logic;
import checkers.model.Player.Color;
import checkers.util.Pair;
import checkers.util.Point;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
import java.util.Random;

/**
 * Bogo iaPlayer
 * Computes Random valid move
 * 
 * @author Cristian Tardivo
 */
public class iaPlayerRandom extends Observable {
    // Game logic associate
    Logic logic;
    
    /**
     * Creates new iaPlayer Random
     * @param gLogic default game logic
     */
    public iaPlayerRandom(Logic gLogic){
        logic = gLogic;
    }
    
    /**
     * Compute next random valid move
     */
    public void computeNextMove(){
        // Get player pieces
        Color color = logic.getModel().getCurrentPlayer().getColor();
        List<Point> pieces = logic.getPieces(color);
        // Get valid moves in current State (Model)
        List<Pair<Point,Point>> validMoves = new LinkedList<Pair<Point,Point>>();
        for(Point pos : pieces){
            // Simple Moves
            Point upLeft = new Point(pos.getFirst() - 1, pos.getSecond() - 1);
            Point upRight = new Point(pos.getFirst() - 1, pos.getSecond() + 1);
            Point downLeft = new Point(pos.getFirst() + 1, pos.getSecond() - 1);
            Point downRight = new Point(pos.getFirst() + 1, pos.getSecond() + 1);
            if(logic.isValidMove(pos,upLeft)) validMoves.add(new Pair(pos,upLeft));
            if(logic.isValidMove(pos,upRight)) validMoves.add(new Pair(pos,upRight));
            if(logic.isValidMove(pos,downLeft)) validMoves.add(new Pair(pos,downLeft));
            if(logic.isValidMove(pos,downRight)) validMoves.add(new Pair(pos,downRight));
            // Eats Moves
            Point upLeft2 = new Point(pos.getFirst() - 2, pos.getSecond() - 2);
            Point upRight2 = new Point(pos.getFirst() - 2, pos.getSecond() + 2);
            Point downLeft2 = new Point(pos.getFirst() + 2, pos.getSecond() - 2);
            Point downRight2 = new Point(pos.getFirst() + 2, pos.getSecond() + 2);
            if(logic.isValidMove(pos,upLeft2)) validMoves.add(new Pair(pos,upLeft2));
            if(logic.isValidMove(pos,upRight2)) validMoves.add(new Pair(pos,upRight2));
            if(logic.isValidMove(pos,downLeft2)) validMoves.add(new Pair(pos,downLeft2));
            if(logic.isValidMove(pos,downRight2)) validMoves.add(new Pair(pos,downRight2));
        }
        // Return Move (notify observer)
        if(!validMoves.isEmpty()){
            Random rnd = new Random();
            setChanged();
            notifyObservers(validMoves.get(rnd.nextInt(validMoves.size())));
        }
    }
    
    /**
     * Stop Current Search
     */
    public void stop(){
        // Nop
    }
}