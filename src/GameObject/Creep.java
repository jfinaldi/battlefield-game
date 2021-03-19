package GameObject;
import GameModifiers.*;
import GameModifiers.ID;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;

public class Creep extends MobileObject {

    private static final int BLOCK_SIZE = 36;
    private static final int HEALTH_BAR_MAX_WIDTH = 50;
    private int animationInterval = 0;
    private int animationFrameIndex = 0;
    private ArrayList<BufferedImage> images;
    private BufferedImage movementArray;
    private BufferedImage spriteFrame;
    private VulnerableObject myEnemy;
    private int[][] pathSequence;
    private int pathLegIndex = 0;
    private int damage = 4;
    private int damageInterval = 0;
    private boolean pathLegFinished = false;
    private boolean lockedOntoEnemy = false;
    private boolean isAttacking = false;

    public Creep(int x, int y, ID id, GameObjectManager handler, BufferedImageLoader imageLoader, BufferedImage image) {
        super(x, y, id, image, handler, imageLoader, 100, 1);
        this.images = new ArrayList<>();
        this.spriteFrame = image.getSubimage(BLOCK_SIZE * 2, 0, BLOCK_SIZE, BLOCK_SIZE);

        //if this is a dire creep, it will be a little stronger
        if(this.getId() == ID.DireCreep){
            damage *= 4;
            hitpoints *= 4;
        }

        //initiate a default path sequence for a creep
        initiatePathSequence();
        toggleRun();
    }

    public VulnerableObject getMyEnemy() { return this.myEnemy; }
    public void setMyEnemy(VulnerableObject v) { this.myEnemy = v; }

    @Override
    public void tick() {
        Random rand = new Random();
        mover.setMoveSpeed(1.0 + rand.nextDouble());

        int[] xycoords;
        xycoords = mover.tick(x, y); //update the object's xy coordinates
        setX(xycoords[0]);
        setY(xycoords[1]);
        this.isAttacking = mover.getIsAttacking();

        if(mover.getIsMoving() || isAttacking){
            if(animationInterval < 4){ //only change sprite every 4 frames
                animationInterval++;
            }
            else {
                changeRunImage();
                animationInterval = 0;
            }
        }
        if(mover.getCollidedWithObject()){
            spriteFrame = images.get(0); //if the hero has collided with object, freeze animation on first image of array
        }

        //if creep reached one leg of the trip, change to the next leg
        pathLegFinished = mover.getDestinationReached();
        if(pathLegFinished && !lockedOntoEnemy){
            if(pathLegIndex < 2) pathLegIndex++;
            resumePath();
        }

        //only if we are not attacking do we check for
        //something to kill or check for collision with object
        if(isAttacking) continueAttack();
        else {
            detectAggro();
            collision();
        }
    }

    public void detectAggro(){

        //check vision radii
        for(int i = 0; i < handler.getObject().size(); i++) {
            GameObject temp = handler.getObject().get(i);
            if(temp instanceof VulnerableObject) {  //WORKS
                VulnerableObject vulObj = (VulnerableObject) temp; //downcast

                //this object sees a vulnerable object
                if (vulObj.vision().getBounds2D().intersects(this.vision().getBounds2D())) {

                    //if this is a radiant creep object
                    if(this.getId() == ID.RadiantCreep) {

                        //this object sees an enemy
                        if (vulObj.getId() == ID.DireTower || vulObj.getId() == ID.DireCreep
                                || vulObj.getId() == ID.DireBase || vulObj.getId() == ID.Player2) {
                            //set this creep's destination coordinates to CENTER of vulObj
                            mover.setDestinationX(vulObj.getX() + 5);
                            mover.setDestinationY(vulObj.getY() + 5);
                            mover.setAngle();
                            pullAnimationSheet(this.image);
                            lockedOntoEnemy = true;
                        }
                    }

                    //if this is a dire creep object
                    if(this.getId() == ID.DireCreep) {

                        //this object sees an enemy
                        if (vulObj.getId() == ID.RadiantTower || vulObj.getId() == ID.RadiantCreep
                                || vulObj.getId() == ID.RadiantBase || vulObj.getId() == ID.Player1) {
                            mover.setDestinationX(vulObj.getX() + 5);
                            mover.setDestinationY(vulObj.getY() + 5);
                            mover.setAngle();
                            pullAnimationSheet(this.image);
                            lockedOntoEnemy = true;
                        }
                    }
                }//intersects
            }//instanceof
        }//for
    }

