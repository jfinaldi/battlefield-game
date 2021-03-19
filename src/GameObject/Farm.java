/**************************************
 This class represents a farm environment
 object. It is for user experience and
 to make the map more interesting. Can
 be upgraded to take damage, adding
 additional challenges.
 *************************************/
package GameObject;
import GameModifiers.BufferedImageLoader;
import GameModifiers.GameObjectManager;
import GameModifiers.ID;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;

public class Farm extends VulnerableObject {

    BufferedImageLoader imageLoader;
    GameObjectManager handler;

    public Farm(int x, int y, ID id, GameObjectManager handler, BufferedImageLoader imageLoader, BufferedImage image){
        super(x,y,id,image, 300,1);
        this.handler = handler;
        this.imageLoader = imageLoader;
    }

    @Override
    public void tick() {

    }

    @Override
    public void render(Graphics g) {
        g.drawImage(image, x, y, null);
    }

    @Override
    public Rectangle getBounds() {
        return new Rectangle(x + (image.getWidth()/4), y + (image.getWidth()/4), image.getWidth() / 2, image.getHeight() /2);
    }

    @Override
    public Ellipse2D.Double vision() { return new Ellipse2D.Double(x, y, image.getWidth(), image.getHeight()); }
}
