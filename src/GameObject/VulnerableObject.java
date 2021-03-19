package GameObject;
/***************************************
 This class adds an additional level of
 abstraction between GameObjects and
 individual objects that are vulnerable
 and can be destroyed. It adds abstract
 functions for handling lives count and
 hitpoints.
 ***************************************/
import GameModifiers.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;

public abstract class VulnerableObject extends GameObject {

    protected int max_hitpoints;
    protected int hitpoints;
    protected int livesCount = 1000;

    public VulnerableObject(int x, int y, ID id, BufferedImage image, int hitpoints, int livesCount){
        super(x, y, id, image);
        this.hitpoints = hitpoints;
        this.max_hitpoints = hitpoints;
        this.livesCount = livesCount;
    }

    public abstract Ellipse2D.Double vision();

    public int getLivesCount(){
        return this.livesCount;
    }
    public int getHitPoints(){
        return this.hitpoints;
    }
    public int getMaxHp(){
        return this.max_hitpoints;
    }

    public void setHitPoints(int x){
        this.hitpoints = x;
    }

    public void adjustHp(int x){
        if((hitpoints + x) > max_hitpoints) hitpoints = max_hitpoints;
        else hitpoints += x;
        if(hitpoints < 1) livesCount--;
    }


}
