package uk.ac.brighton.uni.ab607.mmorpg.common.item;

public enum ItemLevel {
    NORMAL(3, 10),  // refinement chance 100/90/80/70/60
    UNIQUE(5, 15),  // 100/85/70/55/40
    EPIC(10, 20);   // 100/80/60/40/20

    public final int bonus;
    public final int refineChanceReduction;

    private ItemLevel(int bonus, int chance) {
        this.bonus = bonus;
        this.refineChanceReduction = chance;
    }
}
