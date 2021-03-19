package GameModifiers;
/*****************************************
 This class is the main GameModifiers.Game running class
 that starts/ends the game and acts as a
 JPanel to output the graphics to a JFrame.
 ****************************************/
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class Game extends JPanel {

    public static final int UI_WIDTH = 1280;
    public static final int UI_HEIGHT = 720;
    public static final int SCREEN_WIDTH = 896;
    public static final int SCREEN_HEIGHT = 672;
    public static final int WORLD_WIDTH = 4512;
    public static final int WORLD_HEIGHT = 2848;

    private BufferedImage p1Portrait;
    private BufferedImage screenImage = null;
    private BufferedImage world;
    private BufferedImage youLose;
    private BufferedImage youWin;
    private BufferedImageLoader imageLoader;
    private Camera camera;
    private GameMod gameMod;
    private GameObjectManager handler;
    private Graphics2D buffer;
    private HUD hud;
    public JFrame jFrame;
    private MapLoader mapLoader;

    private boolean endgamePhase = false;
    private boolean isRunning = true;

    public void Game(){ }

    public void init(){
        camera = new Camera(0, 0);
        handler = new GameObjectManager();
        imageLoader = new BufferedImageLoader();
        youLose = imageLoader.getImage("youLose");
        youWin = imageLoader.getImage("youWin");
        gameMod = new GameMod(camera, handler, imageLoader);

        //set up the level and world information
        mapLoader = new MapLoader(imageLoader, this.handler);
        mapLoader.loadLevel(); //load all of the starting game objects into the GameModifiers.GameObjectManager ArrayList
        gameMod.init();

        //set up GameModifiers.HUD and world
        p1Portrait = imageLoader.getImage("hero1Portrait");
        hud = new HUD(gameMod, p1Portrait);
        this.world = new BufferedImage(this.WORLD_WIDTH, this.WORLD_HEIGHT + 160, BufferedImage.TYPE_INT_RGB);

        jFrame = new JFrame("GameObject.Tower D");
        jFrame.setLayout(new BorderLayout());
        jFrame.add(this);
        MouseInput mouseInput = new MouseInput(handler, camera);
        this.addMouseListener(mouseInput);
        this.addMouseMotionListener(mouseInput);
        jFrame.setSize(this.UI_WIDTH, this.UI_HEIGHT + 30);
        jFrame.setResizable(false);
        jFrame.setLocationRelativeTo(null);
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jFrame.setVisible(true);

        this.isRunning = true;
        run();
    }


    //updates everything 60 times per second
    //all game events are updated here
    public void tick(){
        handler.tick();

        for(int i = 0; i < handler.getObject().size(); i++){
            if(handler.getObject().get(i).getId() == ID.Player1)
                camera.tick(handler.getObject().get(i));
        }
        gameMod.tick();
        if(gameMod.getEndGame()) this.endgamePhase = true;
    }


    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        super.paintComponent(g);
        buffer = world.createGraphics(); //make it so that the bufferedImage can be drawn onto using graphics
        mapLoader.drawFloor(WORLD_WIDTH, WORLD_HEIGHT, buffer);
        handler.render(buffer); //do all rendering, drawing onto world through buffer Graphics2D object

        if (!endgamePhase) drawPlayScreen(g, g2, false, false);
        else drawPlayScreen(g, g2, true, gameMod.getIsP1Winner());
    }

    public void getCameraSnaps(boolean endgame, boolean isP1Winner){
        int x, y;
        if(endgame && (gameMod.getHero1() != null)){
            if(isP1Winner){
                //center the CameraSnaps around DireBase Dumpster Fire
                x = 0;
                y = 2000;
            }else {
                //center the CameraSnaps around RadiantBase Dumpster Fire
                x = 0;
                y = 200;
            }
        }else {
            x = (int) camera.getX();
            y = (int) camera.getY();
        }
        this.screenImage = world.getSubimage(x, y, 896, 672);
    }

    public void drawPlayScreen(Graphics g, Graphics2D g2, boolean isEndGame, boolean isP1Winner){
        if(gameMod.getHero1() != null) getCameraSnaps(isEndGame, isP1Winner);
        g2.drawImage(imageLoader.getImage("ui"), 0,0,null);
        g2.drawImage(screenImage, 352, 24, null);
        if(gameMod.isCountingToRespawn()) outputRespawnCount(g);
        if(gameMod.getHero1() != null) hud.render(g, world);
        if(isEndGame) endGameEvent(g, isP1Winner);
    }

    public void outputRespawnCount(Graphics g){
        Color screen = new Color(0,0,0, 0.5f );
        g.setColor(screen);
        g.fillRect(352, 24, 896, 672);
        Font boldFont = new Font("Verdana", Font.BOLD, 48);
        g.setFont(boldFont);
        g.setColor(Color.white);
        g.drawString("Respawning in " + gameMod.getSecondsToRespawn() + "...", 352 + 175, 24 + 325);
    }

    public void endGameEvent(Graphics g, boolean isP1Winner){
        System.out.println("Drawing endgame screen...");
        Color screen = new Color(0,0,0, 0.5f );

        if(isP1Winner){
            g.setColor(screen);
            g.fillRect(0, 0, 1280, 720);
            g.drawImage(youWin, 352, 24, null);
        }else {
            g.setColor(screen);
            g.fillRect(0, 0, 1280, 720);
            g.drawImage(youLose, 352, 24, null);
        }
        isRunning = false;
    }

    public void run(){

        Sounds.playGameMusic();

        //start the game
        try {
            while (this.isRunning()) {
                this.tick();
                this.repaint();
                Thread.sleep(1000 / 60);
            }
        } catch (InterruptedException ignored) {
            System.out.println(ignored);
        }

        Sounds.stopMusic();
    }

    public boolean isRunning(){ return this.isRunning; }


    public static void main(String args[]){
        Game game = new Game();
        game.init();
    }
}
