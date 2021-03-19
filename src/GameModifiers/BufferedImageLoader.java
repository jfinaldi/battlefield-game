package GameModifiers;
/**************************************
 This class is responsible for loading
 images from res folder and storing them
 to be retrieved by other parts of the
 program.
 Idea for this class came from Zach
 from codingmadesimple.com
 **************************************/
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import GameObject.*;

public class BufferedImageLoader{

    public HashMap<String, BufferedImage> imageLibrary;

    public BufferedImageLoader(){
        imageLibrary = new HashMap<>();
        BufferedImage image;

        //cycle through enums and add values to hashmap
        for(GameObjectInfo entry : GameObjectInfo.values()){
            String id = entry.getID(); //pull the label from enum
            String imagePath = entry.getPath(); //pull the path from enum
            image = loadImage(imagePath); //generate a bufferedImage from this information
            imageLibrary.put(id, image); //stores the object in hashmap
        }//for
    }

    public BufferedImage getImage(String imageName){
        BufferedImage anImage;
        anImage = imageLibrary.get(imageName);
        return anImage;
    }

    public BufferedImage loadImage(String path){
        BufferedImage image = null;
        try {
            image = ImageIO.read(getClass().getResource(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }

    public ArrayList<BufferedImage> getImageArray(BufferedImage temp, int blockSize){
        ArrayList<BufferedImage> images = new ArrayList<>();
        int width = temp.getWidth();
        int height = temp.getHeight();

        //parse this animation sheet into array of subimages, horizontally like a book
        for(int x = 0; x < width; x += blockSize){
            for(int y = 0; y < height; y += blockSize){
                BufferedImage subImage = temp.getSubimage(x, y, blockSize, blockSize);
                images.add(subImage);
            }
        }
        return images;
    }

}
