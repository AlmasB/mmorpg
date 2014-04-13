package uk.ac.brighton.uni.ab607.mmorpg.common.item;

import java.util.HashMap;

import uk.ac.brighton.uni.ab607.mmorpg.common.Stat;

public class EssenceFactory {

    private static HashMap<String, Essence> defaultEssences = new HashMap<String, Essence>();

    static {
        defaultEssences.put("9001", new Essence("9001", "Angry Wolf",
                Stat.ATK, 5));
    }

    private EssenceFactory() {}

    public static Essence createEssence(String id) {
        return new Essence(defaultEssences.get(id));
    }
}
