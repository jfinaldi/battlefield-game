/****************************************
 This class represents one block of wall
 that is immobile and indestructable, and
 cannot be walked through by Heroes.
 ***************************************/
package GameObject;

import GameModifiers.*;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Wall extends GameObject {

    public Wall(int x, int y, ID id, BufferedImage image){
        super(x,y,id,image);
    }

    @Override
    public void tick() { }

    @Override
    public void render(Graphics g) {
        g.drawImage(image, x, y, null);
    }

    @Override
    public Rectangle getBounds() {
        return new Rectangle(x, y, image.getWidth() - 5, image.getHeight() - 5);
    }
}
