package checkers.network;

import com.jme3.network.Client;
import com.jme3.network.ClientStateListener;
import com.jme3.network.ConnectionListener;
import com.jme3.network.ErrorListener;
import com.jme3.network.HostedConnection;
import com.jme3.network.Message;
import com.jme3.network.MessageListener;
import com.jme3.network.Network;
import com.jme3.network.Server;
import com.jme3.network.serializing.Serializer;
import checkers.util.Point;
import checkers.util.Settings;
import java.io.IOException;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class DNetwork
 * Implements Checkers game network server and client
 * using jMonkey (SpiderMonkey) network module
 * Allows to create a game server and create a client connection
 * to server
 * Notify Observer network changes (client/server connect/disconnect)
 * sending and receiving messages like movements and chat text
 * 
 * @author Cristian Tardivo
 */
public class DNetwork extends Observable implements Runnable{
    // Server
    private Server server;
    private boolean isServer;
    // Client
    private Client client;
    // Conections / Create retries
    private int retries;
    // Network Thread
    private Thread network;
    // Listeners
    private networkListeners listeners;
    // Events Logger
    private static final Logger logger = Logger.getLogger(DNetwork.class.getName());
    
    /**
     * DNetwork Constructor
     * Create and initialize network server or cliente
     * @param server boolean server or client (true/false)
     * @param obs network changes observer
     */
    public DNetwork(boolean server,Observer obs){
        logger.log(Level.INFO,"Starting Network");
        network = new Thread(this);
        this.addObserver(obs);
        isServer = server;
        if(isServer)
            logger.log(Level.INFO,"Creating Server");
        else
            logger.log(Level.INFO,"Creating Client");
        listeners = new networkListeners();
        // Start Network thread
        network.start();
    }
    
    /**
     * Run network thread creation/connection
     */
    public void run(){
        if(isServer){
            // Try to create network server
            try {
                createServer();
                logger.log(Level.INFO,"Server Created");
                logger.log(Level.INFO,"Network Started");
            } catch (Exception ex){
                if(retries < 4){ // can't create server, retry
                    try {Thread.sleep(100);} catch (InterruptedException ex1){}
                    retries++;
                    logger.log(Level.INFO, "Retrying {0}", retries); 
                    run();
                } else { // no more retries, cant' create server
                    logger.log(Level.INFO,"Can't Create Server");
                    setChanged();
                    notifyObservers("CANT_CRETE_SERVER");
                }
            }
        }else { // isClient
            // Try to create network client
            try {
                createClient();
                logger.log(Level.INFO,"Client Connected");
                logger.log(Level.INFO,"Network Started");
            } catch (IOException ex){
                if(retries < 4){ // can't create client, retry
                    retries++;
                    logger.log(Level.INFO, "Retrying {0}", retries); 
                    run();
                } else { // no more retries, can't create client
                    logger.log(Level.INFO,"Can't Connect to Server");
                    setChanged();
                    notifyObservers("CANT_CONNECT");
                }
            }
        }
    }
    
    /**
     * Create new Game Server
     * @throws IOException can't create server
     */
    private void createServer() throws IOException {
        // Register Message class
        Serializer.registerClass(DMessage.class);
        // Create server
        int port = Settings.getInstance().getPort();
        server = Network.createServer(port);
        // Message Listener
        server.addMessageListener(listeners);
        // Conection Listener
        server.addConnectionListener(listeners);
        // Start Server
        server.start();
    }
    
    /**
     * Creates New Game Client
     * @throws IOException can't create client
     */
    private void createClient() throws IOException {
        // Registre Message Class
        Serializer.registerClass(DMessage.class);
        // Create Client
        String ip = Settings.getInstance().getIP();
        int port = Settings.getInstance().getPort();
        client = Network.connectToServer(ip, port);
        // Message Listener
        client.addMessageListener(listeners);
        // Conection Listener
        client.addClientStateListener(listeners);
        // Error Listener
        client.addErrorListener(listeners);
        // Client Start
        client.start(); 
    }
    
