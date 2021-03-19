package GameModifiers;
/**********************************
GameModifiers.Movement handles all of the math
 operations pertaining to vector
 calculations for point-click
 movement for all mobile objects.
 It takes in x,y starting coordinates
 from its moveable character, calculates
 its new coordinates, then gives them
 back to the object.
 **********************************/
import GameObject.*;

public class Movement {

    private int x;
    private int y;
    private int destinationX;
    private int destinationY;
    private int destinationQuadrant;
    private int changeInX;
    private int changeInY;
    private int angle = 0;
    private boolean isMoving;
    private boolean isAttacking;
    private boolean hasCollidedWithObject;
    private boolean destinationReached;
    private double distance;
    private double vx;
    private double vy;
    private double moveSpeed = 2.0f;

    public Movement(int x, int y){
        this.x = x;
        this.y = y;
        this.vx = 0;
        this.vy = 0;
        this.destinationX = x;
        this.destinationY = y;
        this.destinationQuadrant = 0;
        this.isMoving = false;
        this.isAttacking = false;
        this.hasCollidedWithObject = false;
        this.destinationReached = false;
    }

    public double getVx() { return this.vx; }
    public double getVy() { return this.vy; }
    public double getMoveSpeed() { return this.moveSpeed; }
    public int getAngle(){ return this.angle; }
    public boolean getIsMoving() { return isMoving; }
    public boolean getIsAttacking() { return isAttacking; }
    public boolean getCollidedWithObject() { return hasCollidedWithObject; }
    public boolean getDestinationReached() { return destinationReached; }

    public void setMoving(boolean isMoving) { this.isMoving = isMoving; }
    public void setAttacking(boolean isAttacking) { this.isAttacking = isAttacking; }
    public void setMoveSpeed(double speed) { this.moveSpeed = speed; }
    public void setCollidedWithObject(boolean col) { this.hasCollidedWithObject = col; }
    public void setDestinationX(int x) { this.destinationX = x; }
    public void setDestinationY(int y) { this.destinationY = y; }
    public void setDestinationReached(boolean x) { this.destinationReached = x; }

    public int[] tick(int x, int y){

        //update any collision-corrected x,y coords
        this.x = x;
        this.y = y;

        if((x != destinationX || y != destinationY)) {
            if((!isAttacking) && (!hasCollidedWithObject)) {
                setMoving(true);
            }
            if(isAttacking){
                destinationX = x;
                destinationY = y;
                setMoving(false);
                setCollidedWithObject(false);
            }
        }

        if(this.isMoving) { moveForwards(); }

        int[] xycoords = new int[2];
        xycoords[0] = this.x;
        xycoords[1] = this.y;
        return xycoords;
    }

    public void setAngle() {
        double slope;
        changeInX = destinationX - (x);
        changeInY = destinationY - (y);

        //slope is undefined
        if (changeInX == 0 && (changeInY > 0)) { angle = 90; }
        else if (changeInX == 0 && (changeInY < 0)) { angle = 270; }

        //slope is zero
        else if (changeInY == 0) {
            if (destinationX > x) angle = 0;
            else angle = 180;
        }

        //slope is diagonal
        else {
            slope = ((double)changeInY / (double)changeInX);
            angle = (int) Math.toDegrees(Math.atan(slope));
            getDestinationQuadrant();

            switch (destinationQuadrant) {
                case 2: {
                    angle = 180 - Math.abs(angle);
                    break;
                }
                case 3: {
                    angle += 180;
                    break;
                }
                case 4: {
                    angle = 360 - Math.abs(angle);
                }
            }//switch
        }//else

        if(angle == 360) angle = 0;
    }

    private void getDestinationQuadrant(){
        if((destinationX > x) && (destinationY > y)) destinationQuadrant = 1;
        if((destinationX < x) && (destinationY > y)) destinationQuadrant = 2;
        if((destinationX < x) && (destinationY < y)) destinationQuadrant = 3;
        if((destinationX > x) && (destinationY < y)) destinationQuadrant = 4;
    }

    private void moveForwards() {
        distance = Math.sqrt((Math.pow(changeInX, 2)) + (Math.pow(changeInY, 2)));

        if(distance > moveSpeed){
            double ratio = moveSpeed / distance;
            vx = ratio * changeInX;
            vy = ratio * changeInY;

            //avoid truncation
            vx = Math.round(vx);
            vy = Math.round(vy);
            x += (int) vx;
            y += (int) vy;
        } else {
            x = destinationX;
            y = destinationY;
        }

        changeInX = destinationX - x; //results in integer
        changeInY = destinationY - y;
        distance = Math.sqrt((Math.pow(changeInX, 2)) + (Math.pow(changeInY, 2)));
        if(distance >= 0 && distance <= moveSpeed){
            setMoving(false);
            setDestinationReached(true);
        }
    }
}
