/**************************************
  This class represents a destroyed
 tower object created solely for the
 aesthetics of the game to let players
 easily keep track of the progress
 they make in the game.
 *************************************/
package GameObject;
import GameModifiers.*;

import java.awt.*;
import java.awt.image.BufferedImage;

public class DeadTower extends GameObject {

    public DeadTower(int x, int y, ID id, BufferedImage image){
        super(x, y, id, image);
    }

    @Override
    public void tick() {

    }

    @Override
    public void render(Graphics g) {
        g.drawImage(image,x, y, null);
    }

    @Override
    public Rectangle getBounds() {
        return new Rectangle(x, y, image.getWidth(), image.getHeight());
    }
}
