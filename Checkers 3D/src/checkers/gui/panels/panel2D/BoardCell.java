package checkers.gui.panels.panel2D;

import java.awt.Component;
import java.awt.Graphics;
import javax.swing.Icon;
import javax.swing.JLabel;

import checkers.common.Texture;


/**
 * Calss BoardCell
 * Implements a table cell like a Icon with multiple textures
 * 
 * @author Cristian Tardivo
 */
public class BoardCell extends JLabel implements Icon {
    // Cell position on board
    private int row;
    private int col;
    // Cell textures
    private Texture piece;
    private Texture square;
    private Texture selection;
    
    /**
     * Default cell constructor
     * @param nrow row number
     * @param ncol colum number
     */
    public BoardCell(int nrow, int ncol){
        super();
        row = nrow;
        col = ncol;
        setIcon(this);
    }
    
    /**
     * Set Square texture (background)
     * @param nsquare bg texture
     */
    public void setSquare(Texture nsquare){
        square = nsquare;
        repaint();
    }
    
    /**
     * Retrieves square texture (background)
     * @return bg texture
     */
    public Texture getSquare(){
        return square;
    }

    /**
     * Set piece texture
     * @param npiece pice texture
     */
    public void setPiece(Texture npiece){
        piece = npiece;
        repaint();
    }
    
    /**
     * Retrieves current piece texture
     * @return piece texture
     */
    public Texture getPiece(){
        return piece;
    }
    
    /**
     * Clear current piece texture
     */
    public void clearPiece(){
        piece = null;
        repaint();
    }
    
    /**
     * Set current set as selected
     * @param nselection selected texture
     */
    public void setSelection(Texture nselection){
        selection = nselection;
        repaint();
    }
    
    /**
     * Clear selection texture
     */
    public void clearSelection(){
        selection = null;
        repaint();
    }
    
    /**
     * Get cell row number
     * @return 
     */
    public int getRow(){
        return row;
    }
    
    /**
     * Set cell row number
     * @param nrow 
     */
    public void setRow(int nrow){
        row = nrow;
    }
    
    /**
     * Get cell colum number
     * @return 
     */
    public int getCol(){
        return col;
    }
    
    /**
     * Set cell colum number
     * @param ncol 
     */
    public void setCol(int ncol){
        col = ncol;
    }
    
    /**
     * Paint Cell icons
     */
    @Override
    public void paintIcon(Component c, Graphics g, int x, int y){
        if(square != null) doPaint(square, c, g, x, y);
        if(selection != null) doPaint(selection, c, g, x, y);
        if(piece != null) doPaint(piece, c, g, x, y);
    }
    
    /**
     * Paint a icon in the cell
     */
    private void doPaint(Icon icon,Component c, Graphics g, int x, int y){
        int width = square.getIconWidth();
        int height = square.getIconHeight();
        int iconX = getOffset(width, icon.getIconWidth(), 0.5f);
        int iconY = getOffset(height, icon.getIconHeight(), 0.5f);
        icon.paintIcon(c, g, x + iconX, y + iconY);        
    }
    
    /**
     * Compute icon offset from size and alignment
     */
    private int getOffset(int maxValue, int iconValue, float alignment){
        float offset = (maxValue - iconValue) * alignment;
        return Math.round(offset);
    }

    /**
     * Retrieves icon width
     * @return 
     */
    @Override
    public int getIconWidth(){
        return square.getIconWidth();
    }

    /**
     * Retrieves icon height
     * @return 
     */
    @Override
    public int getIconHeight(){
        return square.getIconHeight();
    }
}
