package checkers.gui.panels.panel2D;

import checkers.common.Sound;
import checkers.gui.panels.IBoardPanel;
import checkers.common.Sound.Sounds;
import checkers.common.Texture;
import checkers.common.Texture.Textures;
import checkers.model.Player;
import checkers.common.Pair;
import checkers.common.Point;
import checkers.common.Settings;
import java.awt.Cursor;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Observable;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Class Panel2D
 * Implements IDamasPanel with 2D board view,
 * sound playback, mouse listeners and others
 * 
 * @author Cristian Tardivo
 */
public class Panel2D  extends Observable implements IBoardPanel {
    // Main Panel
    private JPanel mainPanel;
    // Cells Matrix
    private BoardCell[][] matrix;
    // Cells Cursors
    private Cursor handCursor = new Cursor(Cursor.HAND_CURSOR);
    private Cursor defaultCursor = new Cursor(Cursor.DEFAULT_CURSOR);
    // Game Sounds
    private Sound moveSound = new Sound(Sounds.move_sound);
    private Sound wrongSound = new Sound(Sounds.wrong_sound);
    private Sound eatSound = new Sound(Sounds.eat_sound);
    private Sound queenSound = new Sound(Sounds.queen_sound);
    // Texture sizes (Scale)
    private int squareSize = 70;
    private int pieceSize = squareSize - 10;
    private int selectSize = squareSize - 2;
    private int boardSize = squareSize * 8 + 10;
    // General Texture
    private Texture select_texture = new Texture(Textures.selection,selectSize);
    private Texture dark_square_text = new Texture(Textures.dark_square,squareSize);
    private Texture light_square_text = new Texture(Textures.light_square,squareSize);
    private Texture turn_texture = new Texture(Texture.Textures.turn,boardSize,10);
    private Texture turn_empty_text = new Texture(Texture.Textures.clear,boardSize,10);
    // Some Status
    private boolean interaction = false;
    private boolean playSound = Settings.getAudioEnable();
    // Piece Moving Helpers
    private Point move_start;
    private Point move_end;
    // Current Turn label
    private JLabel black_turn;
    private JLabel red_turn;
    
