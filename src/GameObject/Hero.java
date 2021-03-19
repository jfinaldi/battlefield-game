/**********************************************
 This class represents a player object vessel.
 It handles movement, attacking, and collision
 events.
 **********************************************/
package GameObject;
import GameModifiers.*;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Hero extends MobileObject {

    private static final int HEALTH_BAR_MAX_WIDTH = 50;
    private int animationInterval = 0;
    private int animationFrameIndex = 0;
    private int hpRegenInterval = 0;
    private int damage = 5;
    private int damageInterval = 0;
    private int hpRegenRate = 4;
    private int xp = 0;
    private int xpToLevel = 100;
    private int level = 1;
    private boolean maxLevel = false;
    private boolean isMoving = false;
    private boolean isAttacking = false;
    private boolean isRespawning = false;
    private boolean godmode = false;
    private ArrayList<BufferedImage> images;
    private BufferedImage spriteFrame;
    private VulnerableObject myEnemy;

    public Hero(int x, int y, ID id, GameObjectManager handler, BufferedImageLoader imageLoader, BufferedImage image) {
        super(x, y, id, image, handler, imageLoader, 100, 10000);
        this.images = new ArrayList<>();
        this.spriteFrame = image.getSubimage(MAX_BLOCK_SIZE * 2, 0, MAX_BLOCK_SIZE, MAX_BLOCK_SIZE);
    }

    public boolean isMoving() { return isMoving; }
    public boolean isMaxLevel() { return this.maxLevel; }
    public boolean isGodmode() { return this.godmode; }
    public int getXp() { return this.xp; }
    public int getXpToLevel() { return this.xpToLevel; }
    public int getMaxHp() { return max_hitpoints; }
    public int getDamage() { return this.damage; }
    public int getLevel() { return this.level; }
    public boolean isAttacking() { return this.isAttacking; }
    public boolean isRespawning() { return this.isRespawning; }
    public VulnerableObject getMyEnemy() { return this.myEnemy; }
    public void setMyEnemy(VulnerableObject v) { this.myEnemy = v; }
    public void setXp(int xp) { this.xp += xp; }
    public void increaseXpToLevel() { this.xpToLevel += 100; }
    public void setAttacking(boolean isAttacking) { this.isAttacking = isAttacking; }
    public void setRespawning(boolean r) { this.isRespawning = r; }

    public void setGodmode(boolean gm) {
        System.out.println("Toggling god mode: " + gm);
        this.godmode = gm;
        if(gm){
            hitpoints = max_hitpoints;
            mover.setMoveSpeed(10.0f);
        }
        if(gm == false){
            mover.setMoveSpeed((double)level + 2.0f);
        }
    }

    public void tick() {

        if(godmode) hitpoints = max_hitpoints;

        if(hpRegenInterval < 180) hpRegenInterval++;
        else{
            adjustHp(hpRegenRate); //regenerate 10 hp
            hpRegenInterval = 0; //restart the count till next regen
        }

        //hero leveled
        if((xp >= xpToLevel) && (!maxLevel)){
            System.out.println("I am leveling up because my xp is " + xp + "/" + xpToLevel);
            levelUp();
        }

        //update the object's xy coordinates and attack status
        int[] xycoords;
        xycoords = mover.tick(x, y);
        setX(xycoords[0]);
        setY(xycoords[1]);
        this.isAttacking = mover.getIsAttacking();

        //only change sprite every 4 frames
        if((mover.getIsMoving() ) || isAttacking){
            if(animationInterval < 4){
                animationInterval++;
            }
            else {
                changeRunImage();
                animationInterval = 0;
            }
        }
        if(mover.getCollidedWithObject() && !isAttacking){
            spriteFrame = images.get(0); //if the hero has collided with object, freeze animation on first image of array
        }

        //System.out.println("isAttacking = " + isAttacking);
        if(isAttacking) continueAttack();
        else collision(); //if we aren't attack, check for collision
    }

    private void collision(){
        boolean collidedWithSomething = false;
        if(isAttacking) checkDeadThingsCollision();

        for(int i = 0; i < handler.getObject().size(); i++){
            GameObject temp = handler.getObject().get(i);

            //hero collides with a game object
            if(getBounds().intersects(temp.getBounds())) {

                //a radiant hero collides with an enemy building or creep
                if (this.getId() == ID.Player1) {
                    if((temp.getId() == ID.DireBase) || (temp.getId() == ID.DireFarm)
                        || (temp.getId() == ID.DireTower) || (temp.getId() == ID.DireCreep)
                        || (temp.getId() == ID.Player2)) {
                        if(!isAttacking) beginAttack();
                        myEnemy = (VulnerableObject) temp;
                        collidedWithSomething = true;
                    }

                    if((temp.getId() == ID.Nature) || (temp.getId() == ID.Wall) ||
                            (temp.getId() == ID.DireFarm) || (temp.getId() == ID.RadiantFarm) ||
                            (temp.getId() == ID.RadiantBase) || (temp.getId() == ID.RadiantTower)){
                        mover.setCollidedWithObject(true);
                        collidedWithSomething = true;
                        collisionHelper();
                    }
                }//ifOuter

                //if two heroes collide with each other
                if (((this.getId() == ID.Player1) && (temp.getId() == ID.Player2)) ||
                        ((this.getId() == ID.Player2) && (temp.getId() == ID.Player1))) {
                    collisionHelper();
                    collidedWithSomething = true;
                }

                //if hero collides with an invulnerable object
            }//intersects
        }//for

        if(isAttacking && !collidedWithSomething) stopAttacking(); //if we don't collide with anything but are attacking, stop attacking air
    }//collision

    public void collisionHelper(){
        x += mover.getVx() * -1;
        y += mover.getVy() * -1;
        mover.setMoving(false);
        isMoving = false;
    }

    public void continueAttack(){
        if(myEnemy == null) {
            stopAttacking();
            return;
        }

        //only do damage if the enemy is within reach. If not, leave combat and do other things
        if(getBounds().intersects(myEnemy.getBounds())) {
            if (damageInterval < 20) damageInterval++;
            else {
                myEnemy.adjustHp(-damage);
                Sounds.playSwordSound();
                damageInterval = 0;
            }
        }else {
            stopAttacking();
            myEnemy = null;
        }
    }

    public void beginAttack(){
        mover.setDestinationX(x);
        mover.setDestinationY(y);
        mover.setMoving(false);
        BufferedImage attackSheet = null;
        if(this.getId() == ID.Player1) { attackSheet = imageLoader.getImage("p1HeroAttackSheet"); }
        else if(this.getId() == ID.Player2){ attackSheet = imageLoader.getImage("p2HeroAttackSheet"); }
        this.images = pullAnimationSheet(attackSheet);
        isAttacking = true;
        mover.setAttacking(isAttacking);
    }

    public void changeRunImage(){
        if(animationFrameIndex == images.size()) animationFrameIndex = 0; //reset animation frame index to loop back to starting frame
        spriteFrame = images.get(animationFrameIndex); //cycle to the next image to display
        animationFrameIndex++;
    }

    public void checkDeadThingsCollision(){
        for(int i = 0; i < handler.getDeadThings().size(); i++){
            GameObject temp = handler.getDeadThings().get(i);

            //collided with a body
            if (getBounds().intersects(temp.getBounds())) {
                stopAttacking();
            }
        }
    }

    public void stopAttacking(){
        isAttacking = false; //attacking is false
        mover.setAttacking(false);//mover attacking is false
    }

    public void toggleRun(){
        mover.setAngle();
        //mover.setCollidedWithObject(false);
        images = pullAnimationSheet(getImage());
    }

    public ArrayList<BufferedImage> pullAnimationSheet(BufferedImage imageSheet){
        BufferedImage movementArray;
        int column = 0; //default starts at the beginning of first column

        //VERTICAL UP (col 0)
        if(mover.getAngle() >= 262 && mover.getAngle() < 278) column = MAX_BLOCK_SIZE * 0;

        //UP-RIGHT ANGLE (col 1)
        if(mover.getAngle() >= 278 && mover.getAngle() < 352) column = MAX_BLOCK_SIZE * 1;

        //HORIZONTAL RIGHT FACE (col 2)
        if(mover.getAngle() >= 352 || mover.getAngle() < 13) column = MAX_BLOCK_SIZE * 2;

        //DOWN-RIGHT ANGLE (col 3)
        if(mover.getAngle() >= 13 && mover.getAngle() < 82) column = MAX_BLOCK_SIZE * 3;

        //VERTICAL DOWN (col 4)
        if(mover.getAngle() >= 82 && mover.getAngle() < 98) column = MAX_BLOCK_SIZE * 4;

        //DOWN-LEFT ANGLE (col 5)
        if(mover.getAngle() >= 98 && mover.getAngle() < 178) column = MAX_BLOCK_SIZE * 5;

        //HORIZONTAL LEFT FACE (col 6)
        if(mover.getAngle() >= 172 && mover.getAngle() < 188) column = MAX_BLOCK_SIZE * 6;

        //UP-LEFT ANGLE (col 7)
        if(mover.getAngle() >= 188 && mover.getAngle() < 262) column = MAX_BLOCK_SIZE * 7;

        movementArray = imageSheet.getSubimage(column,0,MAX_BLOCK_SIZE, MAX_BLOCK_SIZE * (image.getHeight() / 64));
        images = imageLoader.getImageArray(movementArray, MAX_BLOCK_SIZE);
        //if(images.size() != 0) System.out.println("GameModifiers.Animation sheet successfully pulled");
        //else System.out.println("GameModifiers.Animation sheet pull FAIL");

        return images;
    }

    public void levelUp(){
        level++;
        max_hitpoints = level * 100;
        hitpoints = max_hitpoints;

        if(level == 6){
            maxLevel = true;
            xp = (level - 1) * 100;
            max_hitpoints += 400;
            hitpoints = max_hitpoints;
            hpRegenRate += 35;
        }
        else {
            System.out.println("I'm leveling up!");
            increaseXpToLevel();
        }
        damage += 5;
        hpRegenRate += 2;
        mover.setMoveSpeed(mover.getMoveSpeed() + 1.0);
        Sounds.playLvlUpSound();
    }

    @Override
    public void render(Graphics g) {
        int healthbarX = x + 8;
        int healthbarY = y - 8;
        int healthBarWidth;
        if(!maxLevel) healthBarWidth = hitpoints/(level * 2);
        else healthBarWidth = hitpoints / 20;
        int healthBarHeight = 5;

        //draw the health bar
        Color darkGreen = new Color(0, 64, 0);
        g.setColor(darkGreen);
        g.fillRect(healthbarX, healthbarY, HEALTH_BAR_MAX_WIDTH, healthBarHeight);
        g.setColor(Color.green);
        g.fillRect(healthbarX, healthbarY, healthBarWidth, healthBarHeight);
        g.setColor(Color.black);
        g.drawRect(healthbarX, healthbarY, HEALTH_BAR_MAX_WIDTH, healthBarHeight);

        g.drawImage(spriteFrame, x, y, null);

        //draw xp range area
        //g.setColor(Color.white);
        //g.drawRect(x - 288, y - 300, MAX_BLOCK_SIZE * 10, MAX_BLOCK_SIZE * 10);
    }

    @Override
    public Rectangle getBounds() {
        return new Rectangle(x + (MAX_BLOCK_SIZE / 4), y + (MAX_BLOCK_SIZE / 4), MAX_BLOCK_SIZE - (MAX_BLOCK_SIZE / 4), MAX_BLOCK_SIZE - (MAX_BLOCK_SIZE / 4));
    }

    @Override
    public Ellipse2D.Double vision(){
        return new Ellipse2D.Double(x + (MAX_BLOCK_SIZE / 4), y + (MAX_BLOCK_SIZE / 4), MAX_BLOCK_SIZE - (MAX_BLOCK_SIZE / 4), MAX_BLOCK_SIZE - (MAX_BLOCK_SIZE / 4));
    }

    public Ellipse2D.Double xpRange(){
        return new Ellipse2D.Double(x - 288, y - 300, MAX_BLOCK_SIZE * 10, MAX_BLOCK_SIZE * 10);
    }

}
