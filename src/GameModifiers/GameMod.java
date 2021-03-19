package GameModifiers;
/*****************************************
 This class is responsible for working
 together with GameModifiers.GameObjectManager to alter
 certain GameModifiers.Game Objects, such as spawning
 new players, updating camera coordinates
 and updating their HUDs. A conduit to
 avoid direct access to hero and creep objects.
 Also controls the game state, flagging
 the program to end the game when needed.
 ****************************************/
import GameObject.*;

public class GameMod  {

    private GameObjectManager handler;
    private BufferedImageLoader imageLoader;
    private Hero hero1;
    private Hero hero2;
    private Camera camera;
    private boolean endGame = false;
    private boolean isP1Winner = false;
    private boolean countingToRespawn = false;
    private int respawnInterval = 0;
    private int secondsToRespawn = 10;
    private int hero1Xp;
    private int p1SpawnXCoord;
    private int p1SpawnYCoord;
    private int creepWaveInterval = 1200;
    private static final int RESPAWN_DURATION = 10;
    private static final int FPS = 60;
    private static final int CREEP_WAVE_DELAY_SEC = 20;

    public GameMod(Camera camera, GameObjectManager handler, BufferedImageLoader imageLoader){
        this.camera = camera;
        this.handler = handler;
        this.imageLoader = imageLoader;
    }

    public void init(){
        findHeroes();
        if(hero1 != null) {
            p1SpawnXCoord = hero1.getX();
            p1SpawnYCoord = hero1.getY();
        }
        hero1Xp = hero1.getXp();
    }

    public Hero getHero1() { return hero1; }
    public Hero getHero2() { return hero2; }
    public int getSecondsToRespawn() { return this.secondsToRespawn; }
    public boolean isCountingToRespawn() { return this.countingToRespawn; }
    public boolean getEndGame() { return endGame; }
    public boolean getIsP1Winner() { return isP1Winner; }

    public void setHero1(Hero hero1) { this.hero1 = hero1; }
    public void setHero2(Hero hero2) { this.hero2 = hero2; }


    public void tick(){

        findHeroes();

        hero1Xp = hero1.getXp();
        camera.tick(hero1);

        //increment creep wave interval or spawn a new creep wave
        if(creepWaveInterval < (FPS * CREEP_WAVE_DELAY_SEC)) {
            creepWaveInterval++;
        }else {
            //spawn radiant creep wave
            handler.addObject(new Creep(730,523, ID.RadiantCreep, handler, imageLoader, imageLoader.getImage("radiantCreepWalkSheet")));
            handler.addObject(new Creep(586,523, ID.RadiantCreep, handler, imageLoader, imageLoader.getImage("radiantCreepWalkSheet")));
            handler.addObject(new Creep(730,585, ID.RadiantCreep, handler, imageLoader, imageLoader.getImage("radiantCreepWalkSheet")));
            handler.addObject(new Creep(586,585, ID.RadiantCreep, handler, imageLoader, imageLoader.getImage("radiantCreepWalkSheet")));

            //spawn dire creep wave
            handler.addObject(new Creep(730,2285, ID.DireCreep, handler, imageLoader, imageLoader.getImage("direCreepWalkSheet")));
            handler.addObject(new Creep(586,2285, ID.DireCreep, handler, imageLoader, imageLoader.getImage("direCreepWalkSheet")));
            handler.addObject(new Creep(730,2429, ID.DireCreep, handler, imageLoader, imageLoader.getImage("direCreepWalkSheet")));
            handler.addObject(new Creep(586,2429, ID.DireCreep, handler, imageLoader, imageLoader.getImage("direCreepWalkSheet")));

            //reset the interval
            creepWaveInterval = 0;
        }

        //look at creep stats
        checkOnEnemies();

        if(countingToRespawn) respawnCountDown();
    }

    public void findHeroes(){

        for(int i = 0; i < handler.getObject().size(); i++) {
            GameObject temp = handler.getObject().get(i);
            if(temp.getId() == ID.Player1){
                setHero1((Hero) temp);
            }
        }
    }

    public void respawnCountDown(){
        if(secondsToRespawn > 0){
            respawnInterval++;
            if(respawnInterval > 59){
                secondsToRespawn--;
                respawnInterval = 0;
            }
        }
        else{
            countingToRespawn = false;
            hero1.setRespawning(false);
            secondsToRespawn = RESPAWN_DURATION;
            respawnInterval = 0;
            Sounds.playP1RespawnSound();
        }
    }