    public void collision(){
        boolean collidedWithSomething = false;
        if(isAttacking) checkDeadTowerCollision();

        for (int i = 0; i < handler.getObject().size(); i++) {
            GameObject temp = handler.getObject().get(i);

            //creep collides with anything
            if (getBounds().intersects(temp.getBounds())) {

                //friendly collisions
                //a creep collides with a building or tree
                if (this.getId() == ID.RadiantCreep || this.getId() == ID.DireCreep) {
                    if ((temp.getId() == ID.Nature) || (temp.getId() == ID.Wall)) {
                        collidedWithSomething = true;
                    }
                }

                //if two creeps collide with each other
                if (((this.getId() == ID.RadiantCreep) && (temp.getId() == ID.DireCreep)) ||
                        ((this.getId() == ID.DireCreep) && (temp.getId() == ID.RadiantCreep))) {
                    if(!isAttacking) beginAttack();
                    myEnemy = (VulnerableObject)temp;
                    collidedWithSomething = true;
                }

                //if creep collides with an enemy tower
                if (((this.getId() == ID.RadiantCreep) && (temp.getId() == ID.DireTower) && lockedOntoEnemy)
                    || ((this.getId() == ID.DireCreep) && (temp.getId() == ID.RadiantTower) && lockedOntoEnemy)){
                    if(!isAttacking) beginAttack();
                    myEnemy = (VulnerableObject) temp;
                    collidedWithSomething = true;
                }

                //if creep collides with an enemy base
                if (((this.getId() == ID.RadiantCreep) && (temp.getId() == ID.DireBase) && lockedOntoEnemy)
                        || ((this.getId() == ID.DireCreep) && (temp.getId() == ID.RadiantBase) && lockedOntoEnemy)){
                    if(!isAttacking) beginAttack();
                    myEnemy = (VulnerableObject) temp;
                    collidedWithSomething = true;
                }

                //creep collides with a player
                if(this.getId() == ID.DireCreep){
                    if((temp.getId() == ID.Player1) && lockedOntoEnemy){
                        if (!isAttacking) beginAttack();
                        myEnemy = (VulnerableObject) temp;
                        collidedWithSomething = true;
                    }
                }
            }//.intersects
        }//for
        if(isAttacking && !collidedWithSomething) stopAttacking(); //if we don't collide with anything but are attacking, stop attacking air
        else if(mover.getIsMoving() == false && !collidedWithSomething) stopAttacking(); //unstick if stuck
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
                if((myEnemy instanceof Tower) || (myEnemy instanceof Base))
                    Sounds.playCreepHitSound();
                damageInterval = 0;
            }
        }else {
            stopAttacking();
            myEnemy = null;
        }
    }

    public void checkDeadTowerCollision(){
        for(int i = 0; i < handler.getDeadThings().size(); i++){
            GameObject temp = handler.getDeadThings().get(i);

            //creep collides with a dead thing
            if (getBounds().intersects(temp.getBounds())) {
                stopAttacking();
            }
        }
    }

    public void beginAttack(){

        //destination is now current location, stop moving
        mover.setDestinationX(x);
        mover.setDestinationY(y);
        mover.setMoving(false);

        //get the full attack sheet
        BufferedImage attackSheet = null;
        if(this.getId() == ID.RadiantCreep) attackSheet = imageLoader.getImage("radiantCreepAttackSheet");
        else if(this.getId() == ID.DireCreep) attackSheet = imageLoader.getImage("direCreepAttackSheet");

        //break down the full sheet into an array of the appropriate column of attacks
        this.images = pullAnimationSheet(attackSheet);
        animationInterval = 0;
        animationFrameIndex = 0;

        //set attack-phase
        isAttacking = true;
        mover.setAttacking(isAttacking);
    }

    public void stopAttacking(){
        //if path leg not finished, reset path coordinates to mover destination coords
        isAttacking = false; //attacking is false
        mover.setAttacking(false);//mover attacking is false
        if(!pathLegFinished) resumePath();
        lockedOntoEnemy = false;
        damageInterval = 0;
    }

    public void resumePath(){
        mover.setDestinationX(pathSequence[pathLegIndex][0]);
        mover.setDestinationY(pathSequence[pathLegIndex][1]);
        toggleRun();
        pathLegFinished = false;
        mover.setDestinationReached(false);
        mover.setMoving(true);
    }

    public void toggleRun(){
        mover.setMoveSpeed(1.0f);
        mover.setMoving(true);
        mover.setAngle();
        //mover.setCollidedWithObject(false);
        images = pullAnimationSheet(getImage());
    }

    public ArrayList<BufferedImage> pullAnimationSheet(BufferedImage imageSheet){
        ArrayList<BufferedImage> result;
        int column = 0; //default starts at the beginning of first column

        //VERTICAL UP (col 0)
        if(mover.getAngle() >= 262 && mover.getAngle() < 278) column = BLOCK_SIZE * 0;

        //UP-RIGHT ANGLE (col 1)
        if(mover.getAngle() >= 278 && mover.getAngle() < 352) column = BLOCK_SIZE * 1;

        //HORIZONTAL RIGHT FACE (col 2)
        if(mover.getAngle() >= 352 || mover.getAngle() < 13) column = BLOCK_SIZE * 2;

        //DOWN-RIGHT ANGLE (col 3)
        if(mover.getAngle() >= 13 && mover.getAngle() < 82) column = BLOCK_SIZE * 3;

        //VERTICAL DOWN (col 4)
        if(mover.getAngle() >= 82 && mover.getAngle() < 98) column = BLOCK_SIZE * 4;

        //DOWN-LEFT ANGLE (col 5)
        if(mover.getAngle() >= 98 && mover.getAngle() < 178) column = BLOCK_SIZE * 5;

        //HORIZONTAL LEFT FACE (col 6)
        if(mover.getAngle() >= 172 && mover.getAngle() < 188) column = BLOCK_SIZE * 6;

        //UP-LEFT ANGLE (col 7)
        if(mover.getAngle() >= 188 && mover.getAngle() < 262) column = BLOCK_SIZE * 7;

        //break up the full sheet into one column based on the hero angle
        movementArray = imageSheet.getSubimage(column,0, BLOCK_SIZE, image.getHeight());

        //break up the column into an array of individual image frames
        result = imageLoader.getImageArray(movementArray, BLOCK_SIZE);

        this.images = result;

        return result;
    }

    public void changeRunImage(){
        animationFrameIndex++;
        if(animationFrameIndex == images.size()) animationFrameIndex = 0; //reset animation frame index to loop back to starting frame
        spriteFrame = images.get(animationFrameIndex); //cycle to the next image to display
    }

    private void initiatePathSequence(){
        this.pathSequence = new int[3][2];
        if(this.getId() == ID.RadiantCreep) {
            pathSequence[0][0] = 4045;
            pathSequence[0][1] = 543;
            pathSequence[1][0] = 3981;
            pathSequence[1][1] = 2285;
            pathSequence[2][0] = 445;
            pathSequence[2][1] = 2333;
        }else if(this.getId() == ID.DireCreep){
            pathSequence[0][0] = 3981;
            pathSequence[0][1] = 2285;
            pathSequence[1][0] = 3890;
            pathSequence[1][1] = 545;
            pathSequence[2][0] = 461;
            pathSequence[2][1] = 532;
        }
        mover.setDestinationX(pathSequence[0][0]);
        mover.setDestinationY(pathSequence[0][1]);
    }

    public Ellipse2D.Double vision() {
        return new Ellipse2D.Double(x - 122, y - 120, 275, 275);
    }

    @Override
    public void render(Graphics g) {
        int healthbarX = x - 6;
        int healthbarY = y - 8;
        int healthBar = 50;
        int healthBarHeight = 5;
        Color background = Color.black;
        Color foreground = Color.white;

        //draw selection circle when mouse hovers over unit
        g.setColor(Color.red);
        if(showRedBox) g.drawOval(x - 4, y + 18, 36, 18);

        //get Healthbar colors
        if(this.getId() == ID.RadiantCreep) {
            healthBar = hitpoints / 2;
            background = new Color(0, 64, 0); //dark green
            foreground = Color.green;
        }
        if(this.getId() == ID.DireCreep){
            healthBar = hitpoints / 8;
            background = new Color(64, 0, 0);
            foreground = new Color(200, 0, 0);
        }

        //draw healthbar
        g.setColor(background);
        g.fillRect(healthbarX, healthbarY, HEALTH_BAR_MAX_WIDTH, healthBarHeight);
        g.setColor(foreground);
        g.fillRect(healthbarX, healthbarY, healthBar, healthBarHeight);
        g.setColor(Color.black);
        g.drawRect(healthbarX, healthbarY, HEALTH_BAR_MAX_WIDTH, healthBarHeight);

        //draw the creep
        g.drawImage(spriteFrame, x, y, null);

        //draw vision stuff
        //g.setColor(Color.white);
        //g.drawRect(x - 122, y - 120,275, 275); //draw the vision area
        //g.drawRect(x + ((BLOCK_SIZE / 6)), y + ((BLOCK_SIZE / 6)), BLOCK_SIZE / 2, BLOCK_SIZE / 2); //draw hitbox
    }

    @Override
    public Rectangle getBounds() {
        return new Rectangle(x + ((BLOCK_SIZE / 6)), y + ((BLOCK_SIZE / 6)), BLOCK_SIZE / 2, BLOCK_SIZE / 2);
    }
}
