package checkers.gui.panels.panel3D;


import com.jme3.app.SimpleApplication;
import com.jme3.audio.AudioNode;
import com.jme3.bounding.BoundingBox;
import com.jme3.cinematic.MotionPath;
import com.jme3.cinematic.MotionPathListener;
import com.jme3.cinematic.events.CinematicEvent;
import com.jme3.cinematic.events.CinematicEventListener;
import com.jme3.cinematic.events.MotionEvent;
import com.jme3.collision.CollisionResults;
import com.jme3.input.ChaseCamera;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.ViewPort;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.CameraNode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.CameraControl;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Quad;
import com.jme3.shadow.DirectionalLightShadowRenderer;
import com.jme3.shadow.EdgeFilteringMode;
import com.jme3.texture.Texture;
import com.jme3.ui.Picture;
import com.jme3.font.BitmapText;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.system.JmeCanvasContext;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.concurrent.Callable;
//
import checkers.common.Point;
import checkers.common.Settings;




/**
 * Class AbstracPanel3D
 * Implements 3D Panel (board view) using jMonkey SimpleApplication
 * defines methods to work with board pieces and world Camera type and
 * position
 * 
 * Creates 3d World, Lights, Cameras, Table, Board, Pieces and others
 * 
 * @author Cristian Tardivo
 */
public abstract class AbstractPanel3D extends SimpleApplication implements AnalogListener, ActionListener {
    // Board Square Matrix
    private Geometry[][] board_squares;
    // Board Pieces Matrix
    private Spatial[][] board_pieces;
    // Red Eated Pieces Index
    private int red_index = 0;
    // Black Eated Pieces Index
    private int black_index = 0;
    // Game Board
    private Spatial board;
    // Game Table
    private Geometry table;
    // Geometric Game Center
    private Geometry center;    
    // Board Square Node
    private Node square_node = new Node("squares");
    // Pieces Node
    private Node piece_node = new Node("pieces");
    // Square Size (Sides and Height)
    private float squareSize = 5f;
    private float squareHeight = 0.2f;
    // Global Model Scales
    private float modelScale = 1f;
    // Pieces Height
    private float pieceHeight;
    // Board Height
    private float boardHeight;
    // Shadows Map Size
    private int SHADOWMAP_SIZE = Settings.getShadowsMapSize();
    // Camera Data
    // Las Camera Position
    private Vector3f lastCamPos;
    // Is Current Position top view
    private boolean topView;
    // Chase Camera
    private ChaseCamera chaseCam;
    // Camera Node
    private CameraNode camNode;
    // Basic HUD Text
    private BitmapText hudText;
    private Geometry darkHud;
    private long hudDelay;
    private boolean showHud;
    // Enable Sound
    private boolean playSounds;
    // Enable Auto Camera Rotation
    protected boolean autoRotation;
    // Enable User Interaction
    protected boolean interaction = false;
    // Camera Type's enumeration
    public enum cameras {PLAYER_1,PLAYER_2,GLOBAL,FREE};
    // Last Camera Type
    protected cameras lastCamera;
    // Game Textures
    // Light Square Texture
    private Texture light_texture;
    // Dark Square Texture
    private Texture dark_texture;
    // Selected Dark Square Texture
    private Texture select_dark_texture;
    // Board Texture
    private Texture board_texture;
    // Table Texture
    private Texture table_texture;
    // Pieces Textures
    private Texture red_texture;
    private Texture black_texture;
    private Texture red_queen_texture;
    private Texture black_queen_texture;
    // Background Image
    private Picture background;
    // Background Image Size
    private float background_width = 1920; // consistent with real image size
    private float background_height = 1200;
    // Game Sounds
    // Move Sound
    private AudioNode move_audio;
    // Eat Sound
    private AudioNode eat_audio;
    // Make Queen Sound
    private AudioNode queen_audio;
    // Invalid Move Sound
    private AudioNode invalid_move_audio;
    // Shadow Render
    private DirectionalLightShadowRenderer dlsr;
    // Comand Controller
    private Panel3DController panelControl;
    // Piece Move (selection) helper
    private Spatial piece_orig;
    private Geometry square_dest;
    // Language Bundle
    private static final ResourceBundle lang = ResourceBundle.getBundle("checkers/common/lang");
    
    
    /**
     * Starts 3DPanel (jMonkey Simple Application)
     */
    @Override
    public void simpleInitApp(){
        // Load Textures
        loadTextres();
        // Create Ambient
        createAmbient();
        // Load base Table
        createTable();
        // Load Base Board
        createBoard();
        // Load board squares
        loadSquares();
        // Load pieces
        loadPieces();
        // Load Sounds
        loadSounds();
        // Create Camera
        createCamera();
        // Create basic game hud
        createHud();
        // Load Settings
        updateSettings();
    }
    