    public void checkOnEnemies(){
        for(int i = 0; i < handler.getObject().size(); i++){
            GameObject temp = handler.getObject().get(i);

            //if a Radiant GameObject.Creep is Dead
            if(temp.getId() == ID.RadiantCreep){
                VulnerableObject vO = (VulnerableObject) temp;
                if(vO.getLivesCount() < 1){
                    handler.addDeadThing(new DeadCreep(vO.getX(), vO.getY(), ID.CreepCorpse, imageLoader.getImage("radiantCreepCorpse")));
                    resetAttacker(vO);
                    handler.removeObject(temp);
                    Sounds.playRadiantDeathSound();
                }
            }

            //if a Dire GameObject.Creep is Dead
            if(temp.getId() == ID.DireCreep){
                VulnerableObject vO = (VulnerableObject) temp;
                if(vO.getLivesCount() < 1){
                    handler.addDeadThing(new DeadCreep(vO.getX(), vO.getY(), ID.CreepCorpse, imageLoader.getImage("direCreepCorpse")));
                    if(hero1 != null){
                        if((hero1.xpRange().intersects(temp.getBounds())) && (hero1.isMaxLevel() == false)) {
                            hero1.setXp(10);
                        }
                    }
                    resetAttacker(vO);
                    handler.removeObject(temp);
                    Sounds.playDireDeathSound();
                }
            }

            //if the hero dies
            if(temp.getId() == ID.Player1){
                Hero hero = (Hero) temp;
                if(hero.getHitPoints() < 1) {
                    resetAttacker((VulnerableObject) temp);
                    Sounds.playRadiantDeathSound();
                    respawn();
                    countingToRespawn = true;
                }
            }

            //if a tower is destroyed
            if((temp.getId() == ID.RadiantTower) || (temp.getId() == ID.DireTower)){
                VulnerableObject vO = (VulnerableObject) temp;
                if(vO.getLivesCount() < 1){
                    handler.addDeadThing(new DeadTower(vO.getX(), vO.getY(), ID.DeadTower, imageLoader.getImage("destroyedTower")));
                    if((vO.getId() == ID.DireTower) && (hero1 != null)){
                        if((hero1.xpRange().intersects(temp.getBounds())) && (hero1.isMaxLevel() == false)) {
                            hero1.setXp(25);
                        }
                    }
                    resetAttacker(vO); //free any creeps who were attacking this tower
                    handler.removeObject(temp);
                    Sounds.playFallingTowerSound();
                }
            }
            if(temp.getId() == ID.DireBase){
                VulnerableObject vO = (VulnerableObject) temp;
                if(vO.getLivesCount() < 1) {
                    Sounds.playFallingBaseSound();
                    isEndGame(true);
                }
            }
            if(temp.getId() == ID.RadiantBase){
                VulnerableObject vO = (VulnerableObject) temp;
                if(vO.getLivesCount() < 1) {
                    Sounds.playFallingBaseSound();
                    isEndGame(false);
                }
            }
        }
    }

    public void resetAttacker(VulnerableObject deadObject){
        for(int j = 0; j < handler.getObject().size(); j++) {
            GameObject temp2 = handler.getObject().get(j);

            //if we find a creep
            if(temp2 instanceof Creep){
                Creep attacker = (Creep) temp2;
                if (deadObject.equals(attacker.getMyEnemy())) { //if the dying object (creep, tower, or hero) is the same at a creep's enemy
                    attacker.setMyEnemy(null);
                }
            }
            //if we find a hero
            if(temp2 instanceof Hero){
                Hero attacker = (Hero) temp2;
                if (deadObject.equals(attacker.getMyEnemy())) { //if the dying object (creep, tower, or hero) is the same as a GameObject.Hero's enemy
                    attacker.setMyEnemy(null);
                }
            }
        }
    }

    private void respawn(){
        handler.addDeadThing(new DeadCreep(hero1.getX(), hero1.getY(), ID.CreepCorpse, imageLoader.getImage("radiantCreepCorpse")));
        handler.removeObject(hero1);
        Hero newHero = new Hero(p1SpawnXCoord, p1SpawnYCoord, ID.Player1, handler, imageLoader, imageLoader.getImage("p1HeroWalkSheet"));
        newHero.setXp(hero1Xp);
        newHero.setRespawning(true);
        handler.addObject(newHero);
        setHero1(newHero);
    }

    //play him off, keyboard cat
    public void isEndGame(boolean isPlayer1Winner){
        if(isPlayer1Winner) isP1Winner = true;
        endGame = true;
    }
}
