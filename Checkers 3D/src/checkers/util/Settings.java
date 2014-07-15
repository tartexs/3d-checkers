package checkers.util;

import checkers.ia.iaPlayer.Difficult;
import checkers.model.Player.Color;
import checkers.model.Player.Type;
import java.awt.Dimension;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.awt.Point;

/**
 * Class Settings
 * Implements Global Game Settings Administrator
 * Serializable object allows to save an load 
 * settings from system drive
 * 
 * @author Cristian Tardivo
 */
public class Settings implements Serializable {
    // Settings Instance
    private static Settings instance;
    //
    private static String file_name = "settings.dat";
    // Last Game Settings
    private String player1Name = "Player A";
    private String player2Name = "Player B";
    private Color player1Color = Color.red;
    private Color player2Color = Color.black;
    private Type player1Type = Type.local;
    private Type player2Type = Type.local;
    // IA Settings
    private Difficult difficultLvl = Difficult.easy;
    // Damas Frame (Window) Settings
    private int maximized = 0;  // 0: Normal 6: Maximized Both
    private Dimension size3D = new Dimension(900,560);
    private Dimension size2D = new Dimension(800,640);
    private Dimension minimunSize3D = new Dimension(900,560);
    private Dimension minimunSize2D = new Dimension(800,640);
    private Point location = new Point(0,0);
    private boolean view3D = true;
    // Remote Game Data
    private String ip = "localhost";
    private int port = 6143;
    // 3D Settings
    private boolean vsync = true;
    private int samples = 2;
    private boolean shadows = true;
    private boolean sound = true;
    private boolean autoRotation = true;
    private boolean showFPS = true;
    private boolean limitFPS = false;
    // 0:Bilinear 1:Dither 2:Nearest 3:PCF4 4:PFC8 5:PCFPOISSON
    private int shadowsFilter = 3;
    private int shadowsMapSize = 1024;
    
    /**
     * loadSettings from file (settings string file)
     * @return saved settings if exist and correct or new settings
     */
    private static Settings loadSettings(){
        // Try load file
        try {
            FileInputStream fis = new FileInputStream(file_name);
            ObjectInputStream ois = new ObjectInputStream (fis);
            Settings obj = (Settings) ois.readObject();
            ois.close();
            fis.close();
            return obj;
        } catch (Exception e){
            // If file no exist create new Game Settings
            return new Settings();
        }
    }
    
    /**
     * Save current settings to file (settings string file)
     * @return success or can't save
     */
    public boolean saveSettings(){
        try {
            // Create Save File
            FileOutputStream file = new FileOutputStream(file_name);
            // Output Stream file
            ObjectOutputStream os = new ObjectOutputStream(file);
            // Write file (object)
            os.writeObject(this);
            // Close stream and file
            os.close();
            file.close();
        } catch (Exception e){
            System.err.println("Can't Save Game Settings file: "+file_name);
            return false;
        }
        return true;
    }
    
    /**
     * Retrieves Settings Instance
     * @return 
     */
    public static Settings getInstance(){
        if(instance == null)
            instance = loadSettings();
        return instance;
    }
    
    /**
     * Retrieves 3D Camera auto-rotation on/off
     * @return true or false
     */
    public boolean getAutoRotation(){
        return autoRotation;
    }
    
    /**
     * Set 3D camera auto-rotation on/off
     * @param status true or false
     */
    public void setAutoRotation(boolean status){
        autoRotation = status;
    }
    
    /**
     * Retrieves game sounds on/off
     * @return true or false
     */
    public boolean getAudioEnable(){
        return sound;
    }
    
    /**
     * set game sound on/off
     * @param status true or false
     */
    public void setAudioEnable(boolean status){
        sound = status;
    }
    
    /**
     * Retrieves 3D Shadows on/off
     * @return true or false
     */
    public boolean getShadowsEnable(){
        return shadows;
    }
    
    /**
     * Set 3D Shadows on/off
     * @param status true or false
     */
    public void setShadowsEnable(boolean status){
        shadows = status;
    }
    
    /**
     * Retrieves 3D Shadows Filtering Level
     * @return 0:Bilinear 1:Dither 2:Nearest 3:PCF4 4:PFC8 5:PCFPOISSON
     */
    public int getShadowFilterLevel(){
        return shadowsFilter;
    }
    
    /**
     * Set 3D Shadows Filtering Level
     * @param level 0:Bilinear 1:Dither 2:Nearest 3:PCF4 4:PFC8 5:PCFPOISSON
     */
    public void setShadowFilterLevel(int level){
        if(level < 0 || level > 5){
            System.err.println("Invalid Shadow Filter Level");
            return;
        }
        shadowsFilter = level;
    }
    
    /**
     * Retrieves 3D Shadows Map Size
     * @return size 512 | 1024 | 2048
     */
    public int getShadowsMapSize(){
        return shadowsMapSize;
    }
    
    /**
     * Set 3D Shadows Map Size
     * @param size Map size 512 | 1024 | 2048
     */
    public void setShadowsMapSize(int size){
        if(size != 512 && size != 1024 && size != 2048){
            System.err.println("Invalid Shadows Map Size");
            return;
        }
        shadowsMapSize = size;
    }
    
    /**
     * Retrieves 3D Antialiasing Samples Level
     * @return AA level 0,2,4,8,16
     */
    public int getSamplesLevel(){
        return samples;
    }
    
