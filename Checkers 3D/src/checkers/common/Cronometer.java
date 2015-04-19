package checkers.common;

import java.util.Observable;

/**
 * Class Cronometer
 * Implements object runnable and observable 
 * using a Singleton pattern to have unique instance 
 * of this class 
 * Allows to carry the time of current game
 * 
 * @author Cristian Tardivo
 */
public class Cronometer extends Observable implements Runnable{
    // Cronometer Thread
    Thread crono_thread = null;
    // Cronometer data
    private String time;
    private int seconds;
    private int minutes;
    private int hours;
    private int lap;
    // Unique instance
    private static Cronometer instance;

    /**
     * Retrieves Cronometer instance
     * @return Cronometer unique instance
     */
    public synchronized static Cronometer getInstance(){
        if (instance == null)
            instance = new Cronometer();
        return instance;
    }
    
    /**
     * Private Cronometer constructor
     */
    private Cronometer(){
        crono_thread = new Thread(this);
        crono_thread.setName("Cronometer Thread");
        time = "";
        seconds = 0;
        minutes = 0;
        hours = 0;
        lap = 0;
    }
    
    /**
     * Cronometer thread run
     */
    @Override
    public void run(){
        while(true){
            try {
                // Sleep 1 second and update seconds
                Thread.sleep(1000);
                seconds ++;
                // 60 seconds to 1 minute
                if(seconds == 60){
                    seconds = 0;
                    minutes++;
                }
                // 60 minutes to 1 hour
                if(minutes == 60){
                    minutes = 0;
                    hours++;
                }
                // Generate Time String
                if(hours < 10) time = "0" + hours + ":";
                else time = hours + ":";
                if(minutes < 10) time += "0" + minutes + ":";
                else time += minutes  + ":";
                if(seconds < 10) time += "0" + seconds;
                else time += seconds;
                // Notify Time change to Observer
                setChanged();
                notifyObservers("TIME_CHANGED");
            } catch (InterruptedException ex){
                    System.err.println("Cronometer Thread Error");
            }
        }
    }
    
    /**
     * Start Cronometer
     */
     public void start(){      
        if (crono_thread.isAlive()){
            crono_thread.resume();
        } else {
            crono_thread.start();
        }
    }
    
     /**
      * Reset Cronometer
      */
     public void reset(){
        time = "00:00:00";
        hours = 0;
        minutes = 0;
        seconds = 0;
        lap = 0;
     }
     
     /**
      * Pause Cronometer
      */
    public void pause(){
        crono_thread.suspend();
    }
    
    /**
     * Save/Get last "Lap"
     */
    public int lap(){
        int res = lap;
        lap = seconds+minutes*60+hours*3600;
        return res;
    }
    
    /**
     * Retrieves time String
     * @return current time string
     */
    public String getTime(){
        return  time;
    }
    
    /**
     * Retrives time in second
     * @return current time in seconds
     */
    public int getSeconds(){
        return seconds+minutes*60+hours*3600;
    }
    
    /**
     * Convert Seconds to time String
     * @param seconds time in seconds
     * @return time string
     */
    public static String getString(int time){
        String res = "";
        int hours = time / 3600;
        res += (hours < 10)? "0"+hours+":" : hours+":";
        int minutes = (time % 3600) / 60;
        res += (minutes < 10)? "0"+minutes+":" : minutes+":";
        int seconds = (time % 3600) % 60;
        res += (seconds < 10)? "0"+seconds : seconds;
        return res;
    }
}