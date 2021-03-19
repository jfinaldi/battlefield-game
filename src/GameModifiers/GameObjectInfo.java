package GameModifiers;

public enum GameObjectInfo {
    BOTTOMCENTERTREE("bottomCenterTree","/BottomCenterTree.png"),
    BOTWALL("botWall","/botWall.png"),
    BOTLEFTDIRT("botLeftDirt", "/botLeftDirt.png"),
    BOTMIDDIRT("botMidDirt", "/botMidDirt.png"),
    BOTRIGHTDIRT("botRightDirt", "/botRightDirt.png"),
    CENTERDIRT("centerDirt", "/centerDirt.png"),
    DESTROYEDTOWER("destroyedTower", "/destroyedTower.png"),
    DIRECREEPWALKSHEET("direCreepWalkSheet", "/direCreepWalkSheet.png"),
    DIRECREEPATTACKSHEET("direCreepAttackSheet", "/direCreepAttackSheet.png"),
    DIRECREEPCORPSE("direCreepCorpse", "/direCreepCorpse.png"),
    EXPLOSIONSHEET("explosionSheet", "/explosion.png"),
    FIREBALLSHEET("fireballSheet", "/fireballSheet.png"),
    FLAMESHEET("flameSheet", "/flameSheet.png"),
    FLOORTILE("grass","/grass.png"),
    GOLDMINE("goldMine","/goldMine.png"),
    LEFTEDGETREEA("leftEdgeTreeA","/LeftEdgeTreeA.png"),
    LEFTEDGETREEB("leftEdgeTreeB","/LeftEdgeTreeB.png"),
    LEFTDIRT("leftDirt", "/leftDirt.png"),
    LEVELDESIGN("levelDesign", "/TowerDMap.png"),
    MIDWALL("midWall", "/midWall.png"),
    P1HEROWALKSHEET("p1HeroWalkSheet", "/hero1WalkSheet.png"),
    P1HEROATTCKSHEET("p1HeroAttackSheet", "/hero1AttackSheet.png"),
    P2HEROWALKSHEET("p2HeroWalkSheet", "/hero2WalkSheet.png"),
    P2HEROATTCKSHEET("p2HeroAttackSheet", "/hero2AttackSheet.png"),
    P1HEROPORTRAIT("hero1Portrait", "/hero1Portrait.png"),
    P2HEROPORTRAIT("hero2Portrait", "/hero2Portrait.png"),
    P1FARM("p1Farm", "/p1Farm.png"),
    P2FARM("p2Farm", "/p2Farm.png"),
    P1HERO("p1Hero","/p1Hero.png"),
    P2HERO("p2Hero","/p2Hero.png"),
    P1TOWER("p1Tower","/p1Tower.png"),
    P2TOWER("p2Tower","/p2Tower.png"),
    P1BASE("p1Base","/p1Base.png"),
    P2BASE("p2Base","/p2Base.png"),
    ROCKS("rocks", "/rocks.png"),
    RIGHTDIRT("rightDirt", "/rightDirt.png"),
    RIGHTEDGETREEA("rightEdgeTreeA","/RightEdgeTreeA.png"),
    RIGHTEDGETREEB("rightEdgeTreeB","/RightEdgeTreeB.png"),
    RADIANTCREEPWALKSHEET("radiantCreepWalkSheet", "/radiantCreepWalkSheet.png"),
    RADIANTCREEPATTACKSHEET("radiantCreepAttackSheet", "/radiantCreepAttackSheet.png"),
    RADIANTCREEPCORPSE("radiantCreepCorpse", "/radiantCreepCorpse.png"),
    THICKTREES("thickTrees","/thickTrees.png"),
    TOPLEFTDIRT("topLeftDirt", "/topLeftDirt.png"),
    TOPLEFTTREE("topLeftTree", "/topLeftTree.png"),
    TOPMIDDIRT("topMidDirt", "/topMidDirt.png"),
    TOPRIGHTDIRT("topRightDirt", "/topRightDirt.png"),
    TOPRIGHTTREE("topRightTree", "/topRightTree.png"),
    TOPWALL("topWall","/topWall.png"),
    UI("ui", "/uiFrame.png"),
    WEAPON("weapon","/weapon.png"),
    YOULOSE("youLose", "/youLose.png"),
    YOUWIN("youWin", "/youWin.png");

    private String ID, path;

    GameObjectInfo(String ID, String path){
        this.ID = ID;
        this.path = path;
    }

    public String getID() {
        return ID;

    }
    public String getPath() { return path; }

}
