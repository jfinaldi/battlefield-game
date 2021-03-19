package GameObject;
import GameModifiers.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Base extends VulnerableObject {

    BufferedImageLoader imageLoader;
    GameObjectManager handler;
    private int healthBarWidth;
    private int flameSoundInterval = 60;
    private boolean isOnFire = false;
    private ArrayList<BufferedImage> fireArray;
    private Animation inferno;

    public Base(int x, int y, ID id, GameObjectManager handler, BufferedImageLoader imageLoader, BufferedImage image){
        super(x,y,id,image, 2000,1);
        this.handler = handler;
        this.imageLoader = imageLoader;
        healthBarWidth = hitpoints / 20; //200
    }

    @Override
    public void tick() {
        //if heavy damage taken and tower isn't already on fire, tower catches fire
        if((hitpoints < (max_hitpoints / 2)) && !isOnFire){
            fireArray = imageLoader.getImageArray(imageLoader.getImage("flameSheet"), 54);
            inferno = new Animation(x + 32, y + 32, ID.FireAnimation, fireArray, 30, handler);
            isOnFire = !isOnFire;
        }

        if(isOnFire) {
            inferno.tick();
            if(flameSoundInterval < 60) flameSoundInterval++;
            else{
                Sounds.playBurningSound();
                flameSoundInterval = 0;
            }
        }
    }

    @Override
    public void render(Graphics g) {
        int healthbarX = x - 33;
        int healthbarY = y - 12;
        int healthBarMaxWidth = 200;
        int healthBarHeight = 5;
        Color background = Color.black;
        Color foreground = Color.white;
        if(hitpoints % 10 == 0) healthBarWidth = hitpoints / 10;

        //draw selection circle when mouse hovers over base
        g.setColor(Color.red);
        if(showRedBox) g.drawOval(x - 28, y + 60, 180, 75);

        g.drawImage(image, x, y, null); //draw the base

        //set the colors based on which tower
        if(this.getId() == ID.RadiantBase) {
            background = new Color(0, 64, 0);
            foreground = Color.green;
        } else if(this.getId() == ID.DireBase) {
            background = new Color(64, 0, 0);
            foreground = new Color(200, 0, 0);
        }

        //draw the health bar
        g.setColor(background);
        g.fillRect(healthbarX, healthbarY, healthBarMaxWidth, healthBarHeight);
        g.setColor(foreground);
        g.fillRect(healthbarX, healthbarY, healthBarWidth, healthBarHeight);
        g.setColor(Color.black);
        g.drawRect(healthbarX, healthbarY, healthBarMaxWidth, healthBarHeight);

        if(isOnFire) inferno.render(g); //render the fire animation if applicable
    }

    @Override
    public Ellipse2D.Double vision() { return new Ellipse2D.Double(x, y, image.getWidth(), image.getHeight()); }

    @Override
    public Rectangle getBounds() {
        return new Rectangle(x, y, image.getWidth(), image.getHeight());
    }
}
