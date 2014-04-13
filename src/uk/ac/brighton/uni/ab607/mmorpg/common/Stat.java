package uk.ac.brighton.uni.ab607.mmorpg.common;

/**
 * Stats of a game character
 * 
 * @author Almas Baimagambetov (ab607@uni.brighton.ac.uk)
 * @version 1.0
 *
 */
public enum Stat {
    MAX_HP, MAX_SP, ATK, MATK, DEF, MDEF, ARM, MARM, ASPD, MSPD, CRIT, MCRIT;

    @Override
    public String toString() {
        return this.name();
    }
}
