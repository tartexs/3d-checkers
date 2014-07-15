package checkers.gui.panels.panel3D;

import com.jme3.cinematic.events.CinematicEvent;
import com.jme3.cinematic.events.CinematicEventListener;
import com.jme3.system.AppSettings;
import com.jme3.system.JmeCanvasContext;
import checkers.gui.panels.IDamasPanel;
import checkers.gui.panels.LoadingPanel;
import checkers.model.Player;
import checkers.util.Pair;
import checkers.util.Point;
import checkers.util.Settings;
import java.awt.BorderLayout;
import java.util.Observer;
import java.util.concurrent.Semaphore;
import javax.swing.JPanel;

/**
 * Class Panel3D 
 * Implements IDamasPanel and AbstracPanel3D
 * Implements CinematicEventListener to synchronize
 * events in abstract panel
 * 
 * @author Cristian Tardivo
 */
public class Panel3D extends AbstractPanel3D implements IDamasPanel, CinematicEventListener {
    // Semaphore synchronizer
    Semaphore semaphore;
    // Move send thread
    Thread action;
    // View Observer
    private Observer observer;
    
    /**
     * Default Panel3D Consturctor
     */
    public Panel3D(){
        super();
        super.addControl(this);
        semaphore = new Semaphore(0, true);
    }
    
    /**
     * Add observer to this view
     * @param obs 
     */
    public synchronized void addObserver(Observer obs){
        observer = obs;
    }
    
    /**
     * Create JPanel containing the 3d canvas
     * @return java JPanel
     */
    public JPanel getPanel(){
        // View Panel
        final JPanel mainPanel = new JPanel(new BorderLayout());
        // Loading panel
        final JPanel loadingPanel = new LoadingPanel();
        // Add Loading Panel
        mainPanel.add(loadingPanel,BorderLayout.PAGE_START);
        // Settings
        settings = new AppSettings(true);
        settings.setSamples(Settings.getInstance().getSamplesLevel());
        settings.setVSync(Settings.getInstance().getVSync());
        settings.setFrameRate(Settings.getInstance().getLimitFPS()? 30 : -1);
        setDisplayFps(Settings.getInstance().getShowFPS());
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
        // wait for others comands ends after clear board
        if(action != null){
            try {
                action.join();
            } catch (InterruptedException ex){
                System.err.println("Restart Board Error: Can't join to action");
            }
        }
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
            boolean p1 = Settings.getInstance().getTypePlayerA() == Player.Type.local;
            boolean p2 = Settings.getInstance().getTypePlayerB() == Player.Type.local;
            if(p1 && !p2){
                fixCameraPos(Settings.getInstance().getColorPlayerA());
                return;
            }
            if(!p1 && p2)
                fixCameraPos(Settings.getInstance().getColorPlayerB());
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
      
    /**
     * onPlay cinematicEvent listener
     * @param cinematic not used
     */
    public void onPlay(CinematicEvent cinematic){
        try {
            semaphore.acquire();
        } catch (InterruptedException ex){
            System.err.println("Can't Acquire Semaphore");
        }
    }

    /**
     * onPause cinematicEvent listener
     * @param cinematic not used
     */
    public void onPause(CinematicEvent cinematic){
        System.err.println("Cinematic Event Pause !!!");
    }

    /**
     * onStop cinematicEvent listener
     * @param cinematic 
     */
    public void onStop(CinematicEvent cinematic){
        semaphore.release();
    }
    
    /**
     * Send Move to game Controller
     * @param orig Origin move point
     * @param dest Destinity move point
     */
    public void sendMove(final Point orig, final Point dest){
        // Run Update Thread
        action = new Thread(new Runnable(){
            @Override
            public void run(){
                observer.update(null, new Pair(orig,dest));
            }
        });
        action.setName("Send Move Thread");
        action.start();
    }
}