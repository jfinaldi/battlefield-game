package GameModifiers;
/************************************************************
 This class is responsible for loading and storing all
 GameModifiers.Game objects. It can add objects to the list, delete objects
 from the game, update all of the objects as well as render.
 Idea for this class comes from Zach at codingmadesimple.com
 aka realTutsGML on youtube.
 ***********************************************************/
import GameObject.*;
import java.awt.*;
import java.util.ArrayList;

public class GameObjectManager {

    private ArrayList<GameObject> object = new ArrayList<>();
    private ArrayList<GameObject> deadThings = new ArrayList<>();

    public ArrayList<GameObject> getObject(){ return this.object; }
    public ArrayList<GameObject> getDeadThings() { return this.deadThings; }

    public void tick(){
        for(int i = 0; i < object.size(); i++){
            GameObject tempObject = object.get(i);
            tempObject.tick();  //ticks every object in our list
        }
        for(int i = 0; i < deadThings.size(); i++){
            GameObject tempObject = deadThings.get(i);
            tempObject.tick();  //ticks every object in our list
            if(deadThings.get(i).getId() == ID.CreepCorpse){
                DeadCreep dC = (DeadCreep) deadThings.get(i);
                if(dC.getDecayInterval() > 660) removeDeadThing(deadThings.get(i));
            }
        }

        sortObjects();
    }

    public void sortObjects(){
        for(int i = 0; i < object.size(); i++){
            GameObject temp = object.get(i);
            if(temp instanceof MobileObject){
                object.remove(i); //remove temp from old position
                object.add(temp); //put temp to the end of the list
            }
        }
    }

    public void render(Graphics g){
        GameObject tempObject;

        //render all dead towers, so they're on the very bottom layer
        for(int i = 0; i < deadThings.size(); i++){
            deadThings.get(i).render(g);
        }

        //render all game objects
        for(int i = 0; i < object.size(); i++){
            tempObject = object.get(i);
            tempObject.render(g);  //renders every object in our list
        }
    }

    public void addObject(GameObject tempObject){ object.add(tempObject); }
    public void addDeadThing(GameObject deadThing) { deadThings.add(deadThing); }

    public void removeObject(GameObject tempObject){
        object.remove(tempObject);
    }
    public void removeDeadThing(GameObject tempObject){ deadThings.remove(tempObject); }

}
