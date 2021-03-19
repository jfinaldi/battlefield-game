 /*************************************
 Abstract class that holds all of the base
 functions and attributes for all game
 objects.
 **************************************/
 package GameObject;
import GameModifiers.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public abstract class GameObject {

    protected static final int MAX_BLOCK_SIZE = 32;
    protected int x, y;
    protected ID id;
    protected boolean showRedBox = false;
    protected BufferedImage image;

    public GameObject(int x, int y, ID id, BufferedImage image){
        this.x = x;
        this.y = y;
        this.id = id;
        this.image = image;
    }

    public abstract void tick();
    public abstract void render(Graphics g);
    public abstract Rectangle getBounds();

    public ID getId(){
        return id;
    }
    public int getX() {
        return x;
    }
    public int getY() {
        return y;
    }
    public int getMaxBlockSize() { return this.MAX_BLOCK_SIZE; }
    public BufferedImage getImage() { return this.image; }

    public void setId(ID id){
        this.id = id;
    }
    public void setX(int x) {
        this.x = x;
    }
    public void setY(int y) {
        this.y = y;
    }
    public void setShowRedBox(boolean x) { this.showRedBox = x; }
    public void setImage(BufferedImage image) { this.image = image; }
}
