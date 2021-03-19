/*****************************************
 This class represents an invulnerable
 block of nature object used for map
 environment experience.
 ****************************************/
package GameObject;
import GameModifiers.*;

import java.awt.*;
import java.awt.image.BufferedImage;

public class NatureBlock extends GameObject {

    public NatureBlock(int x, int y, ID id, BufferedImage image) {
        super(x, y, id, image);
    }

    @Override
    public void tick() {}

    @Override
    public void render(Graphics g) {
        g.drawImage(image, x, y, null);
    }

    @Override
    public Rectangle getBounds() {
        return new Rectangle(x, y, image.getWidth(), image.getHeight());
    }
}
