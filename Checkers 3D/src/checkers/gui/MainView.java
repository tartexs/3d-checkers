package checkers.gui;

import checkers.gui.panels.IDamasPanel;
import checkers.gui.panels.LateralPanel;
import checkers.gui.dialogs.EndGameDialog;
import checkers.gui.dialogs.ClientGameDialog;
import checkers.gui.dialogs.WaitingDialog;
import checkers.gui.dialogs.NewGameDialog;
import checkers.gui.dialogs.ServerGameDialog;
import checkers.gui.dialogs.OptionsDialog;
import checkers.gui.panels.panel2D.Panel2D;
import checkers.gui.panels.panel3D.Panel3D;
import checkers.model.Player;
import checkers.util.Point;
import checkers.util.Settings;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Observable;
import java.util.Observer;
import java.util.ResourceBundle;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.text.StyledDocument;


/**
 * Class MainView implements Observable standar view for game
 * uses board panel 3D or 2D according to saved settings
 * 
 * @author Cristian Tardivo
 */
public class MainView extends Observable {
    private JFrame window;
    private LateralPanel lateral_panel;
    private IDamasPanel panel;
    // Menu items
    private JMenuItem start;
    private JMenuItem stop;
    // Some Dialogs
    private WaitingDialog waiting;
    // Language Bundle
    private static final ResourceBundle lang = ResourceBundle.getBundle("checkers/util/lang");
    
    /**
     * Create Main View and Board 
     * Panel 3d or 2d according to settings
     */
    public MainView(){
        // Crete Panel
        panel = (Settings.getInstance().is3DView())? new Panel3D() : new Panel2D();
        // Create Main Frame
        createFrame();
    }
    
    /**
     * Add Observer to this view and the panel 2d/3d
     * @param o view Observer
     */
    @Override
     public synchronized void addObserver(Observer o){
        super.addObserver(o);
        panel.addObserver(o);
        lateral_panel.addObserver(o);
    }
        
