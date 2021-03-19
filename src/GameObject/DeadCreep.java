 /**************************************
 This class represents a destroyed
 creep corpse object created solely for the
 aesthetics of the game to let players
 easily keep track of the progress
 they make in the game.
 *************************************/
 package GameObject;
 import GameModifiers.*;
import java.awt.*;
import java.awt.image.BufferedImage;
public class DeadCreep extends GameObject {

    private int decayInterval = 0; //time spent decayed on the ground

    public DeadCreep(int x, int y, ID id, BufferedImage image){
        super(x, y, id, image);
    }

    public int getDecayInterval() { return this.decayInterval; }

    @Override
    public void tick() {
        decayInterval++;
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
