package checkers.gui.panels.panel3D;

import checkers.common.Pair;
import checkers.common.Point;
import com.jme3.cinematic.events.CinematicEvent;
import com.jme3.cinematic.events.CinematicEventListener;
import java.util.Observable;
import java.util.concurrent.Semaphore;

/**
 *
 * @author Cristian
 */
public class Panel3DController extends Observable implements CinematicEventListener{
    // Semaphore synchronizer
    private Semaphore semaphore;
    // Move send thread
    private Thread action;
    
    /**
     * Create new 3D Panel controller and synchronizer
     */
    public Panel3DController(){
        semaphore = new Semaphore(0, true);
    }
        
    /**
     * Sync Actions
     */
    public void sync(){
        // wait for others comands ends after continue
        if(action != null){
            try {
                action.join();
            } catch (InterruptedException ex){
                System.err.println("Sync Error: Can't join to action");
            }
        }
    }
    
    /**
     * onPlay cinematicEvent listener
     * @param cinematic not used
     */
    @Override
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
    @Override
    public void onPause(CinematicEvent cinematic){
        System.err.println("Cinematic Event Pause !!!");
    }

    /**
     * onStop cinematicEvent listener
     * @param cinematic 
     */
    @Override
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
                setChanged();
                notifyObservers(new Pair(orig,dest));
            }
        });
        action.setName("Send Move Thread");
        action.start();
    }
}