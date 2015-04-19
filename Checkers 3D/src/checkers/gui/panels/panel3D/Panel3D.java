package checkers.gui.panels.panel3D;

import com.jme3.system.AppSettings;
import com.jme3.system.JmeCanvasContext;
import checkers.gui.panels.IBoardPanel;
import checkers.gui.panels.LoadingPanel;
import checkers.model.Player;
import checkers.common.Point;
import checkers.common.Settings;
import java.awt.BorderLayout;
import java.util.Observer;
import javax.swing.JPanel;

/**
 * Class Panel3D 
 * Implements IDamasPanel and AbstracPanel3D
 * Implements CinematicEventListener to synchronize
 * events in abstract panel
 * 
 * @author Cristian Tardivo
 */
public class Panel3D extends AbstractPanel3D implements IBoardPanel {
    // 3D Panel Controller and Sync
    private Panel3DController pControl;
    
    /**
     * Default Panel3D Consturctor
     */
    public Panel3D(){
        super();
        pControl = new Panel3DController();
        super.addControl(pControl);
    }
    
    /**
     * Add observer to this view
     * @param obs 
     */
    @Override
    public synchronized void addObserver(Observer obs){
        pControl.addObserver(obs);
    }
    
    /**
     * Create JPanel containing the 3d canvas
     * @return java JPanel
     */
    @Override
    public JPanel getPanel(){
        // View Panel
        final JPanel mainPanel = new JPanel(new BorderLayout());
        // Loading panel
        final JPanel loadingPanel = new LoadingPanel();
        // Add Loading Panel
        mainPanel.add(loadingPanel,BorderLayout.PAGE_START);
        // Settings
        settings = new AppSettings(true);
        settings.setSamples(Settings.getSamplesLevel());
        settings.setVSync(Settings.getVSync());
        settings.setFrameRate(Settings.getLimitFPS()? 30 : -1);
        setDisplayFps(Settings.getShowFPS());
        setDisplayStatView(false);
        setPauseOnLostFocus(false);
        // 3D Canvas
        createCanvas();
        mainPanel.add(((JmeCanvasContext)getContext()).getCanvas(),BorderLayout.CENTER);
        // Waits for canvas ready
        new Thread(new Runnable(){
            @Override
            public void run(){
                JmeCanvasContext context = (JmeCanvasContext)getContext();
                while(!context.isRenderable() || !context.isCreated()){
                    try {
                        Thread.sleep(800);
                    } catch (InterruptedException ex){
                        System.err.println("Error while waits for canvas be created");
                    }
                }
                mainPanel.remove(loadingPanel);
                mainPanel.revalidate();
        }}).start();
        return mainPanel;
    }
    
    
    /**
     * Start 3d board (Event)
     */
    @Override
    public void startBoard(){
        super.startBoard();
    }
    
    /**
     * Reset 3d board (Event)
     */
    @Override
    public void restartBoard(){
        pControl.sync();
        super.restartBoard();
    }

    /**
     * Enable or disable interaction with the 3d panel (Event)
     * @param status enable or disable
     */
    @Override
    public void enableInteraction(boolean status){
        if(interaction == status) return;
        super.enableInteraction(status);
    }
    
     /**
     * Move piece between 2 points (Event)
     * @param orig origin point
     * @param dest destinity point
     */
    @Override
    public void movePiece(Point orig, Point dest){
        super.movePiece(orig,dest);
    }

    /**
     * Piece eat (Event)
     * @param pos piece to eat
     */
    @Override
    public void eatPiece(Point pos){
        super.eatPiece(pos);
    }

    /**
     * Convert a piece to a queen (Event)
     * @param pos piece position
     */
    @Override
    public void toQueen(Point pos){
        super.toQueen(pos);
    }

    /**
     * Clear current selection (Event)
     * @param points selected points to clear
     */
    @Override
    public void clearSelection(Point... points){
        super.clearSelection(points);
    }   

    /**
     * Change player turn (Event)
     * Sets camera position
     * @param jugador next player
     */
    @Override
    public void setTurn(Player jugador){
        // Autorotation on, always rotate cam
        if(autoRotation){
            if(jugador.isRed())
                super.setPosCamera(cameras.PLAYER_1);
            else
                super.setPosCamera(cameras.PLAYER_2);
        }
        // Autorotation off, fix cam to local player (if not free cam or top view)
        if(!autoRotation){
            boolean p1 = Settings.getTypePlayerA() == Player.Type.local;
            boolean p2 = Settings.getTypePlayerB() == Player.Type.local;
            if(p1 && !p2){
                fixCameraPos(Settings.getColorPlayerA());
                return;
            }
            if(!p1 && p2)
                fixCameraPos(Settings.getColorPlayerB());
        }
    }
    
    /**
     * Used to fix camera position when game start between
     * local player and remote/artificial player set camera to local player 
     * (avoid fix when camera has been manually positioned)
     * @param pcolor 
     */
    private void fixCameraPos(Player.Color pcolor){
        if(pcolor == Player.Color.red){
            if(lastCamera == cameras.PLAYER_2){
                super.setPosCamera(cameras.PLAYER_1);
            }
        } else {
            if(lastCamera == cameras.PLAYER_1){
                super.setPosCamera(cameras.PLAYER_2);
            }
        }
    }
}