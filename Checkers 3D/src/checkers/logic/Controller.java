package checkers.logic;

import checkers.ia.iaPlayer;
import checkers.gui.MainView;
import checkers.common.Settings;
import checkers.model.Model;
import checkers.model.Player;
import checkers.network.DMessage;
import checkers.network.DNetwork;
import checkers.common.Cronometer;
import checkers.common.Pair;
import checkers.common.Point;
import java.util.Observable;
import java.util.Observer;
import java.util.ResourceBundle;

/**
 * Implements Checkers game controller like a observer
 * responds to change in view like request to move using
 * game logic to verify his validity and also responds
 * to changes in the model
 * 
 * @author Cristian Tardivo
 */
public class Controller implements Observer {
    private static final ResourceBundle lang = ResourceBundle.getBundle("checkers/common/lang");
    private iaPlayer iaPlayer;
    private DNetwork network;
    private Logic logic;
    private Model model;
    private MainView view;
    // Current Movement points
    private Point start;
    private Point end;
    
    /**
     * Create a new game Controller
     * @param newLogic game logic asociated
     * @param newView game view asociated
     */
    public Controller(Logic newLogic, MainView newView){
        logic = newLogic;
        view = newView;
        model = logic.getModel();
    }
    
    /**
     * Start controller Components
     */
    public void initController() {
        model.getCronometer().addObserver(this);
        model.addObserver(this);
        view.addObserver(this);
    }
    
    /**
     * Start new Local Game
     * between local players or
     * artificial players
     */
    private void startLocalGame(){
        // Get New Game Settings
        model.getPlayerA().setName(Settings.getNamePlayerA());
        model.getPlayerA().setColor(Settings.getColorPlayerA());
        model.getPlayerA().setType(Settings.getTypePlayerA());
        model.getPlayerB().setName(Settings.getNamePlayerB());
        model.getPlayerB().setColor(Settings.getColorPlayerB());
        model.getPlayerB().setType(Settings.getTypePlayerB());
        // Start Game
        startGame();
        // Create IA if Necessary
        if(model.getPlayerA().isIA() || model.getPlayerB().isIA()){
            iaPlayer = new iaPlayer(logic);
            iaPlayer.setDifficultLevel(Settings.getDifficult());
            iaPlayer.addObserver(this);
        }
        // First Player IA Move
        if(model.getCurrentPlayer().isIA())
            iaPlayer.computeNextMove();
    }

    /**
     * Start new remote game
     * between local player and remote player
     */
    private void startRemoteGame(){
        // Get New Game Settings
        model.getPlayerA().setName(Settings.getNamePlayerA());
        model.getPlayerA().setColor(Settings.getColorPlayerA());
        model.getPlayerA().setType(Settings.getTypePlayerA());
        model.getPlayerB().setName(Settings.getNamePlayerB());
        model.getPlayerB().setColor(Settings.getColorPlayerB());
        model.getPlayerB().setType(Settings.getTypePlayerB());
        // Start Game
        view.enableChat(true);
        startGame();
        // Clear Waiting Dialog
        view.showWaitingDialog(false);
    }
    
    /**
     * Starts Current game (start model)
     * and update view game information
     */
    private void startGame(){
        // Start Model
        model.startGame();
        // Update view
        view.updateGameMode(model.getPlayerA(),model.getPlayerB());
        view.updatePlayerInfo(model.getPlayerA(),model.getPlayerB());
        view.startGame(model.getCurrentPlayer());
        view.enableInteraction(model.getCurrentPlayer().isLocal());
    }
    
    /**
     * Stop Current Game
     * close network connections if is needed
     * stop and reset model
     * stop view game
     */
    private void stopGame(){
        // Close Network if necessary
        if(network != null){
            network.closeConnection();
            network = null;
        }
        // Abort iaPlayer if running
        if(iaPlayer != null){
            iaPlayer.stop();
            iaPlayer = null;
        }
        // Global stop
        view.enableInteraction(false);
        view.enableChat(false);
        view.stopGame();
        model.stopGame();
        model.resetModel();
    }
    