    /**
     * Create main view frame
     */
    private void createFrame(){
        // View Frame
        window = new JFrame(lang.getString("CHECKERS_3D"));
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);        
        window.setMinimumSize(Settings.getInstance().getMinimunSize());
        window.setSize(Settings.getInstance().getSize());
        window.setPreferredSize(Settings.getInstance().getSize());
        window.setLocation(Settings.getInstance().getLocation());
        if(Settings.getInstance().is3DView()){
            window.setExtendedState(Settings.getInstance().isMaximized());
        } else {
            window.setExtendedState(JFrame.NORMAL);
            window.setResizable(false);
        }
        // Skin Set
        try {
            JFrame.setDefaultLookAndFeelDecorated(true);
            JDialog.setDefaultLookAndFeelDecorated(true);
            // Set System look and feel
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e){}
        window.setIconImage(Toolkit.getDefaultToolkit().getImage(MainView.class.getResource("/Interface/icon.png")));
        // Panels
        JPanel main_panel = new JPanel(new BorderLayout());
        main_panel.add(panel.getPanel(),BorderLayout.CENTER);        
        // Save Window Settings Before Close
        window.addWindowListener( new WindowAdapter(){
            @Override
            public void windowClosing(WindowEvent we){
                if(window.getExtendedState() != JFrame.MAXIMIZED_BOTH){
                    Settings.getInstance().setSize(window.getSize());
                    Settings.getInstance().setLocation(window.getLocation());
                }
                Settings.getInstance().setMaximized(window.getExtendedState());
                Settings.getInstance().saveSettings();
            }
        });
        // Frame Menubar
        JMenuBar menuBar = new JMenuBar();
        window.setJMenuBar(menuBar);
        // Game Menu
        JMenu game = new JMenu(lang.getString("GAME"));
        menuBar.add(game);
        // Edit Menu
        JMenu edit = new JMenu(lang.getString("EDIT"));
        menuBar.add(edit);
        //
        start = new JMenu(lang.getString("START"));
        start.setEnabled(true);
        game.add(start);
        //
        JMenuItem startLocal = new JMenuItem(lang.getString("NEW_LOCAL_GAME"));
        startLocal.setEnabled(true);
        start.add(startLocal);
        startLocal.addActionListener(new java.awt.event.ActionListener(){
            public void actionPerformed(ActionEvent e){
                showNewGame();
            }
        });
        //
        JMenuItem startRemote = new JMenuItem(lang.getString("NEW_REMOTE_GAME"));
        startRemote.setEnabled(true);
        start.add(startRemote);        
        startRemote.addActionListener(new java.awt.event.ActionListener(){
            public void actionPerformed(ActionEvent e){
                showNewRemoteGame();
            }
        });
        //
        JMenuItem conectRemote = new JMenuItem(lang.getString("CONNECT_TO_REMOTE_GAME"));
        conectRemote.setEnabled(true);
        start.add(conectRemote);        
        conectRemote.addActionListener(new java.awt.event.ActionListener(){
            public void actionPerformed(ActionEvent e){
                showConnectRemoteGame();
            }
        });
        //
        stop = new JMenuItem(lang.getString("STOP"));
        stop.setEnabled(false);
        game.add(stop);
        stop.addActionListener(new java.awt.event.ActionListener(){
            public void actionPerformed(ActionEvent e){
                showStopGameMenu();
            }
        });
        //
        JMenuItem quit = new JMenuItem(lang.getString("EXIT"));
        quit.setEnabled(true);
        game.add(quit);
        quit.addActionListener(new java.awt.event.ActionListener(){
            public void actionPerformed(ActionEvent e){
                window.dispose();
                System.exit(0);
            }
        });
        //
        JMenuItem options = new JMenuItem(lang.getString("PREFERENCES"));
        edit.add(options);
        options.addActionListener(new java.awt.event.ActionListener(){
            public void actionPerformed(ActionEvent e){
                showOptionsMenu();
            }
        });
        // Lateral information and chat panel
        lateral_panel = new LateralPanel();
        main_panel.add(lateral_panel,BorderLayout.EAST);
        // Show main Window
        window.add(main_panel);
        window.pack();
        window.setVisible(true);
    }
    
    /**
     * Update game mode info
     * @param playerA
     * @param playerB 
     */
    public void updateGameMode(Player playerA, Player playerB){
        String p1,p2;
        switch(playerA.getType()){
            case local: p1 = "P";break;
            case remote: p1 = "R";break;
            case artificial: p1 = "iA";break;
            default: p1 = "NN";
        }
        switch(playerB.getType()){
            case local: p2 = "P";break;
            case remote: p2 = "R";break;
            case artificial: p2 = "iA";break;
            default: p2 = "NN";
        }
        lateral_panel.game_mode.setText(p1+" vs "+p2); 
    }
    
    /**
     * Update player statics information in lateral panel
     * @param playerA
     * @param playerB 
     */
    public void updatePlayerInfo(Player playerA, Player playerB){
        // Player A
        if(playerA.isRed()){
            lateral_panel.player1_color.setText(lang.getString("RED"));
        } else {
            lateral_panel.player1_color.setText(lang.getString("BLACK"));
        }
        lateral_panel.player1_name.setText(playerA.getName());
        lateral_panel.player1_pieces.setText(playerA.getPieceNumber()+"");
        // Player B
        if(playerB.isRed()){
            lateral_panel.player2_color.setText(lang.getString("RED"));
        } else {
            lateral_panel.player2_color.setText(lang.getString("BLACK"));
        }
        lateral_panel.player2_name.setText(playerB.getName());
        lateral_panel.player2_pieces.setText(playerB.getPieceNumber()+"");
    }
    
    /**
     * Update game timer info
     * @param time 
     */
    public void updateTimer(String time){
        lateral_panel.game_time.setText(time);
    }

    /**
     * Hook between move piece in main view and board panel
     * @param orig Origin position
     * @param dest Destinity position
     */
    public void movePiece(Point orig, Point dest){
        panel.movePiece(orig, dest);
    }

    /**
     * Hook between clear selection in main view and board panel
     * @param points Points to clear
     */
    public void clearSelection(Point... points){
        panel.clearSelection(points);
    }

    /**
     * Hook between eat piece in main view and board panel
     * @param pos Piece to eat
     */
    public void eatPiece(Point pos){
        panel.eatPiece(pos);
    }
    
    /**
     * Hook between make queen in main view and board panel
     * @param pos Piece to convert
     */
    public void toQueen(Point pos){
        panel.toQueen(pos);
    }
    
    /**
     * hook between update settings in main view and board panel
     */
    public void updateSettings(){
        panel.updateSettings();
    }
    
    /**
     * hook between enable interaction in main view and board panel
     * @param status true or false
     */
    public void enableInteraction(boolean status){
        panel.enableInteraction(status);
    }

    /**
     * Change next turn information in lateral panel and board panel
     * @param player Current Game Player
     */
    public void setTurn(Player player){
        panel.setTurn(player);
        if(player.isRed()){
            lateral_panel.turno.setForeground(Color.RED);
        } else {
            lateral_panel.turno.setForeground(Color.BLACK);
        }
        lateral_panel.turno.setText(player.getName());
    }
    
    /**
     * Start New Game in view and board panel
     * @param player first player that move
     */
    public void startGame(Player player){
        // Start Game
        start.setEnabled(false);
        stop.setEnabled(true);
        panel.startBoard();
        setTurn(player);
    }
    
    /**
     * Stop current game
     */
    public void stopGame(){
        start.setEnabled(true);
        stop.setEnabled(false);
        panel.restartBoard();
    }
    
    /**
     * End Current game and show winner information
     * @param ganador 
     */
    public void endGame(Player ganador,Player playerA,Player playerB){
        EndGameDialog end = new EndGameDialog(window,false,ganador,playerA,playerB);
        end.setLocationRelativeTo(window);
        end.setVisible(true);
        end.addWindowListener(new WindowAdapter(){
            @Override
            public void windowClosed(WindowEvent e){
                stopGame();
            }
        });
    }
    
    /**
     * Show Warning Message
     * @param msj message to show
     */
    public void showWarning(String msj){        
        JOptionPane.showMessageDialog(window,msj,lang.getString("WARNING"),JOptionPane.WARNING_MESSAGE);
    }
    
    /**
     * Enable or disable game chat
     * @param status true or false
     */
    public void enableChat(boolean status){
        lateral_panel.send_butt.setEnabled(status);
        lateral_panel.chatMensaje.setEnabled(status);
        lateral_panel.chatMensaje.setEditable(status);
        lateral_panel.chatPanel.setEnabled(status);
    }
    
    /**
     * Retrieves last writted chat message
     * @return String with message to send
     */
    public String getChatMessage(){
        String text = lateral_panel.chatMensaje.getText();
        lateral_panel.chatMensaje.setText("");
        return text;
    }
    
    /**
     * Print new chat message
     * @param message Message to show
     * @param name Player name
     * @param color  Player Color
     */
    public void showChatMessage(String message, String name, String color){
        StyledDocument doc = lateral_panel.chatPanel.getStyledDocument();        
        try {
            if(color.equals("red"))
                doc.insertString(doc.getLength(), name+": ", doc.getStyle("Red Player"));
            else
                doc.insertString(doc.getLength(), name+": ", doc.getStyle("Black Player"));
            if(message.length() > name.length() + 28){
                String fline = message.substring(0, 28-name.length());
                doc.insertString(doc.getLength(),fline+"\n", doc.getStyle("Normal Style"));
                String fmessage = message.substring(fline.length(),message.length());
                for(String s:fmessage.split("(?<=\\G.{33})")){
                    doc.insertString(doc.getLength(),s+"\n", doc.getStyle("Normal Style"));
                }
            } else {
                doc.insertString(doc.getLength(),message+"\n", doc.getStyle("Normal Style"));
            }
            
        } catch (Exception ex){
            System.err.println("Add text to chat panel error!");
        }
    }
    
    /**
     * Show Waiting dialog
     * @param status allows to close lasted showed dialog
     */
    public void showWaitingDialog(boolean status){
        if(!status && waiting != null){
            waiting.dispose();
            return;
        }
        // Show waiting
        waiting = new WaitingDialog(window, true);
        waiting.setLocationRelativeTo(window);
        waiting.setVisible(true);
        if(!waiting.getResult()){
            setChanged();
            notifyObservers("CANCEL_CONNECTION");
        }
    }
    
    /**
     * Show stop current game dialog and notifies observers
     */
    private void showStopGameMenu(){
        if(JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(
                window,lang.getString("END_CURRENT_GAME"), lang.getString("CONFIRMATION"), JOptionPane.YES_NO_OPTION)){
            setChanged();
            notifyObservers("GAME_STOP"); 
        }
    }
    
    /**
     * Show start new game dialog and notifies observers
     */
    private void showNewGame(){
        NewGameDialog game = new NewGameDialog(window,true);
        game.setLocationRelativeTo(window);
        game.setVisible(true);
        if(game.getResult()){
            setChanged();
            notifyObservers("GAME_START");
        }
    }
    
    /**
     * Show start new remote game dialog and notifies observers
     */
    private void showNewRemoteGame(){
        ServerGameDialog remote = new ServerGameDialog(window, true);
        remote.setLocationRelativeTo(window);
        remote.setVisible(true);
        if(remote.getResult()){
            setChanged();
            notifyObservers("CREATE_REMOTE");
            showWaitingDialog(true);
        }
    }
    
    /**
     * Show connect to remote game dialog and notifies obserers
     */
    private void showConnectRemoteGame(){
        ClientGameDialog client = new ClientGameDialog(window, true);
        client.setLocationRelativeTo(window);
        client.setVisible(true);
        if(client.getResult()){
            setChanged();
            notifyObservers("CONNECT_REMOTE");
            showWaitingDialog(true);
        }
    }
        
    /**
     * Show change options dialog and notifies observers
     */
    private void showOptionsMenu(){
        OptionsDialog opt = new OptionsDialog(window,true);
        opt.setLocationRelativeTo(window);
        opt.setVisible(true);
        if(opt.getResult()){
            setChanged();
            notifyObservers("SETTINGS_CHANGED");
        }
    }
}