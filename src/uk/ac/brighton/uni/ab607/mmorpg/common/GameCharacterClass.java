package uk.ac.brighton.uni.ab607.mmorpg.common;

public enum GameCharacterClass {
    MONSTER(50, 50),
    NOVICE(10, 10),
    WARRIOR(100, 20),
    SCOUT(75, 55),
    MAGE(55, 100);

    public final int hp;
    public final int sp;
    public final Skill[] skills;

    private GameCharacterClass(int hp, int sp, Skill... skills) {
        this.hp = hp;
        this.sp = sp;
        this.skills = skills;
    }

    @Override
    public String toString() {
        return this.name();
    }
}
