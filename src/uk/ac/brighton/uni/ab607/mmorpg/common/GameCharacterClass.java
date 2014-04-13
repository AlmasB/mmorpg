package uk.ac.brighton.uni.ab607.mmorpg.common;

public enum GameCharacterClass {
    MONSTER(50, 50),
    NOVICE(10, 10),
    WARRIOR(100, 20),
    SCOUT(75, 55),
    MAGE(55, 100);

    public final int hp;
    public final int sp;

    private GameCharacterClass(int hp, int sp) {
        this.hp = hp;
        this.sp = sp;
    }

    @Override
    public String toString() {
        return this.name();
    }
}
