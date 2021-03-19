package GameModifiers;

import GameObject.*;

/*****************************************
 This class handles the player viewpoint
 camera coordinate calibrations. The idea
 for this as it's own class comes from
 codingmadesimple.com
 ****************************************/
public class Camera {

    private float x;
    private float y;
    private static final int MAX_X = 4512 - 896;
    private static final int MAX_Y = 2848 - 672;

    public Camera(float x, float y){
        this.x = x;
        this.y = y;
    }

    public float getX() {
        return x;
    }
    public float getY() {
        return y;
    }
    public void setX(float x) {
        this.x = x;
    }
    public void setY(float y) {
        this.y = y;
    }

    public void tick(GameObject object){ //the object fed into this will always be a player object

        //Codeblock credit: the formula comes from codingmadesimple.com
        x += ((object.getX() - x) - 896/2) /2;
        y += ((object.getY() - y) - 672/2) / 2;

        //adjust the min camera bounds
        if(x <= 0) x = 0;
        if(y <= 0) y = 0;

        //adjust the max camera bounds
        if(x >= MAX_X) x = MAX_X;
        if(y >= MAX_Y) y = MAX_Y;
    }

}