    /**
     * Create base view2D JPanel
     * @return 
     */
    @Override
    public JPanel getPanel(){
        mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout());
        //
        BackgroundPanel board = new BackgroundPanel();
        board.setBackgroundImage(new Texture(Textures.board,boardSize).getImage());
        //
        black_turn = new JLabel(turn_empty_text);
        mainPanel.add(black_turn);
        //
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridy = 1;
        //
        board.add(boarPanel());
        mainPanel.add(board,gridBagConstraints);
        //
        GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
        gridBagConstraints2.gridy = 2;
        red_turn = new JLabel(turn_empty_text);
        mainPanel.add(red_turn,gridBagConstraints2);
        //
        return mainPanel;
    }
    
    /**
     * Create Board Panel and cells
     * @return 
     */
    private JPanel boarPanel(){
        JPanel board = new JPanel();
        board.setLayout(new GridLayout(8,8,0,0));
        matrix = new BoardCell[8][8];
        // Cells Click listener
        MouseListener mouseList = new MouseAdapter(){
            @Override
            public void mouseClicked(MouseEvent evt){selecPiece(evt);}
        };
        //
        for (int row = 0; row < 8; row++){
            for (int col = 0; col < 8; col++){
                BoardCell cell = new BoardCell(row,col);                
                cell.addMouseListener(mouseList);
                matrix[row][col] = cell;
                if ((row + col) % 2 == 0) // light square
                    cell.setSquare(light_square_text);
                else // dark square
                    cell.setSquare(dark_square_text);
                board.add(cell);
            }
        }
        return board;
    }
    
    /**
     * Start board (load pieces)
     */
    @Override
    public void startBoard(){
        for (int row = 0; row < 8; row++){
            for (int col = 0; col < 8; col++){                
                if((row + col) % 2 != 0 && (row <3 || row > 4)){
                    Texture piece;
                    if (row < 3)
                        piece = new Texture(Textures.black,pieceSize);
                    else
                        piece = new Texture(Textures.red,pieceSize);
                    matrix[row][col].setPiece(piece);
                }
            }
        }
    }

    /**
     * Reset board (remove pieces and selection)
     */
    @Override
    public void restartBoard(){
        for (int row = 0; row < 8; row++)
            for (int col = 0; col < 8; col++){
                matrix[row][col].clearPiece();
                matrix[row][col].clearSelection();
            }
        red_turn.setIcon(turn_empty_text);
        black_turn.setIcon(turn_empty_text);
    }

    /**
     * Enable or disable board interaction (mouse clicks)
     * @param status 
     */
    @Override
    public void enableInteraction(boolean status){
        interaction = status;
        for (int row = 0; row < 8; row++){
            for (int col = 0; col < 8; col++){  
                if(status)
                    matrix[row][col].setCursor(handCursor);
                else
                    matrix[row][col].setCursor(defaultCursor);
            }
        }
    }
    
    /**
     * Set current player turn
     * @param jugador 
     */
    @Override
    public void setTurn(Player jugador){
        if(jugador.isBlack()){
            black_turn.setIcon(turn_texture);
            red_turn.setIcon(turn_empty_text);
        } else {
            red_turn.setIcon(turn_texture);
            black_turn.setIcon(turn_empty_text);
        }
    }

    /**
     * Move piece inside the board (swap cells values)
     * @param orig
     * @param dest 
     */
    @Override
    public void movePiece(Point orig, Point dest){
        if(playSound)
            moveSound.play();
        Texture piece = matrix[orig.getFirst()][orig.getSecond()].getPiece();
        matrix[orig.getFirst()][orig.getSecond()].clearPiece();
        matrix[dest.getFirst()][dest.getSecond()].setPiece(piece);
    }

    /**
     * Eat piece (remove piece from board)
     * @param pos 
     */
    @Override
    public void eatPiece(Point pos){
        if(playSound)
            eatSound.play();
        matrix[pos.getFirst()][pos.getSecond()].clearPiece();
    }
    
    /**
     * Make a piece Queen (change piece texture to queen)
     * @param pos 
     */
    @Override
    public void toQueen(Point pos){
        if(playSound)
            queenSound.play();
        Texture piece = matrix[pos.getFirst()][pos.getSecond()].getPiece();
        if(piece.getName() == Textures.red)
            matrix[pos.getFirst()][pos.getSecond()].setPiece(new Texture(Textures.red_queen,pieceSize));
        else
           matrix[pos.getFirst()][pos.getSecond()].setPiece(new Texture(Textures.black_queen,pieceSize)); 
    }

    /**
     * Clear cells selection
     * @param points 
     */
    @Override
    public void clearSelection(Point... points){
        if(playSound && points.length > 1)
                wrongSound.play();
        for(Point p: points)
            matrix[p.getFirst()][p.getSecond()].clearSelection();
    }

    /**
     * Update panel settings
     */
    @Override
    public void updateSettings(){        
        playSound = Settings.getAudioEnable();
    }
    
    /**
     * Select / Deslect pieces or empty cells
     * Send move to observer when it's necesary
     * @param evt 
     */
    private void selecPiece(MouseEvent evt){
        // Interaction disabled ignore mouse events
        if(!interaction)
            return;
        // Deselect Piece/Square button mask (right mouse button)
        if((evt.getModifiers() & InputEvent.BUTTON3_MASK) == InputEvent.BUTTON3_MASK){
            for (int row = 0; row < 8; row++){
                for (int col = 0; col < 8; col++){
                    matrix[row][col].clearSelection();
                }
            }
            move_start = null; move_end = null;
            return;
        }
        // Select Piece/Square button mask (left mouse button)
        if((evt.getModifiers() & InputEvent.BUTTON1_MASK) == InputEvent.BUTTON1_MASK){
            if(evt.getSource() instanceof BoardCell){
                BoardCell selected = (BoardCell)evt.getSource();
                // Avoids select light squares
                if(selected.getSquare().getName() == Textures.light_square)
                    return;
                // Avoids select square before piece
                if(selected.getPiece() == null && move_start == null)
                    return;
                // save move points
                if(move_start == null)
                    move_start = new Point(selected.getRow(),selected.getCol());
                else
                    move_end = new Point(selected.getRow(),selected.getCol());
                // Avoid select the same piece for start and end
                if(move_start.equals(move_end))
                    move_end = null;
                // set selected
                selected.setSelection(select_texture);
                // notifies observer
                if(move_start != null && move_end != null){
                    setChanged();
                    notifyObservers(new Pair(move_start,move_end));
                    move_start = null; move_end = null;
                }
            }
        }
    }
}
