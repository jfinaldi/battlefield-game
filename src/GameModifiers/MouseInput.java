package GameModifiers;
/********************************
 This class acts as the user input
 control class. It involves mouse
 click input to control game objects
 and events. Mouse clicks are only
 recognized from within the game-
 play viewport window.
 * *******************************/

import GameObject.*;

import javax.swing.event.MouseInputAdapter;
import java.awt.*;
import java.awt.event.MouseEvent;

public class MouseInput extends MouseInputAdapter {

    private GameObjectManager handler;
    private Camera camera;
    private int x;
    private int y;

    public MouseInput(GameObjectManager handler, Camera camera){
        this.handler = handler;
        this.camera = camera;
    }

    public int getX() { return x; }
    public int getY() { return y; }

    @Override
    public void mouseClicked(MouseEvent mouseEvent) { /*dont use this one*/ }

    //modified code from base found at
    //wizard top down shooter Part 8
    //url:
    @Override
    public void mousePressed(MouseEvent mouseEvent) {
        if(didWeClickPortrait(mouseEvent.getX(), mouseEvent.getY()) == true){
            System.out.println("Activating godmode...");
            for (int i = 0; i < handler.getObject().size(); i++) {
                GameObject temp = handler.getObject().get(i);
                if(temp.getId() == ID.Player1){
                    Hero hero = (Hero) temp;
                    hero.setGodmode(!hero.isGodmode());
                    return;
                }
            }
        }

        boolean doStuff = false;

        x = mouseEvent.getX() + (int) camera.getX() - 352;
        y = mouseEvent.getY() + (int) camera.getY() - 24;

        //ignore any clicks outside of the gameplay window
        if((mouseEvent.getX()) < 1248 && (mouseEvent.getX()) > 352) {
            if (mouseEvent.getY() < 696 && mouseEvent.getY() > 24) doStuff = true;
        }

        if(doStuff) {
            for (int i = 0; i < handler.getObject().size(); i++) {
                GameObject temp = handler.getObject().get(i);
                if (temp.getId() == ID.Player1) {
                    Hero m = (Hero) temp;
                    if(m.isRespawning() == false) {
                        m.getMover().setDestinationX(x - 32);
                        m.getMover().setDestinationY(y - 32);
                        m.getMover().setMoving(true);
                        m.getMover().setAttacking(false);
                        m.getMover().setCollidedWithObject(false);
                        m.toggleRun();
                    }
                }
            }
        }
    }

    public void mouseMoved(MouseEvent e){
        int x = e.getX() + (int) camera.getX() - 352;
        int y = e.getY() + (int) camera.getY() - 24;

        Rectangle cursor = new Rectangle(x, y, 1, 1); //pixel sized point or box

        for (int i = 0; i < handler.getObject().size(); i++) {
            GameObject temp = handler.getObject().get(i);

            if(cursor.intersects(temp.getBounds())){
                if((temp.getId() == ID.DireTower) || (temp.getId() == ID.DireBase) || (temp.getId() == ID.DireCreep)) {
                    temp.setShowRedBox(true);
                }
            }
            else temp.setShowRedBox(false);
        }
    }

    private boolean didWeClickPortrait(int x, int y){
        Rectangle cursor = new Rectangle(x, y, 1, 1);
        Rectangle portrait = new Rectangle(15, 230, 75, 63);
        if(cursor.intersects(portrait)) return true;
        return false;
    }

    @Override
    public void mouseReleased(MouseEvent mouseEvent) {

    }

    @Override
    public void mouseEntered(MouseEvent mouseEvent) {

    }

    @Override
    public void mouseExited(MouseEvent mouseEvent) {

    }
}