    /**
     * Set 3D Antialiasing Samples Level
     * @param level 0,2,4,8,16 
     */
    public void setSamplesLevel(int level){
        if(level != 0 && level != 2 && level != 4 && level != 8 && level != 16){
            System.err.println("Invalid Antialiasing Samples Level");
            return;
        }
        samples = level;
    }
    
    /**
     * Retrieves 3D vSync on/off
     * @return true or false
     */
    public boolean getVSync(){
        return vsync;
    }
    
    /**
     * Set 3D vSync on/off
     * @param status true or false
     */
    public void setVSync(boolean status){
        vsync = status;
    }
    
    /**
     * Retrieves 3D Fps Showing on/off 
     * @return true or false
     */
    public boolean getShowFPS(){
        return showFPS;
    }

    /**
     * Set 3D Fps Showing on/off
     * @param status true or false
     */
    public void setShowFPS(boolean status){
        showFPS = status;
    }
    
    /**
     * Retrieves 3D 30fps Limit on/off
     * @return true or false
     */
    public boolean getLimitFPS(){
        return limitFPS;
    }
    
    /**
     * Set 3D 30fps Limint on/off
     * @param status true or false
     */
    public void setLimitFPS(boolean status){
        limitFPS = status;
    }
    
    /**
     * Retrives if current view is 3D
     * @return true or false
     */
    public boolean is3DView(){
        return view3D;
    }
    
    /**
     * Set if current view is 3D
     * @param status true or false
     */
    public void set3DView(boolean status){
        view3D = status;
    }
    
    /**
     * Retrieves Game Window location
     * @return location point
     */
    public Point getLocation(){
        return location;
    }
    
    /**
     * Set Game window location
     * @param value location point
     */
    public void setLocation(Point value){
        location = value;
    }
    
    /**
     * Retrieves game window size
     * @return Dimension size
     */
    public Dimension getSize(){
        return (view3D)? size3D : size2D;
    }
    
    /**
     * Set game window size
     * @param value dimension size
     */
    public void setSize(Dimension value){
        size3D = value;
    }
    
    /**
     * Retrieves minimun window size
     * @return 
     */
    public Dimension getMinimunSize(){
        return (view3D)? minimunSize3D : minimunSize2D;
    }
    
    /**
     * Set minimun window size
     * @param value dimension size
     */
    public void setMinimunSize(Dimension value){
        minimunSize3D = value;
    }
    
    /**
     * Retrieves if game window are maximized
     * @return true or false
     */
    public int isMaximized(){
        return maximized;
    }
    
    /**
     * Set if game window are maximized
     * @param status true or false
     */
    public void setMaximized(int status){
        maximized = status;
    }

    /**
     * Retrieves PlayerA name
     * @return player name string
     */
    public String getNamePlayerA(){
        return player1Name;
    }
    
    /**
     * Set PlayerA name
     * @param name player name string
     */
    public void setNamePlayerA(String name){
        name = name.trim();
        player1Name = (name.length() > 12)? name.substring(0,12) : name;
    }
    
    /**
     * Retrieves PlayerB name
     * @return player name string
     */
    public String getNamePlayerB(){
        return player2Name;
    }
    
    /**
     * Set PlayerB name
     * @param name player name string
     */
    public void setNamePlayerB(String name){
        name = name.trim();
        player2Name = (name.length() > 12)? name.substring(0,12) : name;
    }
    
    /**
     * Retrieves PlayerA color
     * @return player color
     */
    public Color getColorPlayerA(){
        return player1Color;
    }
    
    /**
     * Set PlayerA color
     * @param color player Color
     */
    public void setColorPlayerA(Color color){
        player1Color = color;
    }
    
    /**
     * Retrieves PlayerB color
     * @return player color
     */
    public Color getColorPlayerB(){
        return player2Color;
    }
    
    /**
     * Set PlayerB color
     * @param color player Color
     */
    public void setColorPlayerB(Color color){
        player2Color = color;
    }
    
    /**
     * Retrieves PlayerA type
     * @return local | remote | artificial
     */
    public Type getTypePlayerA(){
        return player1Type;
    }
    
    /**
     * Set PlayerA type
     * @param type local | remote | artificial
     */
    public void setTypePlayerA(Type type){
        player1Type = type;
    }
    
    /**
     * Retrieves PlayerB type
     * @return local | remote | artificial
     */
    public Type getTypePlayerB(){
        return player2Type;
    }
    
    /**
     * Set PlayerB type
     * @param type local | remote | artificial
     */
    public void setTypePlayerB(Type type){
        player2Type = type;
    }
    
    /**
     * Set remote server ip
     * @param str server ip
     */
    public void setIP(String str){
        ip = str;
    }
    
    /**
     * Retrieve remote server ip
     * @return string ip
     */
    public String getIP(){
        return ip;
    }
    
    /**
     * Set remote server port
     * @param p server port
     */
    public void setPort(int p){
        port = p;
    }
    
    /**
     * Retrieve remote server port
     * @return integer server port
     */
    public int getPort(){
        return port;
    }
    
    /**
     * Set ia difficult level
     * @param lvl Difficult value
     */
    public void setDifficult(Difficult lvl){
        difficultLvl = lvl;
    }
    
    /**
     * Retrieves ia difficult level
     * @return Difficult value
     */
    public Difficult getDifficult(){
        return difficultLvl;
    }
}