    /**
     * Load Game Sounds
     */
    private void loadSounds(){
        // piece sound trigger on move
        move_audio = new AudioNode(assetManager, "Sounds/move_sound.wav", false);
        move_audio.setPositional(false);
        move_audio.setLooping(false);
        move_audio.setVolume(2);
        rootNode.attachChild(move_audio);
        // eat sound trigger on eat
        eat_audio = new AudioNode(assetManager, "Sounds/eat_sound.wav", false);
        eat_audio.setPositional(false);
        eat_audio.setLooping(false);
        eat_audio.setVolume(2);
        rootNode.attachChild(eat_audio);
        // make queen audio
        queen_audio = new AudioNode(assetManager, "Sounds/queen_sound.wav", false);
        queen_audio.setPositional(false);
        queen_audio.setLooping(false);
        queen_audio.setVolume(2);
        rootNode.attachChild(queen_audio);
        // invalid move sound
        invalid_move_audio = new AudioNode(assetManager, "Sounds/wrong_sound.wav", false);
        invalid_move_audio.setPositional(false);
        invalid_move_audio.setLooping(false);
        invalid_move_audio.setVolume(2);
        rootNode.attachChild(invalid_move_audio);
    }
    
    /**
     * Load Game Textures
     */
    private void loadTextres(){
        // Load Textures
        light_texture = assetManager.loadTexture("Textures/light_square.png");
        dark_texture = assetManager.loadTexture("Textures/dark_square.png");
        select_dark_texture = assetManager.loadTexture("Textures/dark_square_selected.png");
        board_texture = assetManager.loadTexture("Textures/board.png");
        table_texture = assetManager.loadTexture("Textures/table.png");
        red_texture = assetManager.loadTexture("Textures/red.png");
        black_texture = assetManager.loadTexture("Textures/black.png");
        red_queen_texture = assetManager.loadTexture("Textures/red_queen.png");
        black_queen_texture = assetManager.loadTexture("Textures/black_queen_rot.png");
        // Background Image
        background = new Picture("background");
        background.setImage(assetManager, "Interface/background.jpg", false);
        background.setWidth(background_width);
        background.setHeight(background_height);
        background.setPosition(0, 0);
    }
   
    /**
     * Creates Game Global Ambient
     * Light, Shadows, Background, Effects
     */
    private void createAmbient(){
        // Background color
        viewPort.setBackgroundColor(ColorRGBA.LightGray);
        // Create Ambient Ligth
        DirectionalLight ambient_ligth = new DirectionalLight();
        ambient_ligth.setDirection(new Vector3f(0.05f, -1f, 0f).normalizeLocal());
        ambient_ligth.setColor(ColorRGBA.White.mult(2.0f));
        rootNode.addLight(ambient_ligth);
        // Shadows
        dlsr = new DirectionalLightShadowRenderer(assetManager, SHADOWMAP_SIZE, 1);
        dlsr.setLight(ambient_ligth);
        dlsr.setEnabledStabilization(true);
        dlsr.setShadowIntensity(.5f);
        dlsr.setLambda(.5f);
        dlsr.setEdgeFilteringMode(EdgeFilteringMode.PCF4);
        viewPort.addProcessor(dlsr);
        ViewPort pv = renderManager.createPreView("background", cam);
        pv.setClearFlags(true, true, true);
        pv.attachScene(background);
        viewPort.setClearFlags(false, true, true);
        background.updateGeometricState();
    }
    
    /**
     * Create Game Table
     */
    private void createTable(){
        Box box = new Box(61f,1f,61f);
        table = new Geometry("Table", box);
        table.setShadowMode(ShadowMode.Inherit);
        Material mat_table = new Material(assetManager,"Common/MatDefs/Light/Lighting.j3md");
        mat_table.setTexture("DiffuseMap",table_texture);
        mat_table.setTexture("SpecularMap",table_texture);
        table.setMaterial(mat_table);
        table.setLocalTranslation(new Vector3f(40f,-2.5f,40f));
        rootNode.attachChild(table);
    }
    
    /**
     * Create Game Board
     */
    private void createBoard(){
        board = assetManager.loadModel("Models/board.j3o");
        Material mat_board = new Material(assetManager,"Common/MatDefs/Misc/Unshaded.j3md");
        mat_board.setTexture("ColorMap",board_texture);
        board.setMaterial(mat_board);
        board.scale(modelScale, modelScale, modelScale);
        board.setShadowMode(ShadowMode.Inherit);
        boardHeight = ((BoundingBox)board.getWorldBound()).getYExtent();
        rootNode.attachChild(board);
    }
    
