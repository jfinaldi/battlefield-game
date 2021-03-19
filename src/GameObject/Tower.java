/*************************************
GameObject.Tower represents an immobile but
 vulnerable attacking building that
 can shoot projectiles at enemies that
 venture into their detection radius.
 Will catch on fire if hitpoints get
 too low.
 ************************************/
package GameObject;
import GameModifiers.*;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Tower extends VulnerableObject {
    private GameObjectManager handler;
    private BufferedImageLoader imageLoader;
    private ArrayList<BufferedImage> fireArray;
    private Animation inferno;
    private boolean isOnFire = false;
    private int flameSoundInterval = 60;
    private int nonFirePauses = 120;
    private int healthBarWidth;
    private int divisor;
    private static final int MAX_HEALTH_BAR_WIDTH = 100;

    public Tower(int x, int y, ID id, GameObjectManager handler, BufferedImageLoader imageLoader, BufferedImage image) {
        super(x, y, id, image, 500, 1);
        this.handler = handler;
        this.imageLoader = imageLoader;
        healthBarWidth = MAX_HEALTH_BAR_WIDTH;
        divisor = hitpoints / MAX_HEALTH_BAR_WIDTH;
    }

    public void tick() {

        detectEnemies();

        //if heavy damage taken and tower isn't already on fire, tower catches fire
        if((hitpoints < (max_hitpoints / 2)) && !isOnFire){
            fireArray = imageLoader.getImageArray(imageLoader.getImage("flameSheet"), 54);
            inferno = new Animation(x + 4, y + 4, ID.FireAnimation, fireArray, 30, handler);
            isOnFire = !isOnFire;
        }

        if(isOnFire) {
            inferno.tick();
            if(flameSoundInterval < 180) flameSoundInterval++;
            else{
                Sounds.playBurningSound();
                flameSoundInterval = 0;
            }
        }
    }

    public void detectEnemies(){
        for(int i = 0; i < handler.getObject().size(); i++){
            GameObject temp = handler.getObject().get(i);

            if(temp instanceof VulnerableObject) {
                VulnerableObject vulObj = (VulnerableObject) temp;

                //this object sees a vulnerable object
                if (vulObj.vision().getBounds2D().intersects(this.vision().getBounds2D())) {

                    //Radiant GameObject.Tower spots an enemy
                    if(this.getId() == ID.RadiantTower){
                        if(temp.getId() == ID.DireCreep || temp.getId() == ID.Player2){
                            if (nonFirePauses < 120) {
                                nonFirePauses++;
                            } else {
                                shoot(temp.getX(), temp.getY(), ID.RadiantFireBall);
                                nonFirePauses = 0;
                            }
                        }
                    }

                    //Dire GameObject.Tower spots an enemy
                    if(this.getId() == ID.DireTower){
                        if(temp.getId() == ID.RadiantCreep || temp.getId() == ID.Player1){
                            if (nonFirePauses < 120) {
                                nonFirePauses++;
                            } else {
                                shoot(temp.getX(), temp.getY(), ID.DireFireBall);
                                nonFirePauses = 0;
                            }
                        }
                    }
                }
            }
        }
    }

    private void shoot(int destX, int destY, ID id){
        handler.addObject(new Missile(x, y, destX, destY, id, imageLoader.getImage("fireballSheet")
                , handler, imageLoader, 1, 1, vision().getBounds2D()));
    }


    @Override
    public void render(Graphics g) {
        int healthbarX = x - 15;
        int healthbarY = y - 10;
        int healthBarHeight = 5;
        int remain = hitpoints % divisor;
        Color background = Color.black;
        Color foreground = Color.white;
        if(remain == 0) healthBarWidth = hitpoints / divisor; //every 5 hp losses, update the health bar width

        //draw selection circle when mouse hovers over tower
        g.setColor(Color.red);
        if(showRedBox) g.drawOval(x - 2, y + 18, 60, 50);

        g.drawImage(image, x, y, null); //draw the tower

        //set the colors based on which tower
        if(this.getId() == ID.RadiantTower) {
            background = new Color(0, 64, 0);
            foreground = Color.green;
        } else if(this.getId() == ID.DireTower) {
            background = new Color(64, 0, 0);
            foreground = new Color(200, 0, 0);
        }

        //draw the health bar
        g.setColor(background);
        g.fillRect(healthbarX, healthbarY, MAX_HEALTH_BAR_WIDTH, healthBarHeight);
        g.setColor(foreground);
        g.fillRect(healthbarX, healthbarY, healthBarWidth, healthBarHeight);
        g.setColor(Color.black);
        g.drawRect(healthbarX, healthbarY, MAX_HEALTH_BAR_WIDTH, healthBarHeight);

        if(isOnFire) inferno.render(g); //render the fire animation if applicable

        //draw the vision radius
        Color lightGray = new Color(128, 128, 128);
        g.setColor(lightGray);
        //g.drawRect(x - 170, y - 170, 400, 400);
    }

    @Override
    public Rectangle getBounds() {
        return new Rectangle(x, y, image.getWidth(), image.getHeight());
    }

    @Override
    public Ellipse2D.Double vision() {
        return new Ellipse2D.Double(x - 170, y - 170, 400, 400);
    }
}
