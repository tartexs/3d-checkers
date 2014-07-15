package checkers.gui.panels.panel2D;

import java.awt.Graphics;
import java.awt.Image;
import javax.swing.JPanel;

/**
 * Class BackgroundPanel
 * Implements JPanel with background image
 * 
 * @author Cristian Tardivo
 */
public class BackgroundPanel extends JPanel {
    // Background image
    private Image bgImage;
 
    /**
     * Create new background panel
     */
    public BackgroundPanel(){
        super();
        // Make transparent panel
        this.setOpaque(false);
    }
 
    /**
     * Set background image
     * @param bgImage image to set as background
     */
    public void setBackgroundImage(Image bgImage){
        this.bgImage = bgImage;
    }

    /**
     * Override paint method to draw first backgroundimage
     * @param g 
     */
    @Override
    public void paint(Graphics g){
        // First paint background image
        if(bgImage != null)
            g.drawImage(bgImage, 0, 0, null);
        // Paint other elements
        super.paint(g);
    }
}