package uk.ac.brighton.uni.ab607.mmorpg.common;

public enum GameCharacterClass {
    MONSTER(50, 50),
    NOVICE(10, 10, "7000"),
    WARRIOR(100, 20),
    SCOUT(75, 55),
    MAGE(55, 100);

    public final int hp;
    public final int sp;
    public final Skill[] skills;

    private GameCharacterClass(int hp, int sp, String... IDs) {
        this.hp = hp;
        this.sp = sp;
        this.skills = new Skill[IDs.length];
        for (int i = 0; i < skills.length; i++)
            skills[i] = SkillFactory.getSkillById(IDs[i]);
    }

    @Override
    public String toString() {
        return this.name();
    }
}
