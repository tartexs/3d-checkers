package checkers.util;

import java.applet.Applet;
import java.applet.AudioClip;

/**
 * Class Sound
 * Implements sound manager and player
 * 
 * @author Cristian Tardivo
 */
public class Sound {
    
    // Game Sound enumeration
    public enum Sounds {move_sound,eat_sound,queen_sound,wrong_sound};
    
    // Sound data
    private static String sound_path = "Sounds/";
    private static String sound_ext = ".wav";
    private static ClassLoader loader = Sound.class.getClassLoader();
    private AudioClip sound;
    
    /*
     * Acquire AudioClip from sound
     */
    public Sound(Sounds sound){
        this.sound = Applet.newAudioClip(loader.getResource(sound_path+sound+sound_ext));
    }
    
    /* 
     * Play current audio clip
     */
    public void play(){
        sound.play();
    }

    /*
     * Play loop current audio clip
     */
    public void loop(){
        sound.loop();
    }

    /*
     * Stop current playback
     */
    public void stop(){
        sound.stop();
    }
}