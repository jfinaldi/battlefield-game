/****************************************
 This class represents a projectile that
 is emitted from a firing tower. Missiles
 handle collision events, inflicting dmg
 on whatever it collides with, before
 terminating itself.
 ****************************************/
package GameObject;
import GameModifiers.*;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Missile extends MobileObject {

    private static final int BLOCK_SIZE = 33;
    private int damage = 10;
    private int timeOut = 0;
    private int animationInterval = 0;
    private int animationFrameIndex = 0;
    private ArrayList<BufferedImage> images;
    private BufferedImageLoader imageLoader;
    private GameObjectManager handler;


    public Missile(int x, int y, int destX, int destY, ID id, BufferedImage image, GameObjectManager handler,
                   BufferedImageLoader imageLoader, int hitpoints, int livesCount, Rectangle2D rangeOfFlight) {
        super(x, y, id, image, handler, imageLoader, hitpoints, livesCount);
        this.handler = handler;
        this.imageLoader = imageLoader;
        this.images = new ArrayList<>();
        mover.setDestinationX(destX);
        mover.setDestinationY(destY);
        mover.setAngle();
        mover.setMoveSpeed(8.0f);
        pullAnimationSheet();
    }

    @Override
    public void tick() {
        int[] xycoords;
        xycoords = mover.tick(x, y); //update the object's xy coordinates
        setX(xycoords[0]);
        setY(xycoords[1]);

        if (animationInterval < 13) { //only change sprite every 50 frames
            animationInterval++;
        }
        else {
            animationFrameIndex++;
            if (animationFrameIndex == images.size()) animationFrameIndex = 2;
            animationInterval = 0;
        }

        timeOut++;
        if(timeOut > 120) handler.removeObject(this);

        if(mover.getIsMoving() == false){
            triggerExplosion(this);
            handler.removeObject(this);
        }

        collision();
    }

    public void collision() {
        for (int i = 0; i < handler.getObject().size(); i++) {
            GameObject temp = handler.getObject().get(i);

            if (this.getBounds().intersects(temp.getBounds())) {
                if(this.getId() == ID.DireFireBall){
                    if(temp.getId() == ID.Player1 || temp.getId() == ID.RadiantCreep) {
                        VulnerableObject vO = (VulnerableObject) temp;
                        vO.adjustHp(-damage);
                        //collisionEvent(temp);
                        Sounds.playFireHitSound();
                        handler.removeObject(this);
                    }
                }

                if(this.getId() == ID.RadiantFireBall){
                    if(temp.getId() == ID.Player2 || temp.getId() == ID.DireCreep) {
                        VulnerableObject vO = (VulnerableObject) temp;
                        vO.adjustHp(-damage);
                        //collisionEvent(temp);
                        Sounds.playFireHitSound();
                        handler.removeObject(this);
                    }
                }
            }
        }//for
    }

    public void collisionEvent(GameObject temp) {
        triggerExplosion(temp);
    }

    public void pullAnimationSheet(){
        int column = 0; //default starts at the beginning of first column

        //VERTICAL UP (col 0)
        if(mover.getAngle() >= 262 && mover.getAngle() < 278) column = BLOCK_SIZE * 0;

        //UP-RIGHT ANGLE (col 1)
        if(mover.getAngle() >= 278 && mover.getAngle() < 352) column = BLOCK_SIZE * 1;

        //HORIZONTAL RIGHT FACE (col 2)
        if(mover.getAngle() >= 352 || mover.getAngle() < 13) column = BLOCK_SIZE * 2;

        //DOWN-RIGHT ANGLE (col 3)
        if(mover.getAngle() >= 13 && mover.getAngle() < 82) column = BLOCK_SIZE * 3;

        //VERTICAL DOWN (col 4)
        if(mover.getAngle() >= 82 && mover.getAngle() < 98) column = BLOCK_SIZE * 4;

        //DOWN-LEFT ANGLE (col 5)
        if(mover.getAngle() >= 98 && mover.getAngle() < 178) column = BLOCK_SIZE * 5;

        //HORIZONTAL LEFT FACE (col 6)
        if(mover.getAngle() >= 172 && mover.getAngle() < 188) column = BLOCK_SIZE * 6;

        //UP-LEFT ANGLE (col 7)
        if(mover.getAngle() >= 188 && mover.getAngle() < 262) column = BLOCK_SIZE * 7;

        //break up the full sheet into one column based on the hero angle
        BufferedImage movementArray = image.getSubimage(column, 0, BLOCK_SIZE, image.getHeight());

        //break up the column into an array of individual image frames
        this.images = imageLoader.getImageArray(movementArray, BLOCK_SIZE);

    }

    public void triggerExplosion(GameObject temp){
        BufferedImage explosion_sheet = imageLoader.getImage("explosionSheet");
        int interval = 25;

        Animation animate = new Animation(temp.getX() + 16, temp.getY() + 16, ID.Explosion, imageLoader.getImageArray(explosion_sheet, 32), interval, handler);
        handler.addObject(animate);
        //GameModifiers.Sounds.playFireHitSound();
    }

    @Override
    public void render(Graphics g) {
        g.drawImage(images.get(animationFrameIndex), x, y, null);
    }

    @Override
    public Rectangle getBounds() {
        return new Rectangle(x, y, image.getWidth()/2, image.getHeight()/2);
    }

    @Override
    public Ellipse2D.Double vision() { return new Ellipse2D.Double(x, y, 1, 1); }
}