    /**
     * End current game
     * dont reset board in view
     * show view game end information (winner and others)
     * stop and reset model
     */
    private void endGame(){
        if(network != null) network.closeConnection();
        view.enableInteraction(false);
        view.endGame(logic.getWinner(),model.getPlayerA(),model.getPlayerB());
        model.stopGame();
        model.resetModel();
    }
    
    /**
     * Create network Server
     */
    private void createRemoteGame(){
        network = new DNetwork(true,this);
        network.initDNetwork();
    }
    
    /**
     * Create network Client
     */
    private void conectRemoteGame(){
        network = new DNetwork(false,this);
        network.initDNetwork();
    }
    
    /**
     * Computes a move between to points
     * @param orig origin point
     * @param dest destinity point
     */
    private void move(Point orig,Point dest){
        // external movements already validated
        if(model.getCurrentPlayer().isIA() || model.getCurrentPlayer().isRemote()){
            nextMove(!logic.movePiece(orig,dest));
        } else {
            // internal PvsX move
            if(logic.isValidMove(orig,dest)){
                // if rival player is remote, send the valid move
                if(model.getRivalPlayer().isRemote())
                    network.sendMove(orig,dest);
                nextMove(!logic.movePiece(orig,dest));
            } else {
                view.clearSelection(orig,dest);
                view.enableInteraction(true);
            }
        }
    }
    
    /**
     * Computes the next movement in game
     * @param change if change or not the turn
     */
    private void nextMove(boolean change){
        // if change player turn
        if(change)
            logic.changeTurn();
        // if game end
       if(logic.gameEnd()){
            endGame();
            return;
        }
        // if next turn player is IA
        if(model.getCurrentPlayer().isIA()){
            // get next move frome IA Logic
            iaPlayer.computeNextMove();
        } else {
            // next turn is local player
            view.enableInteraction(model.getCurrentPlayer().isLocal());
        }
    }
    
