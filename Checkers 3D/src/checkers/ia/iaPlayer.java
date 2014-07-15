package checkers.ia;

import checkers.logic.Logic;
import checkers.model.Model;
import checkers.util.Settings;
import java.util.List;
import java.util.Observable;

/**
 * Class iaPlayer
 * Implements artificial player using min max search with alpha-beta pruning
 * using iaRules and iaModel
 * 
 * @author Cristian Tardivo
 */
public class iaPlayer extends Observable implements Runnable {
    // Difficults Enumeration
    public enum Difficult {easy,moderate,hard};
    // Max depth on search tree
    private int MAX_TREE_LEVEL = 4;
    // Time Delay
    private int delay = 1000;
    // Game logic associate
    private Logic logic;
    // Default game Model
    private Model defaultModel;
    // Extended Model
    private iaModel resultModel;
    // Game "Rules"
    private iaRules rules;
    // Search Thread
    private Thread iaPlayer;
    // Search Started
    private boolean started;
    
    /**
     * Creates a new iaPlayer with default logic
     * @param gLogic default game logic
     */
    public iaPlayer(Logic gLogic){
        logic = gLogic;
        defaultModel = gLogic.getModel();
        rules = new iaRules(logic);
    }
    
    /**
     * Start Thread to compute next movement
     */
    public void computeNextMove(){
        iaPlayer = new Thread(this);
        iaPlayer.setName("iaPlayer Thread");
        started = true;
        resultModel = null;
        iaPlayer.start();
    }
    
    /**
     * Stop current search
     */
    public void stop(){
        started = false;
        try {
            iaPlayer.join();
            iaPlayer.stop();
        } catch (InterruptedException ex){
            System.err.println("Can't Stop iaPlayer Thread");
        }
        
    }
    
    /**
     * Thread run Method
     * performs min max search
     */
    public void run(){
        // Extends default model to iaModel
        iaModel model = new iaModel(defaultModel);
        logic.setModel(model);
        // Search best move node
        minMaxAB(model,MAX_TREE_LEVEL, model.minVal(), model.maxVal());
        // Reset logic to default model
        logic.setModel(defaultModel);
        // Time Delay
        if(!Settings.getInstance().is3DView())
            try {Thread.sleep(delay);} catch (InterruptedException ex){}
        // Inform changes: Return best move
        if(started){
            // Checks for valid resultModel
            if(resultModel == null){
                System.err.println("iaPlayer: result model null");
                return;
            }
            // Checks for valid move
            if(!logic.isValidMove(resultModel.getMove().getFirst(), resultModel.getMove().getSecond())){
                System.err.println("iaPlayer: invalid move in default model");
                return;
            }
            // Valid Move, notifies observer
            setChanged();
            notifyObservers(resultModel.getMove());
        }
    }
    
    /**
     * min Max search tree
     * @param model  Current model in the search
     * @param depth  Max depth in the search tree
     * @param alpha  Current alpha value
     * @param beta   Current beta value
     * @return       Current node valoration
     */
    private int minMaxAB(iaModel model, int depth, int alpha, int beta){
        // if game end for current player or can't explore more nodes
        if(depth == 0 || logic.gameEnd()){
            return model.evaluate();
        } else {
            // search best move
            List<iaModel> succesors = rules.applyRules(model.clone());
            for(iaModel nextModel : succesors){
                // init resultModel
                if(resultModel == null) resultModel = nextModel;
                // do search
                int value = minMaxAB(nextModel, depth - 1, alpha, beta);
                // select best succesor
                if(model.isMax()){
                    if(value > alpha){
                        alpha = value;
                        if(depth ==  MAX_TREE_LEVEL) resultModel = nextModel;
                    }
                    if(beta <= alpha) break; // beta cut-off
                } else { // model.isMin()
                    if(value < beta){
                        beta = value;
                        if(depth == MAX_TREE_LEVEL) resultModel = nextModel;
                    }
                    if(beta <= alpha) break; // alpha cut-off
                }
            }
            // return result
            if(model.isMax())
                return alpha;
            else
                return beta;
        }
    }

    /**
     * Set maximum depth of the search tree
     * @param level difficult level
     */
    public void setDifficultLevel(Difficult level){
        switch (level){
            case easy: MAX_TREE_LEVEL = 3;delay = 1200;break;
            case moderate: MAX_TREE_LEVEL = 6;delay = 1000;break;
            case hard: MAX_TREE_LEVEL = 8;delay = 800;break;
            default: MAX_TREE_LEVEL = 4;break;
        }
    }
}
