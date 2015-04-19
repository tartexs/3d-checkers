package checkers.ia;

import checkers.logic.Logic;
import checkers.model.Player.Color;
import checkers.common.Pair;
import checkers.common.Point;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Class iaRules
 * Implemens ia move rules
 * allows to make moves and get list of resultant models
 * 
 * @author Cristian Tardivo
 */
public class iaRules {
    // Current game logic
    private Logic gameLogic;
    
    /**
     * Create new iaRules for player red and black
     * @param logic default game logic
     */
    public iaRules(Logic logic){
        gameLogic = logic;
    }
    
    /**
     * Applies rules to current model and retrieves a list of models modified
     * update game logic with each model,
     * ¡use carefully , avoid lost original model!
     * @param current game model to apply rules
     * @return list of models modified
     */
    public List<iaModel> applyRules(iaModel current){
        // Set Current Model to game Logic
        gameLogic.setModel(current);
        // Get current player pieces
        // red player: Min  black player: Max
        List<Point> pieces;
        if(current.isMin())
           pieces = gameLogic.getPieces(Color.red);
        else
           pieces = gameLogic.getPieces(Color.black);
        // Get valid moves in current State (Model)
        List<Pair<Point,Point>> validMoves = new LinkedList<>();
        for(Point pos : pieces){
            // Simple Moves
            Point upLeft = new Point(pos.getFirst() - 1, pos.getSecond() - 1);
            Point upRight = new Point(pos.getFirst() - 1, pos.getSecond() + 1);
            Point downLeft = new Point(pos.getFirst() + 1, pos.getSecond() - 1);
            Point downRight = new Point(pos.getFirst() + 1, pos.getSecond() + 1);
            if(gameLogic.isValidMove(pos,upLeft)) validMoves.add(new Pair(pos,upLeft));
            if(gameLogic.isValidMove(pos,upRight)) validMoves.add(new Pair(pos,upRight));
            if(gameLogic.isValidMove(pos,downLeft)) validMoves.add(new Pair(pos,downLeft));
            if(gameLogic.isValidMove(pos,downRight)) validMoves.add(new Pair(pos,downRight));
            // Eats Moves
            Point upLeft2 = new Point(pos.getFirst() - 2, pos.getSecond() - 2);
            Point upRight2 = new Point(pos.getFirst() - 2, pos.getSecond() + 2);
            Point downLeft2 = new Point(pos.getFirst() + 2, pos.getSecond() - 2);
            Point downRight2 = new Point(pos.getFirst() + 2, pos.getSecond() + 2);
            if(gameLogic.isValidMove(pos,upLeft2)) validMoves.add(new Pair(pos,upLeft2));
            if(gameLogic.isValidMove(pos,upRight2)) validMoves.add(new Pair(pos,upRight2));
            if(gameLogic.isValidMove(pos,downLeft2)) validMoves.add(new Pair(pos,downLeft2));
            if(gameLogic.isValidMove(pos,downRight2)) validMoves.add(new Pair(pos,downRight2));
        }
        // Apply Moves to current Model and Clone
        List<iaModel> result = new LinkedList<>();
        for(Pair pair : validMoves){
            result.add(applyMove(current, pair));
        }
        // Add random value
        Collections.shuffle(result);
        // Returns list of current model clones with movements applied
        return result;
    }
    
    /**
     * Clone model and apply new move
     * @param model original model
     * @param pair valid movement
     * @return clone model with move applied
     */
    private iaModel applyMove(iaModel model,Pair<Point,Point> pair){
        // Clone current model
        iaModel clon = model.clone();
        // Update logic with clon
        gameLogic.setModel(clon);
        // Apply move and change turn if necessary
        if(!gameLogic.movePiece(pair.getFirst(),pair.getSecond()))
            gameLogic.changeTurn();
        // Save Move info
        clon.setMove(pair);
        // Returns updated clon
        return clon;
    }
}