package uk.ac.brighton.uni.ab607.mmorpg.common;

import java.util.HashMap;

public class GameCharacterClassChanger {

    private static HashMap<GameCharacterClass, Ascension> reqList = 
            new HashMap<GameCharacterClass, Ascension>();
    
    static {
        reqList.put(GameCharacterClass.NOVICE, new Ascension(2, 2, GameCharacterClass.WARRIOR,
                GameCharacterClass.SCOUT, GameCharacterClass.MAGE));
        reqList.put(GameCharacterClass.WARRIOR, new Ascension(3, 3, GameCharacterClass.CRUSADER, 
                GameCharacterClass.GLADIATOR));
    }
    
    /**
     * No instances
     */
    private GameCharacterClassChanger() {}
    
    public static boolean canChangeClass(Player ch) {
        Ascension r = reqList.get(ch.charClass);
        return r != null && ch.baseLevel >= r.baseLevel && ch.getJobLevel() >= r.jobLevel;
    }
    
    public static GameCharacterClass[] getAscensionClasses(Player ch) {
        return reqList.get(ch.charClass).classesTo;
    }
    
    private static class Ascension {
        
        public final GameCharacterClass[] classesTo;
        public final int baseLevel, jobLevel;
        
        public Ascension(int baseLevel, int jobLevel, GameCharacterClass... classes) {
            this.baseLevel = baseLevel;
            this.jobLevel = jobLevel;
            this.classesTo = classes;
        }
    }
}
