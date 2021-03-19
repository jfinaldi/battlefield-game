 /********************************************
 This class is an additional abstract class
 to allow moveable objects velocity related
 abstractions.
 *********************************************/
 package GameObject;
 import GameModifiers.*;
import java.awt.image.BufferedImage;

public abstract class MobileObject extends VulnerableObject {

    protected GameObjectManager handler;
    protected BufferedImageLoader imageLoader;
    protected Movement mover;
    public static final int MAX_BLOCK_SIZE = 64;

    public MobileObject(int x, int y, ID id, BufferedImage image, GameObjectManager handler, BufferedImageLoader imageLoader, int hitpoints, int livesCount){
        super(x, y, id, image, hitpoints, livesCount);
        this.handler = handler;
        this.imageLoader = imageLoader;
        this.mover = new Movement(x, y);
    }

    public Movement getMover() { return this.mover; }
}