    /**
     * Observer update Method
     * @param obj observable object
     * @param arg notify command
     */
    @Override
    public void update(Observable obj, Object arg){
        // String Comand
        if(arg instanceof String){
            String command = (String) arg;
            // Update view cronometer
            if(command.equals("TIME_CHANGED")){
                view.updateTimer(((Cronometer)obj).getTime());
                return;
            }
            // Update view settings
            if(command.equals("SETTINGS_CHANGED")){
                view.updateSettings();
            }
            // Start new game
            if(command.equals("GAME_START")){
                startLocalGame();
                return;
            }
            // Stop Current game
            if(command.equals("GAME_STOP")){
                stopGame();
                return;
            }
            // Update view with model move
            if(command.equals("MODEL_MOVE")){
                // if current player is IA or Remote only moves
                if(model.getCurrentPlayer().isIA() || model.getCurrentPlayer().isRemote()){
                    view.movePiece(start,end);
                } else {
                    // if current player is local: clear, move and clear
                    view.clearSelection(start);
                    view.movePiece(start,end);
                    view.clearSelection(end);
                }
                return;
            }
            // Update view with model eat
            if(command.equals("MODEL_EAT")){
                view.eatPiece(logic.getPieceToEat(start,end));
                view.updatePlayerInfo(model.getPlayerA(),model.getPlayerB());
                return;
            }
            // Update view with model queen
            if(command.equals("MODEL_QUEEN")){
                view.toQueen(end);
                return;
            }
            // Update view with model changed turn
            if(command.equals("MODEL_CHANGE_TURN")){
                view.setTurn(model.getCurrentPlayer());
                return;
            }
            // Create Remote Server game
            if(command.equals("CREATE_REMOTE")){
                createRemoteGame();
                return;
            }
            // Connect Remote Game
            if(command.equals("CONNECT_REMOTE")){
                conectRemoteGame();
                return;
            }       
            // Send server data to client when server connects with client
            if(command.equals("SERVER_CONNECT")){
                network.sendData("SERVER_NAME", Settings.getNamePlayerA());
                network.sendData("SERVER_COLOR", Settings.getColorPlayerA().toString());
                network.sendCommand("START_REMOTE");
                return;
            }
            // Send client data to server when client connects with server
            if(command.equals("CLIENT_CONNECT")){
                network.sendData("CLIENT_NAME", Settings.getNamePlayerB());
                network.sendCommand("START_REMOTE");
                return;
            }
            // Cancel current try to connect/create network
            if(command.equals("CANCEL_CONNECTION")){
                network.cancelConnetion(); 
                network.closeConnection();
                return;
            }
            // Show Warning Can't create server
            if(command.equals("CANT_CRETE_SERVER")){
                view.showWaitingDialog(false);
                view.showWarning(lang.getString("CANT_CREATE_SERVER"));
                return;
            }           
            // Show Warning Can't connect to server/client
            if(command.equals("CANT_CONNECT")){
                view.showWaitingDialog(false);
                view.showWarning(lang.getString("CANT_CONNECT_SERVER"));
                return;
            }           
            // Show warning Lost Connection, Stop current game
            if(command.equals("LOST_CONNECTION")){
                view.showWarning(lang.getString("CONNECTION_LOST"));
                stopGame();
                return;
            }
            // Show warning connection rejected
            if(command.equals("CONNECTION_REJECTED")){
                view.showWarning(lang.getString("CONNECTION_REJECTED"));
                return;
            }
            // Show warning client disconnected and stop current game
            if(command.equals("CLIENT_DISCONNECTED")){
                view.showWarning(lang.getString("CONNECTION_LOST"));
                stopGame();
                return;
            }
            // Send Chat Message
            if(command.equals("SEND_CHAT_MESSAGE")){
                String text = view.getChatMessage();
                if(text.isEmpty()) return;
                String name = (model.getPlayerA().isLocal()? model.getPlayerA().getName():model.getPlayerB().getName());
                String color = (model.getPlayerA().isLocal()? model.getPlayerA().getColor():model.getPlayerB().getColor()).toString();
                view.showChatMessage(text,name,color);
                network.sendMessage(text, name, color);
            }
        }
        // Local Move Point Command (Player or IA)
        if(arg instanceof Pair){
            // Save movements points for others commands
            start = (Point)(((Pair)arg).getFirst());
            end = (Point)(((Pair)arg).getSecond());
            // Disable view interaction
            view.enableInteraction(false);
            // Call Controller Move
            move(start,end);
            // Clear last move points
            start = end = null;
            return;
        }
        // Networking Commands
        if(arg instanceof DMessage){
           DMessage message = (DMessage) arg;
           String command = message.getCommand();
           // Network Move
           if(command.equals("REMOTE_MOVE")){               
                // Save movements points for others commands
                start = new Point(((int[])message.getData())[0],((int[])message.getData())[1]);
                end = new Point(((int[])message.getData())[2],((int[])message.getData())[3]);
                // Call Controller Move
                move(start,end);
                // Clear last move points
                start = end = null;
                return;
           }
           // Get Server Name
           if(command.equals("SERVER_NAME")){
               Settings.setNamePlayerA((String)message.getData());
               return;
           }
           // Get Server Color
           if(command.equals("SERVER_COLOR")){
               Settings.setColorPlayerA(message.getData().equals("red")?Player.Color.red:Player.Color.black);
               Settings.setColorPlayerB(message.getData().equals("red")?Player.Color.black:Player.Color.red);
               return;
           }
           // Get Client Name
           if(command.equals("CLIENT_NAME")){
               Settings.setNamePlayerB((String)message.getData());
               return;
           }
           // Chat Message
           if(command.equals("CHAT_MESSAGE")){
               String text = ((String[])message.getData())[0];
               String name = ((String[])message.getData())[1];
               String color = ((String[])message.getData())[2];
               view.showChatMessage(text,name,color);
           }
           // Start Remote Game
           if(command.equals("START_REMOTE")){
               startRemoteGame();
           }
        }
    }
}