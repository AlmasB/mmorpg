package uk.ac.brighton.uni.ab607.mmorpg.common;

public abstract class Skill implements java.io.Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 442371944346845569L;

    protected static int uniqueSkillID = 7000;

    public String id, name, description;    // TODO: final?

    protected static final int MAX_LEVEL = 10;

    protected int level;

    public Skill(String id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    // TODO: extra check by GUI
    public boolean levelUp() {
        if (level < MAX_LEVEL) {
            level++;
            return true;
        }
        return false;
    }

    public int getLevel() {
        return level;
    }
}
