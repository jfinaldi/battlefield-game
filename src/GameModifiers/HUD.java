package GameModifiers;
/********************************************
 GameModifiers.HUD class is a heads-up-display class that
 renders all of the current individual player
 info, such as lives count, stats, xp, portrait
 and minimap.
 ********************************************/
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

public class HUD{

    private GameMod gameMod;
    private BufferedImage p1Portrait;
    private BufferedImage miniMap;

    public HUD(GameMod gameMod, BufferedImage p1Portrait){
        this.gameMod = gameMod;
        this.p1Portrait = p1Portrait;
    }

    public void render(Graphics g, BufferedImage world){

        int xpBarXCoord = 115;
        int xpBarYCoord = 255;
        int xpBarMaxWidth = 200;
        int xpBarWidth;
        if (gameMod.getHero1().getLevel() < 6)
            xpBarWidth = (gameMod.getHero1().getXp() - Math.abs((gameMod.getHero1().getLevel() - 1) * 100)) * 2;
        else xpBarWidth = xpBarMaxWidth;
        int xpBarHeight = 50;
        int xpTextX = 119;
        int xpTextY = 302;
        int miniMapXCoord = 49;
        int miniMapYCoord = 40;

        int p1PortraitX = 15;
        int p1PortraitY = 250;
        int p1NameTextX = 18;
        int p1NameTextY = 330;

        int levelTextX = 260;
        int levelTextY = 325;

        int statsBoxX = 20;
        int statsBoxY = 360;
        int statsBoxWidth = 175;
        int statsBoxHeight = 125;
        int statX = statsBoxX + 2 + 5;
        int hpStatY = statsBoxY + 2 + 15;
        int dmgStatY = hpStatY + 15;
        int speedStatY = dmgStatY + 15;

        Graphics2D g2d = (Graphics2D) g;
        Font font = new Font("Verdana", Font.PLAIN, 13);
        Font boldFont = new Font("Verdana", Font.BOLD, 13);
        Font playerFont = new Font("Verdana", Font.BOLD, 18);
        g2d.setFont(boldFont);
        miniMap = getMiniMap(world); //get the minimap image
        g.setColor(Color.black);

        //draw the xp bar
        g.fillRect(xpBarXCoord - 3, xpBarYCoord - 3, xpBarMaxWidth + 6, xpBarHeight + 6);
        g.setColor(Color.gray);
        g.fillRect(xpBarXCoord, xpBarYCoord, xpBarMaxWidth, xpBarHeight);
        if(gameMod.getHero1().isMaxLevel()) g.setColor(new Color(100, 0, 255));
        else g.setColor(Color.blue);
        g.fillRect(xpBarXCoord, xpBarYCoord, xpBarWidth, xpBarHeight);

        //draw player name and xp texts
        g2d.setColor(Color.white);
        g2d.setFont(font);
        g2d.drawString("xp: " + gameMod.getHero1().getXp() + "/" + gameMod.getHero1().getXpToLevel(), xpTextX, xpTextY);
        g2d.setFont(boldFont);
        g2d.drawString("Level: " + gameMod.getHero1().getLevel(), levelTextX, levelTextY);

        //Player1 Portrait
        g.setColor(Color.black);
        g.fillRect(p1PortraitX, p1PortraitY, p1Portrait.getWidth(), p1Portrait.getHeight());
        g2d.drawImage(p1Portrait, p1PortraitX, p1PortraitY, null);
        g.setColor(Color.white);
        g.setFont(playerFont);
        g.drawString("Luthor", p1NameTextX, p1NameTextY);

        //draw stats
        g.setColor(Color.black);
        g.fillRect(statsBoxX, statsBoxY, statsBoxWidth, statsBoxHeight);
        g.setColor(Color.lightGray);
        g.fillRect(statsBoxX + 2, statsBoxY + 2, statsBoxWidth - 4, statsBoxHeight - 4);
        g.setColor(Color.black);
        g.setFont(boldFont);
        g.drawString("Hitpoints = " + gameMod.getHero1().getHitPoints() + "/" + gameMod.getHero1().getMaxHp(), statX, hpStatY);
        g.drawString("DPS = " + gameMod.getHero1().getDamage() * 3, statX, dmgStatY);
        g.drawString("Speed = " + (int) gameMod.getHero1().getMover().getMoveSpeed(), statX, speedStatY);

        //if god mode on, let the interface show that
        if(gameMod.getHero1().isGodmode()){
            g.setColor(new Color(128, 0, 64));
            g.setFont(boldFont);
            g.drawString("GOD MODE ENABLED", statX, speedStatY + 15);
        }

        g2d.drawImage(miniMap, miniMapXCoord, miniMapYCoord, null);
    }

    //algorithm for transform found on
    //programcreek.com/java-api-examples/?class=java.awt.geom.AffineTransform&method=scale
    public BufferedImage getMiniMap(BufferedImage world){
        int snapshotWidth = 1518;
        int snapshotHeight = 1136;

        int x = gameMod.getHero1().getX() - (snapshotWidth / 2);
        int y = gameMod.getHero1().getY() - (snapshotHeight / 2);

        //calibrate the x,y so they don't go outside the world bounds
        int minX = 0;
        int minY = 0;
        int maxX = world.getWidth() - snapshotWidth - 1;
        int maxY = world.getHeight() - snapshotHeight - 1;
        if(x < minX) x = minX;
        if(y < minY) y = minY;
        if(x > maxX) x = maxX;
        if(y > maxY) y = maxY;

        BufferedImage croppedWorld = world.getSubimage(x, y, snapshotWidth, snapshotHeight);
        int w = croppedWorld.getWidth();
        int h = croppedWorld.getHeight();

        //we want the minimap to be 253x189
        int wantedWidth = w / 6;
        int wantedHeight = h / 6;

        AffineTransform at = new AffineTransform();
        at.scale((double) wantedWidth / (double) w, (double) wantedHeight / (double) h);
        AffineTransformOp scaleOp = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);

        BufferedImage rescaledImage = new BufferedImage(wantedWidth, wantedHeight, BufferedImage.TYPE_INT_ARGB);
        rescaledImage = scaleOp.filter(croppedWorld, rescaledImage);

        return rescaledImage;
    }

}