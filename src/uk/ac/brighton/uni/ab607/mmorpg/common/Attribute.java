package uk.ac.brighton.uni.ab607.mmorpg.common;

/**
 * 9 primary attributes of a game character
 * 
 * @author Almas Baimagambetov (ab607@uni.brighton.ac.uk)
 * @version 1.0
 *
 */
public enum Attribute {
    STRENGTH, VITALITY, DEXTERITY, AGILITY, INTELLECT, WISDOM, WILLPOWER, PERCEPTION, LUCK;

    @Override
    public String toString() {
        return this.name().length() > 3 ? this.name().substring(0, 3) : this.name();
    }
}
