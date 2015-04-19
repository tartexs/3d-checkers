package checkers.common;

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
import java.io.IOException;

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
    // Saved Settings filename
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
    
    // Empty Private Constructor
    private Settings(){
        // Check unique instance
        if(instance != null)
            throw new IllegalStateException("Already instantiated");
    }
    
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
        } catch (IOException | ClassNotFoundException e){
            // If file no exist create new Game Settings
            return new Settings();
        }
    }
    
    /**
     * Save current settings to file (settings string file)
     * @return success or can't save
     */
    public static boolean saveSettings(){
        try {
            // Create Save File
            FileOutputStream file = new FileOutputStream(file_name);
            // Output Stream file
            ObjectOutputStream os = new ObjectOutputStream(file);
            // Write file (object)
            os.writeObject(getInstance());
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
    private static Settings getInstance(){
        if(instance == null)
            instance = loadSettings();
        return instance;
    }
    
    /**
     * Retrieves 3D Camera auto-rotation on/off
     * @return true or false
     */
    public static boolean getAutoRotation(){
        return getInstance().autoRotation;
    }
    
    /**
     * Set 3D camera auto-rotation on/off
     * @param status true or false
     */
    public static void setAutoRotation(boolean status){
        getInstance().autoRotation = status;
    }
    
    /**
     * Retrieves game sounds on/off
     * @return true or false
     */
    public static boolean getAudioEnable(){
        return getInstance().sound;
    }
    
    /**
     * set game sound on/off
     * @param status true or false
     */
    public static void setAudioEnable(boolean status){
        getInstance().sound = status;
    }
    
    /**
     * Retrieves 3D Shadows on/off
     * @return true or false
     */
    public static boolean getShadowsEnable(){
        return getInstance().shadows;
    }
    
    /**
     * Set 3D Shadows on/off
     * @param status true or false
     */
    public static void setShadowsEnable(boolean status){
        getInstance().shadows = status;
    }
    
    /**
     * Retrieves 3D Shadows Filtering Level
     * @return 0:Bilinear 1:Dither 2:Nearest 3:PCF4 4:PFC8 5:PCFPOISSON
     */
    public static int getShadowFilterLevel(){
        return getInstance().shadowsFilter;
    }
    
    /**
     * Set 3D Shadows Filtering Level
     * @param level 0:Bilinear 1:Dither 2:Nearest 3:PCF4 4:PFC8 5:PCFPOISSON
     */
    public static void setShadowFilterLevel(int level){
        if(level < 0 || level > 5){
            System.err.println("Invalid Shadow Filter Level");
            return;
        }
        getInstance().shadowsFilter = level;
    }
    
    /**
     * Retrieves 3D Shadows Map Size
     * @return size 512 | 1024 | 2048
     */
    public static int getShadowsMapSize(){
        return getInstance().shadowsMapSize;
    }
    
    /**
     * Set 3D Shadows Map Size
     * @param size Map size 512 | 1024 | 2048
     */
    public static void setShadowsMapSize(int size){
        if(size != 512 && size != 1024 && size != 2048){
            System.err.println("Invalid Shadows Map Size");
            return;
        }
        getInstance().shadowsMapSize = size;
    }
    
    /**
     * Retrieves 3D Antialiasing Samples Level
     * @return AA level 0,2,4,8,16
     */
    public static int getSamplesLevel(){
        return getInstance().samples;
    }
    
    /**
     * Set 3D Antialiasing Samples Level
     * @param level 0,2,4,8,16 
     */
    public static void setSamplesLevel(int level){
        if(level != 0 && level != 2 && level != 4 && level != 8 && level != 16){
            System.err.println("Invalid Antialiasing Samples Level");
            return;
        }
        getInstance().samples = level;
    }
    
    /**
     * Retrieves 3D vSync on/off
     * @return true or false
     */
    public static boolean getVSync(){
        return getInstance().vsync;
    }
    
    /**
     * Set 3D vSync on/off
     * @param status true or false
     */
    public static void setVSync(boolean status){
        getInstance().vsync = status;
    }
    
    /**
     * Retrieves 3D Fps Showing on/off 
     * @return true or false
     */
    public static boolean getShowFPS(){
        return getInstance().showFPS;
    }

    /**
     * Set 3D Fps Showing on/off
     * @param status true or false
     */
    public static void setShowFPS(boolean status){
        getInstance().showFPS = status;
    }
    
    /**
     * Retrieves 3D 30fps Limit on/off
     * @return true or false
     */
    public static boolean getLimitFPS(){
        return getInstance().limitFPS;
    }
    
    /**
     * Set 3D 30fps Limint on/off
     * @param status true or false
     */
    public static void setLimitFPS(boolean status){
        getInstance().limitFPS = status;
    }
    
    /**
     * Retrives if current view is 3D
     * @return true or false
     */
    public static boolean is3DView(){
        return getInstance().view3D;
    }
    
    /**
     * Set if current view is 3D
     * @param status true or false
     */
    public static void set3DView(boolean status){
        getInstance().view3D = status;
    }
    
    /**
     * Retrieves Game Window location
     * @return location point
     */
    public static Point getLocation(){
        return getInstance().location;
    }
    
    /**
     * Set Game window location
     * @param value location point
     */
    public static void setLocation(Point value){
        getInstance().location = value;
    }
    
    /**
     * Retrieves game window size
     * @return Dimension size
     */
    public static Dimension getSize(){
        Settings stn = getInstance();
        return (stn.view3D)? stn.size3D : stn.size2D;
    }
    
    /**
     * Set game window size
     * @param value dimension size
     */
    public static void setSize(Dimension value){
        getInstance().size3D = value;
    }
    
    /**
     * Retrieves minimun window size
     * @return 
     */
    public static Dimension getMinimunSize(){
        Settings stn = getInstance();
        return (stn.view3D)? stn.minimunSize3D : stn.minimunSize2D;
    }
    
    /**
     * Set minimun window size
     * @param value dimension size
     */
    public static void setMinimunSize(Dimension value){
        getInstance().minimunSize3D = value;
    }
    
    /**
     * Retrieves if game window are maximized
     * @return true or false
     */
    public static int isMaximized(){
        return getInstance().maximized;
    }
    
    /**
     * Set if game window are maximized
     * @param status true or false
     */
    public static void setMaximized(int status){
        getInstance().maximized = status;
    }

    /**
     * Retrieves PlayerA name
     * @return player name string
     */
    public static String getNamePlayerA(){
        return getInstance().player1Name;
    }
    
    /**
     * Set PlayerA name
     * @param name player name string
     */
    public static void setNamePlayerA(String name){
        name = name.trim();
        getInstance().player1Name = (name.length() > 12)? name.substring(0,12) : name;
    }
    
    /**
     * Retrieves PlayerB name
     * @return player name string
     */
    public static String getNamePlayerB(){
        return getInstance().player2Name;
    }
    
    /**
     * Set PlayerB name
     * @param name player name string
     */
    public static void setNamePlayerB(String name){
        name = name.trim();
        getInstance().player2Name = (name.length() > 12)? name.substring(0,12) : name;
    }
    
    /**
     * Retrieves PlayerA color
     * @return player color
     */
    public static Color getColorPlayerA(){
        return getInstance().player1Color;
    }
    
    /**
     * Set PlayerA color
     * @param color player Color
     */
    public static void setColorPlayerA(Color color){
        getInstance().player1Color = color;
    }
    
    /**
     * Retrieves PlayerB color
     * @return player color
     */
    public static Color getColorPlayerB(){
        return getInstance().player2Color;
    }
    
    /**
     * Set PlayerB color
     * @param color player Color
     */
    public static void setColorPlayerB(Color color){
        getInstance().player2Color = color;
    }
    
    /**
     * Retrieves PlayerA type
     * @return local | remote | artificial
     */
    public static Type getTypePlayerA(){
        return getInstance().player1Type;
    }
    
    /**
     * Set PlayerA type
     * @param type local | remote | artificial
     */
    public static void setTypePlayerA(Type type){
        getInstance().player1Type = type;
    }
    
    /**
     * Retrieves PlayerB type
     * @return local | remote | artificial
     */
    public static Type getTypePlayerB(){
        return getInstance().player2Type;
    }
    
    /**
     * Set PlayerB type
     * @param type local | remote | artificial
     */
    public static void setTypePlayerB(Type type){
        getInstance().player2Type = type;
    }
    
    /**
     * Set remote server ip
     * @param str server ip
     */
    public static void setIP(String str){
        getInstance().ip = str;
    }
    
    /**
     * Retrieve remote server ip
     * @return string ip
     */
    public static String getIP(){
        return getInstance().ip;
    }
    
    /**
     * Set remote server port
     * @param p server port
     */
    public static void setPort(int p){
        getInstance().port = p;
    }
    
    /**
     * Retrieve remote server port
     * @return integer server port
     */
    public static int getPort(){
        return getInstance().port;
    }
    
    /**
     * Set ia difficult level
     * @param lvl Difficult value
     */
    public static void setDifficult(Difficult lvl){
        getInstance().difficultLvl = lvl;
    }
    
    /**
     * Retrieves ia difficult level
     * @return Difficult value
     */
    public static Difficult getDifficult(){
        return getInstance().difficultLvl;
    }
}