    /**
     * Create Game Camera
     */
    private void createCamera(){
        // Create a center box helper for camera chase and position
        Box box = new Box(1f,0f,1f);
        center = new Geometry("center", box);
        Material mat_board = new Material(assetManager,"Common/MatDefs/Misc/Unshaded.j3md");
        mat_board.setTexture("ColorMap",board_texture);
        center.setMaterial(mat_board);
        center.setLocalTranslation(new Vector3f(40.0f,0f,40.0f));
        rootNode.attachChild(center);
        // Setting Camera
        flyCam.setEnabled(false);
        chaseCam = new ChaseCamera(cam, center, inputManager);
        chaseCam.setZoomSensitivity(10.0f);
        chaseCam.setMinDistance(80f);
        chaseCam.setMaxDistance(150f);
        chaseCam.setDefaultDistance(110f);
        chaseCam.setToggleRotationTrigger(new MouseButtonTrigger(MouseInput.BUTTON_MIDDLE));
        chaseCam.setDefaultVerticalRotation(0.8f);
        chaseCam.setDefaultHorizontalRotation(-(float) Math.PI*0.5f);
        chaseCam.setSmoothMotion(true);
        // Configure camera node
        camNode = new CameraNode("MotionCam", cam);
        camNode.setControlDir(CameraControl.ControlDirection.SpatialToCamera);
        camNode.setLocalTranslation(cam.getLocation());
        camNode.setLocalRotation(cam.getRotation());
        camNode.setEnabled(false);
        rootNode.attachChild(camNode);
        // Camera Info
        lastCamPos = new Vector3f(40f, 80f, -40f);
        lastCamera = cameras.PLAYER_2;
        topView = false;
    }
    
    /**
     * Create basic game HUD text
     */
    private void createHud(){
        hudText = new BitmapText(guiFont, false);
        hudText.setSize(guiFont.getCharSet().getRenderedSize());
        hudText.setColor(ColorRGBA.White);
        hudText.setText("");
        //
        darkHud = new Geometry("HudBackGround", new Quad(1, hudText.getLineHeight()));
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", new ColorRGBA(0,0,0,0.5f));
        mat.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
        darkHud.setMaterial(mat);
        showHud = false;
    }
    
    /**
     * Create Board Squares
     */
    private void loadSquares(){
        // Board Squares
        board_squares = new Geometry[9][9];
        rootNode.attachChild(square_node);
        // Create Squares for board
        for (int row = 0; row < 8; row++){
            for (int col = 0; col < 8; col++){
                // Position and size
                float x = col * squareSize + squareSize / 2;
                float z = row * squareSize + squareSize / 2;
                float rot = -1.5707964f; //-90.0f * (float)Math.PI / 180.0f;
                // Create Square
                Box box = new Box(squareSize,squareHeight,squareSize);
                Geometry square = new Geometry("Square", box);
                square.setShadowMode(ShadowMode.Inherit);
                // Set Position
                square.setLocalTranslation(new Vector3f(x * 2, 0, z * 2));
                // Create Material
                Material mat_square = new Material(assetManager,"Common/MatDefs/Misc/Unshaded.j3md");
                if ((row + col) % 2 == 0){ // light square
                    mat_square.setTexture("ColorMap",light_texture);
                    square.rotate(0.0f, -rot, 0.0f); // break pattern
                } else { // dark square
                    mat_square.setTexture("ColorMap",dark_texture);
                }
                // Set Material
                square.setMaterial(mat_square);
                // Set Square data
                square.setUserData("row_num", row);
                square.setUserData("col_num", col);
                // Add Square
                board_squares[row][col] = square;
                square_node.attachChild(square);
                rootNode.attachChild(square);
            }
        }
    }
    
    /**
     * Create Game Pieces (Standar Checkers Piece)
     */
    private void loadPieces(){
        // Board of Pieces
        board_pieces = new Spatial[9][9];
        // Materials       
        Material mat_piece_black = new Material(assetManager,"Common/MatDefs/Misc/ColoredTextured.j3md");
        mat_piece_black.setTexture("ColorMap",black_texture);
        Material mat_piece_red = new Material(assetManager,"Common/MatDefs/Misc/ColoredTextured.j3md");
        mat_piece_red.setTexture("ColorMap",red_texture);
        // Piece
        Spatial piece = assetManager.loadModel("Models/piece.j3o");
        piece.scale(modelScale, modelScale, modelScale);
        pieceHeight = ((BoundingBox)piece.getWorldBound()).getYExtent();
        // Create and positioning pieces
        for (int row = 0; row < 8; row++){
            for (int col = 0; col < 8; col++){
                if((row + col) % 2 != 0 && (row <3 || row > 4)){
                    // Create Piece
                    piece = assetManager.loadModel("Models/piece.j3o");
                    piece.scale(modelScale, modelScale, modelScale);
                    piece.setShadowMode(ShadowMode.Inherit);
                    // Calculate Piece Position
                    float x = col * squareSize + squareSize / 2;
                    float z = row * squareSize + squareSize / 2;
                    float y = pieceHeight + squareHeight;
                    piece.setLocalTranslation(new Vector3f(x * 2, y, z * 2));
                    // Set piece Texture
                    if (row < 3){
                        piece.setMaterial(mat_piece_black);
                        piece.setUserData("color","black");
                    } else {
                        piece.setMaterial(mat_piece_red);
                        piece.setUserData("color","red");
                    }
                    // Set Piece data
                    piece.setName("Piece");
                    piece.setUserData("piece_eated", false);
                    piece.setUserData("row_num", row);
                    piece.setUserData("col_num", col);
                    // Add Piece
                    board_pieces[row][col] = piece;
                    piece_node.attachChild(piece);
                }
            }
        }
    }
    
