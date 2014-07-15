package checkers.util;

import java.awt.Image;
import java.net.URL;
import javax.swing.ImageIcon;

/**
 * Class Texture
 * Extends java ImageIcon to implements game 2D textures and Gifs
 * 
 * @author Cristian Tardivo
 */
public class Texture extends ImageIcon {
    // Textures and Gifs
    public enum Textures {red,red_queen,black,black_queen,dark_square,light_square,selection,board,table,turn,clear};
    public enum Gifs {load};
    // Textures/Gifs Data
    private static String texture_path = "Textures/";
    private static String texture_ext = ".png";
    private static String gif_ext = ".gif";
    private static ClassLoader loader = Texture.class.getClassLoader();
    private Textures name;
    
    /**
     * Load new Texture
     * @param txt Texture to load
     */
    public Texture(Textures txt){
        super(loader.getResource(texture_path+txt+texture_ext));
        name = txt;
    }
    
    /**
     * Load and resize new Texture
     * @param txt Texture to load
     * @param size final size
     */
    public Texture(Textures txt, int size){
        super(getResizedImage(loader.getResource(texture_path+txt+texture_ext),size,size));
        name = txt;
    }
    
    /**
     * Load an resie new Texture
     * @param txt Texture to load
     * @param nwidth new texture width
     * @param nheight new texture height
     */
    public Texture(Textures txt, int nwidth, int nheight){
        super(getResizedImage(loader.getResource(texture_path+txt+texture_ext),nwidth,nheight));
        name = txt;
    }
    
    /**
     * Load new Gif
     * @param gif Gif to load
     */
    public Texture(Gifs gif){
        super(loader.getResource(texture_path+gif+gif_ext));
    }
    
    /**
     * Load and resize new Gif
     * @param gif Gif to load
     * @param size final size
     */
    public Texture(Gifs gif, int size){
        super(getResizedImage(loader.getResource(texture_path+gif+gif_ext),size,size));
    }
    
    /**
     * Load and resize new Gif
     * @param gif Gif to load
     * @param nwidth new Gif width
     * @param nheight new Gif height
     */
    public Texture(Gifs gif, int nwidth, int nheight){
        super(getResizedImage(loader.getResource(texture_path+gif+gif_ext),nwidth,nheight));
    }
    
    /**
     * Retrieves current texture/gif name
     * @return name
     */
    public Textures getName(){
        return name;
    }
    
    /**
     * Image resizer method
     * @param location Image URL
     * @param nwidth new width
     * @param nheight new height
     * @return resized image
     */
    private static Image getResizedImage(URL location,int nwidth, int nheight){
        ImageIcon icon = new ImageIcon(location);
        return icon.getImage().getScaledInstance(nwidth, nheight, Image.SCALE_SMOOTH);
    }
}