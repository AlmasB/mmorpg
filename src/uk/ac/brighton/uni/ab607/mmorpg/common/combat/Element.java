package uk.ac.brighton.uni.ab607.mmorpg.common.combat;

public enum Element {
    NEUTRAL(1.00, 0.75, 0.75, 0.75, 0.75),
    FIRE   (1.25, 0.00, 0.25, 0.50, 2.00),
    WATER  (1.25, 2.00, 0.00, 0.25, 0.50),
    AIR    (1.25, 0.50, 2.00, 0.00, 0.25),
    EARTH  (1.25, 0.25, 0.5, 2, 0);

    private double[] modifiers;

    public double getDamageModifierAgainst(Element ele) {
        return this.modifiers[ele.ordinal()];
    }

    private Element(double... modifiers) {
        this.modifiers = modifiers;
    }
}
