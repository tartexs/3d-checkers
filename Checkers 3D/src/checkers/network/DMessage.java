package checkers.network;

import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;

/**
 * Class DMessage
 * Implements Checkers networking messages
 * using jMonkey (SpiderMonkey) network AbstractMessage
 * Allows to send command and message like chat
 * message, game movements and others
 * 
 * @author Cristian Tardivo
 */
@Serializable()
public class DMessage extends AbstractMessage {

    private Object data;
    private String[] strArry;
    private String command;

    /**
     * Default DMessage constructor
     */
    public DMessage(){
        command = "INVALID";
    }
    
    /**
     * Specialized DMessage constructor
     * @param command message command
     * @param data message data
     */
    public DMessage(String ncommand,Object ndata){
        command = ncommand;
        if(data instanceof String[]){
            strArry = (String[])ndata;
        } else {
            data = ndata;
        }
    }
    
    /**
     * Retrieves message command
     * @return String command
     */
    public String getCommand(){
        return command;
    }
    
    /**
     * Retrieves message data
     * @return data Object
     */
    public Object getData(){
        return (data == null)? strArry : data;
    }
}