    /**
     * Close Current Game Connection
     */
    public void closeConnection(){
        logger.log(Level.INFO,"Closing Network Connection");
        if(server != null){ // Close Server
            logger.log(Level.INFO,"Closing Server");
            server.removeConnectionListener(listeners);
            server.removeMessageListener(listeners);
            // Close all client connections
            for(HostedConnection conn : server.getConnections()){
                try {
                    conn.close("SERVER_CLOSE");
                    Thread.sleep(1000);
                } catch (InterruptedException ex){
                    logger.log(Level.WARNING,"Can't close client connection");
                }
            }
            // Close server and remove server listeners
            server.close();
            server = null;
            logger.log(Level.INFO,"Server Closed");
        }
        if(client != null){ // Close Client
            logger.log(Level.INFO,"Closing Client");
            // Remove cliente listeners
            client.removeClientStateListener(listeners);
            client.removeErrorListener(listeners);
            client.removeMessageListener(listeners);
            // Close client
            client.close();
            client = null;
            logger.log(Level.INFO,"Client Closed");
        }
    }
    
    /**
     * Cancel current tries to connect
     */
    public void cancelConnetion(){
        if(network.isAlive())
            network.stop();
    }
    
    /**
     * Send move message through network
     * @param orig Move origin point
     * @param dest Move destination point
     */
    public void sendMove(Point orig, Point dest){
        int[] data = {orig.getFirst(),orig.getSecond(),dest.getFirst(),dest.getSecond()};
        sendData("REMOTE_MOVE",data);
    }
    
    /**
     * Send chat message through network
     * @param message chat message to send
     * @param name player name
     * @param color player color
     */
    public void sendMessage(String message, String name, String color){
        String[] data  = {message,name,color};
        sendData("CHAT_MESSAGE", data);
    }
    
    /**
     * Send game command through network
     * @param command game command string
     */
    public void sendCommand(String command){
        sendData(command,null);
    }
    
    /**
     * Send data through client or server
     * @param command message command
     * @param data message data
     */
    public void sendData(String command, Object data){
        if(server != null)
            server.broadcast(new DMessage(command, data));
        else
            client.send(new DMessage(command, data));
    }
    

    /**
     * Private Class networkListeners 
     * implements all necessary game network listeners 
     * to handle network events
     */
    private class networkListeners implements MessageListener, 
                ConnectionListener , ClientStateListener , ErrorListener {
        
        /**
         * When new message is received, the listener notifies
         * to observers the message
         * @param source not used
         * @param m message received
         */
        public void messageReceived(Object source, Message m){
            setChanged();
            notifyObservers(m);
        }

        /**
         * When a new connection adds to server, checks if server
         * already has a connection, then disconnect new client
         * or notifies observer the new client connection
         * @param server current network server
         * @param conn new client connection
         */
        public void connectionAdded(Server server, HostedConnection conn){
            if(server.getConnections().size()>1){
                conn.close("CONNECTION_LIMIT");
                return;
            }
            setChanged();
            notifyObservers("SERVER_CONNECT");
        }

        /**
         * When a connection is removed (closed) notifies observer
         * the loss of connection
         * @param server not used
         * @param conn not used
         */
        public void connectionRemoved(Server server, HostedConnection conn){
            setChanged();
            notifyObservers("LOST_CONNECTION");
        }

        /**
         * When client connecto to server, notifies observer
         * the new connection
         * @param c not used
         */
        public void clientConnected(Client c){
            setChanged();
            notifyObservers("CLIENT_CONNECT");
        }

        /**
         * When client has disconnected, notifies observer
         * the loss of connection (rejected in case of connection limit reached)
         * @param c not used
         * @param info connection close reason
         */
        public void clientDisconnected(Client c, DisconnectInfo info){
            if(info.reason.equals("SERVER_CLOSE")){
                setChanged();
                notifyObservers("CLIENT_DISCONNECTED");
                return;
            }
            if(info.reason.equals("CONNECTION_LIMIT")){
                setChanged();
                notifyObservers("CONNECTION_REJECTED");
            }
        }

        /**
         * Handle errors for connections lost and others
         * @param source not used
         * @param t not used
         */
        public void handleError(Object source, Throwable t){
            setChanged();
            notifyObservers("LOST_CONNECTION");
        }
    }
}