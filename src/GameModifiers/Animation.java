package GameModifiers;
/*****************************************
 This class handles all animations. General
 idea for this class comes from
 codingmadesimple.com, however parts of the
 code came directly from the comments here:
 youtu.be/BhQ9mMCZTHM
 ****************************************/
import GameObject.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Animation extends GameObject {

    private GameObjectManager handler;
    private ArrayList<BufferedImage> images;
    private int interval, index;
    private long timer, now, lastTime;
    private boolean isAnimating = false;

    public int getIndex(){ return this.index; }
    public boolean getIsAnimating(){ return this.isAnimating; }

    //CREDIT: Commenter on youtube for Wizard Top Down Shooter Tutorial #12 video
    public Animation(int x, int y, ID id, ArrayList<BufferedImage> images, int interval, GameObjectManager handler){
        super(x, y, id, images.get(0));
        this.images = images;
        this.interval = interval;

        //CodeBlock CREDIT: Commenter on youtube for Wizard Top Down Shooter Tutorial #12 video
        index = 0;
        timer = 0;
        now = 0;
        lastTime = System.currentTimeMillis();
        this.x = x;
        this.y = y;
        this.handler = handler;
    }

    public void tick(){
        //CodeBlock CREDIT: Commenter on youtube for Wizard Top Down Shooter Tutorial #12 video
        now = System.currentTimeMillis();
        timer += now - lastTime;
        lastTime = now;
        if(timer >= interval){
            index++;
            timer = 0;
        }

        if(index == images.size()){
            if (this.getId() == ID.Explosion) handler.removeObject(this);
            else index = 0; //we output all the images, start over from the beginning
        }
    }

    @Override
    public void render(Graphics g){
        g.drawImage(images.get(index), x, y, images.get(index).getWidth(), images.get(index).getWidth(), null);
    }

    @Override
    public Rectangle getBounds() {
        return new Rectangle(x, y, getMaxBlockSize(), getMaxBlockSize());
    }

}
