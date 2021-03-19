package GameModifiers; /*****************************************
 This class handles the loading of the
 entire game map. It reads each pixel of
 an image of a level design and creates
 objects to store into the
 GameModifiers.GameObjectManager.
 *****************************************/

import GameObject.*;

import java.awt.*;
import java.awt.image.BufferedImage;

public class MapLoader {

    private static final int BLOCK_SIZE = 32;
    private BufferedImage level; //the bitmap level design
    private BufferedImageLoader imageLoader;
    private GameObjectManager handler;
    private int width;
    private int height;

    public MapLoader(BufferedImageLoader imageLoader, GameObjectManager handler){
        this.handler = handler;
        this.imageLoader = imageLoader;
        this.level = imageLoader.imageLibrary.get("levelDesign");
    }

    public int getWidth() {
        return width;
    }
    public int getHeight() {
        return height;
    }

    public void setWidth(int width) {
        this.width = width;
    }
    public void setHeight(int height) {
        this.height = height;
    }

    //modified from Wizard Top Down Shooter videos by Zach from codingmadesimple.com
    //aka realTutsGML on youtube
    protected void loadLevel(){
        int w = level.getWidth();
        int h = level.getHeight();

        this.setHeight(h * BLOCK_SIZE);
        this.setWidth(w * BLOCK_SIZE);

        for(int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                int pixel = level.getRGB(x, y);

                //break down each pixel into its rgb components
                int red = (pixel >> 16) & 0xff;   //pixel >> 16 AND 11111111
                int green = (pixel >> 8) & 0xff;
                int blue = (pixel) & 0xff;

                //thick Tree tile
                if (red == 255 && green == 0 && blue == 0)
                    handler.addObject(new NatureBlock(x * BLOCK_SIZE, y * BLOCK_SIZE, ID.Nature, imageLoader.getImage("thickTrees")));

                //top left tree block
                if (red == 128 && green == 64 && blue == 0)
                    handler.addObject(new NatureBlock(x * BLOCK_SIZE, y * BLOCK_SIZE, ID.Nature, imageLoader.getImage("topLeftTree")));

                //top right tree block
                if (red == 64 && green == 64 && blue == 0)
                    handler.addObject(new NatureBlock(x * BLOCK_SIZE, y * BLOCK_SIZE, ID.Nature, imageLoader.getImage("topRightTree")));

                //left edge tree A
                if (red == 64 && green == 128 && blue == 128)
                    handler.addObject(new NatureBlock(x * BLOCK_SIZE, y * BLOCK_SIZE, ID.Nature, imageLoader.getImage("leftEdgeTreeA")));

                //left edge tree B
                if (red == 128 && green == 128 && blue == 64)
                    handler.addObject(new NatureBlock(x * BLOCK_SIZE, y * BLOCK_SIZE, ID.Nature, imageLoader.getImage("leftEdgeTreeB")));

                //right edge tree A
                if (red == 64 && green == 0 && blue == 64)
                    handler.addObject(new NatureBlock(x * BLOCK_SIZE, y * BLOCK_SIZE, ID.Nature, imageLoader.getImage("rightEdgeTreeA")));

                //right edge tree B
                if (red == 64 && green == 64 && blue == 64)
                    handler.addObject(new NatureBlock(x * BLOCK_SIZE, y * BLOCK_SIZE, ID.Nature, imageLoader.getImage("rightEdgeTreeB")));

                //bottom center tree
                if (red == 128 && green == 0 && blue == 64)
                    handler.addObject(new NatureBlock(x * BLOCK_SIZE, y * BLOCK_SIZE, ID.Nature, imageLoader.getImage("bottomCenterTree")));

                //goldmine
                if (red == 0 && green == 128 && blue == 0)
                    handler.addObject(new NatureBlock(x * BLOCK_SIZE, y * BLOCK_SIZE, ID.Nature, imageLoader.getImage("goldMine")));

                //p1Tower
                if (red == 255 && green == 128 && blue == 0)
                    handler.addObject(new Tower(x * BLOCK_SIZE, y * BLOCK_SIZE, ID.RadiantTower, handler, imageLoader, imageLoader.getImage("p1Tower")));

                //p2Tower
                if (red == 255 && green == 0 && blue == 255)
                    handler.addObject(new Tower(x * BLOCK_SIZE, y * BLOCK_SIZE, ID.DireTower, handler, imageLoader, imageLoader.getImage("p2Tower")));

                //p1Base
                if (red == 0 && green == 255 && blue == 0)
                    handler.addObject(new Base(x * BLOCK_SIZE, y * BLOCK_SIZE, ID.RadiantBase, handler, imageLoader, imageLoader.getImage("p1Base")));

                //p2Base
                if (red == 0 && green == 0 && blue == 255)
                    handler.addObject(new Base(x * BLOCK_SIZE, y * BLOCK_SIZE, ID.DireBase, handler, imageLoader, imageLoader.getImage("p2Base")));

                //p1Hero
                if (red == 0 && green == 255 && blue == 255)
                    handler.addObject(new Hero(x * BLOCK_SIZE, y * BLOCK_SIZE, ID.Player1, handler, imageLoader, imageLoader.getImage("p1HeroWalkSheet")));

                //p2Hero
                if (red == 255 && green == 255 && blue == 0)
                    //handler.addObject(new GameObject.Hero(x * BLOCK_SIZE, y * BLOCK_SIZE, GameModifiers.ID.Player2, handler, imageLoader, imageLoader.getImage("p2HeroSheet")));

                //Player1 farm
                if (red == 128 && green == 64 && blue == 128)
                    handler.addObject(new Farm(x * BLOCK_SIZE, y * BLOCK_SIZE, ID.RadiantFarm, handler, imageLoader, imageLoader.getImage("p1Farm")));

                //Player2 farm
                if (red == 64 && green == 128 && blue == 64)
                    handler.addObject(new Farm(x * BLOCK_SIZE, y * BLOCK_SIZE, ID.DireFarm, handler, imageLoader, imageLoader.getImage("p2Farm")));

                //Top GameObject.Wall
                if (red == 128 && green == 255 && blue == 0)
                    handler.addObject(new Wall(x * BLOCK_SIZE, y * BLOCK_SIZE, ID.Wall, imageLoader.getImage("topWall")));

                //Mid GameObject.Wall
                if (red == 0 && green == 128 && blue == 255)
                    handler.addObject(new Wall(x * BLOCK_SIZE, y * BLOCK_SIZE, ID.Wall, imageLoader.getImage("midWall")));

                //Bot GameObject.Wall
                if (red == 128 && green == 0 && blue == 255)
                    handler.addObject(new Wall(x * BLOCK_SIZE, y * BLOCK_SIZE, ID.Wall, imageLoader.getImage("botWall")));

            }//forY
        }//forX
        System.out.println("MAP SUCCESSFULLY LOADED...");

    }//loadLevel

    public void drawFloor(int width, int height, Graphics2D buffer){
        BufferedImage grass = imageLoader.getImage("grass");

        //draw grass base
        for(int x = 0; x < width; x += BLOCK_SIZE){
            for(int y = 0; y <= height; y += BLOCK_SIZE)
                buffer.drawImage(grass, x, y, null);
        }

        //draw dirts
        for(int x = 0; x < level.getWidth(); x++) {
            for (int y = 0; y < level.getHeight(); y++) {
                int pixel = level.getRGB(x, y);

                //break down each pixel into its rgb components
                int red = (pixel >> 16) & 0xff;   //pixel >> 16 AND 11111111
                int green = (pixel >> 8) & 0xff;
                int blue = (pixel) & 0xff;

                //rocks
                if (red == 0 && green == 255 && blue == 128)
                    buffer.drawImage(imageLoader.getImage("rocks"), x * BLOCK_SIZE, y * BLOCK_SIZE, null);

                //top left dirt
                if (red == 128 && green == 128 && blue == 128)
                    buffer.drawImage(imageLoader.getImage("topLeftDirt"), x * BLOCK_SIZE, y * BLOCK_SIZE, null);

                //top mid dirt
                if (red == 255 && green == 255 && blue == 255)
                    buffer.drawImage(imageLoader.getImage("topMidDirt"), x * BLOCK_SIZE, y * BLOCK_SIZE, null);

                //top right dirt
                if (red == 128 && green == 0 && blue == 128)
                    buffer.drawImage(imageLoader.getImage("topRightDirt"), x * BLOCK_SIZE, y * BLOCK_SIZE, null);

                //left dirt
                if (red == 0 && green == 64 && blue == 0)
                    buffer.drawImage(imageLoader.getImage("leftDirt"), x * BLOCK_SIZE, y * BLOCK_SIZE, null);

                //center dirt
                if (red == 0 && green == 64 && blue == 64)
                    buffer.drawImage(imageLoader.getImage("centerDirt"), x * BLOCK_SIZE, y * BLOCK_SIZE, null);

                //right dirt
                if (red == 0 && green == 128 && blue == 128)
                    buffer.drawImage(imageLoader.getImage("rightDirt"), x * BLOCK_SIZE, y * BLOCK_SIZE, null);

                //bot left dirt
                if (red == 128 && green == 128 && blue == 0)
                    buffer.drawImage(imageLoader.getImage("botLeftDirt"), x * BLOCK_SIZE, y * BLOCK_SIZE, null);

                //bot mid dirt
                if (red == 128 && green == 64 && blue == 64)
                    buffer.drawImage(imageLoader.getImage("botMidDirt"), x * BLOCK_SIZE, y * BLOCK_SIZE, null);

                //bot right dirt
                if (red == 64 && green == 64 && blue == 128)
                    buffer.drawImage(imageLoader.getImage("botRightDirt"), x * BLOCK_SIZE, y * BLOCK_SIZE, null);

            }
        }
    }
}