    /**
     * Initialize Game Keys
     */
    private void initKeys(){
        inputManager.setCursorVisible(true);
        inputManager.addMapping("top_cam", new KeyTrigger(KeyInput.KEY_S));
        inputManager.addMapping("free_cam", new KeyTrigger(KeyInput.KEY_F));
        inputManager.addMapping("rotate_cam", new KeyTrigger(KeyInput.KEY_SPACE));
        inputManager.addMapping("pick_target", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        inputManager.addMapping("clear_target", new MouseButtonTrigger(MouseInput.BUTTON_RIGHT));
        inputManager.addMapping("auto_rotation", new KeyTrigger(KeyInput.KEY_A));
        inputManager.addListener(this, "top_cam");
        inputManager.addListener(this, "free_cam");
        inputManager.addListener(this, "rotate_cam");
        inputManager.addListener(this, "pick_target");
        inputManager.addListener(this, "clear_target");
        inputManager.addListener(this, "auto_rotation");
    }
    
    /**
     * Change Camera Position
     * @param position new position
     */
    protected void setPosCamera(cameras position){
        // Avoids rotate camera when is correctly positioned
        if(lastCamera == position) return;
        // Camera Motion Path
        MotionPath path = new MotionPath();
        path.setCycle(false);
        // Random Left or Right Rotation
        Random rnd = new Random();
        int dir = rnd.nextInt(3) * 40;
        // Switch Camera Position
        switch(position){
            case PLAYER_1:  // Camera points to Player 1
                            path.addWayPoint(lastCamPos.clone());
                            if(!topView) path.addWayPoint(new Vector3f(dir, 40f, 43.5f));
                            lastCamPos = new Vector3f(40, 80, 114);
                            path.addWayPoint(lastCamPos);
                            topView = false;
                            lastCamera = cameras.PLAYER_1;
                            break;
            case PLAYER_2:  // Camera point to Player 2
                            path.addWayPoint(lastCamPos.clone());
                            if(!topView) path.addWayPoint(new Vector3f(dir, 40f, 20f));
                            lastCamPos = new Vector3f(40, 80, -34f);
                            path.addWayPoint(lastCamPos);
                            topView = false;
                            lastCamera = cameras.PLAYER_2;
                            break;
            case GLOBAL:    // Camera view board from top
                            path.addWayPoint(lastCamPos.clone());
                            lastCamPos = new Vector3f(40, 100, 40);
                            path.addWayPoint(lastCamPos);
                            topView = true;
                            lastCamera = cameras.GLOBAL;
                            break;
            case FREE:      // Free camera Position
                            camNode.setEnabled(false);
                            if(lastCamera == cameras.PLAYER_1){
                                chaseCam.setDefaultVerticalRotation(0.8f);
                                chaseCam.setDefaultHorizontalRotation(-(float) Math.PI*1.5f);
                                chaseCam.setDefaultDistance(110f);
                            }
                            if(lastCamera == cameras.PLAYER_2){
                                chaseCam.setDefaultVerticalRotation(0.8f);
                                chaseCam.setDefaultHorizontalRotation(-(float) Math.PI*0.5f);
                                chaseCam.setDefaultDistance(110f);
                            }
                            if(lastCamera == cameras.GLOBAL){
                                chaseCam.setDefaultVerticalRotation((float)Math.PI *0.5f);
                                chaseCam.setDefaultDistance(110f);
                            }
                            chaseCam.setEnabled(true);
                            lastCamera = cameras.FREE;
                            lastCamPos = new Vector3f(40, 99, 40);
                            return;
        }
        // Path Curve Tension (Circular)
        path.setCurveTension(0.50f);
        // Camera Motion
        camNode.setEnabled(true);
        MotionEvent cameraMotionControl = new MotionEvent(camNode, path);
        // Camera Motion Listeners
        cameraMotionControl.addListener(panelControl);
        // Motion Data
        cameraMotionControl.setLookAt(center.getLocalTranslation(),Vector3f.UNIT_Y);
        cameraMotionControl.setDirectionType(MotionEvent.Direction.LookAt);
        cameraMotionControl.setSpeed(10f);
        // Play Motion
        cameraMotionControl.play();
    }
    
    /**
     * Move Piece between two squares
     * @param orig Origin Piece
     * @param dest Destinity Square
     */
    protected void movePiece(Point orig, Point dest){
        // Move piece in board_pieces
        final int rowOrig = orig.getFirst();
        final int rowDest = dest.getFirst();
        final int colOrig = orig.getSecond();
        final int colDest = dest.getSecond();
        //
        Spatial piece = board_pieces[rowOrig][colOrig];
        Geometry square = board_squares[rowDest][colDest];
        //
        board_pieces[rowDest][colDest] = piece;
        board_pieces[rowOrig][colOrig] = null;
        // Set New Piece Pos
        piece.setUserData("row_num", rowDest);
        piece.setUserData("col_num", colDest);
        // Start Post Mid pos and end Pos
        Vector3f origin = piece.getLocalTranslation();
        Vector3f origin_up = new Vector3f(origin.x, origin.y + 0.5f, origin.z);
        Vector3f destiny = square.getLocalTranslation().add(new Vector3f(0f, pieceHeight + squareHeight, 0f));
        Vector3f destiny_up = new Vector3f(destiny.x, destiny.y + 0.5f, destiny.z);
        Vector3f height = new Vector3f((origin.x + destiny.x) / 2, origin.y + 5f, (origin.z + destiny.z) / 2);
        // Motion Path
        MotionPath motionPath = new MotionPath();
        motionPath.addWayPoint(origin);
        motionPath.addWayPoint(origin_up);
        motionPath.addWayPoint(height);
        motionPath.addWayPoint(destiny_up);
        motionPath.addWayPoint(destiny);
        motionPath.setCycle(false);
        motionPath.setCurveTension(0.50f);
        // Motion Evenet
        MotionEvent motionEvent = new MotionEvent(piece,motionPath);
        // Motion Listener for Play sound and others
        motionEvent.addListener(new CinematicEventListener(){
            @Override public void onPlay(CinematicEvent cinematic){}
            @Override public void onPause(CinematicEvent cinematic){}
            @Override public void onStop(CinematicEvent cinematic){
                if(playSounds) 
                    playSound(move_audio);
            }
        });
        motionEvent.addListener(panelControl);
        // Motion Data
        motionEvent.setDirectionType(MotionEvent.Direction.PathAndRotation);
        motionEvent.setInitialDuration(15f);
        motionEvent.setSpeed(20f);
        // Motion Play
        motionEvent.play();
    }
    
    /**
     * Convert Piece to Queen Piece (animation && change texture)
     * @param pos Piece to Convert
     */
    protected void toQueen(Point pos){
        // Get piece Data
        final Spatial piece = board_pieces[pos.getFirst()][pos.getSecond()];
        Vector3f origin = piece.getLocalTranslation();
        Vector3f height = new Vector3f(origin.x, origin.y + 3f, origin.z);
        // Motion Path
        MotionPath motionPath = new MotionPath();
        motionPath.addWayPoint(origin);
        motionPath.addWayPoint(height);
        motionPath.addWayPoint(origin);
        motionPath.setCycle(false);
        motionPath.setCurveTension(.5f);
        motionPath.addListener(new MotionPathListener(){
            @Override
            public void onWayPointReach(MotionEvent motionControl, int wayPointIndex){
                if(wayPointIndex == 1){
                    enqueue(new Callable<Void>(){
                        @Override
                        public Void call() throws Exception {
                            piece.setLocalRotation(new Quaternion().fromAngleNormalAxis(-FastMath.PI, Vector3f.UNIT_X));
                            String color = piece.getUserData("color");
                            if(color.equals("red")){
                                Material mat_piece_red = new Material(assetManager, "Common/MatDefs/Misc/ColoredTextured.j3md");
                                mat_piece_red.setTexture("ColorMap", red_queen_texture);
                                piece.setMaterial(mat_piece_red);
                            } else {
                                Material mat_piece_black = new Material(assetManager, "Common/MatDefs/Misc/ColoredTextured.j3md");
                                mat_piece_black.setTexture("ColorMap", black_queen_texture);
                                piece.setMaterial(mat_piece_black);
                            }
                            return null;
                        }
                    });
                }
            }
        });
        // Motion Event
        MotionEvent motionEvent = new MotionEvent(piece,motionPath);
        // Motion Listener
        motionEvent.addListener(new CinematicEventListener(){
            @Override public void onPlay(CinematicEvent cinematic){
                if(playSounds) 
                    playSound(queen_audio);
            }
            @Override public void onPause(CinematicEvent cinematic){}
            @Override public void onStop(CinematicEvent cinematic){}
        });
        motionEvent.addListener(panelControl);
        // Motion Data
        motionEvent.setDirectionType(MotionEvent.Direction.PathAndRotation);
        motionEvent.setInitialDuration(15f);
        motionEvent.setSpeed(15f);
        motionEvent.play();
    }
    
    /**
     * Eat Piece in Board (eat animation)
     * @param pos Piece to Eat
     */
    protected void eatPiece(Point pos){
        // Clear Piece Board
        Spatial piece = board_pieces[pos.getFirst()][pos.getSecond()];
        int row = piece.getUserData("row_num");
        int col = piece.getUserData("col_num");
        board_pieces[row][col] = null;
        piece.setUserData("piece_eated", true);
        // Compute Detinity
        Vector3f origin = piece.getLocalTranslation();
        Vector3f destiny;
        String color = piece.getUserData("color");
        if(color.equals("red")){
            Vector3f aux = board_squares[red_index % 4][7].getLocalTranslation();
            destiny = new Vector3f(aux.x + squareSize * 2.5f + 1f, aux.y + (1.2f * (red_index / 4)), aux.z);
            red_index++;
        } else {
            Vector3f aux = board_squares[7 - (black_index % 4)][0].getLocalTranslation();
            destiny = new Vector3f(aux.x - squareSize * 2.5f - 1f, aux.y + (1.2f * (black_index / 4)), aux.z);
            black_index++;
        }
        Vector3f height = new Vector3f((origin.x + destiny.x) / 2, origin.y + 10f,(origin.z + destiny.z) / 2);
        Vector3f dest_down = new Vector3f(destiny.x, destiny.y - (boardHeight + squareHeight), destiny.z);
        // Motion Path (camino del movimient)
        MotionPath motionPath = new MotionPath();
        motionPath.addWayPoint(origin);
        motionPath.addWayPoint(height);
        motionPath.addWayPoint(destiny);
        motionPath.addWayPoint(dest_down);
        motionPath.setCycle(false);
        motionPath.setCurveTension(0.50f);
         // Evento del movimiento
        MotionEvent motionEvent = new MotionEvent(piece,motionPath);
        // Listerner for sound and others
        motionEvent.addListener(new CinematicEventListener(){
            @Override public void onPlay(CinematicEvent cinematic){
                if(playSounds)
                    playSound(eat_audio);
            }
            @Override public void onPause(CinematicEvent cinematic){}
            @Override public void onStop(CinematicEvent cinematic){}
        });
        motionEvent.addListener(panelControl);
        // Motion Data
        motionEvent.setDirectionType(MotionEvent.Direction.PathAndRotation);
        motionEvent.setInitialDuration(25f);
        motionEvent.setSpeed(15f);
        motionEvent.play();
    }
    
    /**
     * Clear board Selection (Pices and Squares)
     * @param points Points to Clear
     */
    protected void clearSelection(Point... points){
        // More than one point is clear from invalid move (in the right usage)
        if (playSounds && points.length > 1){
            printHudText(lang.getString("INVALID_MOVE"));
            playSound(invalid_move_audio);
        }
        for (Point point : points){
            final int rowOrig = point.getFirst();
            final int colOrig = point.getSecond();
            // Clear Selected Square
            enqueue(new Callable<Void>(){
                @Override
                public Void call() throws Exception {
                    board_squares[rowOrig][colOrig].getMaterial().clearParam("Color");
                    board_squares[rowOrig][colOrig].getMaterial().clearParam("ColorMap");
                    // Reset Textures
                    if ((rowOrig + colOrig) % 2 == 0){ // light square
                        board_squares[rowOrig][colOrig].getMaterial().setTexture("ColorMap", light_texture);
                    } else {
                        board_squares[rowOrig][colOrig].getMaterial().setTexture("ColorMap", dark_texture);
                    }
                    return null;
                }
            });
        }
    }
    
    /**
     * Enable User Input (View Interaction)
     * @param status true or false
     */
    protected void enableInteraction(boolean status){
        if(status){
            // Reinitialize Keys
            initKeys();
            interaction = true;
        } else {
            // Remove all mappings
            inputManager.deleteMapping("top_cam");
            inputManager.deleteMapping("free_cam");
            inputManager.deleteMapping("rotate_cam");
            inputManager.deleteMapping("pick_target");
            inputManager.deleteMapping("clear_target");
            inputManager.deleteMapping("auto_rotation");
            inputManager.removeListener(this);
            interaction = false;
        }        
    }
    
    /**
     * Restart Board (Reinitialize Pieces)
     */
    protected void restartBoard(){
        enqueue(new Callable<Void>(){
            @Override
            public Void call() throws Exception {
                piece_node.detachAllChildren();
                rootNode.detachChild(piece_node);
                loadPieces();
                black_index = 0;
                red_index = 0;
                for (int row = 0; row < 8; row++)
                    for (int col = 0; col < 8; col++)
                        clearSelection(new Point(row, col));
                return null;
           }
        });
    }
    
    /**
     * Start Board (attach loaded pices)
     */
    protected void startBoard(){
        enqueue(new Callable<Void>(){
            @Override
            public Void call() throws Exception {
                rootNode.attachChild(piece_node);
                return null;
            }
        });
    }
    
    /**
     * Enqueue and play AudioNode (sound)
     * @param sound AudioNode to play
     */
    private void playSound(final AudioNode sound){
        enqueue(new Callable<Void>(){
            @Override
            public Void call() throws Exception {
                sound.play();
                return null;
            }
        });
    }
    
    /**
     * Add panel control to this view
     * @param control 
     */
    public void addControl(Panel3DController control){
        panelControl = control;
    }
    
    /**
     * Apply 3d panel settings
     */
    public void updateSettings(){
        // Shadows
        if(Settings.getShadowsEnable())
            rootNode.setShadowMode(ShadowMode.CastAndReceive);
        else 
            rootNode.setShadowMode(ShadowMode.Off);
        // Shadows Filter 
        switch (Settings.getShadowFilterLevel()){
            case 0: dlsr.setEdgeFilteringMode(EdgeFilteringMode.Bilinear);break;
            case 1: dlsr.setEdgeFilteringMode(EdgeFilteringMode.Dither);break;
            case 2: dlsr.setEdgeFilteringMode(EdgeFilteringMode.Nearest);break;
            case 3: dlsr.setEdgeFilteringMode(EdgeFilteringMode.PCF4);break;
            case 4: dlsr.setEdgeFilteringMode(EdgeFilteringMode.PCF8);break;
            case 5: dlsr.setEdgeFilteringMode(EdgeFilteringMode.PCFPOISSON);break;
            default: System.err.println("Filtro Sombras: Invalido"); break;
        }
        // Sound
        playSounds = Settings.getAudioEnable();
        // Autorotation
        autoRotation = Settings.getAutoRotation();
        // Show FPS
        setDisplayFps(Settings.getShowFPS());
    }

    /**
     * Print basic text in game HUD
     * @param text string to display
     */
    public void printHudText(final String text){
        enqueue(new Callable<Void>(){
            @Override
            public Void call() throws Exception {
                hudText.setText(text);
                hudDelay = System.currentTimeMillis() + 3500;
                showHud = true;
                guiNode.attachChild(darkHud);
                guiNode.attachChild(hudText);
                return null;
            }
        });

    }
    
    @Override
    public void simpleUpdate(float tpf){
        if(showHud){
            // fix hud position
            hudText.setLocalTranslation(10, ((JmeCanvasContext)getContext()).getCanvas().getHeight(), 0);
            darkHud.setLocalTranslation(0, ((JmeCanvasContext)getContext()).getCanvas().getHeight() - hudText.getLineHeight(), 0);        
            darkHud.setLocalScale(((JmeCanvasContext)getContext()).getCanvas().getWidth(), 1, 1);
            // Clear HUD Text
            if(System.currentTimeMillis() >= hudDelay){
                hudText.setText("");
                guiNode.detachChild(darkHud);
                guiNode.detachChild(hudText);
                showHud = false;
            }
        }
    }
    
    /**
     * Keys Action Listener
     */
    @Override
    public void onAction(String name, boolean keyPressed, float tpf){
        // Enable auto-rotation
        if(name.equals("auto_rotation") && !keyPressed){
            if(autoRotation){
                Settings.setAutoRotation(false);
                autoRotation = false;
                printHudText(lang.getString("CAM_AUTOROT_OFF"));
            } else {
                Settings.setAutoRotation(true);
                autoRotation = true;
                printHudText(lang.getString("CAM_AUTOROT_ON"));
            }
        }        
        // Top Cam Action
        if(name.equals("top_cam") && !keyPressed){
            if(lastCamera == cameras.GLOBAL) return;
            Settings.setAutoRotation(false);
            autoRotation = false;
            enableInteraction(false);
            // Run Update Thread
            new Thread(new Runnable(){
                @Override
                public void run(){
                    printHudText(lang.getString("CAM_TOP_SELECTED"));
                    setPosCamera(cameras.GLOBAL);
                    enableInteraction(true);
                    
                }
            }).start();
            return;
        }
        // Free Cam Action
        if(name.equals("free_cam") && !keyPressed){
            if(lastCamera == cameras.FREE) return;
            enableInteraction(false);
            Settings.setAutoRotation(false);
            autoRotation = false;
            // Run Update Thread
            new Thread(new Runnable(){
                @Override
                public void run(){
                    printHudText(lang.getString("CAM_FREE_SELECTED"));
                    setPosCamera(cameras.FREE);
                    enableInteraction(true);
                }
            }).start();
            return;
        }
        // Rotate Cam Action
        if(name.equals("rotate_cam") && !keyPressed){
            enableInteraction(false);
            Settings.setAutoRotation(false);
            autoRotation = false;
            // Run Update Thread
            new Thread(new Runnable(){
                @Override
                public void run(){
                    switch(lastCamera){
                        case PLAYER_1 : printHudText(lang.getString("CAM_P2_SELECTED"));
                                        setPosCamera(cameras.PLAYER_2);
                                        break;
                        case PLAYER_2 : printHudText(lang.getString("CAM_P1_SELECTED"));
                                        setPosCamera(cameras.PLAYER_1);
                                        break;
                        default: 
                            if(new Random().nextBoolean()){
                                printHudText(lang.getString("CAM_P1_SELECTED"));
                                setPosCamera(cameras.PLAYER_1);
                            } else {
                                printHudText(lang.getString("CAM_P2_SELECTED"));
                                setPosCamera(cameras.PLAYER_2);
                            }
                            break;
                    }
                    enableInteraction(true);
                }
            }).start();
        }
    }
   
    /**
     * Mouse Listener
     */
    @Override
    public void onAnalog(String name, float intensity, float tpf){
        // Remove Previous selected piece
        if (name.equals("clear_target") && piece_orig != null){
            int rowOrig = piece_orig.getUserData("row_num");
            int colOrig = piece_orig.getUserData("col_num");
            clearSelection(new Point(rowOrig,colOrig));
            piece_orig = null;
        }
        // Select piece and destinity
        if (name.equals("pick_target")){
            // Create results list
            CollisionResults results = new CollisionResults();
            // Convert screen click to 3d position
            Vector2f click2d = inputManager.getCursorPosition();
            Vector3f click3d = cam.getWorldCoordinates(new Vector2f(click2d.x, click2d.y), 0f).clone();
            Vector3f dir = cam.getWorldCoordinates(new Vector2f(click2d.x, click2d.y), 1f).subtractLocal(click3d).normalizeLocal();
            // Aim the ray from the clicked spot forwards.
            Ray ray = new Ray(click3d, dir);
            // Collect intersections between ray and all nodes in results list.
            rootNode.collideWith(ray, results);
            // Use the results -- select piece and field to move
            if (results.size() > 0){
                // The closest result is the target that the player picked:
                Geometry target = results.getClosestCollision().getGeometry();
                String target_name = target.getName();
                // Only acept pieces and squares
                if(target_name.equals("Piece") || target_name.equals("Square")){
                    // Get Data from selected model
                    final int row = target.getUserData("row_num");
                    final int col = target.getUserData("col_num");
                    // Select pieces if not previous piece has selected
                    if(target_name.equals("Piece")){
                        // Avoid select eated pieces
                        if(target.getUserData("piece_eated")) 
                            return;
                        // Select piece if has no previuos piece selected
                        if(piece_orig == null){
                            piece_orig = board_pieces[row][col];
                            // Paint of green the square from origin
                            enqueue(new Callable<Void>(){
                                @Override
                                public Void call() throws Exception {
                                    board_squares[row][col].getMaterial().setTexture("ColorMap", select_dark_texture);
                                    return null;
                                }
                            });
                        }
                    }
                    // Select target to move piece if origin piece has selected
                    if(target_name.equals("Square") && piece_orig != null){
                        if(board_pieces[row][col] == null){
                            square_dest = board_squares[row][col];
                            // Paint target board position
                            enqueue(new Callable<Void>(){
                                @Override
                                public Void call() throws Exception {
                                    board_squares[row][col].getMaterial().setTexture("ColorMap", select_dark_texture);
                                    return null;
                                }
                            });
                        }
                    }
                }
            }
            // If two celds are selected then do piece movement
            if(piece_orig != null && square_dest != null){
                // Get Data from piece
                int rowOrig = piece_orig.getUserData("row_num");
                int rowDest = square_dest.getUserData("row_num");
                int colOrig = piece_orig.getUserData("col_num");
                int colDest = square_dest.getUserData("col_num");
                // Send Move to panel controller
                hudDelay = System.currentTimeMillis();
                panelControl.sendMove(new Point(rowOrig,colOrig),new Point(rowDest,colDest));
                // Reset Move Values
                piece_orig = null;
                square_dest = null;
            }
        }
    }
}