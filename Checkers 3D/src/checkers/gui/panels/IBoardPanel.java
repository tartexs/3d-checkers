package checkers.gui.panels;

import checkers.model.Player;
import checkers.common.Point;
import java.util.Observer;
import javax.swing.JPanel;

/**
 * Interface IBoardPanel
 * Defines Common interface for diferentes Panels (3D-2D)
 * 
 * @author Cristian Tardivo
 */
public interface IBoardPanel {

    /**
     * Retrieves main Panel
     * @return JPanel with main view
     */
    public JPanel getPanel();
    
    /**
     * Add Observer to this panel
     * @param obs view Observer
     */
    public void addObserver(Observer obs);
    
    /**
     * Start Game Board
     */
    public void startBoard();
    
    /**
     * Restart Game Board
     */
    public void restartBoard();
    
    /**
     * Enable User Interaction with this board
     * @param status enable or disable
     */
    public void enableInteraction(boolean status);
    
    /**
     * Set current player turn
     * @param jugador game Player
     */
    public void setTurn(Player jugador);
    
    /**
     * Move piece inside the board
     * @param orig Origin Point
     * @param dest Destiny Point
     */
    public void movePiece(Point orig,Point dest);
    
    /**
     * Eats piece inside the board
     * @param pos Piece to eat
     */
    public void eatPiece(Point pos);
   
    /**
     * Converts piece inside the board into queen
     * @param pos Piece to convert
     */
    public void toQueen(Point pos);
    
    /**
     * Clear Board Selection
     * @param points points to clear
     */
    public void clearSelection(Point... points);
    
    /**
     * Update Panel Settings
     */
    public void updateSettings();
}
