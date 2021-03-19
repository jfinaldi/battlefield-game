package GameModifiers; /******************************************
 This class handles all of the game
 sounds including loading and playing both
 individual sounds as well as game music.
 playGameMusic and playSound code here
 found on youtube comment
 https://youtu.be/HRaJXVuZjRM
 ******************************************/

import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.HashMap;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class Sounds {

    private static Clip clip;
    private HashMap<String, AudioInputStream> soundLibrary;

    public Sounds(){}

    public void init(){}

    public static void playGameMusic(){
        try{
            BufferedInputStream myStream = new BufferedInputStream(Sounds.class.getResourceAsStream("/sounds/gameMusic.wav"));
            AudioInputStream menuSound = AudioSystem.getAudioInputStream(myStream);
            clip = AudioSystem.getClip();
            clip.open(menuSound);
            FloatControl volume = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            volume.setValue(-15.0f);
            clip.loop(Clip.LOOP_CONTINUOUSLY);
        }catch(LineUnavailableException | IOException | UnsupportedAudioFileException e){
            e.printStackTrace();
        }
    }

    public static void playSound(String path){
        try{
            BufferedInputStream myStream = new BufferedInputStream(Sounds.class.getResourceAsStream(path));
            AudioInputStream sound = AudioSystem.getAudioInputStream(myStream);
            clip = AudioSystem.getClip();
            clip.open(sound);
            FloatControl volume = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            volume.setValue(-10.0f);
            clip.loop(0);
        }catch(LineUnavailableException | IOException | UnsupportedAudioFileException e){
            e.printStackTrace();
        }
    }

    public static void playFireHitSound(){
        playSound("/sounds/Firehit.wav");
    }
    public static void playBurningSound() { playSound("/sounds/Burning.wav"); }
    public static void playCreepHitSound(){
        playSound("/sounds/creepHit.wav");
    }
    public static void playDireDeathSound() { playSound("/sounds/direDeath.wav"); }
    public static void playRadiantDeathSound() { playSound("/sounds/radiantDeath.wav"); }
    public static void playFallingBaseSound(){
        playSound("/sounds/fallingBase.wav");
    }
    public static void playFallingTowerSound(){
        playSound("/sounds/fallingTower.wav");
    }
    public static void playP1RespawnSound(){
        playSound("/sounds/p1Respawn.wav");
    }
    public static void playLvlUpSound(){
        playSound("/sounds/lvlUp.wav");
    }

    public static void playSwordSound(){
        int randomDrawOfThree = 1 + ((int) (3 * Math.random()));
        switch(randomDrawOfThree) {
            case 1:
                playSound("/sounds/Sword1.wav");
                return;
            case 2:
                playSound("/sounds/Sword2.wav");
                return;
            case 3:
                playSound("/sounds/Sword3.wav");
                return;
        }
    }

    public static void stopMusic(){
        clip.close();
    }

}
